package net.milkyway.client.modules.combat;

import net.milkyway.client.module.Module;

public class ComboCounterModule extends Module {
    
    public ComboCounterModule() {
        super("Combo Counter", "Counts consecutive hits", Category.COMBAT);
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
