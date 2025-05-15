package com.soy.soycheese.skill;

import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnchantedGoldenAppleSkill extends BaseSkill {
    public EnchantedGoldenAppleSkill() {
        super(0);
    }
    public boolean getIslock(Player player) {
        AtomicBoolean locked = new AtomicBoolean(false);
        player.getCapability(PlayerFoodListProvider.PLAYER_FOOD_LIST_CAPABILITY).ifPresent(list -> {
            locked.set(!list.getFoodlist().contains(new ResourceLocation("minecraft:enchanted_golden_apple")));
        });
        return locked.get();
    }
    public void onTick(Player player) {
        int cooltime = player.getPersistentData().getInt("soycheese_EGapple_cooltime");
        cooltime += 1;
        if (cooltime >= 50)  {
            cooltime = 0;
            if(player.getAbsorptionAmount() < 20)
            {
                player.setAbsorptionAmount(Math.min(20,player.getAbsorptionAmount() + (float)(1)));
            }
        }
        player.getPersistentData().putInt("soycheese_EGapple_cooltime", cooltime);
    }
}
