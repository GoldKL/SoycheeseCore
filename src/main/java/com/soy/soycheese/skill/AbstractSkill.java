package com.soy.soycheese.skill;

import com.soy.soycheese.SoycheeseCore;
import net.minecraft.Util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

import com.soy.soycheese.registries.SkillRegistry;

public abstract class AbstractSkill {
    @Nullable
    private String nameid;
    @Nullable
    private String icon;
    private final int type;//0 1 2 3 分类
    public AbstractSkill(int type) {
        this.type = type;
    }
    public String getOrCreateNameid() {
        if (this.nameid == null) {
            this.nameid = Util.makeDescriptionId("soyskill", SkillRegistry.REGISTRY.get().getKey(this));
        }
        return this.nameid;
    }
    public String getorCreateIcon() {
        ResourceLocation res = SkillRegistry.REGISTRY.get().getKey(this);
        if(this.icon == null && res != null) {
            this.icon = res.getPath();
        }
        return this.icon;
    }
    public abstract Component getDescription(Player player);
    public abstract boolean getIslock(Player player);
    public Component getName() {
        return Component.translatable(this.getOrCreateNameid());
    }
    public int getType() {
        return this.type;
    }
    public ResourceLocation getSkillIconResource() {
        return new ResourceLocation(SoycheeseCore.MODID, "textures/gui/skill_icons/" + this.getorCreateIcon() + ".png");
    }
    //该效果每玩家刻执行
    public abstract void onTick(Player player);
    //此时技能已被装备
    public abstract void onEquip(Player player);
    //此时技能已被卸下
    public abstract void onUnequip(Player player);
    //当其他的技能改变时，此时新技能已被装备，旧技能已被卸载
    public abstract void onChangeOtherEquip(Player player, @Nullable AbstractSkill newskill, @Nullable AbstractSkill oldskill);
}
