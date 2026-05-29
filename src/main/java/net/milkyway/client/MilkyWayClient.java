package net.milkyway.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.milkyway.client.module.ModuleManager;
import net.milkyway.client.gui.MilkyWayGui;
import net.milkyway.client.config.ClientConfig;
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
    private MilkyWayGui gui;
    private ClientConfig config;
    private static KeyBinding guiKey;

    @Override
    public void onInitializeClient() {
        try {
            INSTANCE = this;
            LOGGER.info("========================================");
            LOGGER.info("    MilkyWay Client v{} Initializing", VERSION);
            LOGGER.info("========================================");

            this.config = new ClientConfig();
            LOGGER.info("[MilkyWay] Config initialized");

            this.moduleManager = new ModuleManager();
            LOGGER.info("[MilkyWay] ModuleManager initialized with {} modules", 
                moduleManager.getModules().size());

            this.gui = null; // Lazy initialization - will create on first open

            guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.milkyway.opengui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.milkyway"
            ));
            LOGGER.info("[MilkyWay] Keybinding registered (RShift)");

            // Register client tick event
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                try {
                    // Check if GUI key was pressed
                    if (guiKey != null) {
                        while (guiKey.wasPressed()) {
                            if (client.currentScreen == null && client.world != null) {
                                if (gui == null) {
                                    gui = new MilkyWayGui();
                                    LOGGER.info("[MilkyWay] GUI created on first open");
                                }
                                client.setScreen(gui);
                            }
                        }
                    }

                    // Tick all enabled modules
                    if (moduleManager != null && client.world != null) {
                        moduleManager.onTick(client);
                    }
                } catch (Exception e) {
                    LOGGER.error("[MilkyWay] Error in client tick", e);
                }
            });

            LOGGER.info("========================================");
            LOGGER.info("[MilkyWay] Initialization complete!");
            LOGGER.info("========================================");
        } catch (Exception e) {
            LOGGER.error("[MilkyWay] FATAL ERROR during initialization", e);
            e.printStackTrace();
        }
    }

    public ModuleManager getModuleManager() { return moduleManager; }
    public MilkyWayGui   getGui()           { return gui; }
    public ClientConfig  getConfig()        { return config; }
    public static MilkyWayClient getInstance() { return INSTANCE; }
}
