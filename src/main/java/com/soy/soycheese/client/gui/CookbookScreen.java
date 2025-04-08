package com.soy.soycheese.client.gui;

import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.blaze3d.systems.RenderSystem;
import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.capability.skilllist.PlayerSkillList;
import com.soy.soycheese.capability.skilllist.PlayerSkillListProvider;
import com.soy.soycheese.communication.CookbookSwitchSkillMessage;
import com.soy.soycheese.inventory.CookbookMenu;
import com.soy.soycheese.registries.SkillRegistry;
import com.soy.soycheese.skill.BaseSkill;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Integer;

import static com.soy.soycheese.registries.SkillRegistry.REGISTRY;

public class CookbookScreen extends AbstractContainerScreen<CookbookMenu> {
    private final static HashMap<String, Object> guistate = CookbookMenu.guistate;
    private final Level world;
    private final int x, y, z;
    private final Player entity;
    private Button pre_page;
    private Button next_page;
    private final ArrayList<ImageButton> skills_buttons = new ArrayList<>();
    private Button read_mode_button;
    private ImageButton close_button;
    private Button equip_unequip_skills_buttons;
    private final ArrayList<Button> skills_kind_buttons = new ArrayList<>();
    private final ArrayList<ArrayList<ArrayList<ResourceLocation> > > skills = new ArrayList<>();
    class ScreenVar{
        int now_page;
        int now_kind;
        int now_read_mode;
        int choose_skill;
        ResourceLocation now_skill;
    }
    private final ScreenVar screenVar = new ScreenVar();

    public CookbookScreen(CookbookMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 256;
        this.imageHeight = 180;
        for(int i = 0; i < 4; ++i)
        {
            skills.add(new ArrayList<>());
            for(int j = 0; j < 3; ++j)
                skills.get(i).add(new ArrayList<>());
        }
        SkillRegistry.getSkills().stream()
                .map(RegistryObject::get)
                .forEach(skill -> {
                    //SoycheeseCore.LOGGER.info(REGISTRY.get().getKey(skill).toString());
                    //SoycheeseCore.LOGGER.info(skill.getOrCreateNameid());
                    skills.get(skill.getType()).get(0).add(SkillRegistry.REGISTRY.get().getKey(skill));
                    skills.get(skill.getType()).get(skill.getIslock(entity)?1:2).add(SkillRegistry.REGISTRY.get().getKey(skill));
                });
        screenVar.now_page = 0;
        screenVar.now_kind = 0;
        screenVar.now_read_mode = 0;
        screenVar.now_skill = null;
        screenVar.choose_skill = -1;
    }

