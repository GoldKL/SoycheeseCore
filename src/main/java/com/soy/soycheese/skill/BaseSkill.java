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

public abstract class BaseSkill {
    @Nullable
    private String nameid;
    private final int type;//0 1 2 3 分类
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
        //该效果每玩家刻执行
    }
    public void onEquip(Player player) {
        //此时技能已被装备
    }
    public void onUnequip(Player player) {
        //此时技能已被卸下
    }
    public void onChangeOtherEquip(Player player,@Nullable BaseSkill newskill,@Nullable BaseSkill oldskill) {
        //当其他的技能改变时，此时新技能已被装备，旧技能已被卸载
    }
}
