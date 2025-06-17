package com.soy.soycheese.tracking;

import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.capability.foodlist.PlayerFoodList;
import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import com.soy.soycheese.capability.skilllist.PlayerSkillList;
import com.soy.soycheese.capability.skilllist.PlayerSkillListProvider;
import com.soy.soycheese.communication.PlayerFoodListMessage;
import com.soy.soycheese.communication.PlayerSkillListMessage;
import com.soy.soycheese.registries.SkillRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
               cap.getSkilllist()
                       .forEach( skillres ->{
                           var skill = SkillRegistry.getSkill(skillres);
                           if(skill != null)
                           {
                               skill.onTick(event.player);
                           }
                       });
            });
        }
    }
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        //攻击时
        if(attacker instanceof Player player) {
            PlayerSkillList skilllist = attacker.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).orElse(null);
            if(skilllist != null) {
                for(ResourceLocation skillres : skilllist.getSkilllist()) {
                    var skill = SkillRegistry.getSkill(skillres);
                    if(skill != null)
                    {
                        if(skill.onAttack(player,entity,event.getSource(),event.getAmount()))
                        {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
        //被攻击时
        if(entity instanceof Player player) {
            PlayerSkillList skilllist = entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).orElse(null);
            if(skilllist != null) {
                for(ResourceLocation skillres : skilllist.getSkilllist()) {
                    var skill = SkillRegistry.getSkill(skillres);
                    if(skill != null)
                    {
                        if(skill.onAttacked(player,event.getSource(),event.getAmount()))
                        {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        //造成伤害时1
        if(attacker instanceof Player player) {
            PlayerSkillList skilllist = attacker.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).orElse(null);
            if(skilllist != null) {
                float amount = event.getAmount();
                float baseamount = amount;
                for(ResourceLocation skillres : skilllist.getSkilllist()) {
                    var skill = SkillRegistry.getSkill(skillres);
                    if(skill != null) {
                        amount = skill.onHurt(player, entity, event.getSource(), baseamount, amount);
                        if (amount <= 0.0)
                        {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
                event.setAmount(amount);
            }
        }
        //受到伤害时1
        if(entity instanceof Player player) {
            PlayerSkillList skilllist = entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).orElse(null);
            if(skilllist != null) {
                float amount = event.getAmount();
                float baseamount = amount;
                for(ResourceLocation skillres : skilllist.getSkilllist()) {
                    var skill = SkillRegistry.getSkill(skillres);
                    if(skill != null)
                    {
                        amount = skill.onHurted(player, event.getSource(), baseamount, amount);
                        if (amount <= 0.0)
                        {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
                event.setAmount(amount);
            }
        }
    }
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        //造成伤害时2
        if(attacker instanceof Player player) {
            PlayerSkillList skilllist = attacker.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).orElse(null);
            if(skilllist != null) {
                float amount = event.getAmount();
                float baseamount = amount;
                for(ResourceLocation skillres : skilllist.getSkilllist()) {
                    var skill = SkillRegistry.getSkill(skillres);
                    if(skill != null) {
                        amount = skill.onDamage(player, entity, event.getSource(), baseamount, amount);
                        if (amount <= 0.0)
                        {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
                event.setAmount(amount);
            }
        }
        //受到伤害时2
        if(entity instanceof Player player) {
            PlayerSkillList skilllist = entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).orElse(null);
            if(skilllist != null) {
                float amount = event.getAmount();
                float baseamount = amount;
                for(ResourceLocation skillres : skilllist.getSkilllist()) {
                    var skill = SkillRegistry.getSkill(skillres);
                    if(skill != null)
                    {
                        amount = skill.onDamaged(player, event.getSource(), baseamount, amount);
                        if (amount <= 0.0)
                        {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
                event.setAmount(amount);
            }
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
        //attribute会在重生后重置，所以这里主要处理重生后的技能重检
        var target = (ServerPlayer) player;
        PlayerSkillList list = target.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).orElse(null);
        if(list != null)
        {
            for(int i = 0; i < 4; ++i)
            {
                var now_skill = SkillRegistry.getSkill(list.getSkilllist().get(i));
                if(now_skill != null)
                {
                    now_skill.onEquip(player);
                    for(int j = 0 ; j < i ;++j)
                    {
                        var last_skill = SkillRegistry.getSkill(list.getSkilllist().get(j));
                        if(last_skill != null)
                            last_skill.onChangeOtherEquip(player,now_skill,null);
                    }
                }
            }
        }
    }
}
