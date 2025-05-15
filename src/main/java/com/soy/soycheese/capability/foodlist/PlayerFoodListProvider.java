package com.soy.soycheese.capability.foodlist;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerFoodListProvider implements ICapabilityProvider, INBTSerializable {
    private PlayerFoodList playerFoodList;
    public static final Capability<PlayerFoodList> PLAYER_FOOD_LIST_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<PlayerFoodList> playerFoodListLazyOptional = LazyOptional.of(() -> playerFoodList);
    public PlayerFoodListProvider() {
        this.playerFoodList = new PlayerFoodList();
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == PLAYER_FOOD_LIST_CAPABILITY) {
            return playerFoodListLazyOptional.cast();
        }
        else {
            return LazyOptional.empty();
        }
    }
    @Override
    public Tag serializeNBT() {
        var tag = new CompoundTag();
        playerFoodList.saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        playerFoodList.loadNBTData((CompoundTag)nbt);
    }
}
