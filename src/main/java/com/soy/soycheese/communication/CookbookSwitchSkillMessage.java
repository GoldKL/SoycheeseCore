package com.soy.soycheese.communication;

import com.soy.soycheese.capability.skilllist.PlayerSkillListProvider;
import com.soy.soycheese.inventory.CookbookMenu;
import com.soy.soycheese.registries.SkillRegistry;
import com.soy.soycheese.skill.AbstractSkill;
import com.soy.soycheese.tracking.ForgeEventListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class CookbookSwitchSkillMessage {
    private final int buttonID;
    private final ResourceLocation skill;
    private final boolean isequip;

    public CookbookSwitchSkillMessage(FriendlyByteBuf buffer) {
        this.skill = buffer.readResourceLocation();
        this.isequip = buffer.readBoolean();
        this.buttonID = buffer.readInt();
    }

    public CookbookSwitchSkillMessage(ResourceLocation skill,boolean isequip,int buttonID) {
        this.skill = skill;
        this.isequip = isequip;
        this.buttonID = buttonID;
    }

    public static void buffer(CookbookSwitchSkillMessage message, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(message.skill);
        buffer.writeBoolean(message.isequip);
        buffer.writeInt(message.buttonID);
    }

    public static void handler(CookbookSwitchSkillMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player entity = context.getSender();
            ResourceLocation skill = message.skill;
            boolean isequip = message.isequip;
            int buttonID = message.buttonID;
            handleButtonAction(entity, skill ,isequip, buttonID);
        });
        context.setPacketHandled(true);
    }

    public static void handleButtonAction(Player entity,ResourceLocation skill,boolean isequip, int buttonID) {
        Level world = entity.level();
        HashMap guistate = CookbookMenu.guistate;
        // security measure to prevent arbitrary chunk generation
        if (buttonID == 0) {
            AbstractSkill bsk = SkillRegistry.getSkill(skill);
            if (bsk != null) {
                if (isequip) {
                    entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                        AbstractSkill old_bsk = SkillRegistry.getSkill(list.getSkilllist().get(bsk.getType()));
                        if (old_bsk != null) {
                            list.removeSkill(bsk.getType());
                            old_bsk.onUnequip(entity);
                        }
                        list.setSkill(bsk,bsk.getType());
                        bsk.onEquip(entity);
                        for(int i = 0; i < 4 ;++i)
                        {
                            if(i == bsk.getType())continue;
                            AbstractSkill other_bsk = SkillRegistry.getSkill(list.getSkilllist().get(i));
                            if (other_bsk != null) {
                                other_bsk.onChangeOtherEquip(entity,bsk,old_bsk);
                            }
                        }
                    });
                }
                else
                {
                    entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                        list.removeSkill(bsk);
                        bsk.onUnequip(entity);
                        for(int i = 0; i < 4 ;++i)
                        {
                            if(i == bsk.getType())continue;
                            AbstractSkill other_bsk = SkillRegistry.getSkill(list.getSkilllist().get(i));
                            if (other_bsk != null) {
                                other_bsk.onChangeOtherEquip(entity,null,bsk);
                            }
                        }
                    });
                }
                ForgeEventListener.syncPlayerSkillList(entity);
            }
        }
    }

}
