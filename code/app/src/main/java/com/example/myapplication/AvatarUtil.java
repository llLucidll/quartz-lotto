// AvatarUtil.java
package com.example.myapplication;

import android.graphics.*;
import androidx.core.content.ContextCompat;
import com.example.myapplication.R;

public class AvatarUtil {

    /**
     * generates a circular bitmap with a colored background and the first letter of the users name
     *
     * @param firstLetter The first letter of the user's name
     * @param size        The size of the avatar in pixels
     * @param context     The context to access resources
     * @return A bitmap representing the avatar
     */
    public static Bitmap generateAvatar(String firstLetter, int size, android.content.Context context) {
        // Determine background color based on the first letter
        int backgroundColor = getColorForLetter(firstLetter.charAt(0), context);

        // create a bitmap
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // draw the colored circle
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(backgroundColor);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);

        // draw the letter
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE); // text color
        textPaint.setTextSize(size / 2f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // calculating vertical center for the text
        Rect textBounds = new Rect();
        textPaint.getTextBounds(firstLetter, 0, firstLetter.length(), textBounds);
        float textHeight = textBounds.height();
        float x = size / 2f;
        float y = size / 2f + textHeight / 2f;

        canvas.drawText(firstLetter, x, y, textPaint);

        return bitmap;
    }

    /**
     * Determines a background color based on the provided letter
     *
     * @param letter  The letter to base the color on
     * @param context The context to access resources
     * @return An integer representing the color
     */
    private static int getColorForLetter(char letter, android.content.Context context) {
        //set of colors
        int[] colors = {
                ContextCompat.getColor(context, R.color.avatar_color_1),
                ContextCompat.getColor(context, R.color.avatar_color_2),
                ContextCompat.getColor(context, R.color.avatar_color_3),
                ContextCompat.getColor(context, R.color.avatar_color_4),
                ContextCompat.getColor(context, R.color.avatar_color_5),
                ContextCompat.getColor(context, R.color.avatar_color_6)
        };

        //simple hash to assign a color based on the letter
        int index = (Character.toLowerCase(letter) - 'a') % colors.length;
        if (index < 0) index = 0; //non-letter characters

        return colors[index];
    }
}
