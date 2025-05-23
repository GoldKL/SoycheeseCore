package com.soy.soycheese;

import com.soy.soycheese.client.screen.tutorial.TutorialScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class SoycheeeCoreClient {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(SoycheeeCoreClient::InputEvent$MouseButton$Post);
    }

    public static void InputEvent$MouseButton$Post(InputEvent.MouseButton.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || event.getAction() != 1 || event.getButton() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return;
        if (!minecraft.player.isCrouching()) return;
        minecraft.setScreen(new TutorialScreen());
    }
}
