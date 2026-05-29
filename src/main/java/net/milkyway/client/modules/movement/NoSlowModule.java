package net.milkyway.client.modules.movement;

import net.milkyway.client.module.Module;

public class NoSlowModule extends Module {
    
    public NoSlowModule() {
        super("No Slow", "Full speed while using items", Category.MOVEMENT);
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
