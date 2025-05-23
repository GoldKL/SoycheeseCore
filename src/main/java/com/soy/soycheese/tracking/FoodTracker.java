package com.soy.soycheese.tracking;


import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import com.soy.soycheese.inventory.CookbookMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.network.NetworkHooks;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = SoycheeseCore.MODID)
public class FoodTracker {
    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        //ItemTooltipEvent 用来修改鼠标放上去的显示，只用于客户端
        if (!(event.getEntity() instanceof Player player)) return;
        if(!player.isAlive())return;
        if(player.level().isClientSide)
        {
            /*player.getCapability(PlayerFoodListProvider.PLAYER_FOOD_LIST_CAPABILITY).ifPresent(list -> {
                list.getFoodlist().stream()
                        .forEach(x -> {
                            SoycheeseCore.LOGGER.info(x.toString());
                        });
            });*/
            //SoycheeseCore.LOGGER.info(SkillRegistry.SKILL_REGISTRY_KEY.toString());
            return;
        }
        ItemStack usedItem = event.getItem();
        if (!usedItem.isEdible()) return;
        player.getCapability(PlayerFoodListProvider.PLAYER_FOOD_LIST_CAPABILITY).ifPresent(list -> {
            list.addFood(usedItem.getItem());
        });
        ForgeEventListener.syncPlayerFoodList(player);

        if (player instanceof ServerPlayer _ent) {
            BlockPos _bpos = BlockPos.containing(_ent.getX(), _ent.getY(), _ent.getZ());
            NetworkHooks.openScreen((ServerPlayer) _ent, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("cookbook");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                    return new CookbookMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
                }
            }, _bpos);
        }

    }
}