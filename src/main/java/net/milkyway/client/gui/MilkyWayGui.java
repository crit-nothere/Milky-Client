package net.milkyway.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.milkyway.client.MilkyWayClient;
import net.milkyway.client.module.Module;
import net.milkyway.client.util.RenderUtil;

import java.util.*;

public class MilkyWayGui extends Screen {

    private static final int DARK_BG = 0x0F0F1A;  // #0F0F1A - Dark space
    private static final int PURPLE_ACCENT = 0x8B00FF;  // Purple accent
    private static final int PURPLE_LIGHT = 0xB933FF;   // Light purple
    private static final int TEXT_COLOR = 0xFFFFFF;     // White text

    private final List<Float> starX = new ArrayList<>();
    private final List<Float> starY = new ArrayList<>();
    private final List<Float> starBrightness = new ArrayList<>();
    
    private float logoRotation = 0;
    private float logoScale = 0;
    private float panelAlpha = 0;
    private boolean animatingOpen = true;
    private long openStartTime = 0;

    private int selectedCategory = 0;  // 0=Render, 1=Combat, 2=Movement, 3=World, 4=HUD, 5=Optimization
    private final Module.Category[] categories = {
        Module.Category.RENDER,
        Module.Category.COMBAT,
        Module.Category.MOVEMENT,
        Module.Category.WORLD,
        Module.Category.HUD,
        Module.Category.OPTIMIZATION
    };

    private float scrollOffset = 0;
    private int panelX = 50;
    private int panelY = 50;
    private final int panelWidth = 350;
    private final int panelHeight = 400;

    public MilkyWayGui() {
        super(Text.literal("MilkyWay Client"));
        
        // Generate stars for night sky
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            starX.add(rand.nextFloat() * 10000);  // Large range for parallax effect
            starY.add(rand.nextFloat() * 10000);
            starBrightness.add(rand.nextFloat() * 0.7f + 0.3f);
        }
        
        openStartTime = System.currentTimeMillis();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Update animations
        updateAnimations(delta);

        // Draw night sky background
        drawNightSky(context);

        // Draw logo animation
        if (animatingOpen) {
            drawLogoAnimation(context);
        }

