package com.soy.soycheese.tracking;

import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import com.soy.soycheese.capability.skilllist.PlayerSkillListProvider;
import com.soy.soycheese.client.screen.cook_book.CookbookScreen;
import com.soy.soycheese.network.communication.CookbookSwitchSkillMessage;
import com.soy.soycheese.network.communication.PlayerFoodListMessage;
import com.soy.soycheese.network.communication.PlayerSkillListMessage;
import com.soy.soycheese.registries.MenuRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,modid = SoycheeseCore.MODID)
public class ModEventListener {
    @SubscribeEvent
    public static void RegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerFoodListProvider.class);
        event.register(PlayerSkillListProvider.class);
    }

    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(MenuRegistry.COOKBOOK.get(), CookbookScreen::new);
        });
    }

}
