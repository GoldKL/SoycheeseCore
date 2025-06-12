// priority: 0

// Visit the wiki for more info - https://kubejs.com/

console.info('Hello, World! (Loaded startup scripts)')
const $PlayerFoodListProviderkey = Java.loadClass("com.soy.soycheese.capability.foodlist.PlayerFoodListProvider")
const $PlayerFoodListkey = Java.loadClass("com.soy.soycheese.capability.foodlist.PlayerFoodList")

//const $KubejsSkillkey = Java.loadClass("com.soy.soycheese.skill.KubejsSkill")
StartupEvents.registry("soycheese_core:skills",event =>{
    event.create("enchanted_golden_apple4","basic")
        .type(0)
        .getIslock((kubejsSkill, player)=>{
            //console.info($PlayerFoodListProviderkey.PLAYER_FOOD_LIST_CAPABILITY)
            let playerFoodList = player.getCapability($PlayerFoodListProviderkey.PLAYER_FOOD_LIST_CAPABILITY,null).orElse(null);
            if (playerFoodList != null) {
                return !playerFoodList["containsFood(net.minecraft.world.item.ItemStack)"](Item.of("minecraft:enchanted_golden_apple"))
            }
            return false;
        })
        .onTick((kubejsSkill, player) => {
            //if(player.level.isClientSide)return;
            if(player.getLevel().isClientSide())return;

            if(player.age % 50 === 0)
            {
                if(player.getAbsorptionAmount() < 20)
                {
                    player.setAbsorptionAmount(Math.min(20,player.getAbsorptionAmount() +1));
                }
            }
        })
})
