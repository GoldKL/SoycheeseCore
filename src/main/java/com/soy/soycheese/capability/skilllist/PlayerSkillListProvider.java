package com.soy.soycheese.capability.skilllist;

import com.soy.soycheese.capability.foodlist.PlayerFoodList;
import com.soy.soycheese.capability.skilllist.PlayerSkillList;
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

public class PlayerSkillListProvider implements ICapabilityProvider, INBTSerializable {
    private PlayerSkillList playerSkillList;
    public static final Capability<PlayerSkillList> PLAYER_SKILL_LIST_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<PlayerSkillList> playerSkillListLazyOptional = LazyOptional.of(() -> playerSkillList);
    public PlayerSkillListProvider() {
        this.playerSkillList = new PlayerSkillList();
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == PLAYER_SKILL_LIST_CAPABILITY) {
            return playerSkillListLazyOptional.cast();
        }
        else {
            return LazyOptional.empty();
        }
    }
    @Override
    public Tag serializeNBT() {
        var tag = new CompoundTag();
        playerSkillList.saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        playerSkillList.loadNBTData((CompoundTag)nbt);
    }
}
