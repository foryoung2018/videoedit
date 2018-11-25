package com.dongci.sun.gpuimglibrary.gles.filter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dongci.sun.gpuimglibrary.R;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.DCGPUImageEmptyFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.FWHudsonFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.FWSutroFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.FWValenciaFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.GPUImageErosionFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.GPUImageLowPassFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.GPUImageMaskFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.GPUImagePolkaDotFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.GPUImageZoomBlurFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageCrosshatchFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageFalseColorFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageGrayscaleFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageSobelEdgeDetectionFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filterold.GPUImageLookupFilter;

import java.util.ArrayList;
import java.util.List;

public class FilterUtils {

    public static String filterResFoler;

//    public List<FilterInfoEntity> init(){
//
//        List<FilterInfoEntity> FILTER_LIST = new ArrayList<>();
//        int i = 0;
//        for(FilterType type: FilterType.values()){
////           FILTER_LIST.add(new FilterInfoEntity(i++, R.drawable.recorder_filter_0,String.valueOf(type)));
//        }
//        Log.d("tag","FILTER_LIST--size->"+FILTER_LIST.size());
//
//        return  FILTER_LIST;
//    }

    public List<FilterType> init1() {

        List<FilterType> FILTER_LIST = new ArrayList<FilterType>();

        int i = 1;

        for (FilterType type : FilterType.values()) {
            FILTER_LIST.add(type);
        }

        return FILTER_LIST;

    }

    public static List<FilterType> getAllFilterList() {

        List<FilterType> FILTER_LIST = new ArrayList<FilterType>();

        int i = 1;

        for (FilterType type : FilterType.values()) {
            FILTER_LIST.add(type);
        }

        return FILTER_LIST;

    }

    public enum FilterType {
        LFGPUFilterTypeNormal,
        LFGPUFilterTypeBL1,
        LFGPUFilterTypeBL2,
        LFGPUFilterTypeBL3,
        LFGPUFilterTypeBL4,
        LFGPUFilterTypeBL5,
        LFGPUFilterTypeYE1,
        LFGPUFilterTypeYE2,
        LFGPUFilterTypeYE3,
        LFGPUFilterTypeYE4,
        LFGPUFilterTypeYE5,
        LFGPUFilterTypeYE6,
        LFGPUFilterTypeYE7,
        LFGPUFilterTypeRE1,
        LFGPUFilterTypeGR1,
        LFGPUFilterTypeGR2,
        LFGPUFilterTypeGR3,
        LFGPUFilterTypeGR4,
        LFGPUFilterTypeGR5,
        LFGPUFilterTypePU1,
        LFGPUFilterTypePU2,
        LFGPUFilterTypeBW1,
        LFGPUFilterTypeBW2,
        LFGPUFilterTypeBW3,
        LFGPUFilterTypeSP1,
        LFGPUFilterTypeSP2,
        LFGPUFilterTypeSP3,
        //        LFGPUFilterTypeSP4,
        LFGPUFilterTypeSP5,
        LFGPUFilterTypeSP6,
        LFGPUFilterTypeSP7
    }

    public static GPUImageFilter filterWithType(final Context context, final FilterType type) {

        switch (type) {

            case LFGPUFilterTypeNormal:

                return new DCGPUImageEmptyFilter();

            case LFGPUFilterTypeBL1:

                GPUImageLookupFilter filter0 = new GPUImageLookupFilter();

                filter0.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img7));

                return filter0;

            case LFGPUFilterTypeBL2:

                GPUImageLookupFilter filter1 = new GPUImageLookupFilter();

