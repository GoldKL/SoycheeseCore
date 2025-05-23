package com.soy.soycheese.network.packet;

import com.soy.soycheese.client.screen.tutorial.InitTutorial;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AwardServerPacket {
    public String identifier;

    public AwardServerPacket(String identifier) {
        this.identifier = identifier;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.identifier);
    }

    public static AwardServerPacket decode(FriendlyByteBuf buf) {
        return new AwardServerPacket(buf.readUtf());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            InitTutorial.PAGES.forEach(page -> {
                if (page.identifier.equals(this.identifier)) {
                    page.award.forEach(stack -> context.getSender().spawnAtLocation(stack));
                }
            });
        });
        context.setPacketHandled(true);
    }
}
