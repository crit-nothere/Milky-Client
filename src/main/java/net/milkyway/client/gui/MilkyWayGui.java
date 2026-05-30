package net.milkyway.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.milkyway.client.MilkyWayClient;
import net.milkyway.client.config.ClientConfig;
import net.milkyway.client.module.Module;
import net.milkyway.client.util.RenderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MilkyWayGui extends Screen {

    // ── Palette (all ARGB) ────────────────────────────────────────────────────
    private static final int BG          = 0xFF0F0F1A;
    private static final int PURPLE      = 0xFF8B00FF;
    private static final int PURPLE_L    = 0xFFB933FF;
    private static final int PURPLE_DIM  = 0xFF4B0082;
    private static final int PANEL_BG    = 0xE0100010;
    private static final int MODULE_ON   = 0xCC3D0066;
    private static final int MODULE_OFF  = 0x991A0033;
    private static final int MODULE_HOV  = 0xCC5500AA;
    private static final int WHITE       = 0xFFFFFFFF;
    private static final int GREEN       = 0xFF00FF88;
    private static final int RED         = 0xFFFF4455;

    // ── Night sky ─────────────────────────────────────────────────────────────
    private static final int STAR_COUNT = 80;
    private final float[] starX   = new float[STAR_COUNT];
    private final float[] starY   = new float[STAR_COUNT];
    private final float[] starB   = new float[STAR_COUNT]; // base brightness 0-1
    private final float[] starSz  = new float[STAR_COUNT]; // 1 or 2

    // ── Open animation ────────────────────────────────────────────────────────
    private float openProgress  = 0f; // 0→1
    private long  openStartMs   = 0L;
    private static final long ANIM_MS = 600L;

    // ── Panel layout ─────────────────────────────────────────────────────────
    private int panelX = 0, panelY = 0;
    private static final int PANEL_W = 340;
    private static final int PANEL_H = 420;
    private static final int HEADER_H = 30;
    private static final int TAB_H    = 24;
    private static final int LIST_TOP  = HEADER_H + TAB_H;
    private static final int ITEM_H   = 22;

    // ── State ─────────────────────────────────────────────────────────────────
    private int    selectedTab  = 0;
    private float  scrollY      = 0f;
    private int    hoveredIndex = -1;

    // Dragging
    private boolean dragging   = false;
    private int     dragOffX   = 0;
    private int     dragOffY   = 0;

    private final Module.Category[] CATS = {
        Module.Category.RENDER,
        Module.Category.COMBAT,
        Module.Category.MOVEMENT,
        Module.Category.WORLD,
        Module.Category.HUD,
        Module.Category.OPTIMIZATION
    };
    private final String[] CAT_NAMES = {
        "Render", "Combat", "Move", "World", "HUD", "Opt"
    };

    public MilkyWayGui() {
        super(Text.literal("MilkyWay Client"));
        Random rng = new Random();
        for (int i = 0; i < STAR_COUNT; i++) {
            starX[i] = rng.nextFloat();   // stored as fraction of screen
            starY[i] = rng.nextFloat();
            starB[i] = 0.3f + rng.nextFloat() * 0.7f;
            starSz[i] = rng.nextFloat() < 0.2f ? 2 : 1;
        }
        openStartMs = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        // Centre the panel on init / resize
        panelX = (this.width  - PANEL_W) / 2;
        panelY = (this.height - PANEL_H) / 2;
        scrollY = 0f;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  render()
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // update open animation
        long elapsed = System.currentTimeMillis() - openStartMs;
        ClientConfig cfg = MilkyWayClient.getInstance().getConfig();
        if (cfg.enableAnimations) {
            openProgress = Math.min(1f, (float) elapsed / ANIM_MS);
        } else {
            openProgress = 1f;
        }

        // 1. night sky background
        if (cfg.enableNightSky) {
            drawNightSky(ctx);
        } else {
            RenderUtil.fill(ctx, 0, 0, this.width, this.height, BG);
        }

        // 2. logo intro animation (only plays once during opening)
        if (cfg.showLogoAnimation && openProgress < 1f) {
            drawLogoAnimation(ctx, openProgress);
        }

        // 3. main panel (fades in)
        int panelAlpha = (int)(openProgress * 255);
        if (panelAlpha > 0) {
            drawPanel(ctx, mouseX, mouseY, panelAlpha);
        }

        // call super AFTER our custom rendering so vanilla elements overlay properly
        super.render(ctx, mouseX, mouseY, delta);
    }

    // ── Night sky ─────────────────────────────────────────────────────────────
    private void drawNightSky(DrawContext ctx) {
        RenderUtil.fill(ctx, 0, 0, this.width, this.height, BG);

        long t = System.currentTimeMillis();
        for (int i = 0; i < STAR_COUNT; i++) {
            float twinkle = (float)(Math.sin((t * 0.002f) + i * 1.3f) * 0.3f + 0.7f);
            float b = starB[i] * twinkle;
            int alpha = Math.max(0, Math.min(255, (int)(b * 255)));
            int color = (alpha << 24) | 0xFFFFFF;

            int sx = (int)(starX[i] * this.width);
            int sy = (int)(starY[i] * this.height);
            int sz = (int) starSz[i];
            RenderUtil.fill(ctx, sx, sy, sx + sz, sy + sz, color);
        }

        // subtle purple nebula gradient across bottom
        int nebulaTop = RenderUtil.withAlpha(0x2D005F, 0);
        int nebulaBot = RenderUtil.withAlpha(0x2D005F, 60);
        RenderUtil.fillGradientH(ctx, 0, this.height / 2, this.width, this.height,
                                 nebulaTop, nebulaBot);
    }

    // ── Logo animation ────────────────────────────────────────────────────────
    private void drawLogoAnimation(DrawContext ctx, float progress) {
        int cx = this.width / 2;
        int cy = this.height / 2;

        // pulsing ring
        float scale = (float)(Math.sin(progress * Math.PI));
        int   size  = (int)(60 * scale);
        int   alpha = (int)((1f - progress) * 200);

        if (size > 0 && alpha > 0) {
            int ringColor = (alpha << 24) | 0xB933FF;
            RenderUtil.drawBorder(ctx, cx - size, cy - size, size * 2, size * 2, 2, ringColor);

            // inner fill with lower alpha
            int fillColor = ((alpha / 4) << 24) | 0x8B00FF;
            RenderUtil.fill(ctx, cx - size + 2, cy - size + 2,
                            cx + size - 2, cy + size - 2, fillColor);
        }

        // "MilkyWay Client" text fades in then out
        float textAlpha = (float) Math.sin(progress * Math.PI);
        int   tAlpha    = (int)(textAlpha * 255);
        if (tAlpha > 0) {
            String label = "MilkyWay Client";
            int tw = RenderUtil.textWidth(label);
            RenderUtil.drawText(ctx, label, cx - tw / 2, cy + 70,
                                RenderUtil.withAlpha(0xFFFFFF, tAlpha), false);
        }
    }

    // ── Main panel ────────────────────────────────────────────────────────────
    private void drawPanel(DrawContext ctx, int mx, int my, int alpha) {

        // panel shadow
        int shadow = RenderUtil.withAlpha(0x000000, alpha / 4);
        RenderUtil.fill(ctx, panelX + 4, panelY + 4,
                        panelX + PANEL_W + 4, panelY + PANEL_H + 4, shadow);

        // panel background
        int bg = RenderUtil.withAlpha(0x100010, (int)(alpha * 0.88f));
        RenderUtil.drawRoundRect(ctx, panelX, panelY, PANEL_W, PANEL_H, 8, bg);

        // border
        RenderUtil.drawBorder(ctx, panelX, panelY, PANEL_W, PANEL_H, 1,
                              RenderUtil.withAlpha(0xB933FF, alpha));

        // glass shine at top
        int shine = RenderUtil.withAlpha(0xFFFFFF, alpha / 10);
        RenderUtil.fill(ctx, panelX + 1, panelY + 1,
                        panelX + PANEL_W - 1, panelY + 4, shine);

        // header
        drawHeader(ctx, alpha);

        // category tabs
        drawTabs(ctx, mx, my, alpha);

        // module list (clipped)
        int listY1 = panelY + LIST_TOP;
        int listY2 = panelY + PANEL_H;
        ctx.enableScissor(panelX, listY1, panelX + PANEL_W, listY2);
        drawModuleList(ctx, mx, my, alpha, listY1, listY2 - listY1);
        ctx.disableScissor();

        // scroll indicator
        drawScrollBar(ctx, alpha, listY1, listY2 - listY1);
    }

    private void drawHeader(DrawContext ctx, int alpha) {
        // gradient header bar
        int hLeft  = RenderUtil.withAlpha(0x8B00FF, alpha);
        int hRight = RenderUtil.withAlpha(0x4B0082, alpha);
        RenderUtil.fillGradientH(ctx,
            panelX, panelY,
            panelX + PANEL_W, panelY + HEADER_H,
            hLeft, hRight);

        // title text
        RenderUtil.drawText(ctx, "* MilkyWay Client",
            panelX + 8, panelY + (HEADER_H - 9) / 2,
            RenderUtil.withAlpha(0xFFFFFF, alpha), true);

        // version
        String ver = "v1.0.0";
        int vw = RenderUtil.textWidth(ver);
        RenderUtil.drawText(ctx, ver,
            panelX + PANEL_W - vw - 8, panelY + (HEADER_H - 9) / 2,
            RenderUtil.withAlpha(0xCC88FF, alpha), false);
    }

    private void drawTabs(DrawContext ctx, int mx, int my, int alpha) {
        int tabW = PANEL_W / CAT_NAMES.length;
        int ty   = panelY + HEADER_H;

        for (int i = 0; i < CAT_NAMES.length; i++) {
            int tx = panelX + i * tabW;
            boolean active  = (i == selectedTab);
            boolean hovered = mx >= tx && mx < tx + tabW && my >= ty && my < ty + TAB_H;

            int tabColor;
            if (active) {
                tabColor = RenderUtil.withAlpha(0x8B00FF, alpha);
            } else if (hovered) {
                tabColor = RenderUtil.withAlpha(0x5500AA, alpha);
            } else {
                tabColor = RenderUtil.withAlpha(0x2A0055, alpha);
            }
            RenderUtil.fill(ctx, tx, ty, tx + tabW, ty + TAB_H, tabColor);

            // bottom accent for active tab
            if (active) {
                RenderUtil.fill(ctx, tx, ty + TAB_H - 2,
                                tx + tabW, ty + TAB_H,
                                RenderUtil.withAlpha(0xDD66FF, alpha));
            }

            // tab label
            int tw = RenderUtil.textWidth(CAT_NAMES[i]);
            int textColor = active
                ? RenderUtil.withAlpha(0xFFFFFF, alpha)
                : RenderUtil.withAlpha(0xAA88CC, alpha);
            RenderUtil.drawText(ctx, CAT_NAMES[i],
                tx + (tabW - tw) / 2,
                ty + (TAB_H - 9) / 2,
                textColor, false);

            // divider
            if (i > 0) {
                RenderUtil.fill(ctx, tx, ty + 4, tx + 1, ty + TAB_H - 4,
                                RenderUtil.withAlpha(0x8B00FF, alpha / 2));
            }
        }
    }

    private void drawModuleList(DrawContext ctx, int mx, int my,
                                int alpha, int listY, int listH) {
        List<Module> mods = MilkyWayClient.getInstance()
            .getModuleManager()
            .getModulesByCategory(CATS[selectedTab]);

        hoveredIndex = -1;
        int yOff = listY + 4 + (int) scrollY;

        for (int i = 0; i < mods.size(); i++) {
            Module m  = mods.get(i);
            int    iy = yOff + i * ITEM_H;

            // skip if fully outside clip
            if (iy + ITEM_H < listY || iy > listY + listH) continue;

            boolean hovered = mx >= panelX + 4 && mx < panelX + PANEL_W - 4
                           && my >= iy && my < iy + ITEM_H - 2;
            if (hovered) hoveredIndex = i;

            // background
            int bg;
            if (m.isEnabled()) {
                bg = hovered ? RenderUtil.withAlpha(0x5500AA, alpha)
                             : RenderUtil.withAlpha(0x3D0066, (int)(alpha * 0.85f));
            } else {
                bg = hovered ? RenderUtil.withAlpha(0x350040, alpha)
                             : RenderUtil.withAlpha(0x1A0033, (int)(alpha * 0.7f));
            }
            RenderUtil.drawRoundRect(ctx,
                panelX + 4, iy, PANEL_W - 8, ITEM_H - 2, 4, bg);

            // left accent bar if enabled
            if (m.isEnabled()) {
                RenderUtil.fill(ctx,
                    panelX + 4, iy,
                    panelX + 6, iy + ITEM_H - 2,
                    RenderUtil.withAlpha(0xDD66FF, alpha));
            }

            // module name
            int nameColor = m.isEnabled()
                ? RenderUtil.withAlpha(0xFFFFFF, alpha)
                : RenderUtil.withAlpha(0xAA88CC, alpha);
            RenderUtil.drawText(ctx, m.getName(),
                panelX + 10, iy + (ITEM_H - 2 - 9) / 2,
                nameColor, false);

            // on/off pill
            String pill      = m.isEnabled() ? "ON" : "OFF";
            int    pillColor = m.isEnabled()
                ? RenderUtil.withAlpha(0x00FF88, alpha)
                : RenderUtil.withAlpha(0xFF4455, alpha);
            int pillW  = RenderUtil.textWidth(pill);
            int pillX  = panelX + PANEL_W - pillW - 12;
            int pillCY = iy + (ITEM_H - 2 - 9) / 2;
            RenderUtil.drawText(ctx, pill, pillX, pillCY, pillColor, false);
        }
    }

    private void drawScrollBar(DrawContext ctx, int alpha, int listY, int listH) {
        List<Module> mods = MilkyWayClient.getInstance()
            .getModuleManager()
            .getModulesByCategory(CATS[selectedTab]);

        int totalH = mods.size() * ITEM_H;
        if (totalH <= listH) return;

        float ratio    = (float) listH / totalH;
        int   barH     = Math.max(20, (int)(listH * ratio));
        float scrollFraction = Math.abs(scrollY) / (totalH - listH);
        int   barY     = listY + (int)((listH - barH) * scrollFraction);

        int trackColor = RenderUtil.withAlpha(0x4B0082, alpha / 2);
        int barColor   = RenderUtil.withAlpha(0xB933FF, alpha);

        // track
        RenderUtil.fill(ctx,
            panelX + PANEL_W - 5, listY,
            panelX + PANEL_W - 3, listY + listH,
            trackColor);
        // bar
        RenderUtil.fill(ctx,
            panelX + PANEL_W - 5, barY,
            panelX + PANEL_W - 3, barY + barH,
            barColor);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Input
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int imx = (int) mx, imy = (int) my;

        // header drag start
        if (imy >= panelY && imy < panelY + HEADER_H
         && imx >= panelX && imx < panelX + PANEL_W) {
            dragging = true;
            dragOffX = imx - panelX;
            dragOffY = imy - panelY;
            return true;
        }

        // tab click
        int tabY = panelY + HEADER_H;
        if (imy >= tabY && imy < tabY + TAB_H
         && imx >= panelX && imx < panelX + PANEL_W) {
            int tabW = PANEL_W / CAT_NAMES.length;
            int tab  = (imx - panelX) / tabW;
            if (tab >= 0 && tab < CAT_NAMES.length) {
                selectedTab = tab;
                scrollY     = 0f;
            }
            return true;
        }

        // module list click — right-click to toggle
        int listY = panelY + LIST_TOP;
        int listH = panelY + PANEL_H - listY;
        if (imy >= listY && imy < listY + listH
         && imx >= panelX + 4 && imx < panelX + PANEL_W - 4) {
            int relY  = imy - listY - 4 - (int) scrollY;
            int index = relY / ITEM_H;
            List<Module> mods = MilkyWayClient.getInstance()
                .getModuleManager()
                .getModulesByCategory(CATS[selectedTab]);
            if (index >= 0 && index < mods.size()) {
                if (button == 1) { // right-click
                    mods.get(index).toggle();
                } else if (button == 0) { // left-click also toggles
                    mods.get(index).toggle();
                }
            }
            return true;
        }

        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        dragging = false;
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button,
                                double deltaX, double deltaY) {
        if (dragging) {
            panelX = (int) mx - dragOffX;
            panelY = (int) my - dragOffY;
            // clamp to screen
            panelX = Math.max(0, Math.min(this.width  - PANEL_W, panelX));
            panelY = Math.max(0, Math.min(this.height - PANEL_H, panelY));
            return true;
        }
        return super.mouseDragged(mx, my, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mx, double my,
                                 double horizontalAmount, double verticalAmount) {
        int listY1 = panelY + LIST_TOP;
        int listY2 = panelY + PANEL_H;
        int imx = (int) mx, imy = (int) my;
        if (imx >= panelX && imx < panelX + PANEL_W
         && imy >= listY1 && imy < listY2) {
            scrollY += (float) verticalAmount * 12f;
            clampScroll();
        }
        return true;
    }

    private void clampScroll() {
        List<Module> mods = MilkyWayClient.getInstance()
            .getModuleManager()
            .getModulesByCategory(CATS[selectedTab]);
        int listH  = PANEL_H - LIST_TOP;
        int totalH = mods.size() * ITEM_H;
        int maxScroll = -(Math.max(0, totalH - listH + 8));
        scrollY = Math.min(0f, Math.max(maxScroll, scrollY));
    }

    @Override
    public boolean shouldCloseOnEsc() { return true; }

    @Override
    public void close() {
        if (this.client != null) this.client.setScreen(null);
    }

    @Override
    public boolean shouldPause() { return false; }
}
