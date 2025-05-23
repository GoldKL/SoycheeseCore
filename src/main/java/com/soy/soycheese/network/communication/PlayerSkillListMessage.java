package com.soy.soycheese.network.communication;

import com.soy.soycheese.capability.skilllist.PlayerSkillList;
import com.soy.soycheese.capability.skilllist.PlayerSkillListProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerSkillListMessage {
    private CompoundTag capabilityNBT;

    public PlayerSkillListMessage(PlayerSkillList skillList) {
        var tag = new CompoundTag();
        skillList.saveNBTData(tag);
        this.capabilityNBT = tag;
    }

    public PlayerSkillListMessage(FriendlyByteBuf buffer) {
        this.capabilityNBT = buffer.readNbt();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(capabilityNBT);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Handler.handle(this, context));
    }

    private static class Handler {
        static void handle(PlayerSkillListMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                Player player = Minecraft.getInstance().player;
                assert player != null;

                player.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                    list.loadNBTData(message.capabilityNBT);
                });
            });
            context.get().setPacketHandled(true);
        }
    }
}
