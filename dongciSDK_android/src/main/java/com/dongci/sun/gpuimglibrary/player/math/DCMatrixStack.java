package com.dongci.sun.gpuimglibrary.player.math;

import java.util.Stack;

public class DCMatrixStack {
    private DCMatrix4 modelMatrix = new DCMatrix4();
    private Stack<DCMatrix4> modelMatrixStack = new Stack<>();

    public DCMatrixStack() {

    }

    public DCMatrix4 getMatrix() {
        return this.modelMatrix;
    }

    public void gl_flushMatrix() {
        this.modelMatrixStack.clear();
        this.modelMatrix = new DCMatrix4();
    }

    public void gl_pushMatrix() {
        this.modelMatrixStack.push(new DCMatrix4(this.modelMatrix));
    }

    public void gl_popMatrix() {
        if (this.modelMatrixStack.size() == 0) {
            return;
        }
        this.modelMatrix = this.modelMatrixStack.pop();
    }

    public void gl_multiplyMatrix(DCMatrix4 mat4) {
        this.modelMatrix = DCMatrix4.multiply(mat4, this.modelMatrix);
    }

    public void gl_translate(DCVector3 translation) {
        this.modelMatrix = DCMatrix4.multiply(DCMatrix4.createTranslation(translation.x(), translation.y(), translation.z()), this.modelMatrix);
    }

    public void gl_scale(DCVector3 scale) {
        this.modelMatrix = DCMatrix4.multiply(DCMatrix4.createScale(scale.x(), scale.y(), scale.z()), this.modelMatrix);
    }

    public void gl_rotate(DCVector3 rotation) {
        this.modelMatrix = DCMatrix4.multiply(DCMatrix4.createXRotation(rotation.x()), this.modelMatrix);
        this.modelMatrix = DCMatrix4.multiply(DCMatrix4.createYRotation(rotation.y()), this.modelMatrix);
        this.modelMatrix = DCMatrix4.multiply(DCMatrix4.createZRotation(rotation.z()), this.modelMatrix);
    }
}
