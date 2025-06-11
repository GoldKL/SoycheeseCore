package com.soy.soycheese.capability.skilllist;

import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.skill.AbstractSkill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import com.soy.soycheese.registries.SkillRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PlayerSkillList {
    public static ResourceLocation noneSkill = new ResourceLocation(SoycheeseCore.MODID,"noneskill");
    private final ArrayList<ResourceLocation> skilllist;
    public static final String NBT_KEY_SKILL_LIST = "soycheeseskilllist";
    public PlayerSkillList(){
        this.skilllist = new ArrayList<>(Arrays.asList(noneSkill,noneSkill,noneSkill,noneSkill));
    }
    public ArrayList<ResourceLocation> getSkilllist() {
        return skilllist;
    }
    public void setSkilllist(ArrayList<ResourceLocation> skilllist) {
        this.skilllist.clear();
        this.skilllist.addAll(skilllist);
    }
    public void clearskilllist() {
        this.skilllist.clear();
        this.skilllist.addAll(Arrays.asList(noneSkill,noneSkill,noneSkill,noneSkill));
    }
    public void setSkill(AbstractSkill skill, int slot) {
        this.skilllist.set(slot,SkillRegistry.REGISTRY.get().getKey(skill));
    }
    public void removeSkill(int slot) {
        this.skilllist.set(slot,noneSkill);
    }
    public void removeSkill(ResourceLocation skill) {
        int slot = this.skilllist.indexOf(skill);
        if(slot == -1) return;
        removeSkill(slot);
    }
    public void removeSkill(AbstractSkill skill) {
        int slot = this.skilllist.indexOf(SkillRegistry.REGISTRY.get().getKey(skill));
        if(slot == -1) return;
        removeSkill(slot);
    }
    public boolean containsSkill(AbstractSkill skill) {
        return this.skilllist.contains(SkillRegistry.REGISTRY.get().getKey(skill));
    }
    public boolean containsSkill(ResourceLocation skill) {
        return this.skilllist.contains(skill);
    }
    public void saveNBTData(CompoundTag tag) {
        var list = new ListTag();
        skilllist.stream()
                .map(String::valueOf)
                .filter(Objects::nonNull)
                .map(StringTag::valueOf)
                .forEach(list::add);
        tag.put(NBT_KEY_SKILL_LIST, list);
    }
    public void loadNBTData(CompoundTag tag) {
        var list = tag.getList(NBT_KEY_SKILL_LIST, Tag.TAG_STRING);
        skilllist.clear();
        list.stream()
                .filter(Objects::nonNull)
                .map(nbt -> (StringTag) nbt)
                .map(StringTag::getAsString)
                .map(ResourceLocation::new)
                .forEach(skilllist::add);
    }
}
