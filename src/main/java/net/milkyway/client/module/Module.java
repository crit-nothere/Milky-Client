package net.milkyway.client.module;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    public enum Category {
        RENDER("Render"), 
        COMBAT("Combat"), 
        MOVEMENT("Movement"), 
        WORLD("World"),
        HUD("HUD"),
        OPTIMIZATION("Optimization");

        private final String display;
        Category(String d) { this.display = d; }
        public String getDisplayName() { return display; }
    }

    protected final String name;
    protected final String description;
    protected final Category category;
    protected boolean enabled = false;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient client) {}

    public void toggle() {
        if (enabled) {
            enabled = false;
            onDisable();
        } else {
            enabled = true;
            onEnable();
        }
    }

    public void setEnabled(boolean e) {
        if (this.enabled != e) {
            toggle();
        }
    }

    public boolean isEnabled() { return enabled; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
}
