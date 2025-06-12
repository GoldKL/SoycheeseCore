package com.soy.soycheese.inventory;

import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.registries.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

@Mod.EventBusSubscriber
public class CookbookMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>>{
    public final static HashMap<String, Object> guistate = new HashMap<>();
    public final Level world;
    public final Player entity;

    private final Map<Integer, Slot> customSlots = new HashMap<>();
    @Override
    public Map<Integer, Slot> get() {
        return customSlots;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        //此处写关闭菜单时的处理
    }
    public CookbookMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(MenuRegistry.COOKBOOK.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level();
        //此处处理打开菜单时的处理
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player entity = event.player;
        if (event.phase == TickEvent.Phase.END && entity.containerMenu instanceof CookbookMenu) {
            Level world = entity.level();
            //此处处理gui开启时玩家刻的处理
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
