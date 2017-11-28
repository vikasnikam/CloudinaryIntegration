package com.vikas.dreamworthtest.Utils;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * Created by Vikas on 11/24/2017.
 */

public class Utils {

    public static int getScreenWidth(Context context) {
        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        window.getDefaultDisplay().getSize(point);
        return point.x;
    }
}
