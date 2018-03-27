package kz.nis.findthewordru;

import android.content.Context;
import android.graphics.Typeface;

public class FontFactory {
	private static String FONT1 = "ComfortaaBold.ttf";
	private static Typeface font1 = null;

	public static Typeface getFont1(Context context) {
		if (font1 == null) {
			font1 = Typeface.createFromAsset(context.getAssets(), FONT1);
		}
		return font1;
	}
}
