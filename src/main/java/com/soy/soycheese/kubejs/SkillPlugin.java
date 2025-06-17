package com.soy.soycheese.kubejs;

import com.soy.soycheese.registries.SkillRegistry;
import com.soy.soycheese.skill.BaseSkill;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;

public class SkillPlugin extends KubeJSPlugin {
    public static final RegistryInfo<BaseSkill>SKILLS = RegistryInfo.of(SkillRegistry.SKILL_REGISTRY_KEY, BaseSkill.class);

    @Override
    public void init()
    {
        SKILLS.addType("basic", KubejsSkillBuilder.class,KubejsSkillBuilder::new);
        super.init();
    }
}
