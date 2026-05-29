package net.milkyway.client.modules.world;

import net.milkyway.client.module.Module;

public class XRayModule extends Module {
    
    public XRayModule() {
        super("X-Ray", "See minerals through blocks", Category.WORLD);
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
