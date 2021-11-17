package net.vanillaEssence;

import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class vanillaEssenceClient implements ClientModInitializer {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }
}
