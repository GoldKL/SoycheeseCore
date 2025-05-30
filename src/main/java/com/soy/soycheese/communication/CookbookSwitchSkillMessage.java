package com.soy.soycheese.communication;

import com.soy.soycheese.capability.skilllist.PlayerSkillListProvider;
import com.soy.soycheese.inventory.CookbookMenu;
import com.soy.soycheese.registries.SkillRegistry;
import com.soy.soycheese.skill.BaseSkill;
import com.soy.soycheese.tracking.ForgeEventListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.Supplier;

import static com.soy.soycheese.tracking.ForgeEventListener.syncPlayerSkillList;

public class CookbookSwitchSkillMessage {
    private final int buttonID, x, y, z;
    private final ResourceLocation skill;
    private final boolean isequip;

    public CookbookSwitchSkillMessage(FriendlyByteBuf buffer) {
        this.skill = buffer.readResourceLocation();
        this.isequip = buffer.readBoolean();
        this.buttonID = buffer.readInt();
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
    }

    public CookbookSwitchSkillMessage(ResourceLocation skill,boolean isequip,int buttonID,int x, int y, int z) {
        this.skill = skill;
        this.isequip = isequip;
        this.buttonID = buttonID;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void buffer(CookbookSwitchSkillMessage message, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(message.skill);
        buffer.writeBoolean(message.isequip);
        buffer.writeInt(message.buttonID);
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
    }

    public static void handler(CookbookSwitchSkillMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player entity = context.getSender();
            ResourceLocation skill = message.skill;
            boolean isequip = message.isequip;
            int buttonID = message.buttonID;
            int x = message.x;
            int y = message.y;
            int z = message.z;
            handleButtonAction(entity, skill ,isequip, buttonID, x, y, z);
        });
        context.setPacketHandled(true);
    }

    public static void handleButtonAction(Player entity,ResourceLocation skill,boolean isequip, int buttonID, int x, int y, int z) {
        Level world = entity.level();
        HashMap guistate = CookbookMenu.guistate;
        // security measure to prevent arbitrary chunk generation
        if (!world.hasChunkAt(new BlockPos(x, y, z)))
            return;
        if (buttonID == 0) {
            BaseSkill bsk = SkillRegistry.getSkill(skill);
            if (bsk != null) {
                if (isequip) {
                    entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                        BaseSkill old_bsk = SkillRegistry.getSkill(list.getSkilllist().get(bsk.getType()));
                        if (old_bsk != null) {
                            list.removeSkill(bsk.getType());
                            old_bsk.onUnequip(entity);
                        }
                        list.setSkill(bsk,bsk.getType());
                        bsk.onEquip(entity);
                    });
                }
                else
                {
                    entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                        list.removeSkill(bsk);
                        bsk.onUnequip(entity);
                    });
                }
                ForgeEventListener.syncPlayerSkillList(entity);
            }
        }
    }

}
