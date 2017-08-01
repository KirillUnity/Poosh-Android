package me.appstockus.poosh_android.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

import me.appstockus.poosh_android.R;

/**
 * Orginal http://stackoverflow.com/questions/23122088/colored-boxed-with-letters-a-la-gmail
 * Used to create a {@link Bitmap} that contains a letter used in the English
 * alphabet or digit, if there is no letter or digit available, a default image
 * is shown instead.
 *
 * Modified by CITILINK-PC 31.10.16
 */
public class LetterBitmap {

    /**
     * The number of available tile colors
     */
    private static final int NUM_OF_TILE_COLORS = 5;

    /**
     * The {@link TextPaint} used to draw the letter onto the tile
     */
    private final TextPaint mPaint = new TextPaint();
    /**
     * The bounds that enclose the letter
     */
    private final Rect mBounds = new Rect();
    /**
     * The {@link Canvas} to draw on
     */
    private final Canvas mCanvas = new Canvas();
    /**
     * The first char of the name being displayed
     */
    private final char[] mFirstChar = new char[2];

    /**
     * The background colors of the tile
     */
    private final TypedArray mColors;
    /**
     * The font size used to display the letter
     */
//    private final int mTileLetterFontSize;
    /**
     * The default image to display
     */
    private final Bitmap mDefaultBitmap;

    /**
     * Constructor for <code>LetterTileProvider</code>
     *
     * @param context The {@link Context} to use
     */
    public LetterBitmap(Context context) {
        final Resources res = context.getResources();

        mPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);

        mColors = res.obtainTypedArray(R.array.letter_tile_colors);
//        mTileLetterFontSize = res.getDimensionPixelSize(R.dimen.tile_letter_font_size);

        mDefaultBitmap = BitmapFactory.decodeResource(res, android.R.drawable.sym_def_app_icon);
    }

    /**
     * @param displayName The name used to create the letter for the tile
     * @param key         The key used to generate the background color for the tile
     * @param size       The desired width and height of the tile
     * @return A {@link Bitmap} that contains a letter used in the English
     * alphabet or digit, if there is no letter or digit available, a
     * default image is shown instead
     */
    public Bitmap getLetterTile(String displayName, String key, int size) {
        final Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        mFirstChar[0] = Character.toUpperCase( displayName.charAt(0) );
        mFirstChar[1] = Character.toUpperCase( key.charAt(0) );

        mPaint.setColor(pickColor(key));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        final float r = size / 2f;

        final Canvas c = mCanvas;
        c.setBitmap(bitmap);
        c.drawCircle(r, r, r, mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(
                size * 0.50f
        );
//        mPaint.setFakeBoldText(true);
        mPaint.getTextBounds(mFirstChar, 0, 2, mBounds);

        c.drawText(
                mFirstChar,
                0, 2,
                r, r + (mBounds.bottom - mBounds.top) / 2,
                mPaint
        );
        return bitmap;
    }

    /**
     * @param c The char to check
     * @return True if <code>c</code> is in the English alphabet or is a digit,
     * false otherwise
     */
    private static boolean isEnglishLetterOrDigit(char c) {
        return 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || '0' <= c && c <= '9';
    }

    /**
     * @param key The key used to generate the tile color
     * @return A new or previously chosen color for <code>key</code> used as the
     * tile background color
     */
    private int pickColor(String key) {
        // String.hashCode() is not supposed to change across java versions, so
        // this should guarantee the same key always maps to the same color
        final int color = Math.abs(key.hashCode()) % NUM_OF_TILE_COLORS;
        try {
            return mColors.getColor(color, Color.BLACK);
        }
        finally {
//            mColors.recycle();
        }
    }
}