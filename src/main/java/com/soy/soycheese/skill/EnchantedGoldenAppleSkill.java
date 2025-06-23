package com.soy.soycheese.skill;

import com.soy.soycheese.capability.foodlist.PlayerFoodList;
import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class EnchantedGoldenAppleSkill extends BaseSkill {
    public EnchantedGoldenAppleSkill() {
        super(0);
        this.initSkillArgument("player_tick_interval",50);
        this.initSkillArgument("player_max_absorption_amount",20.0f);
        this.initSkillArgument("player_add_absorption_amount",1.0f);
    }
    @Override
    public boolean getIslock(Player player) {
        PlayerFoodList playerFoodList = player.getCapability(PlayerFoodListProvider.PLAYER_FOOD_LIST_CAPABILITY).orElse(null);
        if (playerFoodList != null) {
            return !playerFoodList.containsFood(Items.ENCHANTED_GOLDEN_APPLE);
        }
        return false;
    }
    @Override
    public void onTick(Player player) {
        if(player.level().isClientSide)return;
        if(player.tickCount % (int)(this.getSkillArgument("player_tick_interval")) == 0)
        {
            if(player.getAbsorptionAmount() < (float)this.getSkillArgument("player_max_absorption_amount"))
            {
                player.setAbsorptionAmount(Math.min((float)this.getSkillArgument("player_max_absorption_amount")
                        ,player.getAbsorptionAmount() + (float)this.getSkillArgument("player_add_absorption_amount")));
            }
        }
    }
}
