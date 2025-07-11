package com.soy.soycheese.registries;

import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.capability.foodlist.PlayerFoodList;
import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import com.soy.soycheese.capability.skilllist.PlayerSkillList;
import com.soy.soycheese.skill.BaseSkill;
import com.soy.soycheese.skill.EnchantedGoldenAppleSkill;
import com.soy.soycheese.skill.KubejsSkill;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

import java.util.Collection;
import java.util.function.Supplier;

public class SkillRegistry {

    public static final ResourceKey<Registry<BaseSkill>> SKILL_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(SoycheeseCore.MODID, "skills"));
    private static final DeferredRegister<BaseSkill> SKILLS = DeferredRegister.create(SKILL_REGISTRY_KEY, SoycheeseCore.MODID);
    public static final Supplier<IForgeRegistry<BaseSkill>> REGISTRY = SKILLS.makeRegistry(() -> new RegistryBuilder<BaseSkill>().disableSaving().disableOverrides());

    public static final RegistryObject<BaseSkill> ENCHANTEDGOLDENAPPLE = SKILLS.register("enchanted_golden_apple", EnchantedGoldenAppleSkill::new);
    public static final RegistryObject<BaseSkill> ENCHANTEDGOLDENAPPLE2 = SKILLS.register("enchanted_golden_apple2", ()->new BaseSkill.Builder(0).build( builder -> new BaseSkill(builder) {
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
            if (player.level().isClientSide) return;
            if (player.tickCount % 50 == 0) {
                if (player.getAbsorptionAmount() < 20) {
                    player.setAbsorptionAmount(Math.min(20, player.getAbsorptionAmount() + (float) (1)));
                }
            }
        }
    }));
    public static final RegistryObject<BaseSkill> ENCHANTEDGOLDENAPPLE3 = SKILLS.register("enchanted_golden_apple3",
            ()->KubejsSkill.builder(0).getIslock(((kubejsSkill, player) -> {
                        PlayerFoodList playerFoodList = player.getCapability(PlayerFoodListProvider.PLAYER_FOOD_LIST_CAPABILITY).orElse(null);
                        if (playerFoodList != null) {
                            return !playerFoodList.containsFood(Items.ENCHANTED_GOLDEN_APPLE);
                        }
                        return false;
                    })).onTick((kubejsSkill, player) -> {
                        if(player.level().isClientSide)return;
                        if(player.tickCount % 50 == 0)
                        {
                            if(player.getAbsorptionAmount() < 20)
                            {
                                player.setAbsorptionAmount(Math.min(20,player.getAbsorptionAmount() + (float)(1)));
                            }
                        }
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        SKILLS.register(eventBus);
    }
    public static BaseSkill getSkill(ResourceLocation resourceLocation) {
        if(resourceLocation == null||resourceLocation.equals(PlayerSkillList.noneSkill)) return null;
        return REGISTRY.get().getValue(resourceLocation);
    }
    public static Collection<BaseSkill> getSkills() {
        return REGISTRY.get().getValues();
    }
}
