package net.milkyway.client.modules.render;

import net.milkyway.client.module.Module;

public class NoHurtcamModule extends Module {
    
    public NoHurtcamModule() {
        super("No Hurtcam", "Removes camera shake", Category.RENDER);
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
