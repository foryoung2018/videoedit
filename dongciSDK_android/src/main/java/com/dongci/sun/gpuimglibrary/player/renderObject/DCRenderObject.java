package com.dongci.sun.gpuimglibrary.player.renderObject;

import android.opengl.GLES20;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.common.Rotation;
import com.dongci.sun.gpuimglibrary.gles.EasyGlUtils;
import com.dongci.sun.gpuimglibrary.gles.TextureRotationUtil;
import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageFilterGroup;
import com.dongci.sun.gpuimglibrary.player.DCAssetInfo;
import com.dongci.sun.gpuimglibrary.player.DCOptions;
import com.dongci.sun.gpuimglibrary.player.math.DCMatrix4;
import com.dongci.sun.gpuimglibrary.player.math.DCMatrixStack;
import com.dongci.sun.gpuimglibrary.player.math.DCVector3;
import com.dongci.sun.gpuimglibrary.player.script.DCActor;
import com.dongci.sun.gpuimglibrary.player.script.DCTimeEvent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class DCRenderObject {
    private static final String TAG = "DCRenderObject";

    static final String vertexShader =
            "uniform mat4 mvpMatrix;\n" +
                    "uniform mat4 uvMatrix;\n" +
                    "attribute vec4 position;\n" +
                    "attribute vec4 inputTextureCoordinate;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "void main() {\n" +
                    "  vec4 pos = mvpMatrix * position;\n" +
                    "  gl_Position = vec4(pos.xy, 0.0, 1.0);\n" +
                    "  textureCoordinate = (uvMatrix * inputTextureCoordinate).xy;\n" +
                    "}\n";
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int VERTICES_DATA_STRIDE_BYTES = 3 * FLOAT_SIZE_BYTES;
    private static final int COORDS_DATA_STRIDE_BYTES = 2 * FLOAT_SIZE_BYTES;

    private int program = 0;
    private int mvpMatrixHandle;
    private int positionHandle;
    private int textureCoordinateHandle;
    private int uniformTransparencyHandle;
    int uvMatrixHandle;
    int uniformTextureHandle;

    public boolean firstRended = false;

    public DCAssetInfo assetInfo;
    int textureID;
    public GPUImageFilterGroup filterGroup;



    DCRenderObject(DCAssetInfo assetInfo) {
        this.assetInfo = assetInfo;

    }

    public void draw(DCMatrixStack matrixStack, long presentationTime, DCActor actor, DCTimeEvent timeEvent, GPUImageFilterGroup filter, GPUImageFilterGroup imageFilter, int viewportWidth, int viewportHeight) {
        if (updateTexture(presentationTime, timeEvent) == 0) {
            return;
        }
        float[] coords = calculateCoordinates(actor.currentCropRect);
        FloatBuffer coordBuffer = ByteBuffer.allocateDirect(
                coords.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        coordBuffer.put(coords).position(0);


        int texture_id = 0;
        int texId = 0;
        if (filter != null) {
            texId = filter.onDraw(this.textureID, null, coordBuffer);

            // reset render size to video size
            GLES20.glViewport(0, 0, viewportWidth, viewportHeight);
        }

        if(imageFilter != null) {
            texture_id = drawFrames(viewportWidth,viewportHeight);
            if(texture_id != 0) {
                texId = imageFilter.onDraw(texture_id);
                GLES20.glViewport(0, 0, viewportWidth, viewportHeight);
            }

        }
        if (!useProgram()) {
            return;
        }
        matrixStack.gl_pushMatrix();
        {
            // transform
            matrixStack.gl_translate(actor.translation);
            matrixStack.gl_rotate(new DCVector3(actor.rotation.x(), actor.rotation.y(), (float)(actor.rotation.z() + assetInfo.assetWrapper.getAsset().zRotation * Math.PI / 180.0)));
            matrixStack.gl_scale(actor.scale);

            // set transparency
            GLES20.glUniform1f(uniformTransparencyHandle, actor.transparency);

            // set mvp matrix
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrixStack.getMatrix().m, 0);

            // update shader parameters
            //updateParameters(texId == 0 ? this.textureID : texId);
            //updateParameters(texture_id == 0 ? this.textureID : texture_id);

            updateParameters(texId == 0 ? (texture_id == 0 ? this.textureID : texture_id) : texId);

            //updateParameters(texId == 0 ? textureId : texId);

            //updateParameters(textureId);
            // set vertices
            float[] vertices = calculateVertices(actor.currentCropRect);
            FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(
                    vertices.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            vertexBuffer.put(vertices).position(0);

            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,
                    VERTICES_DATA_STRIDE_BYTES, vertexBuffer);
            GLES20.glEnableVertexAttribArray(positionHandle);

            // set coords
            if (texId != 0) {
                float[] flipTexture = TextureRotationUtil.getRotation(Rotation.NORMAL, false, true);
                coordBuffer.put(flipTexture).position(0);
            }
            GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                    COORDS_DATA_STRIDE_BYTES, coordBuffer);
            GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

            // draw elements
            GLES20.glDepthMask(false);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            checkGlError("glDrawArrays");

            GLES20.glDisable(GLES20.GL_BLEND);
            GLES20.glDepthMask(true);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
        matrixStack.gl_popMatrix();
    }

    protected void updateParameters(int textureId) {
    }

    protected int drawFrames(int width, int height) {
        return  0;
    }

    public int updateTexture(long presentationTime, DCTimeEvent timeEvent) {
        return this.textureID;
    }

    protected int createProgram() {
        return 0;
    }

    private boolean useProgram() {
        if (program == 0) {
            program = createProgram();
            if (program == 0) {
                return false;
            }

            positionHandle = GLES20.glGetAttribLocation(program, "position");
            checkGlError("glGetAttribLocation position");
            if (positionHandle == -1) {
                return false;
            }
            textureCoordinateHandle = GLES20.glGetAttribLocation(program, "inputTextureCoordinate");
            checkGlError("glGetAttribLocation inputTextureCoordinate");
            if (textureCoordinateHandle == -1) {
                return false;
            }

            mvpMatrixHandle = GLES20.glGetUniformLocation(program, "mvpMatrix");
            checkGlError("glGetUniformLocation mvpMatrix");
            if (mvpMatrixHandle == -1) {
                return false;
            }

            uvMatrixHandle = GLES20.glGetUniformLocation(program, "uvMatrix");
            checkGlError("glGetUniformLocation surfaceMatrix");
            if (uvMatrixHandle == -1) {
                return false;
            }

            uniformTransparencyHandle = GLES20.glGetUniformLocation(program, "transparency");
            checkGlError("glGetUniformLocation transparency");
            if (uniformTransparencyHandle == -1) {
                return false;
            }

            uniformTextureHandle = GLES20.glGetUniformLocation(program, "texture");
            checkGlError("glGetUniformLocation uniformTextureHandle");
            if (uniformTextureHandle == -1) {
                return false;
            }
        }
        GLES20.glUseProgram(program);
        checkGlError("glUseProgram");
        return true;
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(pixelShader);

        return program;
    }

    private static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    static boolean checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            return false;
        }
        return true;
    }

    private float[] calculateVertices(DCOptions.DCCoordinateInfo cropRect) {
        if (cropRect.lt.x() == 0 && cropRect.lt.y() == 0 &&
                cropRect.rt.x() == 1 && cropRect.rt.y() == 0 &&
                cropRect.lb.x() == 0 && cropRect.lb.y() == 1 &&
                cropRect.rb.x() == 1 && cropRect.rb.y() == 1) {
            return this.assetInfo.vertices.getData();
        }

        DCOptions.DCVertexInfo vertices = this.assetInfo.vertices;
        float w = Math.abs(vertices.rt.x() - vertices.lt.x());
        float h = Math.abs(vertices.rb.y() - vertices.rt.y());

        DCOptions.DCVertexInfo dest = new DCOptions.DCVertexInfo();
        dest.lt.setRawData(w * cropRect.lt.x() - w / 2.0f, h * cropRect.lt.y() - h / 2.0f, 0.0f);
        dest.rt.setRawData(w * cropRect.rb.x() - w / 2.0f, h * cropRect.lt.y() - h / 2.0f, 0.0f);
        dest.lb.setRawData(w * cropRect.lt.x() - w / 2.0f, h * cropRect.rb.y() - h / 2.0f, 0.0f);
        dest.rb.setRawData(w * cropRect.rb.x() - w / 2.0f, h * cropRect.rb.y() - h / 2.0f, 0.0f);

        DCVector3 centerVertex = new DCVector3((dest.rb.x() + dest.lt.x()) / 2.0f, (dest.rb.y() + dest.lt.y()) / 2.0f, 0.0f);

        // build tranlate matrix to translate the vertices center pos to (0, 0, 0)
        DCMatrix4 translateMat = DCMatrix4.createTranslation(-centerVertex.x(), -centerVertex.y(), -centerVertex.z());

        dest.lt = translateMat.matrixMultiplyVector3(dest.lt);
        dest.rt = translateMat.matrixMultiplyVector3(dest.rt);
        dest.lb = translateMat.matrixMultiplyVector3(dest.lb);
        dest.rb = translateMat.matrixMultiplyVector3(dest.rb);

        return dest.getData();
    }

    private float[] calculateCoordinates(DCOptions.DCCoordinateInfo cropRect) {
        if (cropRect.lt.x() == 0 && cropRect.lt.y() == 0 &&
                cropRect.rt.x() == 1 && cropRect.rt.y() == 0 &&
                cropRect.lb.x() == 0 && cropRect.lb.y() == 1 &&
                cropRect.rb.x() == 1 && cropRect.rb.y() == 1) {
            return this.assetInfo.coordinates.getData();
        }

        DCOptions.DCCoordinateInfo coordinates = this.assetInfo.coordinates;
        float w = Math.abs(coordinates.rt.x() - coordinates.lt.x());
        float h = Math.abs(coordinates.rb.y() - coordinates.rt.y());

        DCOptions.DCCoordinateInfo dest = new DCOptions.DCCoordinateInfo();
        dest.lt.setRawData(coordinates.lt.x() + w * cropRect.lt.x(), coordinates.lt.y() + h * cropRect.lt.y());
        dest.rt.setRawData(coordinates.lt.x() + w * cropRect.rt.x(), coordinates.lt.y() + h * cropRect.rt.y());
        dest.lb.setRawData(coordinates.lt.x() + w * cropRect.lb.x(), coordinates.lt.y() + h * cropRect.lb.y());
        dest.rb.setRawData(coordinates.lt.x() + w * cropRect.rb.x(), coordinates.lt.y() + h * cropRect.rb.y());

        return dest.getData();
    }

    public void addFilter(GPUImageFilter filter,int width,int height) {

        if(filter != null) {
            if (this.filterGroup == null) {
                this.filterGroup = new GPUImageFilterGroup();
                this.filterGroup.addFilter(filter);
                this.filterGroup.init();
                this.filterGroup.onInputSizeChanged(width, height);
                this.filterGroup.onDisplaySizeChanged(width, height);
                filter.onInputSizeChanged(width, height);
                filter.onDisplaySizeChanged(width, height);
            }
        }
    }

    public void release() {
        if (this.textureID != 0) {
            int[] textures = {this.textureID};
            GLES20.glDeleteTextures(1, textures, 0);
            this.textureID = 0;
        }

        if (this.program != 0) {
            GLES20.glDeleteProgram(this.program);
            this.program = 0;
        }

        if(this.filterGroup != null) {
            filterGroup.destroy();
            filterGroup = null;
        }
    }
}
