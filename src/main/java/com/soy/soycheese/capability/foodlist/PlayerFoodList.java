package com.soy.soycheese.capability.foodlist;

import com.soy.soycheese.SoycheeseCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Objects;

public class PlayerFoodList {
    private final HashSet<ResourceLocation> foodlist = new HashSet<>();
    public static final String NBT_KEY_FOOD_LIST = "soycheesefoodlist";
    public PlayerFoodList() {
    }
    public HashSet<ResourceLocation> getFoodlist() {
        return foodlist;
    }
    public void setFoodlist(HashSet<ResourceLocation> foodlist) {
        this.foodlist.clear();
        this.foodlist.addAll(foodlist);
    }
    public void clearFoodlist() {
        this.foodlist.clear();
    }
    public void addFood(Item food) {
        this.foodlist.add(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(food));
    }
    public void removeFood(Item food) {
        this.foodlist.remove(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(food));
    }
    public boolean containsFood(ItemStack food) {
        return this.foodlist.contains(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(food.getItem()));
    }
    public boolean containsFood(Item food) {
        return this.foodlist.contains(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(food));
    }
    public boolean containsFood(ResourceLocation food) {
        return this.foodlist.contains(food);
    }
    public int getFoodCount() {
        return this.foodlist.size();
    }
    public void saveNBTData(CompoundTag tag) {
        var list = new ListTag();
        foodlist.stream()
                .map(String::valueOf)
                .filter(Objects::nonNull)
                .map(StringTag::valueOf)
                .forEach(list::add);
        tag.put(NBT_KEY_FOOD_LIST, list);
    }
    public void loadNBTData(CompoundTag tag) {
        var list = tag.getList(NBT_KEY_FOOD_LIST, Tag.TAG_STRING);
        foodlist.clear();
        list.stream()
                .filter(Objects::nonNull)
                .map(nbt -> (StringTag) nbt)
                .map(StringTag::getAsString)
                .map(ResourceLocation::new)
                .forEach(foodlist::add);
    }
}
