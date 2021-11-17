package net.vanillaEssence;

import net.minecraft.client.MinecraftClient;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.vanillaEssence.config.Callbacks;
import net.vanillaEssence.event.ClientTickHandler;
import net.vanillaEssence.event.RenderHandler;

public class InitHandler implements IInitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        IRenderer renderer = new RenderHandler();
        RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);

        TickHandler.getInstance().registerClientTickHandler(new ClientTickHandler());

//        Callbacks.init(MinecraftClient.getInstance());
    }
}
