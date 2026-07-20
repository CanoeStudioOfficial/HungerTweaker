# HungerTweaker
HungerTweaker exposes the [AppleCore](https://github.com/squeek502/AppleCore) API to [CraftTweaker](https://github.com/CraftTweaker/CraftTweaker) scripts, and can be used to modify a variety of food and hunger-related properties.

HungerTweaker consists of two parts. Firstly, it includes a simplified wrapper around the AppleCore API. This can be used to set a variety of default values, as well as modify the [properties of food items](https://github.com/coolsquid/HungerTweaker/wiki/FoodValues). Secondly, it provides access to most of AppleCore's [events](https://github.com/coolsquid/HungerTweaker/wiki/HungerEvents). This can be used to dynamically modify and react to changes in a player's hunger, exhaustion, starvation, and regen.

The simplified wrapper is designed to be easy to use, and largely consists of static ZenMethods such as `mods.hungertweaker.Hunger.setMaxHunger(20)`. It has access to no other context than the previous value of the property, which can be used in [expressions](https://github.com/coolsquid/HungerTweaker/wiki/Expression), such as `mods.hungertweaker.Hunger.setMaxHunger("x/2")`.

The event system functions largely like CraftTweaker's own event system. Scripts can register event handler functions, which are executed whenever the event occurs and have access to context, such as the IPlayer in question. As such, the event system can be used to produce far more advanced results than the simplified wrapper.

HungerTweaker attempts to apply its changes after all other mods. The simplified options are handled before the events, and event handlers may override the default values set by the simplified options.

## Compatibility CT API Reference

HungerTweaker also exposes optional CraftTweaker helpers for Nutrition, The Spice of Life, and Spice of Life: Carrot Edition. Except for `isLoaded()`, these methods require the corresponding mod to be loaded and will throw if it is missing.

Common parameter types:

| Parameter | CT type | Meaning |
| --- | --- | --- |
| `player` | `crafttweaker.player.IPlayer` | Target player. In HungerTweaker events, use `event.player`. |
| `food` | `crafttweaker.item.IItemStack` | Food item stack, such as `<minecraft:apple>`. |
| `nutrientName` | `string` | Nutrition nutrient id. Defaults are `dairy`, `fruit`, `grain`, `protein`, and `vegetable`; custom Nutrition configs may add more. Names are treated case-insensitively. |
| `nutrients` | `IData` map | Map from nutrient id to number, for example `{"protein": 80.0, "grain": 45.0}`. |
| `value` | `float` | Absolute nutrient value. Nutrition clamps values to its normal 0-100 range. |
| `amount` | `float` | Relative nutrient change. Positive adds, negative subtracts. |
| `foodGroup` | `string` | The Spice of Life food group identifier from its food group config. |
| `modifier` | `float` | The Spice of Life nutritional value multiplier; `1.0` means unchanged, `0.5` means 50%. |

Returned `IData` maps use plain string keys. Food value maps have `hunger`, `saturationModifier`, and `saturationIncrement`. Item maps have `empty`, `commandString`, `displayName`, `amount`, and `metadata`.

### Nutrition

Zen class: `mods.hungertweaker.Nutrition`

Nutrient metadata:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `isLoaded()` | none | `bool` | Whether Nutrition is loaded. |
| `getNutrientNames()` | none | `string[]` | All loaded Nutrition nutrient ids. |
| `getNutrients()` | none | `IData` map | Map of `nutrientName -> nutrientInfo`. |
| `getNutrientInfo(nutrientName)` | `string nutrientName` | `IData` map | Info for one nutrient: `name`, `color`, `icon`, `decay`, `visible`, `oreDict`, `foodItemCount`. |
| `getNutrientIcon(nutrientName)` | `string nutrientName` | `IItemStack` | GUI icon stack for that nutrient. |
| `getNutrientColor(nutrientName)` | `string nutrientName` | `int` | Packed ARGB color used by Nutrition. |
| `isNutrientVisible(nutrientName)` | `string nutrientName` | `bool` | Whether Nutrition marks the nutrient visible in its GUI. |
| `getNutrientDecay(nutrientName)` | `string nutrientName` | `float` | Per-nutrient decay value from Nutrition config data. |

Player nutrient values:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `getNutrient(player, nutrientName)` | `IPlayer player`, `string nutrientName` | `float` | Current value of one player nutrient. |
| `setNutrient(player, nutrientName, value)` | `IPlayer player`, `string nutrientName`, `float value` | `void` | Sets one player nutrient and syncs to client on server. |
| `addNutrient(player, nutrientName, amount)` | `IPlayer player`, `string nutrientName`, `float amount` | `void` | Adds to one player nutrient and syncs to client on server. |
| `resetNutrient(player, nutrientName)` | `IPlayer player`, `string nutrientName` | `void` | Resets one player nutrient to Nutrition's default. |
| `getPlayerNutrition(player)` | `IPlayer player` | `IData` map | Current player nutrition, such as `{"dairy": 50.0, "fruit": 75.0}`. |
| `setPlayerNutrition(player, nutrients)` | `IPlayer player`, `IData nutrients` | `void` | Sets every nutrient listed in the map; omitted nutrients are unchanged. |
| `addPlayerNutrition(player, nutrients)` | `IPlayer player`, `IData nutrients` | `void` | Adds each mapped amount; omitted nutrients are unchanged. |
| `resetPlayerNutrition(player)` | `IPlayer player` | `void` | Resets every loaded nutrient. |

Default nutrient shortcuts:

| Nutrient | Getter | Setter | Add | Reset |
| --- | --- | --- | --- | --- |
| Dairy | `getDairy(player)` | `setDairy(player, value)` | `addDairy(player, amount)` | `resetDairy(player)` |
| Fruit | `getFruit(player)` | `setFruit(player, value)` | `addFruit(player, amount)` | `resetFruit(player)` |
| Grain | `getGrain(player)` | `setGrain(player, value)` | `addGrain(player, amount)` | `resetGrain(player)` |
| Protein | `getProtein(player)` | `setProtein(player, value)` | `addProtein(player, amount)` | `resetProtein(player)` |
| Vegetable | `getVegetable(player)` | `setVegetable(player, value)` | `addVegetable(player, amount)` | `resetVegetable(player)` |

Food nutrition:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `isValidFood(food)` | `IItemStack food` | `bool` | Whether Nutrition considers the item a valid food source. |
| `addFoodNutrition(player, food)` | `IPlayer player`, `IItemStack food` | `bool` | Calculates the food's Nutrition gain and applies it to the player. Returns whether anything was applied. |
| `calculateFoodNutrition(food)` | `IItemStack food` | `IData` map | Nutrient gain for the food without player context. |
| `calculateFoodNutritionForPlayer(food, player)` | `IItemStack food`, `IPlayer player` | `IData` map | Nutrient gain using player-sensitive Nutrition adapters. |
| `foodContainsNutrient(food, nutrientName)` | `IItemStack food`, `string nutrientName` | `bool` | Whether the food is listed under that nutrient. |
| `getFoodNutrientScale(food, nutrientName)` | `IItemStack food`, `string nutrientName` | `float` | Nutrition scale for this food under the nutrient; `0` means not matched. |
| `registerFoodItem(nutrientName, food)` | `string nutrientName`, `IItemStack food` | `void` | Registers the food under a nutrient with scale `1.0` and `META_SENSITIVE` matching. |
| `registerFoodItem(nutrientName, food, scale)` | `string nutrientName`, `IItemStack food`, `float scale` | `void` | Registers the food with custom nutrient scale. |
| `registerFoodItem(nutrientName, food, scale, compareType)` | `string nutrientName`, `IItemStack food`, `float scale`, `string compareType` | `void` | Registers the food with custom scale and compare behavior. |
| `removeFoodItem(nutrientName, food)` | `string nutrientName`, `IItemStack food` | `void` | Removes the food from that nutrient's explicit item list. |

`compareType` values follow Nutrition's API:

| Value | Meaning |
| --- | --- |
| `DEFAULT` | Match item type only. |
| `META_SENSITIVE` | Match item type and metadata. |
| `ONLY_NBT_SENSITIVE` | Match by NBT data. |
| `ALL_SENSITIVE` | Match item type, metadata, and NBT data. |

Example:

```zenscript
mods.hungertweaker.events.HungerEvents.onNutritionFoodEaten(function(event as mods.hungertweaker.events.NutritionFoodEatenEvent) {
    if (event.food == <minecraft:apple>) {
        event.addNutrient("fruit", 2.0);
        event.protein = 50.0;
    }
});
```

### The Spice of Life

Zen class: `mods.hungertweaker.SpiceOfLife`

Food modifier and food values:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `isLoaded()` | none | `bool` | Whether The Spice of Life is loaded. |
| `getFoodModifier(player, food)` | `IPlayer player`, `IItemStack food` | `float` | Current diminishing returns modifier for that player and food. |
| `getFoodGroupModifier(player, food, foodGroup)` | `IPlayer player`, `IItemStack food`, `string foodGroup` | `float` | Modifier calculated for a specific food group. |
| `getModifiedFoodValues(hunger, saturationModifier, modifier)` | `int hunger`, `float saturationModifier`, `float modifier` | `IData` map | Applies Spice of Life's value adjustment to raw food values. |
| `getModifiedFoodValuesForPlayer(player, food)` | `IPlayer player`, `IItemStack food` | `IData` map | Applies the player's current modifier to the food's actual AppleCore food values. |

Food history:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `getFoodHistory(player)` | `IPlayer player` | `IData` map | Summary with `historyLength`, `historySize`, `totalFoodsEatenAllTime`, `wasGivenFoodJournal`, `ticksActive`, `distinctFoodGroups`, and `lastEatenFood`. |
| `getHistoryFoods(player)` | `IPlayer player` | `IData` list | Current history entries. Each entry has `food`, `foodValues`, `worldTimeEaten`, `playerTimeEaten`, and `foodGroups`. |
| `getHistoryLength(player)` | `IPlayer player` | `int` | History length in Spice of Life's active unit: entries, hunger restored, or time. |
| `getTotalFoodsEatenAllTime(player)` | `IPlayer player` | `int` | Player's all-time food eaten counter from Spice of Life. |
| `getTicksActive(player)` | `IPlayer player` | `long` | Spice of Life's active tick counter for the player. |
| `getLastEatenFood(player)` | `IPlayer player` | `IItemStack` | Last food recorded in the player's history, or `null` if none. |
| `getFoodCount(player, food)` | `IPlayer player`, `IItemStack food` | `int` | Exact food count in the current history, ignoring food groups. |
| `getFoodGroupCount(player, food, foodGroup)` | `IPlayer player`, `IItemStack food`, `string foodGroup` | `int` | Count for the food or a matching entry in that food group. |
| `containsFoodOrItsFoodGroups(player, food)` | `IPlayer player`, `IItemStack food` | `bool` | Whether the history contains the exact food or an overlapping food group. |
| `getTotalFoodValues(player, food)` | `IPlayer player`, `IItemStack food` | `IData` map | Total hunger/saturation represented by matching exact foods in history. |
| `getTotalFoodValuesForFoodGroup(player, food, foodGroup)` | `IPlayer player`, `IItemStack food`, `string foodGroup` | `IData` map | Total hunger/saturation represented by that food group in history. |
| `addFoodToHistory(player, food)` | `IPlayer player`, `IItemStack food` | `bool` | Adds a food history entry and counts it toward all-time eaten foods. |
| `addFoodToHistory(player, food, countsTowardsAllTime)` | `IPlayer player`, `IItemStack food`, `bool countsTowardsAllTime` | `bool` | Adds a history entry and optionally skips the all-time counter. |
| `resetFoodHistory(player)` | `IPlayer player` | `void` | Clears Spice of Life history and all-time counters for the player. |
| `validateFoodHistory(player)` | `IPlayer player` | `void` | Removes invalid foods from history, then syncs. |
| `syncFoodHistory(player)` | `IPlayer player` | `void` | Sends current history to client on server side. |

Food groups:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `getFoodGroups()` | none | `string[]` | All loaded food group identifiers. |
| `getFoodGroupsForFood(food)` | `IItemStack food` | `string[]` | Food group identifiers matching the food. |
| `getFoodGroupInfo(foodGroup)` | `string foodGroup` | `IData` map | Group info: `identifier`, `name`, `enabled`, `blacklist`, `formula`, `color`, `hidden`. |
| `getFoodGroupInfoForFood(food)` | `IItemStack food` | `IData` map | Map of matching `foodGroup -> foodGroupInfo`. |
| `isFoodBlacklisted(food)` | `IItemStack food` | `bool` | Whether Spice of Life excludes this food from diminishing returns. |

Example:

```zenscript
mods.hungertweaker.events.HungerEvents.onSpiceOfLifeFoodEaten(function(event as mods.hungertweaker.events.SpiceOfLifeFoodEatenEvent) {
    if (event.modifier < 0.25) {
        print("Low Spice of Life value for " ~ event.food.commandString);
    }
});
```

### Spice of Life: Carrot Edition

Zen class: `mods.hungertweaker.SpiceOfLifeCarrotEdition`

Progress and checks:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `isLoaded()` | none | `bool` | Whether Spice of Life: Carrot Edition is loaded. |
| `getEatenFoodCount(player)` | `IPlayer player` | `int` | Number of unique foods stored in the player's Carrot food list. |
| `getFoodsEatenForMilestones(player)` | `IPlayer player` | `int` | Number of unique foods that currently count toward milestones after config filtering. |
| `hasEaten(player, food)` | `IPlayer player`, `IItemStack food` | `bool` | Whether the player's Carrot food list already contains the food. |
| `shouldCount(player, food)` | `IPlayer player`, `IItemStack food` | `bool` | Whether this food would count toward milestones using current Carrot config. |
| `isAllowed(player, food)` | `IPlayer player`, `IItemStack food` | `bool` | Whether blacklist/whitelist rules allow the food. |
| `isHearty(player, food)` | `IPlayer player`, `IItemStack food` | `bool` | Whether the food meets Carrot Edition's minimum food value. |
| `getMilestonesAchieved(player)` | `IPlayer player` | `int` | Number of reached Carrot milestones. |
| `getNextMilestone(player)` | `IPlayer player` | `int` | Required food count for the next milestone, or `-1` if maxed. |
| `getFoodsUntilNextMilestone(player)` | `IPlayer player` | `int` | Remaining count until next milestone, or a negative value if maxed. |
| `hasReachedMax(player)` | `IPlayer player` | `bool` | Whether the player has reached the final milestone. |
| `getHealthModifier(player)` | `IPlayer player` | `int` | Max-health modifier in health points, not hearts. |
| `getProgress(player)` | `IPlayer player` | `IData` map | Full progress data: `eatenFoodCount`, `foodsEaten`, `milestonesAchieved`, `nextMilestone`, `foodsUntilNextMilestone`, `hasReachedMax`, `healthModifier`, `config`. |
| `getEatenFoods(player)` | `IPlayer player` | `IData` list | List of item maps for all foods in the player's Carrot food list. |

Mutating methods:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `addFood(player, food)` | `IPlayer player`, `IItemStack food` | `bool` | Adds the food to Carrot Edition's food list. Returns true if it was newly added and counted. Updates max health and syncs on server. |
| `clearFoods(player)` | `IPlayer player` | `void` | Clears the player's Carrot food list, updates max health, and syncs. |
| `updateProgressInfo(player)` | `IPlayer player` | `void` | Rebuilds Carrot progress info from config and the food list, then syncs. |
| `updateMaxHealth(player)` | `IPlayer player` | `bool` | Reapplies Carrot max-health modifier. Returns whether the modifier changed. |
| `syncFoodList(player)` | `IPlayer player` | `void` | Sends the current Carrot food list to client on server side. |

`getProgress(player).config` contains `milestones`, `baseHearts`, `heartsPerMilestone`, `shouldShowUneatenFoods`, `minimumFoodValue`, `blacklist`, `whitelist`, and `hasWhitelist`.

Example:

```zenscript
mods.hungertweaker.events.HungerEvents.onSOLCarrotFoodEaten(function(event as mods.hungertweaker.events.SpiceOfLifeCarrotFoodEatenEvent) {
    if (event.shouldCount && !event.hasEaten) {
        print("New Carrot Edition food: " ~ event.food.commandString);
    }
});
```

### Compatibility Events

Zen class: `mods.hungertweaker.events.HungerEvents`

| Registration method | Handler event type | Fires when | Extra data |
| --- | --- | --- | --- |
| `onNutritionFoodEaten(handler)` | `mods.hungertweaker.events.NutritionFoodEatenEvent` | AppleCore `FoodEaten` fires and Nutrition is loaded. | `foodNutrition`, `playerNutrition`, nutrient getters/setters, and `getNutrient/addNutrient/setNutrient/resetNutrient`. |
| `onSpiceOfLifeFoodEaten(handler)` | `mods.hungertweaker.events.SpiceOfLifeFoodEatenEvent` | AppleCore `FoodEaten` fires and The Spice of Life is loaded. | `modifier`, `history`, `foodHistory`, food count/group helpers, reset/sync helpers. |
| `onSpiceOfLifeCarrotFoodEaten(handler)` | `mods.hungertweaker.events.SpiceOfLifeCarrotFoodEatenEvent` | AppleCore `FoodEaten` fires and Carrot Edition is loaded. | `hasEaten`, `shouldCount`, `progress`, `foodProgress`, add/clear/update/sync helpers. |
| `onSpiceOfLifeCarrotEditionFoodEaten(handler)` | Same as above | Alias for Carrot Edition. | Same as above. |
| `onSOLCarrotFoodEaten(handler)` | Same as above | Short alias for Carrot Edition. | Same as above. |

These compatibility events extend `FoodEatenEvent`, so they also have the normal `player`, `food`, `hunger`, `saturationModifier`, `hungerAdded`, and `saturationAdded` values.

The normal `FoodEatenEvent` and `GetFoodValuesEvent` also expose these optional getters:

| Getter | Type | Meaning |
| --- | --- | --- |
| `event.nutrition` | `IData` map | Food nutrition gain map, or empty map when Nutrition is not loaded. |
| `event.playerNutrition` | `IData` map | Player nutrition values, or empty map when Nutrition is not loaded. |
| `event.spiceOfLife` | `IData` map | Spice of Life food/history data for this food, or empty map when not loaded. |
| `event.spiceOfLifeModifier` | `float` | Spice of Life modifier for this food, or `1.0` when not loaded. |
| `event.spiceOfLifeCarrot` | `IData` map | Carrot Edition food/progress data for this food, or empty map when not loaded. |
| `event.solCarrot` | `IData` map | Alias of `event.spiceOfLifeCarrot`. |

For a more complete overview, visit the HungerTweaker wiki.
https://github.com/coolsquid/HungerTweaker/wiki/