        // Draw panel
        drawPanel(context, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    private void updateAnimations(float delta) {
        if (animatingOpen) {
            long elapsed = System.currentTimeMillis() - openStartTime;
            float progress = Math.min(1.0f, elapsed / 500.0f);  // 500ms animation

            logoRotation = progress * 360;
            logoScale = progress;
            panelAlpha = progress;

            if (progress >= 1.0f) {
                animatingOpen = false;
            }
        } else {
            logoScale = 1.0f;
            panelAlpha = 1.0f;
        }
    }

    private void drawNightSky(DrawContext context) {
        // Background
        context.fill(0, 0, this.width, this.height, DARK_BG);

        // Draw stars with subtle animation
        long time = System.currentTimeMillis() / 50;
        for (int i = 0; i < starX.size(); i++) {
            float brightness = starBrightness.get(i);
            brightness += (float)Math.sin((time + i) * 0.01f) * 0.2f;
            brightness = Math.max(0.1f, Math.min(1.0f, brightness));

            float x = (starX.get(i) + time * 0.1f) % this.width;
            float y = (starY.get(i) + time * 0.05f) % this.height;

            int starColor = (int)(brightness * 255) << 24 | 0xFFFFFF;
            context.fill((int)x, (int)y, (int)x + 1, (int)y + 1, starColor);
        }
    }

    private void drawLogoAnimation(DrawContext context) {
        if (logoScale <= 0) return;

        int centerX = this.width / 2;
        int centerY = this.height / 2 - 50;

        // Draw rotating circles with motion blur effect
        int rotationSteps = 3;
        for (int i = rotationSteps - 1; i >= 0; i--) {
            float alpha = 1.0f - ((float) i / rotationSteps) * 0.5f;
            float rotation = logoRotation - (i * 20);
            float size = 50 * logoScale;

            int col = (int)(alpha * 255) << 24 | 0xB933FF;
            context.fill((int)(centerX - size), (int)(centerY - size),
                        (int)(centerX + size), (int)(centerY + size), col);
        }

        // Draw text below logo
        String text = "MilkyWay Client v1.0.0";
        int textWidth = this.textRenderer.getWidth(text);
        context.drawText(this.textRenderer, text,
            centerX - textWidth / 2, centerY + 80,
            (int)(TEXT_COLOR | ((int)(panelAlpha * 255) << 24)), false);
    }

    private void drawPanel(DrawContext context, int mouseX, int mouseY) {
        if (panelAlpha <= 0) return;

        // Semi-transparent background
        int bgColor = (int)(panelAlpha * 200) << 24 | DARK_BG;
        RenderUtil.drawRoundRect(context, panelX, panelY, panelWidth, panelHeight, 10, bgColor);

        // Border
        drawBorder(context, panelX, panelY, panelWidth, panelHeight, 2, PURPLE_LIGHT);

        // Title bar
        context.fill(panelX, panelY, panelX + panelWidth, panelY + 30,
            (int)(panelAlpha * 255) << 24 | PURPLE_ACCENT);
        RenderUtil.drawText(context, "MilkyWay", panelX + 10, panelY + 8, TEXT_COLOR, false);

        // Category tabs
        drawCategoryTabs(context, mouseX, mouseY);

        // Module list
        drawModuleList(context, mouseX, mouseY);
    }

    private void drawCategoryTabs(DrawContext context, int mouseX, int mouseY) {
        String[] catNames = {"Render", "Combat", "Move", "World", "HUD", "Opt"};
        int tabWidth = panelWidth / 6;
        int tabY = panelY + 30;

        for (int i = 0; i < catNames.length; i++) {
            int tabX = panelX + (i * tabWidth);
            int color = (i == selectedCategory) ? PURPLE_ACCENT : 0x4B0082;
            
            context.fill(tabX, tabY, tabX + tabWidth, tabY + 25, color);
            
            int textWidth = this.textRenderer.getWidth(catNames[i]);
            context.drawText(this.textRenderer, catNames[i],
                tabX + (tabWidth - textWidth) / 2, tabY + 7,
                TEXT_COLOR, false);

            // Handle tab click
            if (mouseX >= tabX && mouseX < tabX + tabWidth && mouseY >= tabY && mouseY < tabY + 25) {
                if (this.mouseClicked(mouseX, mouseY, 0)) {
                    selectedCategory = i;
                }
            }
        }
    }

    private void drawModuleList(DrawContext context, int mouseX, int mouseY) {
        int listY = panelY + 55;
        int listHeight = panelHeight - 55;
        
        // Enable scissor for scrolling
        context.enableScissor(panelX, listY, panelX + panelWidth, listY + listHeight);

        var modules = MilkyWayClient.getInstance().getModuleManager()
            .getModulesByCategory(categories[selectedCategory]);

        float yOffset = listY + scrollOffset;
        for (Module module : modules) {
            if (yOffset > listY - 20 && yOffset < listY + listHeight) {
                drawModuleItem(context, module, (int)yOffset, mouseX, mouseY);
            }
            yOffset += 25;
        }

        context.disableScissor();
    }

    private void drawModuleItem(DrawContext context, Module module, int y, int mouseX, int mouseY) {
        // Module background
        int bgColor = module.isEnabled() ? 0x3D0066 : 0x1A0033;
        context.fill(panelX + 5, y, panelX + panelWidth - 5, y + 20, bgColor);

        // Module name
        RenderUtil.drawText(context, module.getName(), panelX + 10, y + 4, TEXT_COLOR, false);

        // Toggle button (right side)
        String toggleText = module.isEnabled() ? "[ON]" : "[OFF]";
        int toggleColor = module.isEnabled() ? 0x00FF00 : 0xFF0000;
        RenderUtil.drawText(context, toggleText, panelX + panelWidth - 40, y + 4, toggleColor, false);

        // Click detection
        if (mouseX >= panelX + 5 && mouseX < panelX + panelWidth - 5 &&
            mouseY >= y && mouseY < y + 20) {
            if (this.mouseClicked(mouseX, mouseY, 1)) {  // Right click to toggle
                module.toggle();
            }
        }
    }

    private void drawBorder(DrawContext context, int x, int y, int w, int h, int thickness, int color) {
        // Top
        context.fill(x, y, x + w, y + thickness, color);
        // Bottom
        context.fill(x, y + h - thickness, x + w, y + h, color);
        // Left
        context.fill(x, y, x + thickness, y + h, color);
        // Right
        context.fill(x + w - thickness, y, x + w, y + h, color);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset += (float) verticalAmount * 10;
        return true;
    }

    @Override
    public void close() {
        this.client.setScreen(null);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
