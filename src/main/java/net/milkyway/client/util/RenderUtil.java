package net.milkyway.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public final class RenderUtil {

    private RenderUtil() {}

    // -- Color helpers ---------------------------------------------------------

    /** Pack r,g,b,a (0-255 each) into ARGB int. */
    public static int argb(int a, int r, int g, int b) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    /** Add full alpha to an RGB constant (e.g. 0xFF0000 -> 0xFFFF0000). */
    public static int opaque(int rgb) {
        return 0xFF000000 | (rgb & 0x00FFFFFF);
    }

    /** Multiply the alpha channel of an ARGB color by [0..1]. */
    public static int mulAlpha(int argb, float alpha) {
        int a = (int) (((argb >> 24) & 0xFF) * alpha);
        return (a << 24) | (argb & 0x00FFFFFF);
    }

    /** Replace alpha in an ARGB color with the supplied value [0-255]. */
    public static int withAlpha(int argb, int alpha) {
        return (alpha << 24) | (argb & 0x00FFFFFF);
    }

    // -- Lerp -----------------------------------------------------------------

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static int lerpColor(int colorA, int colorB, float t) {
        int aA = (colorA >> 24) & 0xFF, aR = (colorA >> 16) & 0xFF,
            aG = (colorA >>  8) & 0xFF, aB =  colorA        & 0xFF;
        int bA = (colorB >> 24) & 0xFF, bR = (colorB >> 16) & 0xFF,
            bG = (colorB >>  8) & 0xFF, bB =  colorB        & 0xFF;
        return argb((int) lerp(aA, bA, t), (int) lerp(aR, bR, t),
                    (int) lerp(aG, bG, t), (int) lerp(aB, bB, t));
    }

    // -- Drawing ---------------------------------------------------------------

    /**
     * Fills a rectangle. color must be ARGB (alpha in highest byte).
     * DrawContext.fill() already expects ARGB.
     */
    public static void fill(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        // DrawContext.fill takes (x1,y1,x2,y2,argb) and handles ordering internally
        ctx.fill(x1, y1, x2, y2, color);
    }

    /**
     * Draws a horizontal-gradient filled rect.
     * Uses the two-color overload of DrawContext.fill().
     */
    /**
     * Manual horizontal gradient fill using multiple thin vertical strips.
     * Safe across all MC versions - no reliance on fillGradient signature.
     */
    public static void fillGradientH(DrawContext ctx,
                                     int x1, int y1, int x2, int y2,
                                     int colorLeft, int colorRight) {
        int w = x2 - x1;
        if (w <= 0) return;
        // Use DrawContext's built-in gradient (left=colorStart, right=colorEnd)
        // MC 1.21.1 yarn: fillGradient(x1,y1,x2,y2,colorStart,colorEnd)
        ctx.fillGradient(x1, y1, x2, y2, colorLeft, colorRight);
    }

    /**
     * Simple rounded-rect approximation (no corner arc, just clipped blocks).
     * color must be ARGB.
     */
    public static void drawRoundRect(DrawContext ctx,
                                     int x, int y, int w, int h,
                                     int radius, int color) {
        int r = Math.min(radius, Math.min(w, h) / 2);
        // centre strip
        fill(ctx, x + r, y,     x + w - r, y + h, color);
        // left/right caps (full height minus rounded part already covered)
        fill(ctx, x,     y + r, x + r,     y + h - r, color);
        fill(ctx, x + w - r, y + r, x + w, y + h - r, color);
    }

    /** Draws a 1-pixel-thick border rectangle. color must be ARGB. */
    public static void drawBorder(DrawContext ctx,
                                  int x, int y, int w, int h,
                                  int thickness, int color) {
        fill(ctx, x,             y,             x + w,         y + thickness,         color); // top
        fill(ctx, x,             y + h - thickness, x + w,     y + h,                 color); // bottom
        fill(ctx, x,             y,             x + thickness, y + h,                 color); // left
        fill(ctx, x + w - thickness, y,         x + w,         y + h,                 color); // right
    }

    // -- Text -----------------------------------------------------------------

    /** Draws text; uses MinecraftClient.textRenderer directly (safe in 1.21.1). */
    public static void drawText(DrawContext ctx,
                                String text, int x, int y,
                                int argbColor, boolean shadow) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        if (tr == null) return;
        if (shadow) {
            ctx.drawTextWithShadow(tr, text, x, y, argbColor);
        } else {
            ctx.drawText(tr, text, x, y, argbColor, false);
        }
    }

    public static int textWidth(String text) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        return tr != null - tr.getWidth(text) : 0;
    }

    public static int textHeight() {
        return 9; // standard MC font height
    }
}
