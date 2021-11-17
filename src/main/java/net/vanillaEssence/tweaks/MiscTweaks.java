package net.vanillaEssence.tweaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigInteger;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import net.vanillaEssence.config.FeatureToggle;
import org.jetbrains.annotations.Nullable;

public class MiscTweaks
{
//    private static final KeybindState KEY_STATE_ATTACK = new KeybindState(MinecraftClient.getInstance().options.keyAttack, (mc) -> ((IMinecraftClientInvoker) mc).leftClickMouseAccessor());
//    private static final KeybindState KEY_STATE_USE = new KeybindState(MinecraftClient.getInstance().options.keyUse, (mc) -> ((IMinecraftClientInvoker) mc).rightClickMouseAccessor());

//    private static class KeybindState
//    {
//        private final KeyBinding keybind;
//        private final Consumer<MinecraftClient> clickFunc;
//        private boolean state;
//        private int durationCounter;
//        private int intervalCounter;
//
//        public KeybindState(KeyBinding keybind, Consumer<MinecraftClient> clickFunc)
//        {
//            this.keybind = keybind;
//            this.clickFunc = clickFunc;
//        }
//
//        public void reset()
//        {
//            this.state = false;
//            this.intervalCounter = 0;
//            this.durationCounter = 0;
//        }
//
//        public void handlePeriodicHold(int interval, int holdDuration, MinecraftClient mc)
//        {
//            if (this.state)
//            {
//                if (++this.durationCounter >= holdDuration)
//                {
//                    this.setKeyState(false, mc);
//                    this.durationCounter = 0;
//                }
//            }
//            else if (++this.intervalCounter >= interval)
//            {
//                this.setKeyState(true, mc);
//                this.intervalCounter = 0;
//                this.durationCounter = 0;
//            }
//        }
//
//        public void handlePeriodicClick(int interval, MinecraftClient mc)
//        {
//            if (++this.intervalCounter >= interval)
//            {
//                this.clickFunc.accept(mc);
//                this.intervalCounter = 0;
//                this.durationCounter = 0;
//            }
//        }
//
//        private void setKeyState(boolean state, MinecraftClient mc)
//        {
//            this.state = state;
//
//            InputUtil.Key key = InputUtil.fromTranslationKey(this.keybind.getBoundKeyTranslationKey());
//            KeyBinding.setKeyPressed(key, state);
//
//            if (state)
//            {
//                this.clickFunc.accept(mc);
//                KeyBinding.onKeyPressed(key);
//            }
//        }
//    }

    public static void onTick(MinecraftClient mc)
    {
        ClientPlayerEntity player = mc.player;

        if (player == null)
        {
            return;
        }

//        doPeriodicClicks(mc);

//        if (FeatureToggle.TWEAK_REPAIR_MODE.getBooleanValue())
//        {
//            InventoryUtils.repairModeSwapItems(player);
//        }

        if (player.input != null)
        {
            CameraEntity.movementTick(player.input.sneaking, player.input.jumping);
        }
    }

    public static void onGameLoop(MinecraftClient mc)
    {
        PlacementTweaks.onTick(mc);

        // Reset the counters after rendering each frame
        Tweakeroo.renderCountItems = 0;
        Tweakeroo.renderCountXPOrbs = 0;
    }

//    private static void doPeriodicClicks(MinecraftClient mc)
//    {
//        if (GuiUtils.getCurrentScreen() == null)
//        {
//            handlePeriodicClicks(
//                    KEY_STATE_ATTACK,
//                    FeatureToggle.TWEAK_PERIODIC_HOLD_ATTACK,
//                    FeatureToggle.TWEAK_PERIODIC_ATTACK,
//                    Configs.Generic.PERIODIC_HOLD_ATTACK_INTERVAL,
//                    Configs.Generic.PERIODIC_HOLD_ATTACK_DURATION,
//                    Configs.Generic.PERIODIC_ATTACK_INTERVAL, mc);
//
//            handlePeriodicClicks(
//                    KEY_STATE_USE,
//                    FeatureToggle.TWEAK_PERIODIC_HOLD_USE,
//                    FeatureToggle.TWEAK_PERIODIC_USE,
//                    Configs.Generic.PERIODIC_HOLD_USE_INTERVAL,
//                    Configs.Generic.PERIODIC_HOLD_USE_DURATION,
//                    Configs.Generic.PERIODIC_USE_INTERVAL, mc);
//        }
//        else
//        {
//            KEY_STATE_ATTACK.reset();
//            KEY_STATE_USE.reset();
//        }
//    }
//
//    private static void handlePeriodicClicks(
//            KeybindState keyState,
//            IConfigBoolean cfgPeriodicHold,
//            IConfigBoolean cfgPeriodicClick,
//            IConfigInteger cfgHoldClickInterval,
//            IConfigInteger cfgHoldDuration,
//            IConfigInteger cfgClickInterval,
//            MinecraftClient mc)
//    {
//        if (cfgPeriodicHold.getBooleanValue())
//        {
//            int interval = cfgHoldClickInterval.getIntegerValue();
//            int holdDuration = cfgHoldDuration.getIntegerValue();
//            keyState.handlePeriodicHold(interval, holdDuration, mc);
//        }
//        else if (cfgPeriodicClick.getBooleanValue())
//        {
//            int interval = cfgClickInterval.getIntegerValue();
//            keyState.handlePeriodicClick(interval, mc);
//        }
//        else
//        {
//            keyState.reset();
//        }
//    }

    @Nullable
    public static FlatChunkGeneratorLayer[] parseBlockString(String blockString)
    {
        List<FlatChunkGeneratorLayer> list = new ArrayList<>();
        String[] strings = blockString.split(",");
        final int count = strings.length;
        int thicknessSum = 0;

        for (String str : strings) {
            FlatChunkGeneratorLayer layer = parseLayerString(str, thicknessSum);

            if (layer == null) {
                list = Collections.emptyList();
                break;
            }

            list.add(layer);
            thicknessSum += layer.getThickness();
        }

        return list.toArray(new FlatChunkGeneratorLayer[list.size()]);
    }

    @Nullable
    private static FlatChunkGeneratorLayer parseLayerString(String string, int startY)
    {
        String[] strings = string.split("\\*", 2);
        int thickness;

        if (strings.length == 2)
        {
            try
            {
                thickness = Math.max(Integer.parseInt(strings[0]), 0);
            }
            catch (NumberFormatException e)
            {
//                Tweakeroo.logger.error("Error while parsing flat world string => {}", e.getMessage());
                return null;
            }
        }
        else
        {
            thickness = 1;
        }

        int endY = Math.min(startY + thickness, 256);
        int finalThickness = endY - startY;
        Block block;

        try
        {
            block = getBlockFromName(strings[strings.length - 1]);
        }
        catch (Exception e)
        {
//            Tweakeroo.logger.error("Error while parsing flat world string => {}", e.getMessage());
            return null;
        }

        if (block == null)
        {
//            Tweakeroo.logger.error("Error while parsing flat world string => Unknown block, {}", strings[strings.length - 1]);
            return null;
        }
        else
        {
            // FIXME 1.17 is this just not needed anymore?
            //layer.setStartY(startY);
            return new FlatChunkGeneratorLayer(finalThickness, block);
        }
    }

    @Nullable
    private static Block getBlockFromName(String name)
    {
        try
        {
            Identifier identifier = new Identifier(name);
            return Registry.BLOCK.getOrEmpty(identifier).orElse(null);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
