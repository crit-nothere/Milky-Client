package net.milkyway.client.modules.optimization;

import net.milkyway.client.module.Module;

public class NoDisconnectModule extends Module {
    
    public NoDisconnectModule() {
        super("No Disconnect", "Prevent timeout", Category.OPTIMIZATION);
    }

    @Override
    public void onEnable() {
        // Module enabled
    }

    @Override
    public void onDisable() {
        // Module disabled
    }
}
