package com.soy.soycheese.skill;

import com.google.gson.JsonObject;
import com.soy.soycheese.SoycheeseCore;
import net.minecraft.Util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

import com.soy.soycheese.registries.SkillRegistry;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class BaseSkill {
    @Nullable
    private String nameid;
    @Nullable
    private ResourceLocation skillid;
    private final int type;//0 1 2 3 分类
    final SkillArguments skillArguments;
    public final static Set<Class<?>> ACCEPT_TYPES = Set.of(Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, BigDecimal.class, BigInteger.class,String.class);
    public static <T> boolean isAcceptType(T value)
    {
        return value != null && ACCEPT_TYPES.contains(value.getClass());
    }
    public BaseSkill(int type) {
        this.type = type;
        this.skillArguments = new SkillArguments();
    }
    public BaseSkill(int type,SkillArguments skillArguments) {
        this.type = type;
        this.skillArguments = new SkillArguments(skillArguments);
    }
    public BaseSkill(int type,LinkedHashMap<String,Object> skill_arguments,HashMap<String,Class<?>> skill_types) {
        this.type = type;
        this.skillArguments = new SkillArguments(skill_arguments,skill_types);
    }
    protected BaseSkill(BaseSkill.Builder builder) {
        this(builder.type);
    }
    final protected <T> void initSkillArgument(String name, T value) {
        skillArguments.initSkillArgument(name,value);
    }
    //给kubejs用的
    final public Object getSkillArgument(String name) {
        return skillArguments.getSkillArgument(name);
    }
    final public <T> T getSkillArgument(String name, @NotNull Class<T> type) {
        return skillArguments.getSkillArgument(name,type);
    }
    final public void readjson(JsonObject config)
    {
        skillArguments.readjson(config);
    }
    public String getOrCreateNameid() {
        if (this.nameid == null) {
            this.nameid = Util.makeDescriptionId("soyskill", this.getorCreateSkillid());
        }
        return this.nameid;
    }
    public ResourceLocation getorCreateSkillid() {
        if(this.skillid == null) {
            this.skillid = SkillRegistry.REGISTRY.get().getKey(this);
        }
        return this.skillid;
    }
    public Object[] getSkillArguments() {
        return this.skillArguments.getSkillArguments();
    }
    //获取该技能描述，一般不用重写
    public Component getDescription(Player player)  {
        if(this.getIslock(player))
            return Component.translatable(getOrCreateNameid() + ".lock");
        return Component.translatable(getOrCreateNameid() + ".unlock",this.getSkillArguments());
    }
    //确认该技能是否解锁，一般要重写
    public boolean getIslock(Player player) {
        return true;
    }
    //获取技能名称
    public Component getName() {
        return Component.translatable(this.getOrCreateNameid());
    }
    //获取技能类别
    public int getType() {
        return this.type;
    }
    //获取技能图标
    public ResourceLocation getSkillIconResource() {
        return new ResourceLocation(this.getorCreateSkillid().getNamespace(), "soycheese_core/soyskill_icons/" +this.getorCreateSkillid().getPath() + ".png");
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