    private static final ResourceLocation texture = new ResourceLocation("soycheese_core:textures/gui/book_gui.png");

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if(screenVar.choose_skill != -1)
            if (screenVar.choose_skill < 24) {
                int num = screenVar.now_page * 24 + screenVar.choose_skill;
                if ( num < skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size())
                    screenVar.now_skill = skills.get(screenVar.now_kind).get(screenVar.now_read_mode).get(num);
            }
            else if(screenVar.choose_skill < 28)
                entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                    if (list.getSkilllist().get(screenVar.choose_skill - 24) != PlayerSkillList.noneSkill)
                        screenVar.now_skill = list.getSkilllist().get(screenVar.choose_skill - 24);
                    else
                        screenVar.now_skill = null;
                });
        if(screenVar.now_skill != null && SkillRegistry.getSkill(screenVar.now_skill) != null){
            BaseSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
            guiGraphics.blit(bsk.getSkillIconResource(), this.leftPos+49, this.topPos+16, 0, 0, 16, 16, 16, 16);
            if(bsk.getIslock(entity))
                guiGraphics.blit(texture, this.leftPos+49, this.topPos+16, 63, 221, 16, 16, 256, 256);
        }
        for(int i = 0; i < 24 ; ++i)
        {
            int num = screenVar.now_page * 24 + i;
            if(num >= skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size())break;
            BaseSkill bsk = SkillRegistry.getSkill(skills.get(screenVar.now_kind).get(screenVar.now_read_mode).get(num));
            guiGraphics.blit(bsk.getSkillIconResource(), this.leftPos+139+(i%4)*20, this.topPos+33+(i/4)*20, 0, 0, 16, 16, 16, 16);//还可以额外加参数把图片缩小
            if(bsk.getIslock(entity))
                guiGraphics.blit(texture, this.leftPos+139+(i%4)*20, this.topPos+33+(i/4)*20, 63, 221, 16, 16, 256, 256);
        }
        entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
            for(int i = 0; i < 4; ++i) {
                BaseSkill bsk = SkillRegistry.getSkill(list.getSkilllist().get(i));
                if (bsk != null) {
                    guiGraphics.blit(bsk.getSkillIconResource(), this.leftPos + 238, this.topPos + 3 + i * 20, 0, 0, 16, 16, 16, 16);
                }
            }
        });
        if(gx>=this.leftPos+169&&gx<=this.leftPos+184&&gy>=this.topPos+153&&gy<=this.topPos+168)
            guiGraphics.blit(texture, this.leftPos+169, this.topPos+153, screenVar.now_read_mode * 16, 240, 16, 16, 256, 256);
        else
            guiGraphics.blit(texture, this.leftPos+169, this.topPos+153, screenVar.now_read_mode * 16, 224, 16, 16, 256, 256);
        if(screenVar.now_page > 0)
            if(gx>=this.leftPos+131&&gx<=this.leftPos+154&&gy>=this.topPos+153&&gy<=this.topPos+167)
                guiGraphics.blit(texture, this.leftPos+131, this.topPos+153, 24, 206, 24, 15, 256, 256);
            else
                guiGraphics.blit(texture, this.leftPos+131, this.topPos+153, 24, 191, 24, 15, 256, 256);
        if(screenVar.now_page + 1 < skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size() / 24 + (skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size() % 24 == 0 ? 0 : 1))
            if(gx>=this.leftPos+199&&gx<=this.leftPos+222&&gy>=this.topPos+153&&gy<=this.topPos+167)
                guiGraphics.blit(texture, this.leftPos+199, this.topPos+153, 0, 206, 24, 15, 256, 256);
            else
                guiGraphics.blit(texture, this.leftPos+199, this.topPos+153, 0, 191, 24, 15, 256, 256);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(screenVar.now_skill != null)
        {
            BaseSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
            if(bsk != null)
            {
                int num = bsk.getDescription(entity).size();
                for(int i = 0; i < num; ++i)
                {
                    guiGraphics.drawString(this.font, bsk.getDescription(entity).get(i), 17, 37 + 10*i, 0x0, false);
                }
                if(!bsk.getIslock(entity)) {
                    entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                        if (list.containsSkill(bsk)) {
                            guiGraphics.drawString(this.font, Component.translatable("gui.soycheese_core.cookbook.unequip"), 23, 154, 0xFFFFFFFF, false);
                        } else {
                            guiGraphics.drawString(this.font, Component.translatable("gui.soycheese_core.cookbook.equip"), 23, 154, 0xFFFFFFFF, false);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void init() {
        super.init();
        guistate.put("screenvar:screenvar", screenVar);
        for(int i = 0 ; i < 24; ++i)
        {
            int index = i;
            ImageButton Frame = new ImageButton(this.leftPos + 137 + (i%4)*20, this.topPos + 31 + (i/4)*20, 20, 20, 236, 216, 20, texture, 256, 256, e -> {
                if(index + 24 * screenVar.now_page < skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size())
                {
                    if(screenVar.choose_skill != index)
                        screenVar.choose_skill = index;
                    else
                    {
                        screenVar.choose_skill = -1;
                        screenVar.now_skill = null;
                    }
                }
            }) {
                @Override
                public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
                    if(screenVar.choose_skill == index){
                        super.render(guiGraphics, gx, gy, ticks);
                    }
                }
            };
            skills_buttons.add(Frame);
            guistate.put("imagebutton:framebutton_"+Integer.toString(i), Frame);
            this.addRenderableWidget(Frame);
        }
        for(int i = 0 ; i < 4; ++i)
        {
            int index = i + 24;
            ImageButton Frame = new ImageButton(this.leftPos + 236, this.topPos + 1 + i*20, 20, 20, 236, 216, 20, texture, 256, 256, e -> {
                if(screenVar.choose_skill != index)
                    screenVar.choose_skill = index;
                else
                {
                    screenVar.choose_skill = -1;
                    screenVar.now_skill = null;
                }
            }) {
                @Override
                public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
                    if(screenVar.choose_skill == index){
                        super.render(guiGraphics, gx, gy, ticks);
                    }
                }
            };
            skills_buttons.add(Frame);
            guistate.put("imagebutton:framebutton_"+Integer.toString(index), Frame);
            this.addRenderableWidget(Frame);
        }
        close_button = new ImageButton(this.leftPos + -7, this.topPos + -7, 15, 15, 48, 191, 15, texture, 256, 256, e -> {
            entity.closeContainer();
        });
        guistate.put("button:close_button", close_button);
        this.addRenderableWidget(close_button);
        for(int i = 0 ; i < 4; ++i)
        {
            int index = i;
            Button tembutton = Button.builder(Component.translatable("gui.soycheese_core.cookbook.kindbutton_"+Integer.toString(i)), e -> {
                screenVar.now_kind = index;
                screenVar.now_page = 0;
                if(screenVar.choose_skill < 24)
                    screenVar.choose_skill = -1;
            }).bounds(this.leftPos + 131 + i*23, this.topPos + 14, 23, 15).build();
            skills_kind_buttons.add(tembutton);
            guistate.put("button:kindbutton_" + Integer.toString(i), tembutton);
            this.addRenderableWidget(tembutton);
        }
        read_mode_button = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {
            screenVar.now_read_mode = (screenVar.now_read_mode + 1) % 3;
            screenVar.now_page = 0;
            if(screenVar.choose_skill < 24)
                screenVar.choose_skill = -1;
        }).bounds(this.leftPos + 169, this.topPos + 153, 16, 16).build(builder -> new Button(builder) {
            @Override
            public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
            }
        });
        guistate.put("button:read_mode_button", read_mode_button);
        this.addRenderableWidget(read_mode_button);
        pre_page = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {
            if(screenVar.now_page > 0)
                screenVar.now_page -= 1;
        }).bounds(this.leftPos + 131, this.topPos + 153, 24, 15).build(builder -> new Button(builder) {
            @Override
            public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
            }
        });
        guistate.put("button:pre_page", pre_page);
        this.addRenderableWidget(pre_page);
        next_page = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {
            int temp_num = skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size();
            if(screenVar.now_page + 1 < temp_num / 24 + (temp_num % 24 == 0 ? 0 : 1))
                screenVar.now_page += 1;
        }).bounds(this.leftPos + 199, this.topPos + 153, 24, 15).build(builder -> new Button(builder) {
            @Override
            public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
            }
        });
        guistate.put("button:next_page", next_page);
        this.addRenderableWidget(next_page);
        equip_unequip_skills_buttons = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {
            BaseSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
            if(bsk != null)
                if(!bsk.getIslock(entity))
                {
                    entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                        if(list.containsSkill(bsk))
                        {
                            SoycheeseCore.channel.sendToServer(new CookbookSwitchSkillMessage(screenVar.now_skill,false,0,x,y,z));
                        }
                        else
                        {
                            SoycheeseCore.channel.sendToServer(new CookbookSwitchSkillMessage(screenVar.now_skill,true,0,x,y,z));
                        }
                    });
                }
        }).bounds(this.leftPos + 17, this.topPos + 150, 80, 15).build(builder -> new Button(builder) {
            @Override
            public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
                BaseSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
                if(bsk != null)
                    if(!bsk.getIslock(entity))
                        super.render(guiGraphics, gx, gy, ticks);
            }
        });
        guistate.put("button:equip_unequip_skills_buttons", equip_unequip_skills_buttons);
        this.addRenderableWidget(equip_unequip_skills_buttons);
    }
}

