package app.fynnjason.lib;

import android.content.Context;

/**
 * Created by FynnJason.
 * Des：各种工具
 */

public class SundryUtils {
    /**
     * dp转px
     */
    public static int dp2px(float dpValue, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp转px
     */
    public static int sp2px(float spValue, Context context) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }
}
