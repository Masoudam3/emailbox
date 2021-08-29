package com.example.weather.utils;


import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import java.util.Random;

public class MdColorUtils {

    public static final String[] NAMES = {
            "red", "pink", "purple", "deep_purple", "indigo", "blue", "light_blue", "cyan",
            "teal", "green", "light_green", "lime", "yellow", "amber", "orange", "deep_orange",
            "brown" , "grey", "blue_grey"
    };

    public static final String[] CODES = {
            "50", "100", "200", "300", "400", "500", "600", "700",
            "800", "900", "A100", "A200", "A400", "A700"
    };
    public static final int MAX_NAME_INDEX = 15;



    public static int randomColor(Context context){
        Random random = new Random();
        int color = Color.DKGRAY;
        String name = NAMES[random.nextInt(MAX_NAME_INDEX + 1)];
        String code = CODES[random.nextInt(CODES.length)];
        String colorname = "md_" + name + "_" + code;
        int id = context.getResources().getIdentifier(colorname, "color", context.getPackageName());
        if(id > 0){
            color = ContextCompat.getColor(context, id);
        }
        return color;
    }




}
