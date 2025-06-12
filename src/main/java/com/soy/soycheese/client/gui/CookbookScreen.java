package com.soy.soycheese.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.capability.skilllist.PlayerSkillList;
import com.soy.soycheese.capability.skilllist.PlayerSkillListProvider;
import com.soy.soycheese.communication.CookbookSwitchSkillMessage;
import com.soy.soycheese.inventory.CookbookMenu;
import com.soy.soycheese.registries.SkillRegistry;
import com.soy.soycheese.skill.AbstractSkill;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Integer;

public class CookbookScreen extends AbstractContainerScreen<CookbookMenu> {
    private final static HashMap<String, Object> guistate = CookbookMenu.guistate;
    private final Level world;
    private final int x, y, z;
    private final Player entity;
    private Button pre_page;
    private Button next_page;
    private final ArrayList<Button> skills_buttons = new ArrayList<>();
    private Button equip_unequip_skills_buttons;
    private final ArrayList<Button> skills_kind_buttons = new ArrayList<>();
    private final ArrayList<Button> read_mode_buttons = new ArrayList<>();
    private final ArrayList<ArrayList<ArrayList<ResourceLocation> > > skills = new ArrayList<>();
    private Button slider;
    class ScreenVar{
        int now_page;
        int now_kind;
        int now_read_mode;
        int choose_skill;
        ResourceLocation now_skill;
        int skill_des_length;
        int fix_des;
    }
    private final int view_length = 112;
    private final int slider_length = 132;
    private final ScreenVar screenVar = new ScreenVar();
    private int getskilldeslength(ResourceLocation now_skill)
    {
        AbstractSkill bsk = SkillRegistry.getSkill(now_skill);
        if(bsk != null)
            return 21 + 9 * this.font.split(bsk.getDescription(entity), 82).size();
        return 0;
    }
    public CookbookScreen(CookbookMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 283;//背景+右侧
        this.imageHeight = 245;//背景+底部
        for(int i = 0; i < 4; ++i)//四个技能分类
        {
            skills.add(new ArrayList<>());
            for(int j = 0; j < 3; ++j)//全部，已解锁，未解锁
                skills.get(i).add(new ArrayList<>());
        }
        //遍历所有技能
        SkillRegistry.getSkills().stream()
                //.map(RegistryObject::get)
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
        screenVar.skill_des_length = 0;
        screenVar.fix_des = 0;
    }

