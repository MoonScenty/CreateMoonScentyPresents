package me.moonscenty.createmoonscentypresents.compat.jade;

import me.moonscenty.createmoonscentypresents.content.firing.PitKilnBlock;
import me.moonscenty.createmoonscentypresents.content.tapping.TapperBlock;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/** Only loaded when Jade is present; Jade finds it by the annotation. */
@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    /** Progress the client has no copy of has to be asked for from the server. */
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(PitKilnComponent.INSTANCE, PitKilnBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(TapperComponent.INSTANCE, TapperBlock.class);
        registration.registerBlockComponent(PitKilnComponent.INSTANCE, PitKilnBlock.class);
    }
}
