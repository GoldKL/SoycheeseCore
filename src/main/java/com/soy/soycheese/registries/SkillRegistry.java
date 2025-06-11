package com.soy.soycheese.registries;

import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.capability.skilllist.PlayerSkillList;
import com.soy.soycheese.skill.BaseSkill;
import com.soy.soycheese.skill.EnchantedGoldenAppleSkill;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.function.Supplier;

public class SkillRegistry {

    public static final ResourceKey<Registry<BaseSkill>> SKILL_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(SoycheeseCore.MODID, "skills"));
    private static final DeferredRegister<BaseSkill> SKILLS = DeferredRegister.create(SKILL_REGISTRY_KEY, SoycheeseCore.MODID);
    public static final Supplier<IForgeRegistry<BaseSkill>> REGISTRY = SKILLS.makeRegistry(() -> new RegistryBuilder<BaseSkill>().disableSaving().disableOverrides());

    public static final RegistryObject<EnchantedGoldenAppleSkill> ENCHANTEDGOLDENAPPLE = SKILLS.register("enchanted_golden_apple", EnchantedGoldenAppleSkill::new);
    public static void register(IEventBus eventBus) {
        SKILLS.register(eventBus);
    }
    public static BaseSkill getSkill(ResourceLocation resourceLocation) {
        if(resourceLocation == null||resourceLocation.equals(PlayerSkillList.noneSkill)) return null;
        return REGISTRY.get().getValue(resourceLocation);
    }
    public static Collection<RegistryObject<BaseSkill>> getSkills() {
        return SKILLS.getEntries();
    }
}
