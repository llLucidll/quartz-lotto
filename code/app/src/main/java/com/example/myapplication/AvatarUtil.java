// File: AvatarUtil.java
package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Locale;

/*
Utility class for generating avatars.
 */

public class AvatarUtil {

    public static Bitmap generateAvatar(String letter, int size, Context context) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //color stuff
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getColorForLetter(letter));
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        //letter stuff
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(size / 2f);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        //vertical center
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float y = size / 2f - (fontMetrics.ascent + fontMetrics.descent) / 2f;

        canvas.drawText(letter, size / 2f, y, textPaint);

        return bitmap;
    }

    /**
     * Generates a color based on the input letter.
     *
     * @param letter The input letter.
     * @return An integer color.
     */
    private static int getColorForLetter(String letter) {
        // hash to get a color based on the letter
        int hash = letter.toUpperCase(Locale.US).charAt(0);
        int r = (hash * 123) % 256;
        int g = (hash * 456) % 256;
        int b = (hash * 789) % 256;
        return Color.rgb(r, g, b);
    }
}
