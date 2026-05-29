package net.milkyway.client.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.ColorHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;

public class RenderUtil {

    public static void drawRoundRect(DrawContext ctx, float x, float y, float w, float h, float radius, int color) {
        drawRoundRect(ctx, x, y, w, h, radius, color, color);
    }

    public static void drawRoundRect(DrawContext ctx, float x, float y, float w, float h, float radius, int colorStart, int colorEnd) {
        // Simple rounded rectangle using quads
        float r = Math.min(radius, Math.min(w, h) / 2);
        
        // Main body
        fill(ctx, (int)(x + r), (int)y, (int)(x + w - r), (int)(y + h), colorStart);
        fill(ctx, (int)x, (int)(y + r), (int)(x + w), (int)(y + h - r), colorStart);
    }

    public static void fill(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        if (x1 < x2) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            int j = y1;
            y1 = y2;
            y2 = j;
        }
        ctx.fill(x2, y2, x1, y1, color);
    }

    public static void drawText(DrawContext ctx, String text, float x, float y, int color, boolean shadow) {
        var textRenderer = ctx.getContext().getTextRenderer();
        if (shadow) {
            ctx.drawTextWithShadow(textRenderer, text, (int)x, (int)y, color);
        } else {
            ctx.drawText(textRenderer, text, (int)x, (int)y, color, false);
        }
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static double lerpDouble(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static int lerpColor(int colorA, int colorB, float t) {
        int aA = (colorA >> 24) & 0xFF;
        int aR = (colorA >> 16) & 0xFF;
        int aG = (colorA >> 8) & 0xFF;
        int aB = colorA & 0xFF;

        int bA = (colorB >> 24) & 0xFF;
        int bR = (colorB >> 16) & 0xFF;
        int bG = (colorB >> 8) & 0xFF;
        int bB = colorB & 0xFF;

        int r = (int) lerp(aR, bR, t);
        int g = (int) lerp(aG, bG, t);
        int b = (int) lerp(aB, bB, t);
        int a = (int) lerp(aA, bA, t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int mulAlpha(int color, float alpha) {
        int a = (int) ((color >> 24 & 0xFF) * alpha);
        return (a << 24) | (color & 0x00FFFFFF);
    }

    public static int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    public static void drawLine(DrawContext ctx, float x1, float y1, float x2, float y2, float width, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return;

        dx /= len;
        dy /= len;

        float px = -dy * width / 2;
        float py = dx * width / 2;

        fill(ctx, (int)(x1 + px), (int)(y1 + py), (int)(x2 + px), (int)(y2 + py), color);
        fill(ctx, (int)(x1 - px), (int)(y1 - py), (int)(x2 - px), (int)(y2 - py), color);
    }
}
