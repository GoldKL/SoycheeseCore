package com.soy.soycheese.tracking;

import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.capability.foodlist.PlayerFoodList;
import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import com.soy.soycheese.capability.skilllist.PlayerSkillList;
import com.soy.soycheese.capability.skilllist.PlayerSkillListProvider;
import com.soy.soycheese.communication.PlayerFoodListMessage;
import com.soy.soycheese.communication.PlayerSkillListMessage;
import com.soy.soycheese.registries.SkillRegistry;
import com.soy.soycheese.skill.BaseSkill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = SoycheeseCore.MODID)
public class ForgeEventListener {
    /*
    个人总结：
    当玩家从主世界到下界/下界返回主世界，主世界到末地传送门，触发PlayerChangedDimensionEvent
    当玩家从末地返回主世界，依次触发clone和RespawnEvent
    当玩家登录游戏时，触发PlayerLoggedInEvent
    */
    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        var originalPlayer = event.getOriginal();
        originalPlayer.reviveCaps();
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
        originalPlayer.invalidateCaps();
    }
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncPlayerFoodList(event.getEntity());
        syncPlayerSkillList(event.getEntity());
        recheckPlayerSkillList(event.getEntity());
    }
    @SubscribeEvent
    public static void onPlayerDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncPlayerFoodList(event.getEntity());
        syncPlayerSkillList(event.getEntity());
        recheckPlayerSkillList(event.getEntity());
    }
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        syncPlayerFoodList(event.getEntity());
        syncPlayerSkillList(event.getEntity());
        recheckPlayerSkillList(event.getEntity());
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
    public static void recheckPlayerSkillList(Player player) {
        if (player.level().isClientSide) return;
        var target = (ServerPlayer) player;
        target.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(cap->{
            cap.getSkilllist().stream()
                    .forEach( skillres ->{
                        if(skillres!= PlayerSkillList.noneSkill)
                        {
                            var skill = SkillRegistry.getSkill(skillres);
                            if(skill != null)
                            {
                                skill.onEquip(target);
                            }
                        }
                    });
        });
    }
}
