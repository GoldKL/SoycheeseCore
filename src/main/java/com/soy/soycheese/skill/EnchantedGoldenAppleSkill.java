package com.soy.soycheese.skill;

import com.soy.soycheese.capability.foodlist.PlayerFoodList;
import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class EnchantedGoldenAppleSkill extends BaseSkill {
    public EnchantedGoldenAppleSkill() {
        super(0);
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
        if(player.tickCount % 50 == 0)
        {
            if(player.getAbsorptionAmount() < 20)
            {
                player.setAbsorptionAmount(Math.min(20,player.getAbsorptionAmount() + (float)(1)));
            }
        }
    }
}
