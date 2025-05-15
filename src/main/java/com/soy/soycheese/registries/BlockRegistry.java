package com.soy.soycheese.registries;

import com.soy.soycheese.SoycheeseCore;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SoycheeseCore.MODID);
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}