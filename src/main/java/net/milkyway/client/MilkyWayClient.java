package net.milkyway.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.milkyway.client.config.ClientConfig;
import net.milkyway.client.gui.MilkyWayGui;
import net.milkyway.client.module.ModuleManager;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MilkyWayClient implements ClientModInitializer {

    public static final String MOD_ID   = "milkyway";
    public static final String MOD_NAME = "MilkyWay Client";
    public static final String VERSION  = "1.0.0";
    public static final Logger LOGGER   = LoggerFactory.getLogger(MOD_NAME);

    public static MilkyWayClient INSTANCE;

    private ModuleManager moduleManager;
    private MilkyWayGui   gui;
    private ClientConfig  config;

    private static KeyBinding guiKey;

    @Override
    public void onInitializeClient() {
        try {
            INSTANCE = this;
            LOGGER.info("[MilkyWay] ========================================");
            LOGGER.info("[MilkyWay] Initializing MilkyWay Client v{}", VERSION);
            LOGGER.info("[MilkyWay] ========================================");

            this.config        = new ClientConfig();
            this.moduleManager = new ModuleManager();
            this.gui           = null; // lazy — created on first open

            guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.milkyway.opengui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.milkyway"
            ));

            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                try {
                    if (guiKey != null) {
                        while (guiKey.wasPressed()) {
                            if (client.currentScreen == null) {
                                if (gui == null) {
                                    gui = new MilkyWayGui();
                                }
                                client.setScreen(gui);
                            }
                        }
                    }
                    if (moduleManager != null) {
                        moduleManager.onTick(client);
                    }
                } catch (Exception e) {
                    LOGGER.error("[MilkyWay] Error in tick", e);
                }
            });

            LOGGER.info("[MilkyWay] Loaded {} modules", moduleManager.getModules().size());
            LOGGER.info("[MilkyWay] Ready! Press RShift in-game to open GUI");

        } catch (Exception e) {
            LOGGER.error("[MilkyWay] Fatal error during initialization", e);
        }
    }

    public static MilkyWayClient getInstance() { return INSTANCE; }
    public ModuleManager getModuleManager()    { return moduleManager; }
    public ClientConfig  getConfig()           { return config; }
    public MilkyWayGui   getGui()              { return gui; }
}
