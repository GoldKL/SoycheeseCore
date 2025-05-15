package com.soy.soycheese.tracking;

import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.capability.foodlist.PlayerFoodList;
import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import com.soy.soycheese.capability.skilllist.PlayerSkillList;
import com.soy.soycheese.capability.skilllist.PlayerSkillListProvider;
import com.soy.soycheese.communication.PlayerFoodListMessage;
import com.soy.soycheese.communication.PlayerSkillListMessage;
import com.soy.soycheese.registries.SkillRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = SoycheeseCore.MODID)
public class ForgeEventListener {
    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(PlayerFoodListProvider.PLAYER_FOOD_LIST_CAPABILITY).ifPresent(old -> {
            event.getEntity().getCapability(PlayerFoodListProvider.PLAYER_FOOD_LIST_CAPABILITY).ifPresent( cap ->{
                cap.setFoodlist(old.getFoodlist());
            });
        });
        event.getOriginal().getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(old -> {
            event.getEntity().getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent( cap ->{
                cap.setSkilllist(old.getSkilllist());
            });
        });
    }
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncPlayerFoodList(event.getEntity());
        syncPlayerSkillList(event.getEntity());
    }
    @SubscribeEvent
    public static void onPlayerDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncPlayerFoodList(event.getEntity());
        syncPlayerSkillList(event.getEntity());
    }
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        syncPlayerFoodList(event.getEntity());
        syncPlayerSkillList(event.getEntity());
    }
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            event.player.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent( cap -> {
               cap.getSkilllist().stream()
                       .forEach( skillres ->{
                           if(skillres!= PlayerSkillList.noneSkill)
                           {
                               var skill = SkillRegistry.getSkill(skillres);
                               if(skill != null)
                               {
                                   skill.onTick(event.player);
                               }
                           }
                       });
            });
        }
    }
    public static void syncPlayerFoodList(Player player) {
        if (player.level().isClientSide) return;
        var target = (ServerPlayer) player;
        SoycheeseCore.channel.sendTo(
                new PlayerFoodListMessage(target.getCapability(PlayerFoodListProvider.PLAYER_FOOD_LIST_CAPABILITY).orElse(new PlayerFoodList())),
                target.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
        );
    }
    public static void syncPlayerSkillList(Player player) {
        if (player.level().isClientSide) return;
        var target = (ServerPlayer) player;
        SoycheeseCore.channel.sendTo(
                new PlayerSkillListMessage(target.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).orElse(new PlayerSkillList())),
                target.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
        );
    }
}
