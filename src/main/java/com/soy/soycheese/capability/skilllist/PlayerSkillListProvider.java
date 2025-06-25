package com.soy.soycheese.capability.skilllist;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerSkillListProvider implements ICapabilitySerializable<CompoundTag> {
    private PlayerSkillList playerSkillList;
    public static final Capability<PlayerSkillList> PLAYER_SKILL_LIST_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<PlayerSkillList> playerSkillListLazyOptional = LazyOptional.of(() -> playerSkillList);
    public PlayerSkillListProvider() {
        this.playerSkillList = new PlayerSkillList();
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return PLAYER_SKILL_LIST_CAPABILITY.orEmpty(cap, playerSkillListLazyOptional);
    }
    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        playerSkillList.saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        playerSkillList.loadNBTData(nbt);
    }
}
