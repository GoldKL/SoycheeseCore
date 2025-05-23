package com.soy.soycheese.network;

import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.client.packet.PageClientPacket;
import com.soy.soycheese.network.communication.CookbookSwitchSkillMessage;
import com.soy.soycheese.network.communication.PlayerFoodListMessage;
import com.soy.soycheese.network.communication.PlayerSkillListMessage;
import com.soy.soycheese.network.packet.AwardServerPacket;
import com.soy.soycheese.network.packet.PageServerPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class SoycheesePacket {
    public static final String VERSION = "1.0";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SoycheeseCore.MODID, "main"),
            () -> VERSION,
            VERSION::equals,
            VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.messageBuilder(PlayerFoodListMessage.class, id++)
                .encoder(PlayerFoodListMessage::write)
                .decoder(PlayerFoodListMessage::new)
                .consumerMainThread(PlayerFoodListMessage::handle)
                .add();
        INSTANCE.messageBuilder(PlayerSkillListMessage.class, id++)
                .encoder(PlayerSkillListMessage::write)
                .decoder(PlayerSkillListMessage::new)
                .consumerMainThread(PlayerSkillListMessage::handle)
                .add();
        INSTANCE.messageBuilder(CookbookSwitchSkillMessage.class, id++)
                .encoder(CookbookSwitchSkillMessage::buffer)
                .decoder(CookbookSwitchSkillMessage::new)
                .consumerMainThread(CookbookSwitchSkillMessage::handler)
                .add();
        INSTANCE.messageBuilder(PageServerPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PageServerPacket::encode)
                .decoder(PageServerPacket::decode)
                .consumerMainThread(PageServerPacket::handle)
                .add();
        INSTANCE.messageBuilder(AwardServerPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(AwardServerPacket::encode)
                .decoder(AwardServerPacket::decode)
                .consumerMainThread(AwardServerPacket::handle)
                .add();
        INSTANCE.messageBuilder(PageClientPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PageClientPacket::encode)
                .decoder(PageClientPacket::decode)
                .consumerMainThread(PageClientPacket::handle)
                .add();
    }

    public static <MSG> void sendToClient(MSG msg, ServerPlayer serverPlayer) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
    }

    public static <MSG> void sendToServer(MSG msg) {
        INSTANCE.sendToServer(msg);
    }
}
