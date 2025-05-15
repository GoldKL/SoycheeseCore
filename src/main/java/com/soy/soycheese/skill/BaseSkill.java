package com.soy.soycheese.skill;

import com.soy.soycheese.SoycheeseCore;
import net.minecraft.Util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import com.soy.soycheese.registries.SkillRegistry;

public class BaseSkill {
    @Nullable
    private String nameid;
    private final int type;
    public BaseSkill(int type) {
        this.type = type;
    }
    public String getOrCreateNameid() {
        if (this.nameid == null) {
            this.nameid = Util.makeDescriptionId("soyskill", SkillRegistry.REGISTRY.get().getKey(this));
        }
        return this.nameid;
    }
    public Component getDescription(Player player) {
        if(this.getIslock(player))
            return Component.translatable(getOrCreateNameid() + ".lock");
        return Component.translatable(getOrCreateNameid() + ".unlock");
    }
    public boolean getIslock(Player player) {
        return true;
    }
    public Component getName() {
        return Component.translatable(this.getOrCreateNameid());
    }
    public int getType() {
        return this.type;
    }
    public ResourceLocation getSkillIconResource() {
        return new ResourceLocation(SoycheeseCore.MODID, "textures/gui/skill_icons/" + this.getOrCreateNameid() + ".png");
    }
    public void onTick(Player player) {

    }
    public void onEquip(Player player) {

    }
    public void onUnequip(Player player) {

    }
}
