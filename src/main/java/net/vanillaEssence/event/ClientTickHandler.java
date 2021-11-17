package net.vanillaEssence.event;

import net.minecraft.client.MinecraftClient;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.vanillaEssence.tweaks.MiscTweaks;

public class ClientTickHandler implements IClientTickHandler
{
    @Override
    public void onClientTick(MinecraftClient mc)
    {
        if (mc.world != null && mc.player != null)
        {
            MiscTweaks.onTick(mc);
        }
    }
}

