package com.soy.soycheese.tracking;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.soy.soycheese.SoycheeseCore;
import com.soy.soycheese.registries.SkillRegistry;
import com.soy.soycheese.skill.BaseSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.io.InputStreamReader;

@Mod.EventBusSubscriber(modid = SoycheeseCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SkillArgumentLoader {
    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<JsonObject>() {
            @Override
            protected JsonObject prepare(ResourceManager manager, ProfilerFiller profiler) {
                // 异步加载 JSON 文件（在后台线程执行）
                return loadConfig(manager);
            }

            @Override
            protected void apply(JsonObject config, ResourceManager manager, ProfilerFiller profiler) {
                // 在主线程应用配置
                applyConfig(config);
            }
        });
    }
    private static JsonObject loadConfig(ResourceManager manager) {
        // 从 data/yourmodid/skills/ 读取 JSON
        JsonObject res = new JsonObject();
        for (BaseSkill skill : SkillRegistry.REGISTRY.get())
        {
            ResourceLocation skillid = skill.getorCreateSkillid();
            ResourceLocation configPath = new ResourceLocation(skillid.getNamespace(), SoycheeseCore.MODID + "/skills/"+skillid.getPath()+".json");
            try {
                if (manager.getResource(configPath).isPresent()) {
                    try (InputStreamReader reader = new InputStreamReader(manager.getResource(configPath).get().open())) {
                        res.add(skillid.toString(), JsonParser.parseReader(reader).getAsJsonObject());
                    }
                }
            } catch (IOException e) {
                SoycheeseCore.LOGGER.error("Couldn't load {} skill argument",skillid);
            }
        }
        return res; // 默认空配置
    }

    private static void applyConfig(JsonObject config) {
        // 将配置应用到模组逻辑
        for (BaseSkill skill : SkillRegistry.REGISTRY.get())
        {
            try {
                skill.readjson(config.get(skill.getorCreateSkillid().toString()).getAsJsonObject());
            }catch (Exception e) {
                SoycheeseCore.LOGGER.error("Couldn't apply {} skill argument",skill.getorCreateSkillid());
            }
        }
    }
}
