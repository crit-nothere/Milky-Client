package net.milkyway.client.config;

public class ClientConfig {
    // Animation settings
    public boolean enableAnimations = true;
    public boolean enableNightSky = true;
    public float animationSpeed = 1.0f;
    public boolean showLogoAnimation = true;
    public boolean showLogoHUD = true;
    public boolean showLogoMainMenu = true;

    public ClientConfig() {
        // Default config loaded
    }

    public void toggle(String option) {
        switch (option) {
            case "animations" -> enableAnimations = !enableAnimations;
            case "nightsky" -> enableNightSky = !enableNightSky;
            case "logoanimation" -> showLogoAnimation = !showLogoAnimation;
            case "logohud" -> showLogoHUD = !showLogoHUD;
            case "logomenu" -> showLogoMainMenu = !showLogoMainMenu;
        }
    }
}
