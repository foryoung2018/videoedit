package com.dongci.sun.gpuimglibrary.api.apiTest;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.dongci.sun.gpuimglibrary.R;
import com.dongci.sun.gpuimglibrary.gles.filter.filterold.GPUImageLookupFilter;

public class LookUpUtils {

    public static GPUImageLookupFilter createFilterLook(Context context, int index){

        GPUImageLookupFilter amatorka = new GPUImageLookupFilter();

        amatorka.setBitmap(BitmapFactory.decodeResource(context.getResources(), ds[index]));

        return amatorka;
    }

    public static int[] ds =
            {
                    R.drawable.img7,
                    R.drawable.img22,
                    R.drawable.img68,
                    R.drawable.img69,
                    R.drawable.img2,
                    R.drawable.img5,
                    R.drawable.img9,
                    R.drawable.img13,
                    R.drawable.img23,
                    R.drawable.img61,
                    R.drawable.img49,
                    R.drawable.img12,
                    R.drawable.img15,
                    R.drawable.img54,
                    R.drawable.img62,
                    R.drawable.img35,
                    R.drawable.img45,
                    R.drawable.img11
            };

}
