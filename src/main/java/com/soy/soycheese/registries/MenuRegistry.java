package com.soy.soycheese.registries;

import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.inventory.CookbookMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuRegistry {
    private static final DeferredRegister<MenuType<?>> MENU = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SoycheeseCore.MODID);
    public static final RegistryObject<MenuType<CookbookMenu>> COOKBOOK = MENU.register("cookbook", () -> IForgeMenuType.create(CookbookMenu::new));
    public static void register(IEventBus eventBus) {
        MENU.register(eventBus);
    }
}
