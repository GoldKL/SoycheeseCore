package com.soy.soycheese.capability.foodlist;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerFoodListProvider implements ICapabilitySerializable<CompoundTag> {
    private PlayerFoodList playerFoodList;
    public static final Capability<PlayerFoodList> PLAYER_FOOD_LIST_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<PlayerFoodList> playerFoodListLazyOptional = LazyOptional.of(() -> playerFoodList);
    public PlayerFoodListProvider() {
        this.playerFoodList = new PlayerFoodList();
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return PLAYER_FOOD_LIST_CAPABILITY.orEmpty(cap, playerFoodListLazyOptional);
    }
    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        playerFoodList.saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        playerFoodList.loadNBTData(nbt);
    }
}
