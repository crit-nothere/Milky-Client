package net.milkyway.client.modules.render;

import net.milkyway.client.module.Module;

public class NoRenderModule extends Module {
    
    public NoRenderModule() {
        super("No Render", "Selective rendering disable", Category.RENDER);
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
