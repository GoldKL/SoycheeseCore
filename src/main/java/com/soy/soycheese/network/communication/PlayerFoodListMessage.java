package com.soy.soycheese.network.communication;

import com.soy.soycheese.capability.foodlist.PlayerFoodList;
import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PlayerFoodListMessage {
    private CompoundTag capabilityNBT;

    public PlayerFoodListMessage(PlayerFoodList foodList) {
        var tag = new CompoundTag();
        foodList.saveNBTData(tag);
        this.capabilityNBT = tag;
    }

    public PlayerFoodListMessage(FriendlyByteBuf buffer) {
        this.capabilityNBT = buffer.readNbt();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(capabilityNBT);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Handler.handle(this, context));
    }

    private static class Handler {
        static void handle(PlayerFoodListMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                Player player = Minecraft.getInstance().player;
                assert player != null;

                player.getCapability(PlayerFoodListProvider.PLAYER_FOOD_LIST_CAPABILITY).ifPresent(list -> {
                    list.loadNBTData(message.capabilityNBT);
                });
            });
            context.get().setPacketHandled(true);
        }
    }
}
