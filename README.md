# HungerTweaker
HungerTweaker exposes the [AppleCore](https://github.com/squeek502/AppleCore) API to [CraftTweaker](https://github.com/CraftTweaker/CraftTweaker) scripts, and can be used to modify a variety of food and hunger-related properties.

HungerTweaker consists of two parts. Firstly, it includes a simplified wrapper around the AppleCore API. This can be used to set a variety of default values, as well as modify the [properties of food items](https://github.com/coolsquid/HungerTweaker/wiki/FoodValues). Secondly, it provides access to most of AppleCore's [events](https://github.com/coolsquid/HungerTweaker/wiki/HungerEvents). This can be used to dynamically modify and react to changes in a player's hunger, exhaustion, starvation, and regen.

The simplified wrapper is designed to be easy to use, and largely consists of static ZenMethods such as `mods.hungertweaker.Hunger.setMaxHunger(20)`. It has access to no other context than the previous value of the property, which can be used in [expressions](https://github.com/coolsquid/HungerTweaker/wiki/Expression), such as `mods.hungertweaker.Hunger.setMaxHunger("x/2")`.

The event system functions largely like CraftTweaker's own event system. Scripts can register event handler functions, which are executed whenever the event occurs and have access to context, such as the IPlayer in question. As such, the event system can be used to produce far more advanced results than the simplified wrapper.

HungerTweaker attempts to apply its changes after all other mods. The simplified options are handled before the events, and event handlers may override the default values set by the simplified options.

## Compatibility CT Methods

HungerTweaker also exposes optional CraftTweaker helpers for Nutrition, The Spice of Life, and Spice of Life: Carrot Edition. These methods only work when the corresponding mod is loaded; use `isLoaded()` if your scripts may run without that mod.

### Nutrition

Zen class: `mods.hungertweaker.Nutrition`

General nutrient methods:

```zenscript
mods.hungertweaker.Nutrition.isLoaded();
mods.hungertweaker.Nutrition.getNutrientNames();
mods.hungertweaker.Nutrition.getNutrients();
mods.hungertweaker.Nutrition.getNutrientInfo("protein");
mods.hungertweaker.Nutrition.getNutrientIcon("protein");
mods.hungertweaker.Nutrition.getNutrientColor("protein");
mods.hungertweaker.Nutrition.isNutrientVisible("protein");
mods.hungertweaker.Nutrition.getNutrientDecay("protein");
```

Player nutrient methods:

```zenscript
mods.hungertweaker.Nutrition.getNutrient(player, "protein");
mods.hungertweaker.Nutrition.setNutrient(player, "protein", 80.0);
mods.hungertweaker.Nutrition.addNutrient(player, "protein", 5.0);
mods.hungertweaker.Nutrition.resetNutrient(player, "protein");
mods.hungertweaker.Nutrition.getPlayerNutrition(player);
mods.hungertweaker.Nutrition.setPlayerNutrition(player, {"dairy": 60.0, "fruit": 70.0});
mods.hungertweaker.Nutrition.addPlayerNutrition(player, {"grain": 5.0, "vegetable": 5.0});
mods.hungertweaker.Nutrition.resetPlayerNutrition(player);
```

Five default nutrient shortcuts:

```zenscript
mods.hungertweaker.Nutrition.getDairy(player);
mods.hungertweaker.Nutrition.setDairy(player, 60.0);
mods.hungertweaker.Nutrition.addDairy(player, 5.0);
mods.hungertweaker.Nutrition.resetDairy(player);

mods.hungertweaker.Nutrition.getFruit(player);
mods.hungertweaker.Nutrition.setFruit(player, 60.0);
mods.hungertweaker.Nutrition.addFruit(player, 5.0);
mods.hungertweaker.Nutrition.resetFruit(player);

mods.hungertweaker.Nutrition.getGrain(player);
mods.hungertweaker.Nutrition.setGrain(player, 60.0);
mods.hungertweaker.Nutrition.addGrain(player, 5.0);
mods.hungertweaker.Nutrition.resetGrain(player);

mods.hungertweaker.Nutrition.getProtein(player);
mods.hungertweaker.Nutrition.setProtein(player, 60.0);
mods.hungertweaker.Nutrition.addProtein(player, 5.0);
mods.hungertweaker.Nutrition.resetProtein(player);

mods.hungertweaker.Nutrition.getVegetable(player);
mods.hungertweaker.Nutrition.setVegetable(player, 60.0);
mods.hungertweaker.Nutrition.addVegetable(player, 5.0);
mods.hungertweaker.Nutrition.resetVegetable(player);
```

Food methods:

```zenscript
mods.hungertweaker.Nutrition.isValidFood(<minecraft:apple>);
mods.hungertweaker.Nutrition.addFoodNutrition(player, <minecraft:apple>);
mods.hungertweaker.Nutrition.calculateFoodNutrition(<minecraft:apple>);
mods.hungertweaker.Nutrition.calculateFoodNutritionForPlayer(<minecraft:apple>, player);
mods.hungertweaker.Nutrition.foodContainsNutrient(<minecraft:apple>, "fruit");
mods.hungertweaker.Nutrition.getFoodNutrientScale(<minecraft:apple>, "fruit");
mods.hungertweaker.Nutrition.registerFoodItem("fruit", <minecraft:apple>);
mods.hungertweaker.Nutrition.registerFoodItem("fruit", <minecraft:apple>, 1.0);
mods.hungertweaker.Nutrition.registerFoodItem("fruit", <minecraft:apple>, 1.0, "META_SENSITIVE");
mods.hungertweaker.Nutrition.removeFoodItem("fruit", <minecraft:apple>);
```

`registerFoodItem` compare types are `DEFAULT`, `META_SENSITIVE`, `ONLY_NBT_SENSITIVE`, and `ALL_SENSITIVE`.

### The Spice of Life

Zen class: `mods.hungertweaker.SpiceOfLife`

```zenscript
mods.hungertweaker.SpiceOfLife.isLoaded();
mods.hungertweaker.SpiceOfLife.getFoodModifier(player, <minecraft:apple>);
mods.hungertweaker.SpiceOfLife.getFoodGroupModifier(player, <minecraft:apple>, "fruit");
mods.hungertweaker.SpiceOfLife.getModifiedFoodValues(4, 0.3, 0.5);
mods.hungertweaker.SpiceOfLife.getModifiedFoodValuesForPlayer(player, <minecraft:apple>);
mods.hungertweaker.SpiceOfLife.getFoodHistory(player);
mods.hungertweaker.SpiceOfLife.getHistoryFoods(player);
mods.hungertweaker.SpiceOfLife.getHistoryLength(player);
mods.hungertweaker.SpiceOfLife.getTotalFoodsEatenAllTime(player);
mods.hungertweaker.SpiceOfLife.getTicksActive(player);
mods.hungertweaker.SpiceOfLife.getLastEatenFood(player);
mods.hungertweaker.SpiceOfLife.getFoodCount(player, <minecraft:apple>);
mods.hungertweaker.SpiceOfLife.getFoodGroupCount(player, <minecraft:apple>, "fruit");
mods.hungertweaker.SpiceOfLife.containsFoodOrItsFoodGroups(player, <minecraft:apple>);
mods.hungertweaker.SpiceOfLife.getTotalFoodValues(player, <minecraft:apple>);
mods.hungertweaker.SpiceOfLife.getTotalFoodValuesForFoodGroup(player, <minecraft:apple>, "fruit");
mods.hungertweaker.SpiceOfLife.getFoodGroups();
mods.hungertweaker.SpiceOfLife.getFoodGroupsForFood(<minecraft:apple>);
mods.hungertweaker.SpiceOfLife.getFoodGroupInfo("fruit");
mods.hungertweaker.SpiceOfLife.getFoodGroupInfoForFood(<minecraft:apple>);
mods.hungertweaker.SpiceOfLife.isFoodBlacklisted(<minecraft:apple>);
mods.hungertweaker.SpiceOfLife.addFoodToHistory(player, <minecraft:apple>);
mods.hungertweaker.SpiceOfLife.addFoodToHistory(player, <minecraft:apple>, false);
mods.hungertweaker.SpiceOfLife.resetFoodHistory(player);
mods.hungertweaker.SpiceOfLife.validateFoodHistory(player);
mods.hungertweaker.SpiceOfLife.syncFoodHistory(player);
```

### Spice of Life: Carrot Edition

Zen class: `mods.hungertweaker.SpiceOfLifeCarrotEdition`

```zenscript
mods.hungertweaker.SpiceOfLifeCarrotEdition.isLoaded();
mods.hungertweaker.SpiceOfLifeCarrotEdition.getEatenFoodCount(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.getFoodsEatenForMilestones(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.hasEaten(player, <minecraft:apple>);
mods.hungertweaker.SpiceOfLifeCarrotEdition.shouldCount(player, <minecraft:apple>);
mods.hungertweaker.SpiceOfLifeCarrotEdition.isAllowed(player, <minecraft:apple>);
mods.hungertweaker.SpiceOfLifeCarrotEdition.isHearty(player, <minecraft:apple>);
mods.hungertweaker.SpiceOfLifeCarrotEdition.getMilestonesAchieved(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.getNextMilestone(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.getFoodsUntilNextMilestone(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.hasReachedMax(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.getHealthModifier(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.getProgress(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.getEatenFoods(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.addFood(player, <minecraft:apple>);
mods.hungertweaker.SpiceOfLifeCarrotEdition.clearFoods(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.updateProgressInfo(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.updateMaxHealth(player);
mods.hungertweaker.SpiceOfLifeCarrotEdition.syncFoodList(player);
```

### Compatibility Event Methods

Zen class: `mods.hungertweaker.events.HungerEvents`

```zenscript
mods.hungertweaker.events.HungerEvents.onNutritionFoodEaten(function(event as mods.hungertweaker.events.NutritionFoodEatenEvent) {
    print(event.foodNutrition);
    print(event.playerNutrition);
    print(event.protein);
    event.addNutrient("protein", 2.0);
});

mods.hungertweaker.events.HungerEvents.onSpiceOfLifeFoodEaten(function(event as mods.hungertweaker.events.SpiceOfLifeFoodEatenEvent) {
    print(event.modifier);
    print(event.history);
    print(event.foodHistory);
});

mods.hungertweaker.events.HungerEvents.onSpiceOfLifeCarrotFoodEaten(function(event as mods.hungertweaker.events.SpiceOfLifeCarrotFoodEatenEvent) {
    print(event.progress);
    print(event.foodProgress);
});
```

Carrot Edition event aliases:

```zenscript
mods.hungertweaker.events.HungerEvents.onSpiceOfLifeCarrotEditionFoodEaten(handler);
mods.hungertweaker.events.HungerEvents.onSOLCarrotFoodEaten(handler);
```

The normal `FoodEatenEvent` and `GetFoodValuesEvent` also expose these getters:

```zenscript
event.nutrition;
event.playerNutrition;
event.spiceOfLife;
event.spiceOfLifeModifier;
event.spiceOfLifeCarrot;
event.solCarrot;
```

For a more complete overview, visit the HungerTweaker wiki.
https://github.com/coolsquid/HungerTweaker/wiki/