    private static final ResourceLocation newtexture = new ResourceLocation("soycheese_core:textures/gui/new_book_gui.png");
    private static final ResourceLocation element_texture = new ResourceLocation("soycheese_core:textures/gui/book_element.png");

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    @Override
    public boolean mouseDragged(double p_94699_, double p_94700_, int p_94701_, double p_94702_, double p_94703_) {
        return this.getFocused() != null && this.isDragging() && p_94701_ == 0 ? this.getFocused().mouseDragged(p_94699_, p_94700_, p_94701_, p_94702_, p_94703_) : false;
    }
    //渲染
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        //底层背景
        guiGraphics.blit(newtexture, this.leftPos, this.topPos, 0, 0, 256, 223, 256, 256);
        //已选择技能图标
        if(screenVar.now_skill != null && SkillRegistry.getSkill(screenVar.now_skill) != null) {
            AbstractSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
            guiGraphics.enableScissor(this.leftPos + 25, this.topPos + 60, this.leftPos+109, this.topPos+171);
            guiGraphics.blit(element_texture, this.leftPos + 57, this.topPos + 60 - screenVar.fix_des, 1, 1, 20, 20, 256, 256);
            if (bsk != null) {
                if (bsk.getIslock(entity))
                    guiGraphics.blit(element_texture, this.leftPos + 58, this.topPos + 61 - screenVar.fix_des, 46, 2, 18, 18, 256, 256);
                else
                    guiGraphics.blit(bsk.getSkillIconResource(), this.leftPos + 58, this.topPos + 61 - screenVar.fix_des, 0, 0, 18, 18, 18, 18);
            }
            guiGraphics.disableScissor();
            //渲染拖拉条
            if(screenVar.skill_des_length > view_length)
            {
                int gup = (int)(((float)screenVar.fix_des)/((float)screenVar.skill_des_length)*slider_length);
                int sli_length = (int)(((float)view_length)/((float)screenVar.skill_des_length)*slider_length);
                guiGraphics.blit(element_texture, this.leftPos + 108, this.topPos + 58, 176, 0, 3, 132, 256, 256);
                guiGraphics.blitNineSliced(element_texture,this.leftPos + 108, this.topPos + 58 + gup,3,sli_length,1,3,132,198,0);
            }
        }
        //输出右边技能图鉴
        for(int i = 0; i < 28 ; ++i) {
            int num = screenVar.now_page * 28 + i;
            if(num >= skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size())break;
            AbstractSkill bsk = SkillRegistry.getSkill(skills.get(screenVar.now_kind).get(screenVar.now_read_mode).get(num));
            //还可以额外加参数把图片缩小
            guiGraphics.blit(element_texture, this.leftPos + 146 + (i % 4) * 22, this.topPos + 30 + (i / 4) * 23, 1, 1, 20, 20, 256, 256);
            if(screenVar.choose_skill == i)
                guiGraphics.blit(element_texture, this.leftPos + 145 + (i % 4) * 22, this.topPos + 29 + (i / 4) * 23, 22, 0, 22, 22, 256, 256);
            if(bsk != null) {
                if (bsk.getIslock(entity))
                    guiGraphics.blit(element_texture, this.leftPos + 147 + (i % 4) * 22, this.topPos + 31 + (i / 4) * 23, 46, 2, 18, 18, 256, 256);
                else
                    guiGraphics.blit(bsk.getSkillIconResource(), this.leftPos + 147 + (i % 4) * 22, this.topPos + 31 + (i / 4) * 23, 0, 0, 18, 18, 18, 18);
            }
        }
        //输出玩家技能
        entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
            for(int i = 0; i < 4; ++i) {
                AbstractSkill bsk = SkillRegistry.getSkill(list.getSkilllist().get(i));
                guiGraphics.blit(element_texture, this.leftPos+ 24 + i * 22, this.topPos+30, 1, 1, 20, 20, 256, 256);
                if(screenVar.choose_skill >= 28 && screenVar.choose_skill == i + 28)
                    guiGraphics.blit(element_texture, this.leftPos + 23 + i * 22, this.topPos + 29, 22, 0, 22, 22, 256, 256);
                if (bsk != null) {
                    guiGraphics.blit(bsk.getSkillIconResource(), this.leftPos + 25 + i * 22, this.topPos + 31, 0, 0, 18, 18, 18, 18);
                }
                else {
                    guiGraphics.blit(element_texture, this.leftPos+25 + i * 22, this.topPos+31, 2 + i * 22, 24, 18, 18, 256, 256);
                }
            }
        });
        //左翻页按钮
        if(screenVar.now_page > 0)
            if(gx>=this.leftPos+138&&gx<=this.leftPos+151&&gy>=this.topPos+197&&gy<=this.topPos+203)
                guiGraphics.blit(element_texture, this.leftPos+138, this.topPos+197, 66, 66, 14, 7, 256, 256);
            else
                guiGraphics.blit(element_texture, this.leftPos+138, this.topPos+197, 46, 66, 14, 7, 256, 256);
        //右翻页按钮
        if(screenVar.now_page + 1 < skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size() / 28 + (skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size() % 28 == 0 ? 0 : 1))
            if(gx>=this.leftPos+199&&gx<=this.leftPos+222&&gy>=this.topPos+153&&gy<=this.topPos+167)
                guiGraphics.blit(element_texture, this.leftPos+199, this.topPos+153, 66, 44, 14, 7, 256, 256);
            else
                guiGraphics.blit(element_texture, this.leftPos+199, this.topPos+153, 46, 44, 14, 7, 256, 256);
        //屏幕右侧阅读模式分类
        for(int i = 0; i < 2; ++i)
        {
            if((screenVar.now_read_mode == i + 1)||(gx>=this.leftPos + 246 &&gx<=this.leftPos + 282 &&gy>=this.topPos + 6 + i * 29 &&gy<=this.topPos + 28 + i * 29)) {
                guiGraphics.blit(element_texture, this.leftPos+246, this.topPos+ 6 + i * 29, 88+ i*44, 0, 44, 23, 256, 256);
            }
            else {
                guiGraphics.blit(element_texture, this.leftPos+246, this.topPos+ 6 + i * 29, 88 + i*44, 23, 44, 23, 256, 256);
            }
        }
        //屏幕底下四个分类按钮
        for(int i = 0;i < 4 ; ++i)
        {
            if(screenVar.now_kind == i || (gx>=this.leftPos+139 + i*27 &&gx<=this.leftPos+161 + i*27 &&gy>=this.topPos+209&&gy<=this.topPos+244))
            {
                if(i == 0) {
                    guiGraphics.blit(element_texture, this.leftPos+139+i*27, this.topPos+209, 0, 176, 23, 44, 256, 256);
                } else if (i == 1) {
                    guiGraphics.blit(element_texture, this.leftPos+139+i*27, this.topPos+209, 0, 88, 23, 44, 256, 256);
                } else if (i == 2) {
                    guiGraphics.blit(element_texture, this.leftPos+139+i*27, this.topPos+209, 0, 44, 23, 44, 256, 256);
                } else {
                    guiGraphics.blit(element_texture, this.leftPos+139+i*27, this.topPos+209, 0, 132, 23, 44, 256, 256);
                }
            }
            else
            {
                if(i == 0) {
                    guiGraphics.blit(element_texture, this.leftPos+139+i*27, this.topPos+209, 23, 176, 23, 44, 256, 256);
                } else if (i == 1) {
                    guiGraphics.blit(element_texture, this.leftPos+139+i*27, this.topPos+209, 23, 88, 23, 44, 256, 256);
                } else if (i == 2) {
                    guiGraphics.blit(element_texture, this.leftPos+139+i*27, this.topPos+209, 23, 44, 23, 44, 256, 256);
                } else {
                    guiGraphics.blit(element_texture, this.leftPos+139+i*27, this.topPos+209, 23, 132, 23, 44, 256, 256);
                }
            }
        }
        //装备按钮
        if(screenVar.now_skill != null) {
            AbstractSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
            if (bsk != null) {
                if(!bsk.getIslock(entity)) {
                    if(gx >= this.leftPos + 25 && gy >= this.topPos+175 && gx <= this.leftPos + 97 && gy <= this.topPos+197)
                    {
                        guiGraphics.blit(element_texture, this.leftPos+24, this.topPos+174, 88, 64, 75, 18, 256, 256);
                    }
                    else
                    {
                        guiGraphics.blit(element_texture, this.leftPos+24, this.topPos+174, 88, 46, 75, 18, 256, 256);
                    }
                }
            }
        }
        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        if(screenVar.skill_des_length > view_length)
        {
            if(key == 264)
            {
                screenVar.fix_des = Math.min(screenVar.fix_des + 6,screenVar.skill_des_length - view_length);
            }
            else if(key == 265)
            {
                screenVar.fix_des = Math.max(screenVar.fix_des - 6,0);
            }
        }
        return super.keyPressed(key, b, c);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double Scroll) {
        int gx = (int)mouseX;
        int gy = (int)mouseY;
        int gz = (int)Scroll;
        //SoycheeseCore.LOGGER.info("mouseScrolled: {} {} {}", mouseX, mouseY, Scroll);
        //鼠标滚轮翻页
        if(gx >= this.leftPos + 144 && gy >= this.topPos+27 && gx <= this.leftPos + 233 && gy <= this.topPos+190 )
        {
            if(gz > 0) {
                int temp_num = skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size();
                if(screenVar.now_page + 1 < temp_num / 24 + (temp_num % 24 == 0 ? 0 : 1))
                {
                    screenVar.now_page += 1;
                    screenVar.choose_skill = -1;
                }
            } else if (gz < 0) {
                if(screenVar.now_page > 0)
                {
                    screenVar.now_page -= 1;
                    screenVar.choose_skill = -1;
                }
            }
        }
        //鼠标滚轮描述
        if(screenVar.skill_des_length > view_length && gx >= this.leftPos + 22 && gy >= this.topPos+57 && gx <= this.leftPos + 111 && gy <= this.topPos+190 ) {
            screenVar.fix_des -= 4 * gz;
            screenVar.fix_des = Math.max(screenVar.fix_des, 0);
            screenVar.fix_des = Math.min(screenVar.fix_des, screenVar.skill_des_length - view_length);
        }
        return super.mouseScrolled(mouseX, mouseY, Scroll);
    }
    @Override
    public void containerTick() {
        super.containerTick();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(screenVar.now_skill != null)
        {
            AbstractSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
            if(bsk != null)
            {
                guiGraphics.enableScissor(this.leftPos + 25, this.topPos + 60, this.leftPos+109, this.topPos+171);
                //float a = 1f;
                //guiGraphics.pose().pushPose();
                //guiGraphics.pose().scale(a, a, 1.0f); // 0.8 倍大小
                //输出描述————未来可能添加修改字体大小
                guiGraphics.drawWordWrap(this.font,bsk.getDescription(entity),25,60+21 - screenVar.fix_des,82,0x0);
                //guiGraphics.pose().popPose();
                guiGraphics.disableScissor();
                if(!bsk.getIslock(entity)) {
                    float a = 1.5f;
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().scale(a, a, 1.0f); // 1.5 倍大小
                    entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                        if (list.containsSkill(bsk)) {
                            guiGraphics.drawString(this.font, Component.translatable("gui.soycheese_core.cookbook.unequip"), (int)((25+37-a*this.font.width(Component.translatable("gui.soycheese_core.cookbook.unequip").getVisualOrderText()) / 2.0)/a), (int)(177/a), 0xFFFFFFFF, false);
                        } else {
                            guiGraphics.drawString(this.font, Component.translatable("gui.soycheese_core.cookbook.equip"), (int)((25+37-a*this.font.width(Component.translatable("gui.soycheese_core.cookbook.equip").getVisualOrderText()) / 2.0)/a), (int)(177/a), 0xFFFFFFFF, false);
                        }
                    });
                    guiGraphics.pose().popPose();
                }
            }
        }
    }

    @Override
    public void init() {
        super.init();
        //变量初始化
        guistate.put("screenvar:screenvar", screenVar);
        //右侧的技能选择
        for(int i = 0 ; i < 28; ++i)
        {
            int index = i;
            Button Frame = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {
                int num = screenVar.now_page * 28 + index;
                if(num < skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size())
                {
                    if(hasShiftDown())
                    {
                        screenVar.choose_skill = index;
                        screenVar.now_skill = skills.get(screenVar.now_kind).get(screenVar.now_read_mode).get(num);
                        screenVar.skill_des_length = getskilldeslength(screenVar.now_skill);
                        screenVar.fix_des = 0;
                        AbstractSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
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
                    else
                    {
                        if (screenVar.choose_skill != index) {
                            screenVar.choose_skill = index;
                            screenVar.now_skill = skills.get(screenVar.now_kind).get(screenVar.now_read_mode).get(num);
                            screenVar.skill_des_length = getskilldeslength(screenVar.now_skill);
                            screenVar.fix_des = 0;
                        } else {
                            screenVar.choose_skill = -1;
                            screenVar.now_skill = null;
                            screenVar.skill_des_length = 0;
                            screenVar.fix_des = 0;
                        }
                    }
                }
            }).bounds(this.leftPos + 146+ (i%4)*22, this.topPos + 30+ (i/4)*23, 20, 20).build(builder -> new Button(builder) {
                @Override
                public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
                }
                @Override
                public boolean keyPressed(int p_93374_, int p_93375_, int p_93376_)
                {
                    int num = screenVar.now_page * 28 + index;
                    if(num < skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size())
                        return super.keyPressed(p_93374_, p_93375_, p_93376_);
                    return false;
                }
                @Override
                protected boolean clicked(double p_93681_, double p_93682_)
                {
                    int num = screenVar.now_page * 28 + index;
                    boolean ret = super.clicked(p_93681_, p_93682_);
                    if(ret)
                        return num < skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size();
                    return false;
                }
            });
            skills_buttons.add(Frame);
            guistate.put("button:button_"+Integer.toString(index), Frame);
            this.addRenderableWidget(Frame);
        }
        //左侧已装备技能选择
        for(int i = 0 ; i < 4; ++i)
        {
            int index = i;
            Button Frame = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {
                boolean can_click = false;
                PlayerSkillList plist = entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).orElse(null);
                if(plist != null)
                {
                    ResourceLocation presourceLocation = plist.getSkilllist().get(index);
                    if (!(presourceLocation == null||presourceLocation.equals(PlayerSkillList.noneSkill)))
                        can_click = true;
                }
                if(can_click)
                {
                    if(hasShiftDown()) {
                        entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                            screenVar.now_skill = list.getSkilllist().get(index);
                            screenVar.skill_des_length = getskilldeslength(screenVar.now_skill);
                            screenVar.fix_des = 0;
                        });
                        AbstractSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
                        entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                            SoycheeseCore.channel.sendToServer(new CookbookSwitchSkillMessage(screenVar.now_skill,false,0,x,y,z));
                            screenVar.choose_skill = -1;
                        });
                    }
                    else {
                        if (screenVar.choose_skill != index + 28) {
                            screenVar.choose_skill = index + 28;
                            entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                                screenVar.now_skill = list.getSkilllist().get(index);
                                screenVar.skill_des_length = getskilldeslength(screenVar.now_skill);
                                screenVar.fix_des = 0;
                            });
                        } else {
                            screenVar.choose_skill = -1;
                            screenVar.now_skill = null;
                            screenVar.skill_des_length = 0;
                            screenVar.fix_des = 0;
                        }
                    }
                }
            }).bounds(this.leftPos + 24 + i * 22, this.topPos + 30, 20, 20).build(builder -> new Button(builder) {
                @Override
                public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
                }
                @Override
                public boolean keyPressed(int p_93374_, int p_93375_, int p_93376_)
                {
                    boolean can_click = false;
                    PlayerSkillList plist = entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).orElse(null);
                    if(plist != null)
                    {
                        ResourceLocation presourceLocation = plist.getSkilllist().get(index);
                        if (!(presourceLocation == null||presourceLocation.equals(PlayerSkillList.noneSkill)))
                            can_click = true;
                    }
                    if(can_click)
                        return super.keyPressed(p_93374_, p_93375_, p_93376_);
                    return false;
                }
                @Override
                protected boolean clicked(double p_93681_, double p_93682_)
                {
                    boolean ret = super.clicked(p_93681_, p_93682_);
                    if(ret)
                    {
                        boolean can_click = false;
                        PlayerSkillList plist = entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).orElse(null);
                        if(plist != null)
                        {
                            ResourceLocation presourceLocation = plist.getSkilllist().get(index);
                            if (!(presourceLocation == null||presourceLocation.equals(PlayerSkillList.noneSkill)))
                                can_click = true;
                        }
                        return can_click;
                    }
                    return false;
                }
            });
            skills_buttons.add(Frame);
            guistate.put("button:button_"+Integer.toString(index + 28), Frame);
            this.addRenderableWidget(Frame);
        }
        //技能分类
        for(int i = 0 ; i < 4; ++i)
        {
            int index = i;
            Button tembutton = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {
                if(screenVar.now_kind != index)
                {
                    screenVar.now_kind = index;
                    screenVar.now_page = 0;
                    if(screenVar.choose_skill < 24)
                        screenVar.choose_skill = -1;
                }
            }).bounds(this.leftPos + 139 + i * 27, this.topPos + 209, 23, 36).build(builder -> new Button(builder) {
                @Override
                public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
                }
            });
            skills_kind_buttons.add(tembutton);
            guistate.put("button:kindbutton_" + Integer.toString(i), tembutton);
            this.addRenderableWidget(tembutton);
        }
        //阅读模式
        for (int i = 0 ; i < 2; ++i)
        {
            int index = i + 1;
            Button tembutton = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {
                if(screenVar.now_read_mode != index)
                    screenVar.now_read_mode = index;
                else
                    screenVar.now_read_mode = 0;
                screenVar.now_page = 0;
                if(screenVar.choose_skill < 24)
                    screenVar.choose_skill = -1;
            }).bounds(this.leftPos + 246, this.topPos + 6 + 29*i, 37, 23).build(builder -> new Button(builder) {
                @Override
                public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
                }
            });
            read_mode_buttons.add(tembutton);
            guistate.put("button:readmodbutton_" + Integer.toString(i), tembutton);
            this.addRenderableWidget(tembutton);
        }
        //关闭按钮————已废弃
        /*
        close_button = new ImageButton(this.leftPos + -7, this.topPos + -7, 15, 15, 48, 191, 15, texture, 256, 256, e -> {
            entity.closeContainer();
        });
        guistate.put("button:close_button", close_button);
        this.addRenderableWidget(close_button);
        */
        //左翻页
        pre_page = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {
            if(screenVar.now_page > 0)
            {
                screenVar.now_page -= 1;
                screenVar.choose_skill = -1;
            }
        }).bounds(this.leftPos + 138, this.topPos + 197, 14, 7).build(builder -> new Button(builder) {
            @Override
            public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
            }
            @Override
            public boolean keyPressed(int p_93374_, int p_93375_, int p_93376_)
            {
                if(screenVar.now_page > 0)
                    return super.keyPressed(p_93374_, p_93375_, p_93376_);
                return false;
            }
            @Override
            protected boolean clicked(double p_93681_, double p_93682_)
            {
                boolean ret = super.clicked(p_93681_, p_93682_);
                if(ret)
                    return screenVar.now_page > 0;
                return false;
            }
        });
        guistate.put("button:pre_page", pre_page);
        this.addRenderableWidget(pre_page);
        //右翻页
        next_page = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {
            int temp_num = skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size();
            if(screenVar.now_page + 1 < temp_num / 28 + (temp_num % 28 == 0 ? 0 : 1))
            {
                screenVar.now_page += 1;
                screenVar.choose_skill = -1;
            }
        }).bounds(this.leftPos + 228, this.topPos + 197, 14, 7).build(builder -> new Button(builder) {
            @Override
            public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
            }
            @Override
            public boolean keyPressed(int p_93374_, int p_93375_, int p_93376_)
            {
                int temp_num = skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size();
                if(screenVar.now_page + 1 < temp_num / 28 + (temp_num % 28 == 0 ? 0 : 1))
                    return super.keyPressed(p_93374_, p_93375_, p_93376_);
                return false;
            }
            @Override
            protected boolean clicked(double p_93681_, double p_93682_)
            {
                boolean ret = super.clicked(p_93681_, p_93682_);
                if(ret)
                {
                    int temp_num = skills.get(screenVar.now_kind).get(screenVar.now_read_mode).size();
                    return screenVar.now_page + 1 < temp_num / 28 + (temp_num % 28 == 0 ? 0 : 1);
                }
                return false;
            }
        });
        guistate.put("button:next_page", next_page);
        this.addRenderableWidget(next_page);

        //装备/卸下按钮
        equip_unequip_skills_buttons = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {
            AbstractSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
            if(bsk != null) {
                if (!bsk.getIslock(entity)) {
                    entity.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).ifPresent(list -> {
                        if (list.containsSkill(bsk)) {
                            SoycheeseCore.channel.sendToServer(new CookbookSwitchSkillMessage(screenVar.now_skill, false, 0, x, y, z));
                            if (screenVar.choose_skill >= 28)
                                screenVar.choose_skill = -1;
                        } else {
                            SoycheeseCore.channel.sendToServer(new CookbookSwitchSkillMessage(screenVar.now_skill, true, 0, x, y, z));
                        }
                    });
                }
            }
        }).bounds(this.leftPos + 25, this.topPos + 175, 73, 16).build(builder -> new Button(builder) {
            @Override
            public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
            }
            @Override
            public boolean keyPressed(int p_93374_, int p_93375_, int p_93376_)
            {
                AbstractSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
                if(bsk != null) {
                    if (!bsk.getIslock(entity)){
                        return super.keyPressed(p_93374_, p_93375_, p_93376_);
                    }
                }
                return false;
            }
            @Override
            protected boolean clicked(double p_93681_, double p_93682_)
            {
                boolean ret = super.clicked(p_93681_, p_93682_);
                if(ret)
                {
                    AbstractSkill bsk = SkillRegistry.getSkill(screenVar.now_skill);
                    if(bsk != null) {
                        return !bsk.getIslock(entity);
                    }
                }
                return false;
            }
        });
        guistate.put("button:equip_unequip_skills_buttons", equip_unequip_skills_buttons);
        this.addRenderableWidget(equip_unequip_skills_buttons);
        //滑条
        int temtoppos = this.topPos;
        slider = Button.builder(Component.translatable("gui.soycheese_core.cookbook.notext_button"), e -> {}).bounds(this.leftPos + 108, this.topPos + 58, 3, 132).build(builder -> new Button(builder) {
            @Override
            public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
            }
            @Override
            protected void onDrag(double p_93636_, double p_93637_, double p_93638_, double p_93639_)
            {
                if(screenVar.skill_des_length > view_length) {
                    if(p_93637_>= 189)
                    {
                        screenVar.fix_des = screenVar.skill_des_length - view_length;
                    }
                    else if(p_93637_<= 58)
                    {
                        screenVar.fix_des = 0;
                    }
                    else
                    {
                        int gup = (int)(((float)screenVar.fix_des)/((float)screenVar.skill_des_length)*slider_length);
                        int sli_length = (int)(((float)view_length)/((float)screenVar.skill_des_length)*slider_length);
                        double minup = 0;
                        double maxup = 132 - sli_length;
                        double nup = gup + p_93639_;
                        nup = Math.max(nup,minup);
                        nup = Math.min(nup,maxup);
                        screenVar.fix_des = (int)(((float)nup)*((float)screenVar.skill_des_length)/slider_length);
                    }
                }
            }
            @Override
            public void onClick(double p_93371_, double p_93372_)
            {
                if(screenVar.skill_des_length > view_length)
                {
                    int gup = (int)(((float)screenVar.fix_des)/((float)screenVar.skill_des_length)*slider_length);
                    int sli_length = (int)(((float)view_length)/((float)screenVar.skill_des_length)*slider_length);
                    int gdown = gup + sli_length;
                    int minup = 0;
                    int maxup = 132 - sli_length;
                    if(p_93372_< temtoppos + gup || p_93372_ > temtoppos + gdown)
                    {
                        double nup =(p_93372_ - (double) sli_length /2) - 58;
                        nup = Math.max(nup,minup);
                        nup = Math.min(nup,maxup);
                        screenVar.fix_des = (int)(((float)nup)*((float)screenVar.skill_des_length)/slider_length);
                    }
                }
            }
            @Override
            public boolean keyPressed(int p_93374_, int p_93375_, int p_93376_)
            {
                return false;
            }
            @Override
            protected boolean clicked(double p_93681_, double p_93682_)
            {
                boolean ret = super.clicked(p_93681_, p_93682_);
                if(ret)
                    return screenVar.skill_des_length > view_length;
                return false;
            }
        });
        guistate.put("button:slider", slider);
        this.addRenderableWidget(slider);
    }
}

