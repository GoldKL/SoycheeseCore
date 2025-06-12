package com.soy.soycheese.tracking;


import com.mojang.logging.LogUtils;
import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.capability.foodlist.PlayerFoodList;
import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import com.soy.soycheese.communication.PlayerFoodListMessage;
import com.soy.soycheese.inventory.CookbookMenu;
import com.soy.soycheese.registries.ItemRegistry;
import com.soy.soycheese.registries.SkillRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import javax.annotation.Nullable;

import static com.soy.soycheese.tracking.ForgeEventListener.syncPlayerFoodList;

import net.minecraftforge.network.NetworkHooks;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = SoycheeseCore.MODID)
public class FoodTracker {
    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if(!player.isAlive())return;
        if(player.level().isClientSide) return;
        ItemStack usedItem = event.getItem();
        if (!usedItem.isEdible()) return;
        player.getCapability(PlayerFoodListProvider.PLAYER_FOOD_LIST_CAPABILITY).ifPresent(list -> {
            list.addFood(usedItem.getItem());
        });
        ForgeEventListener.syncPlayerFoodList(player);
    /*打开天赋系统
        if (player instanceof ServerPlayer _ent) {
            _ent.stopUsingItem();
            NetworkHooks.openScreen((ServerPlayer) _ent, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("cookbook");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                    return new CookbookMenu(id, inventory, null);
                    //new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos)
                }
            });
        }
    */
    }
    @SubscribeEvent
    public static void onPlayerAttacked(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if(!player.isAlive()) return;
        if(player.level().isClientSide) return;
        if(player.containerMenu instanceof CookbookMenu)
            player.closeContainer();
    }
    //ItemTooltipEvent 用来修改鼠标放上去的显示，只用于客户端
}