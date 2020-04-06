package com.wp.app.resource.utils;

import android.graphics.Color;

/**
 * Created by wp on 2019/3/30.
 */
public class ColorUtil {
	
	public static int changeAlpha(int color, float fraction) {
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		int alpha = (int) (Color.alpha(color) * fraction);
		return Color.argb(alpha, red, green, blue);
	}
}
