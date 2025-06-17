package com.soy.soycheese.skill;


import com.soy.soycheese.kubejs.KubejsSkillBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class KubejsSkill extends BaseSkill {
    private final BiConsumer<KubejsSkill,Player> onTick;
    private final BiConsumer<KubejsSkill,Player> onEquip;
    private final BiConsumer<KubejsSkill,Player> onUnequip;
    private final BiConsumer<KubejsSkill,SkillContext> onChangeOtherEquip;
    private final BiFunction<KubejsSkill,Player,Component> getDescription;
    private final BiFunction<KubejsSkill,Player,Boolean> getIslock;
    private final BiFunction<KubejsSkill,AttackContext,Boolean> onAttack;
    private final BiFunction<KubejsSkill,AttackedContext,Boolean> onAttacked;
    private final BiFunction<KubejsSkill,HurtContext,Boolean> onHurt;
    private final BiFunction<KubejsSkill,HurtedContext,Boolean> onHurted;
    private final BiFunction<KubejsSkill,HurtContext,Boolean> onDamage;
    private final BiFunction<KubejsSkill,HurtedContext,Boolean> onDamaged;
    public static class SkillContext {
        public final Player player;
        @Nullable
        public final BaseSkill newskill;
        @Nullable
        public final BaseSkill oldskill;
        SkillContext(Player player, @Nullable BaseSkill newskill, @Nullable BaseSkill oldskill) {
            this.player = player;
            this.newskill = newskill;
            this.oldskill = oldskill;
        }
    }
    public static class AttackContext {
        public final Player player;
        public final LivingEntity target;
        public final DamageSource source;
        public final float damage;
        AttackContext(Player player,LivingEntity target, DamageSource source, float damage) {
            this.player = player;
            this.target = target;
            this.source = source;
            this.damage = damage;
        }
    }
    public static class AttackedContext {
        public final Player player;
        public final DamageSource source;
        public final float damage;
        AttackedContext(Player player, DamageSource source, float damage) {
            this.player = player;
            this.source = source;
            this.damage = damage;
        }
    }
    public static class HurtContext {
        public final Player player;
        public final LivingEntity target;
        public final DamageSource source;
        public final float basedamage;
        public final float damage;
        HurtContext(Player player, LivingEntity target, DamageSource source, float basedamage, float damage) {
            this.player = player;
            this.target = target;
            this.source = source;
            this.basedamage = basedamage;
            this.damage = damage;
        }
    }
    public static class HurtedContext {
        public final Player player;
        public final DamageSource source;
        public final float basedamage;
        public final float damage;
        HurtedContext(Player player, DamageSource source,float basedamage , float damage){
            this.player = player;
            this.source = source;
            this.basedamage = basedamage;
            this.damage = damage;
        }
    }
    public KubejsSkill(int type,
                       BiConsumer<KubejsSkill,Player> onTick,
                       BiConsumer<KubejsSkill,Player> onEquip,
                       BiConsumer<KubejsSkill,Player> onUnequip,
                       BiConsumer<KubejsSkill,SkillContext> onChangeOtherEquip,
                       BiFunction<KubejsSkill,Player, Component> getDescription,
                       BiFunction<KubejsSkill,Player, Boolean> getIslock,
                       BiFunction<KubejsSkill,AttackContext, Boolean> onAttack,
                       BiFunction<KubejsSkill,AttackedContext,Boolean> onAttacked,
                       BiFunction<KubejsSkill,HurtContext,Boolean> onHurt,
                       BiFunction<KubejsSkill,HurtedContext,Boolean> onHurted,
                       BiFunction<KubejsSkill,HurtContext,Boolean> onDamage,
                       BiFunction<KubejsSkill,HurtedContext,Boolean> onDamageed) {
        super(type);
        this.onTick = onTick;
        this.onEquip = onEquip;
        this.onUnequip = onUnequip;
        this.onChangeOtherEquip = onChangeOtherEquip;
        this.getDescription = getDescription;
        this.getIslock = getIslock;
        this.onAttack = onAttack;
        this.onAttacked = onAttacked;
        this.onHurt = onHurt;
        this.onHurted = onHurted;
        this.onDamage = onDamage;
        this.onDamaged = onDamageed;
    }
    public KubejsSkill(KubejsSkillBuilder builder) {
        this(builder.type,
                builder.onTick,
                builder.onEquip,
                builder.onUnequip,
                builder.onChangeOtherEquip,
                builder.getDescription,
                builder.getIslock,
                builder.onAttack,
                builder.onAttacked,
                builder.onHurt,
                builder.onHurted,
                builder.onDamage,
                builder.onDamaged);
    }
    protected KubejsSkill(KubejsSkill.Builder builder) {
        this(builder.type,
                builder.onTick,
                builder.onEquip,
                builder.onUnequip,
                builder.onChangeOtherEquip,
                builder.getDescription,
                builder.getIslock,
                builder.onAttack,
                builder.onAttacked,
                builder.onHurt,
                builder.onHurted,
                builder.onDamage,
                builder.onDamaged);
    }

    public static KubejsSkill.Builder builder(int type) {
        return new KubejsSkill.Builder(type);
    }
    @Override
    public Component getDescription(Player player) {
        if(this.getDescription == null)
            return super.getDescription(player);
        return this.getDescription.apply(this,player);
    }

    @Override
    public boolean getIslock(Player player) {
        if(this.getIslock == null)
            return super.getIslock(player);
        return this.getIslock.apply(this,player);
    }

    @Override
    public void onTick(Player player) {
        if(this.onTick == null)
            super.onTick(player);
        else
            this.onTick.accept(this,player);
    }

    @Override
    public void onEquip(Player player) {
        if(this.onEquip == null)
            super.onEquip(player);
        else
            this.onEquip.accept(this,player);
    }

    @Override
    public void onUnequip(Player player) {
        if(this.onUnequip == null)
            super.onUnequip(player);
        else
            this.onUnequip.accept(this,player);
    }

    @Override
    public void onChangeOtherEquip(Player player, @Nullable BaseSkill newskill, @Nullable BaseSkill oldskill) {
        if(this.onChangeOtherEquip == null)
            super.onChangeOtherEquip(player, newskill, oldskill);
        else
            this.onChangeOtherEquip.accept(this, new SkillContext(player, newskill, oldskill));
    }
    @Override
    public boolean onAttack(Player player, LivingEntity target, DamageSource source, float damage){
        if(this.onAttack == null)
            return super.onAttack(player, target, source, damage);
        return this.onAttack.apply(this,new AttackContext(player,target,source,damage));
    }
    @Override
    public boolean onAttacked(Player player, DamageSource source,float damage){
        if(this.onAttacked == null)
            return super.onAttacked(player, source, damage);
        return this.onAttacked.apply(this,new AttackedContext(player,source,damage));
    }
    public static class Builder {
        private BiConsumer<KubejsSkill,Player> onTick;
        private BiConsumer<KubejsSkill,Player> onEquip;
        private BiConsumer<KubejsSkill,Player> onUnequip;
        private BiConsumer<KubejsSkill,SkillContext> onChangeOtherEquip;
        private BiFunction<KubejsSkill,Player, Component> getDescription;
        private BiFunction<KubejsSkill,Player, Boolean> getIslock;
        private BiFunction<KubejsSkill,AttackContext, Boolean> onAttack;
        private BiFunction<KubejsSkill,AttackedContext,Boolean> onAttacked;
        private BiFunction<KubejsSkill,HurtContext,Boolean> onHurt;
        private BiFunction<KubejsSkill,HurtedContext,Boolean> onHurted;
        private BiFunction<KubejsSkill,HurtContext,Boolean> onDamage;
        private BiFunction<KubejsSkill,HurtedContext,Boolean> onDamaged;
        int type;
        public Builder(int type)
        {
            this.type = type;
        }
        public Builder onTick(BiConsumer<KubejsSkill,Player> onTick) {
            this.onTick = onTick;
            return this;
        }
        public Builder onEquip(BiConsumer<KubejsSkill,Player> onEquip) {
            this.onEquip = onEquip;
            return this;
        }
        public Builder onUnequip(BiConsumer<KubejsSkill,Player> onUnequip) {
            this.onUnequip = onUnequip;
            return this;
        }
        public Builder onChangeOtherEquip(BiConsumer<KubejsSkill,SkillContext> onChangeOtherEquip) {
            this.onChangeOtherEquip = onChangeOtherEquip;
            return this;
        }
        public Builder getDescription(BiFunction<KubejsSkill,Player,Component> getDescription) {
            this.getDescription = getDescription;
            return this;
        }
        public Builder getIslock(BiFunction<KubejsSkill,Player,Boolean> getIslock) {
            this.getIslock = getIslock;
            return this;
        }
        public Builder onAttack(BiFunction<KubejsSkill,AttackContext, Boolean> onAttack) {
            this.onAttack = onAttack;
            return this;
        }
        public Builder onAttacked(BiFunction<KubejsSkill,AttackedContext, Boolean> onAttacked) {
            this.onAttacked = onAttacked;
            return this;
        }
        public Builder onHurt(BiFunction<KubejsSkill,HurtContext, Boolean> onHurt) {
            this.onHurt = onHurt;
            return this;
        }
        public Builder onHurted(BiFunction<KubejsSkill,HurtedContext, Boolean> onHurted) {
            this.onHurted = onHurted;
            return this;
        }
        public Builder onDamage(BiFunction<KubejsSkill,HurtContext, Boolean> onDamage) {
            this.onDamage = onDamage;
            return this;
        }
        public Builder onDamaged(BiFunction<KubejsSkill,HurtedContext, Boolean> onDamaged) {
            this.onDamaged = onDamaged;
            return this;
        }
        public KubejsSkill build() {
            return build(KubejsSkill::new);
        }
        public KubejsSkill build(java.util.function.Function<KubejsSkill.Builder, KubejsSkill> builder) {
            return builder.apply(this);
        }
    }
}