                filter1.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img22));

                return filter1;

            case LFGPUFilterTypeBL3:

                GPUImageLookupFilter filter2 = new GPUImageLookupFilter();

                filter2.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img68));

                return filter2;

            case LFGPUFilterTypeBL4:

                GPUImageLookupFilter filter3 = new GPUImageLookupFilter();

                filter3.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img69));

                return filter3;

            case LFGPUFilterTypeBL5:// 5

                return new FWHudsonFilter(BitmapFactory.decodeResource(context.getResources(), R.drawable.hudsonbackground), BitmapFactory.decodeResource(context.getResources(), R.drawable.overlaymap), BitmapFactory.decodeResource(context.getResources(), R.drawable.hudsonmap));

            case LFGPUFilterTypeYE1:

                GPUImageLookupFilter filter4 = new GPUImageLookupFilter();

                filter4.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img2));

                return filter4;

            case LFGPUFilterTypeYE2:

                GPUImageLookupFilter filter5 = new GPUImageLookupFilter();

                filter5.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img5));

                return filter5;

            case LFGPUFilterTypeYE3:

                GPUImageLookupFilter filter6 = new GPUImageLookupFilter();

                filter6.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img9));

                return filter6;

            case LFGPUFilterTypeYE4:

                GPUImageLookupFilter filter7 = new GPUImageLookupFilter();

                filter7.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img13));

                return filter7;

            case LFGPUFilterTypeYE5:

                GPUImageLookupFilter filter8 = new GPUImageLookupFilter();

                filter8.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img23));

                return filter8;

            case LFGPUFilterTypeYE6:

                GPUImageLookupFilter filter9 = new GPUImageLookupFilter();

                filter9.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img61));

                return filter9;

            case LFGPUFilterTypeYE7:

                return new FWSutroFilter(BitmapFactory.decodeResource(context.getResources(), R.drawable.vignettemap), BitmapFactory.decodeResource(context.getResources(), R.drawable.sutrometal), BitmapFactory.decodeResource(context.getResources(), R.drawable.softlight), BitmapFactory.decodeResource(context.getResources(), R.drawable.sutroedgeburn), BitmapFactory.decodeResource(context.getResources(), R.drawable.sutrocurves));

            case LFGPUFilterTypeRE1:

                GPUImageLookupFilter filter10 = new GPUImageLookupFilter();

                filter10.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img49));

                return filter10;

            case LFGPUFilterTypeGR1:

                GPUImageLookupFilter filter11 = new GPUImageLookupFilter();

                filter11.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img12));

                return filter11;

            case LFGPUFilterTypeGR2:

                GPUImageLookupFilter filter12 = new GPUImageLookupFilter();

                filter12.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img15));

                return filter12;

            case LFGPUFilterTypeGR3:

                GPUImageLookupFilter filter13 = new GPUImageLookupFilter();

                filter13.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img54));

                return filter13;

            case LFGPUFilterTypeGR4:

                GPUImageLookupFilter filter14 = new GPUImageLookupFilter();

                filter14.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img62));

                return filter14;

            case LFGPUFilterTypeGR5:

                return new FWValenciaFilter(BitmapFactory.decodeResource(context.getResources(), R.drawable.valenciamap), BitmapFactory.decodeResource(context.getResources(), R.drawable.valenciagradientmap));

            case LFGPUFilterTypePU1:

                GPUImageLookupFilter filter15 = new GPUImageLookupFilter();

                filter15.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img35));

                return filter15;

            case LFGPUFilterTypePU2:

                GPUImageLookupFilter filter16 = new GPUImageLookupFilter();

                filter16.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img45));

                return filter16;

            case LFGPUFilterTypeBW1:

                GPUImageLookupFilter filter17 = new GPUImageLookupFilter();

                filter17.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.img11));

                return filter17;

            case LFGPUFilterTypeBW2:

//                return null;
                return new GPUImageErosionFilter(1);

            case LFGPUFilterTypeBW3:

                return new GPUImageGrayscaleFilter();

            case LFGPUFilterTypeSP1:

                return new GPUImageFalseColorFilter();

            case LFGPUFilterTypeSP2:

                return new GPUImageCrosshatchFilter();

            case LFGPUFilterTypeSP3:

                return new GPUImagePolkaDotFilter();

//            case LFGPUFilterTypeSP4:

//                return new GPUImageMotionDetector;

            case LFGPUFilterTypeSP5:

                return new GPUImageSobelEdgeDetectionFilter();

            case LFGPUFilterTypeSP6:

                return new GPUImageZoomBlurFilter();

            case LFGPUFilterTypeSP7:

                return new GPUImageLowPassFilter();

            default:
                break;

        }

        return null;

    }

    public static GPUImageFilter maskFilterWithName(String name) {
        String path = filterResFoler + "/mask_" + name + ".png";
        return maskFilterWithPath(path);
    }

    public static GPUImageFilter maskFilterWithPath(String path) {
        Bitmap bmp = BitmapFactory.decodeFile(path);
        if (bmp != null) {
            GPUImageMaskFilter filter = new GPUImageMaskFilter();
            filter.setBitmap(bmp);
            return filter;
        }
        return null;
    }

}
