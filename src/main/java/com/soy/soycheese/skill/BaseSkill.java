package com.soy.soycheese.skill;

import net.minecraft.Util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

import com.soy.soycheese.registries.SkillRegistry;

public class BaseSkill {
    @Nullable
    private String nameid;
    @Nullable
    private ResourceLocation icon;
    private final int type;//0 1 2 3 分类
    public BaseSkill(int type) {
        this.type = type;
    }
    protected BaseSkill(BaseSkill.Builder builder) {
        this(builder.type);
    }
    public String getOrCreateNameid() {
        if (this.nameid == null) {
            this.nameid = Util.makeDescriptionId("soyskill", SkillRegistry.REGISTRY.get().getKey(this));
        }
        return this.nameid;
    }
    public ResourceLocation getorCreateIcon() {
        ResourceLocation res = SkillRegistry.REGISTRY.get().getKey(this);
        if(this.icon == null && res != null) {
            this.icon = res;
        }
        return this.icon;
    }
    //获取该技能描述，一般不用重写
    public Component getDescription(Player player)  {
        if(this.getIslock(player))
            return Component.translatable(getOrCreateNameid() + ".lock");
        return Component.translatable(getOrCreateNameid() + ".unlock");
    }
    //确认该技能是否解锁，一般要重写
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
        return new ResourceLocation(this.getorCreateIcon().getNamespace(), "soycheese_core/soyskill_icons/" +this.getorCreateIcon().getPath() + ".png");
    }
    //装备时触发，此时技能已被装备
    public void onEquip(Player player){

    }
    //装备时触发，此时技能已被卸下
    public void onUnequip(Player player){

    }
    //当其他的技能改变时触发，此时新技能已被装备，旧技能已被卸载
    public void onChangeOtherEquip(Player player, @Nullable BaseSkill newskill, @Nullable BaseSkill oldskill){

    }
    //该效果每玩家刻执行
    public void onTick(Player player){

    }
    //玩家攻击时，如果返回true则取消攻击
    public boolean onAttack(Player player, LivingEntity target, DamageSource source, float damage){
        return false;
    }
    //玩家受到攻击时，如果返回true则取消攻击
    public boolean onAttacked(Player player, DamageSource source,float damage){
        return false;
    }
    //玩家造成伤害时①，此时未计算护甲，返回值不大于0则取消事件
    public float onHurt(Player player, LivingEntity target, DamageSource source,float basedamage , float damage){
        return damage;
    }
    //玩家受到伤害时①，此时未计算护甲，返回值不大于0则取消事件
    public float onHurted(Player player, DamageSource source,float basedamage , float damage){
        return damage;
    }
    //玩家造成伤害时②，此时已计算护甲，返回值不大于0则取消事件
    public float onDamage(Player player, LivingEntity target, DamageSource source,float basedamage , float damage){
        return damage;
    }
    //玩家受到伤害时②，此时已计算护甲，返回值不大于0则取消事件
    public float onDamaged(Player player, DamageSource source,float basedamage , float damage){
        return damage;
    }
    public static class Builder {
        int type;
        public Builder(int type)
        {
            this.type = type;
        }
        public BaseSkill build() {
            return build(BaseSkill::new);
        }
        public BaseSkill build(java.util.function.Function<BaseSkill.Builder, BaseSkill> builder) {
            return builder.apply(this);
        }
    }
}
