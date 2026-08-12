# HungerTweaker Community Edition

HungerTweaker Community Edition is an unofficial maintenance project by CanoeStudio. It continues the original HungerTweaker project by exposing the [AppleCore](https://github.com/squeek502/AppleCore) API to [CraftTweaker](https://github.com/CraftTweaker/CraftTweaker) scripts, and can be used to modify a variety of food and hunger-related properties.

As part of the unofficial maintenance work, the Community Edition adds optional CraftTweaker compatibility APIs and event hooks for [Nutrition](https://www.curseforge.com/minecraft/mc-mods/nutrition-unofficial-extended-life), The Spice of Life, Spice of Life: Carrot Edition, and FoodSpoiling. The added functionality includes:

- Nutrition integration: reads loaded nutrient definitions and metadata, supports the five default nutrients (`dairy`, `fruit`, `grain`, `protein`, and `vegetable`) as well as custom nutrients, and lets scripts read, set, add, or reset player nutrition. Scripts can also calculate food nutrition, register or remove foods from nutrient lists, choose item matching sensitivity, and handle the Nutrition food-eaten event.
- The Spice of Life integration: reads the current diminishing-return modifier, food groups, food-group configuration, food history, eaten-food counts, and total food values. Scripts can add or reset history, validate and synchronize it, inspect food-group matches, and respond when food is eaten.
- Spice of Life: Carrot Edition integration: reads unique foods eaten, milestone progress, the next milestone, health modifiers, whitelist/blacklist results, and the full Carrot configuration. Scripts can add or clear foods, rebuild progress, update max health, synchronize the food list, and respond to Carrot Edition food-eaten events.
- FoodSpoiling integration: reads rot state, expiration, lifetime, remaining ticks, spoilage, freshness, container lifetime factors, and stack timing data. It also provides helpers for calculating spoilage-adjusted saturation. Its food-values event fires during AppleCore food-value calculation, allowing scripts to adjust hunger and saturation based on the current spoilage state; FoodSpoiling's own configuration remains the source of the spoilage rules.
- EditableEdibles-compatible food effects: adds ordinary chance effects, weighted effect choices, potion cures, and positive/negative/all potion cure groups without requiring EditableEdibles. Potion Core effects can be passed through CraftTweaker's normal `IPotionEffect` values; Potion Core itself remains optional.
- Hunger Overhaul-compatible food and hunger controls: exposes the food divider, eating-speed, Well Fed, regeneration, starvation, respawn, stack-size, constant-hunger-loss, and low-stat effect behaviors through CT methods. It uses AppleCore events and does not require the original Hunger Overhaul mod.
- Sanity integration: directly calls Sanity's `Capabilities.SANITY` and `ISanity` API for capability access, CT-configured food values, freshness-scaled positive recovery, and a Sanity food-eaten event. The Sanity CT class and event are manually registered only after Forge detects the `sanity` mod; when Sanity is absent, this integration is not registered.
- AppleSkin and LemonSkin compatibility: these client-side HUD and tooltip mods continue to display the food values supplied through AppleCore; HungerTweaker does not duplicate their HUD implementation.
- Tough As Nails integration: exposes TAN thirst, hydration, exhaustion, temperature, temperature ranges, external temperature modifiers, gameplay switches, water types, and configured drink data. It also provides a TAN drink-finished event.
- SimpleDifficulty integration: exposes SD thirst and temperature capabilities, temperature targets and world temperatures, temporary modifiers, armor temperature NBT helpers, thirst types, configured consumable data, drink helpers, and the complete runtime `JsonConfig` registration surface. It also provides an SD drink-finished event.

These integrations are optional and are only active when their corresponding mod is loaded. TAN and SimpleDifficulty are independent: loading one never requires or converts the other's data. The Nutrition integration supports branches and forks that preserve the expected `ca.wescook.nutrition` package layout and public API. The README includes the required imports, complete method signatures, parameter and return-value meanings, event timing, and practical examples for developers.

## Scope and Partial Reimplementation

The Community Edition provides a CraftTweaker compatibility layer for the requested food, hunger, sanity, and potion-effect features. It is **not a complete replacement or a full internal port** of every system in the reference mods. The following parts are intentionally outside the current implementation scope:

### EditableEdibles and Potion Core

- The main food-effect behavior is available through `mods.hungertweaker.FoodEffects`: chance effects, weighted effects, duration/amplifier stacking, potion cures, cure groups, always-edible food, and best-effort default potion-effect cancellation.
- Potion Core effects can be supplied through CraftTweaker's normal `IPotionEffect` values when Potion Core is installed.
- Potion Core's own potion implementations, attributes, movement systems, custom projectiles, client rendering, configuration, and other standalone mechanics are not reimplemented.
- EditableEdibles' MistyWorld-specific intoxication and pollution compatibility is not included. The current implementation also cannot cancel arbitrary custom `onFoodEaten` side effects such as fire, damage, entity spawning, or other non-potion behavior.

### Hunger Overhaul

- The CT layer covers the requested food-value changes, eating speed, Well Fed behavior, regeneration and exhaustion changes, starvation damage, respawn hunger, food stack sizing, constant hunger loss, low-stat effects, animal delays, crop growth conditions, and bone-meal controls.
- Hunger Overhaul's independent systems are not fully ported, including its JSON food database, recipe and loot changes, village trades, seed and grass systems, Pam's HarvestCraft/Natura/Biomes O' Plenty/Tinkers Construct-specific modules, commands, and mod-specific integration modules.
- Crop and bone-meal handling is implemented through Forge/AppleCore hooks for supported vanilla growth blocks. It is not a byte-for-byte reproduction of every Hunger Overhaul block or third-party crop implementation.

### Sanity

- The CT layer covers direct Sanity capability access, CT-configured food values, the Sanity food-eaten event, FoodSpoiling freshness and rotten-food penalties, and Nutrition average-value factors.
- Sanity's original environment and gameplay systems remain provided by Sanity itself and are not exposed as a complete new CT API. This includes sleep, rain, darkness, hunger, choking, combat, damage, lightning, dimension travel, mobs, pets, jukeboxes, advancements, equipment, overlays, sounds, shaders, fake entities, and shadow-monster behavior.
- Sanity's original configuration remains the source for those non-CT behaviors. The HungerTweaker methods only add or override the documented CT layer.

### Compatibility Boundary

Optional compatibility classes are registered only after Forge confirms that the corresponding mod is loaded. This protects modpacks that install only part of the compatibility set, but it does not make incompatible forks compatible when they change the referenced public package or API. Always check `isLoaded()` before calling an optional integration from a script that may run without that mod.

The project consists of two parts. Firstly, it includes a simplified wrapper around the AppleCore API. This can be used to set a variety of default values, as well as modify the [properties of food items](#foodvalues). Secondly, it provides access to most of AppleCore's [events](#core-events). This can be used to dynamically modify and react to changes in a player's hunger, exhaustion, starvation, and regen.

The simplified wrapper is designed to be easy to use, and largely consists of static ZenMethods such as `mods.hungertweaker.Hunger.setMaxHunger(20)`. It has access to no other context than the previous value of the property, which can be used in [expressions](#expressions), such as `mods.hungertweaker.Hunger.setMaxHunger("x/2")`.

The event system functions largely like CraftTweaker's own event system. Scripts can register event handler functions, which are executed whenever the event occurs and have access to context, such as the IPlayer in question. As such, the event system can be used to produce far more advanced results than the simplified wrapper.

HungerTweaker attempts to apply its changes after all other mods. The simplified options are handled before the events, and event handlers may override the default values set by the simplified options.

## Credits and License

HungerTweaker Community Edition is an unofficial community-maintained continuation of the original [HungerTweaker project](https://github.com/coolsquid/HungerTweaker), maintained by CanoeStudio. It is not affiliated with or endorsed by the original author or the original project.

The original project and the retained original API are released under the [Unlicense](LICENSE). The Unlicense is a public-domain dedication intended to release the work to the public domain, with a permissive fallback license in jurisdictions where a public-domain dedication is not recognized. This repository follows that license for the original content and the Community Edition additions.

The original API documentation has been migrated into this README. The Community Edition adds the optional compatibility APIs, event hooks, documentation, and compatibility fixes described below.

## Original Core CT API Reference

This section contains the original HungerTweaker API that was previously documented in the project Wiki. The API is still available in HungerTweaker Community Edition and uses the same `mods.hungertweaker...` ZenScript paths.

### Core Imports

```zenscript
import mods.hungertweaker.FoodValues;
import mods.hungertweaker.HUD;
import mods.hungertweaker.Hunger;
import mods.hungertweaker.Exhaustion;
import mods.hungertweaker.ExhaustingAction;
import mods.hungertweaker.Starvation;
import mods.hungertweaker.Regen;
import mods.hungertweaker.SaturatedRegen;
import mods.hungertweaker.PeacefulRegen;
import mods.hungertweaker.events.HungerEvents;
```

### Common Parameters

Most numeric setters accept an `IData` value. The value may be a literal number or a quoted HungerTweaker expression. A literal is evaluated when the script loads; a quoted expression is evaluated whenever AppleCore asks for the value.

| Input | Meaning |
| --- | --- |
| `20` | A fixed numeric value. |
| `"x/2"` | A dynamic expression. `x` is the value before HungerTweaker applies this setting. |
| `0`, `1`, `2` | Status values for deny, Vanilla/default behavior, and allow. |
| `"DENY"`, `"DEFAULT"`, `"ALLOW"` | The named forms of the three status values. Names are case-insensitive. |

### FoodValues

Zen class: `mods.hungertweaker.FoodValues`

`FoodValues` is obtained from an `IIngredient` through the `foodValues` getter. It applies to every matching food item. For example, `<minecraft:apple>.foodValues` targets apples, while an ore-dictionary ingredient can target multiple items. If multiple `FoodValues` instances match the same item, the instance retrieved last takes precedence for the AppleCore food values.

| Property | Type | Access | Meaning |
| --- | --- | --- | --- |
| `hunger` | `int` | read/write | Hunger points restored by the food. The getter only works for one `IItemStack`; the setter can apply to all matching foods. |
| `saturationModifier` | `float` | read/write | Saturation modifier used with the hunger value to calculate saturation restored. The getter only works for one `IItemStack`. |
| `unmodifiedHunger` | `int` | read-only | Original hunger before HungerTweaker and other food-value changes. Only available for one `IItemStack`. |
| `unmodifiedSaturationModifier` | `float` | read-only | Original saturation modifier before HungerTweaker and other food-value changes. Only available for one `IItemStack`. |
| `alwaysEdible` | `bool` | read/write | Whether the `ItemFood` can be eaten regardless of hunger. |
| `wolfFood` | `bool` | read/write | Whether the food is treated as a wolf's favorite food. |
| `effect` | `IPotionEffect` | read/write | Potion effect applied by the food. |
| `effectProbability` | `float` | read/write | Probability of applying the food's potion effect. |

The core methods are also available as property setters:

| Method | Parameters | Meaning |
| --- | --- | --- |
| `setHunger(value)` | `IData value` | Sets hunger for all matching food items. Accepts a number or expression. |
| `setSaturationModifier(value)` | `IData value` | Sets saturation modifier for all matching food items. Accepts a number or expression. |
| `setAlwaysEdible(value)` | `bool value` | Sets `alwaysEdible` on all matching food items. |
| `setWolfFood(value)` | `bool value` | Sets `wolfFood` on all matching food items. |
| `setEffect(value)` | `IPotionEffect value` | Sets the potion effect on all matching food items. |
| `setEffectProbability(value)` | `float value` | Sets the potion effect probability on all matching food items. |

Example:

```zenscript
import mods.hungertweaker.FoodValues;

<minecraft:apple>.foodValues.hunger = 10;
<minecraft:apple>.foodValues.saturationModifier = "x / 2";
<minecraft:apple>.foodValues.alwaysEdible = true;
```

### Simple Hunger and Food Settings

| Zen class | Method | Parameters | Meaning |
| --- | --- | --- | --- |
| `mods.hungertweaker.Hunger` | `setMaxHunger(value)` | `IData value` | Sets the size of the hunger bar. The UI is not resized and Vanilla hunger thresholds, such as the sprint threshold, remain unchanged. |
| `mods.hungertweaker.Exhaustion` | `setMaxExhaustionLevel(value)` | `IData value` | Sets the exhaustion threshold at which exhaustion causes hunger loss. |
| `mods.hungertweaker.Exhaustion` | `setConstantExhaustionIncrease(value)` | numeric `IData value` | Adds this amount of exhaustion to every player every tick. Set it to `0` to disable the extra increase. |
| `mods.hungertweaker.Exhaustion` | `setDeltaExhaustion(value)` | `IData value` | Sets exhaustion removed when the maximum exhaustion level is reached. |
| `mods.hungertweaker.Exhaustion` | `setDeltaHunger(value)` | `IData value` | Sets the hunger change caused by exhaustion. |
| `mods.hungertweaker.Exhaustion` | `setDeltaSaturation(value)` | `IData value` | Sets the saturation change caused by exhaustion. |
| `mods.hungertweaker.Exhaustion` | `setStatus(value)` | `IData value` | Enables, disables, or defers exhaustion using `DENY`, `DEFAULT`, or `ALLOW`. |
| `mods.hungertweaker.Starvation` | `setInterval(value)` | `IData value` | Sets ticks between starvation damage applications. |
| `mods.hungertweaker.Starvation` | `setDamage(value)` | `IData value` | Sets damage applied by starvation. |
| `mods.hungertweaker.Starvation` | `setStatus(value)` | `IData value` | Enables, disables, or defers starvation. |
| `mods.hungertweaker.HUD` | `setStatus(value)` | `IData value` | Controls the AppleCore food overlay: `DENY` disables it, `DEFAULT` uses Vanilla behavior, and `ALLOW` enables it. |

### EditableEdibles-Compatible Food Effects

Zen class: `mods.hungertweaker.FoodEffects`

These methods are implemented by HungerTweaker and do not require EditableEdibles. A `chance` from `0.0` to `1.0` is a normal probability. A `chance` greater than `1.0` is treated as a weight, and exactly one weighted effect is selected from matching weighted entries. Potion durations are ticks and potion amplifiers are zero-based.

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `addEffect(food, effect, chance)` | `IIngredient food`, `IPotionEffect effect`, `float chance` | `void` | Adds one chance-based food effect. |
| `addEffect(food, effect, chance, additiveDuration, maxDuration, additiveAmplifier, maxAmplifier)` | `IIngredient`, `IPotionEffect`, `float`, `bool`, `int`, `bool`, `int` | `void` | Adds an effect with optional duration/amplifier stacking and caps. `-1` disables each cap. |
| `addCureEffect(food, effect, chance)` | `IIngredient`, `IPotionEffect`, `float chance` | `void` | Chance to remove the matching potion when the food is eaten. The configured duration/amplifier are minimum matching requirements; use `-1` in the effect to ignore them. |
| `addCureType(food, cureType, chance)` | `IIngredient`, `string cureType`, `float chance` | `void` | Chance to remove `ALL`, `POSITIVE`, or `NEGATIVE` active potion effects. |
| `setAlwaysEdible(food, value)` | `IIngredient`, `bool value` | `void` | Convenience wrapper for setting `food.foodValues.alwaysEdible`; this changes the actual `ItemFood` property. |
| `setCancelDefaultEffects(food, cancel)` | `IIngredient`, `bool cancel` | `void` | Best-effort cancellation of potion effects added by the food's default eat hook. It restores the potion state captured before eating after the hook runs; it cannot intercept arbitrary custom side effects such as damage, fire, or entity spawning. |
| `clearEffects(food)` | `IIngredient food` | `void` | Removes configured effects and cures for the matching ingredient. |
| `clearAll()` | none | `void` | Removes all FoodEffects rules. |

Example:

```zenscript
import mods.hungertweaker.FoodEffects;

FoodEffects.addEffect(<minecraft:apple>, <effect:minecraft:speed>, 0.5);
FoodEffects.addEffect(<minecraft:golden_apple>, <effect:minecraft:regeneration>.withDuration(200), 1.0,
    true, 600, true, 2);
FoodEffects.addCureType(<minecraft:milk_bucket>, "NEGATIVE", 1.0);
FoodEffects.setAlwaysEdible(<minecraft:bread>, true);
FoodEffects.setCancelDefaultEffects(<minecraft:pufferfish>, true);
```

### Hunger Overhaul-Compatible Settings

Zen class: `mods.hungertweaker.HungerOverhaul`

The methods below reproduce the requested Hunger Overhaul-style controls without requiring the original Hunger Overhaul mod. Expressions accept a number or a quoted expression using `x` as the value supplied by AppleCore. Methods are inactive until called by a script, except that existing HungerTweaker behavior remains unchanged.

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `setModifyFoodValues(value)` | `bool value` | `void` | Enables or disables the divider-based food-value changes. |
| `setFoodHungerDivider(value)` | `IData value` | `void` | Divides the current hunger value by the evaluated divider. |
| `setFoodSaturationDivider(value)` | `IData value` | `void` | Divides the current saturation modifier by the evaluated divider. |
| `setFoodHungerToSaturationDivider(value)` | `IData value` | `void` | Sets saturation modifier to the modified hunger divided by the evaluated divider. |
| `setEatingDuration(value)` | `IData value` | `void` | Sets food use duration in ticks from the food hunger value. |
| `setEatingDurationMultiplier(value)` | `IData value` | `void` | Multiplies the calculated eating duration. |
| `setWellFedEffect(effect)` | `IPotionEffect effect` | `void` | Chooses the potion effect used for Well Fed. |
| `setWellFedDuration(value)` | `IData value` | `void` | Sets Well Fed duration in ticks from food hunger. |
| `setWellFedDurationMultiplier(value)` | `IData value` | `void` | Multiplies Well Fed duration. Existing Well Fed duration is stacked when another food is eaten. |
| `setWellFedEffectiveness(value)` | `float value` | `void` | Health regeneration reduction in interval, clamped to `0.0..1.0`; `0.25` is 25% faster. |
| `setWellFedSaturationEffectiveness(value)` | `float value` | `void` | While the Well Fed potion is active, reduces exhaustion-based hunger/saturation loss by this fraction. `1.0` effectively prevents that loss. |
| `setHungerLossRate(percentage)` | `float percentage` | `void` | Sets the global hunger loss speed. `100` is normal and `0` denies exhaustion hunger loss. |
| `setHealthRegenRate(percentage)` | `float percentage` | `void` | Sets health regeneration speed. `0` denies both normal and saturated regeneration events. |
| `setRequireMinimumHungerToHeal(value)` | `bool value` | `void` | Requires the configured minimum food level before normal regeneration is allowed. |
| `setMinimumHungerToHeal(value)` | `int value` | `void` | Sets the minimum food level and enables the requirement. |
| `setDisableHealingHungerDrain(value)` | `bool value` | `void` | Removes exhaustion generated by health regeneration. |
| `setDifficultyScalingHunger(value)` | `bool value` | `void` | Applies Peaceful/Easy/Hard hunger-loss scaling. |
| `setDifficultyScalingHealing(value)` | `bool value` | `void` | Applies difficulty scaling to regeneration intervals. |
| `setDifficultyScalingEffects(value)` | `bool value` | `void` | Adjusts custom low-stat potion amplifiers by difficulty. |
| `setModifyRegenRateOnLowHealth(value)` | `bool value` | `void` | Enables low-health regeneration slowdown. |
| `setLowHealthRegenRateModifier(value)` | `float value` | `void` | Sets the low-health slowdown factor and enables it. |
| `setRespawnHunger(value, difficultyModifier, difficultyScaling)` | `int`, `int`, `bool` | `void` | Sets hunger after respawn and the first login. The initial-login value is applied once per player using persistent player data. |
| `setInstantStarvation(value)` | `bool value` | `void` | Makes starvation damage lethal when the starvation event fires. |
| `setDamageOnStarve(value)` | `IData value` | `void` | Replaces the normal starvation damage with an expression evaluated against the original damage. |
| `setPeacefulExhaustionHungerLoss(value)` | `bool value` | `void` | In Peaceful, makes exhaustion remove hunger instead of only saturation when saturation is empty. |
| `setConstantHungerLoss(value)` | `bool value` | `void` | Enables `0.01` exhaustion per tick, or disables it. |
| `setConstantHungerLossAmount(amount)` | `float amount` | `void` | Sets custom exhaustion per tick; `0` disables it. |
| `setModifyFoodStackSize(value, multiplier)` | `bool`, `int multiplier` | `void` | Scales food stack sizes based on hunger value. |
| `addLowHungerEffect(effect, maximumFoodLevel)` | `IPotionEffect`, `int` | `void` | Reapplies the effect every second while food is at or below the threshold. |
| `addLowHealthEffect(effect, maximumHealthPercent)` | `IPotionEffect`, `float` | `void` | Reapplies the effect every second while health is at or below the threshold. Values above `1` are interpreted as percentages. |
| `clearLowStatEffects()` | none | `void` | Removes all custom low-hunger and low-health rules. |
| `setEggTimeoutMultiplier(multiplier)` | `float multiplier >= 1` | `void` | Delays chicken egg laying using a probabilistic per-tick multiplier. |
| `setBreedingTimeoutMultiplier(multiplier)` | `float multiplier >= 1` | `void` | Delays the adult breeding cooldown. |
| `setChildDurationMultiplier(multiplier)` | `float multiplier >= 1` | `void` | Delays child animal growth to adulthood. |
| `setAnimalDelayMultipliers(egg, breeding, child)` | three `float` values | `void` | Sets all three animal delay multipliers. |
| `setCropGrowthMultiplier(multiplier)` | `float multiplier > 0` | `void` | Changes random-tick growth probability for supported vanilla growing blocks. `4.0` is approximately one quarter of normal growth attempts. |
| `setCropGrowthDaylightOnly(value)` | `bool value` | `void` | Prevents supported crop growth during nighttime. |
| `setCropGrowthNeedsSky(value)` | `bool value` | `void` | Enables sky visibility as a crop-growth condition. |
| `setCropGrowthNoSkyMultiplier(multiplier)` | `float multiplier >= 0` | `void` | Multiplies growth time when the block cannot see the sky; `0` prevents growth without sky. |
| `setBonemealEffectiveness(value)` | `float value` in `0.0..1.0` | `void` | Chance that bone meal is allowed to work on supported crops; `0` disables it. |
| `setModifyBonemealGrowth(value)` | `bool value` | `void` | Enables reduced one-stage bone-meal growth for vanilla crops and beetroot. |
| `setDifficultyScalingBoneMeal(value)` | `bool value` | `void` | Applies Easy/Normal/Hard bone-meal success scaling. |

Supported crop rules cover vanilla crops, beetroot, reeds, cactus, stems, cocoa, nether wart, and saplings. Bone-meal state reduction is implemented for `BlockCrops` and beetroot; other supported growing blocks use their normal state logic when the chance check succeeds.

Example:

```zenscript
import mods.hungertweaker.HungerOverhaul;

HungerOverhaul.setWellFedEffect(<effect:minecraft:regeneration>.withDuration(1));
HungerOverhaul.setWellFedEffectiveness(0.25);
HungerOverhaul.setWellFedSaturationEffectiveness(0.5);
HungerOverhaul.setRespawnHunger(20, 4, true);
HungerOverhaul.setAnimalDelayMultipliers(4.0, 4.0, 4.0);
HungerOverhaul.setCropGrowthMultiplier(4.0);
HungerOverhaul.setCropGrowthDaylightOnly(true);
HungerOverhaul.setCropGrowthNoSkyMultiplier(2.0);
HungerOverhaul.setBonemealEffectiveness(0.5);
HungerOverhaul.setDifficultyScalingBoneMeal(true);
HungerOverhaul.setDamageOnStarve("x * 2");
```

### ExhaustingAction

Zen class: `mods.hungertweaker.ExhaustingAction`

The following action constants can be configured individually:

`HARVEST_BLOCK`, `NORMAL_JUMP`, `SPRINTING_JUMP`, `ATTACK_ENTITY`, `DAMAGE_TAKEN`, `HUNGER_POTION`, `MOVEMENT_DIVE`, `MOVEMENT_SWIM`, `MOVEMENT_SPRINT`, `MOVEMENT_CROUCH`, and `MOVEMENT_WALK`.

`ExhaustingAction.ALL` contains every action. Each action has a `name` property and a `setDeltaExhaustion(value)` method. The value accepts a number or expression and controls the exhaustion added by that action. Applying a setting to `ALL` first and then changing a specific action lets you create a general rule with exceptions.

```zenscript
import mods.hungertweaker.ExhaustingAction;

for action in ExhaustingAction.ALL {
    action.setDeltaExhaustion("x / 2");
}
ExhaustingAction.MOVEMENT_SPRINT.setDeltaExhaustion("x * 2");
```

### Regen Settings

| Zen class | Method | Parameters | Meaning |
| --- | --- | --- | --- |
| `mods.hungertweaker.Regen` | `setInterval(value)` | `IData value` | Sets ticks between normal health regeneration checks. |
| `mods.hungertweaker.Regen` | `setDeltaExhaustion(value)` | `IData value` | Sets exhaustion added when normal health regeneration occurs. |
| `mods.hungertweaker.Regen` | `setDeltaHealth(value)` | `IData value` | Sets health restored by normal regeneration. |
| `mods.hungertweaker.Regen` | `setStatus(value)` | `IData value` | Enables, disables, or defers normal regeneration. |
| `mods.hungertweaker.SaturatedRegen` | `setInterval(value)` | `IData value` | Sets ticks between health regeneration checks caused by full hunger and saturation. |
| `mods.hungertweaker.SaturatedRegen` | `setDeltaExhaustion(value)` | `IData value` | Sets exhaustion added by saturated regeneration. |
| `mods.hungertweaker.SaturatedRegen` | `setDeltaHealth(value)` | `IData value` | Sets health restored by saturated regeneration. |
| `mods.hungertweaker.SaturatedRegen` | `setStatus(value)` | `IData value` | Enables, disables, or defers saturated regeneration. |
| `mods.hungertweaker.PeacefulRegen` | `setDeltaHealth(value)` | `IData value` | Sets health restored by peaceful-mode health regeneration. |
| `mods.hungertweaker.PeacefulRegen` | `setDeltaHunger(value)` | `IData value` | Sets hunger added by peaceful-mode health regeneration. |
| `mods.hungertweaker.PeacefulRegen` | `setHealthStatus(value)` | `IData value` | Controls peaceful-mode health regeneration. |
| `mods.hungertweaker.PeacefulRegen` | `setHungerStatus(value)` | `IData value` | Controls peaceful-mode hunger regeneration. |
| `mods.hungertweaker.PeacefulRegen` | `setStatus(value)` | `IData value` | Deprecated alias for `setHealthStatus(value)`. |

### Core Events

Zen class: `mods.hungertweaker.events.HungerEvents`

Every registration method accepts one `handler` function and returns a CraftTweaker event handle. The handler receives one event object. Writable fields can be assigned in the handler; read-only fields provide context for the decision.

| Registration method | Event type | Fires when | Writable or cancelable data | Read-only data |
| --- | --- | --- | --- | --- |
| `onGetFoodValues(handler)` | `GetFoodValuesEvent` | AppleCore retrieves food values. | `hunger`, `saturationModifier` | `unmodifiedHunger`, `unmodifiedSaturationModifier`, `food`, `player` |
| `onFoodEaten(handler)` | `FoodEatenEvent` | A food item has been eaten. | None | `hunger`, `saturationModifier`, `hungerAdded`, `saturationAdded`, `food`, `player` |
| `onFoodStatsAddition(handler)` | `FoodStatsAdditionEvent` | A food is about to be eaten. | Cancelable | `hunger`, `saturationModifier`, `player` |
| `onAllowExhaustion(handler)` | `AllowExhaustionEvent` | AppleCore checks whether exhaustion is allowed. | `allow()`, `deny()`, `pass()` | `player` |
| `onExhausted(handler)` | `ExhaustedEvent` | The player reaches the exhaustion threshold. | `deltaExhaustion`, `deltaHunger`, `deltaSaturation`; cancelable | `currentExhaustionLevel`, `player` |
| `onExhaustingAction(handler)` | `ExhaustingActionEvent` | The player performs an exhausting action. | `deltaExhaustion` | `action`, `player` |
| `onGetMaxExhaustion(handler)` | `GetMaxExhaustionEvent` | AppleCore retrieves maximum exhaustion. | `maxExhaustionLevel` | `player` |
| `onGetMaxHunger(handler)` | `GetMaxHungerEvent` | AppleCore retrieves maximum hunger. | `maxHunger` | `player` |
| `onAllowStarvation(handler)` | `AllowStarvationEvent` | AppleCore checks whether starvation is allowed. | `allow()`, `deny()`, `pass()` | `player` |
| `onGetStarveTickPeriod(handler)` | `GetStarveTickPeriodEvent` | AppleCore retrieves the starvation interval. | `starveTickPeriod` | `player` |
| `onStarve(handler)` | `StarveEvent` | Starvation is about to deal damage. | `starveDamage`; cancelable | `player` |
| `onAllowRegen(handler)` | `AllowRegenEvent` | AppleCore checks normal regeneration. | `allow()`, `deny()`, `pass()` | `player` |
| `onAllowSaturatedRegen(handler)` | `AllowSaturatedRegenEvent` | AppleCore checks saturation regeneration. | `allow()`, `deny()`, `pass()` | `player` |
| `onGetRegenTickPeriod(handler)` | `GetRegenTickPeriodEvent` | AppleCore retrieves normal regeneration interval. | `regenTickPeriod` | `player` |
| `onGetSaturatedRegenTickPeriod(handler)` | `GetSaturatedRegenTickPeriodEvent` | AppleCore retrieves saturation regeneration interval. | `regenTickPeriod` | `player` |
| `onPeacefulRegen(handler)` | `PeacefulRegenEvent` | Peaceful-mode health regeneration occurs. | `deltaHealth`; cancelable | `player` |
| `onPeacefulHungerRegen(handler)` | `PeacefulHungerRegenEvent` | Hunger is added by peaceful-mode regeneration. | `deltaHunger`; cancelable | `player` |
| `onRegen(handler)` | `RegenEvent` | Normal health regeneration occurs. | `deltaHealth`, `deltaExhaustion`; cancelable | `player` |
| `onSaturatedRegen(handler)` | `SaturatedRegenEvent` | Saturation-based health regeneration occurs. | `deltaHealth`, `deltaExhaustion`; cancelable | `player` |

`Allow...Event` handlers use `event.allow()`, `event.deny()`, or `event.pass()`. `pass()` restores AppleCore/Vanilla decision-making. Event fields such as `hunger`, `maxHunger`, `deltaHealth`, and `starveDamage` are assigned directly.

Example:

```zenscript
import mods.hungertweaker.events.HungerEvents;
import mods.hungertweaker.events.GetFoodValuesEvent;
import mods.hungertweaker.events.StarveEvent;

HungerEvents.onGetFoodValues(function(event as GetFoodValuesEvent) {
    if (event.food == <minecraft:apple>) {
        event.hunger = event.unmodifiedHunger + 2;
        event.saturationModifier = event.unmodifiedSaturationModifier;
    }
});

HungerEvents.onStarve(function(event as StarveEvent) {
    event.starveDamage = 0;
});
```

### Expressions

An expression is a string evaluated by HungerTweaker. It can use the previous value as `x`, so the same script can scale values supplied by Vanilla or another mod.

| Syntax | Meaning |
| --- | --- |
| `+`, `-`, `*`, `/`, `^` | Addition, subtraction, multiplication, division, and exponentiation. |
| `sqrt(value)` | Square root. |
| `sin(value)`, `cos(value)`, `tan(value)` | Trigonometric functions; input is interpreted in degrees. |
| `ceil(value)`, `round(value)` | Ceiling and nearest-integer rounding. |
| `nextUp(value)`, `nextDown(value)` | The next representable floating-point value above or below the input. |
| `random()` | Random number from `0` inclusive to `1` exclusive. |
| `random(bounds)` | Random integer from `0` inclusive to `bounds` exclusive. |
| `max(a, b)`, `min(a, b)` | Larger or smaller of two values. |
| `clamp(value, min, max)` | Restricts a value to the inclusive range. |

Use a quoted string when `x` must be evaluated at runtime. Use an unquoted CraftTweaker expression when the result can be calculated once while the script loads.

```zenscript
import mods.hungertweaker.Hunger;

Hunger.setMaxHunger("x * 2"); // Dynamic: doubles the value currently supplied by AppleCore.
Hunger.setMaxHunger(2 * 2);    // Static: CraftTweaker evaluates this once as 4.
```

`Hunger.setMaxHunger(x * 2)` is invalid because `x` is not a CraftTweaker variable. `Hunger.setMaxHunger("2 * 2")` is valid, but slower than using `2 * 2` because HungerTweaker parses and evaluates the string repeatedly.

## Compatibility CT API Reference

HungerTweaker also exposes optional CraftTweaker helpers for Nutrition, The Spice of Life, Spice of Life: Carrot Edition, and FoodSpoiling. Except for `isLoaded()`, these methods require the corresponding mod to be loaded and will throw if it is missing. The tables below show the ZenScript method signature, the CT parameter names, and what each value means.

### Nutrition Package Compatibility

The Nutrition CT helpers are built against the original `ca.wescook.nutrition` package layout used by [WesCook/Nutrition 1.12](https://github.com/WesCook/Nutrition/tree/1.12/src/main/java/ca/wescook/nutrition). After testing, this integration is compatible with Nutrition and Nutrition forks/branches as long as they keep that package layout and the same public API paths. It is not tied to one specific fork.

If a Nutrition fork changes the Java package path away from `ca.wescook.nutrition`, HungerTweaker cannot link to it and the Nutrition CT helpers will not be compatible.

| Nutrition mod | Compatibility |
| --- | --- |
| Any Nutrition branch/fork that keeps `ca.wescook.nutrition` | Compatible. |
| [Nutrition Unofficial Extended Life](https://www.curseforge.com/minecraft/mc-mods/nutrition-unofficial-extended-life) | Compatible; tested with this integration. |
| [Nutrition Unofficial Extended Life Continued](https://www.curseforge.com/minecraft/mc-mods/nutrition-unofficial-extended-life-continued) | Compatible; it keeps the original package layout. |
| [WesCook/Nutrition](https://github.com/WesCook/Nutrition) | Compatible when using the 1.12 package layout/API expected by this integration. |
| Branches/forks that rename or move the package path | Not compatible; HungerTweaker links against `ca.wescook.nutrition`. |

Common CT parameter names:

| Parameter | CT type | Meaning |
| --- | --- | --- |
| `handler` | `function(event as EventType) as void` | Event callback registered with `mods.hungertweaker.events.HungerEvents`. The callback receives one event object. |
| `player` | `crafttweaker.player.IPlayer` | Target player. In HungerTweaker events, use `event.player`. |
| `food` | `crafttweaker.item.IItemStack` | Food item stack, such as `<minecraft:apple>`. |
| `nutrientName` | `string` | Nutrition nutrient id. Defaults are `dairy`, `fruit`, `grain`, `protein`, and `vegetable`; custom Nutrition configs may add more. Names are treated case-insensitively. |
| `nutrients` | `IData` map | Map from nutrient id to number, for example `{"protein": 80.0, "grain": 45.0}`. |
| `value` | `float` | Absolute nutrient value. Nutrition clamps values to its normal 0-100 range. |
| `amount` | `float` | Relative nutrient change. Positive adds, negative subtracts. |
| `scale` | `float` | Nutrition gain multiplier for a registered food item. `1.0` means the food gives the normal configured amount for that nutrient. |
| `compareType` | `string` | Nutrition item matching mode. Use `DEFAULT`, `META_SENSITIVE`, `ONLY_NBT_SENSITIVE`, or `ALL_SENSITIVE`. Hyphenated names are also accepted. |
| `hunger` | `int` | Hunger points restored or adjusted by a food value calculation. |
| `saturationModifier` | `float` | Minecraft saturation modifier, not final saturation points. |
| `spoilage` | `float` | FoodSpoiling rot progress from `0.0` fresh to `1.0` fully spoiled. |
| `freshness` | `float` | Inverse of `spoilage`; `1.0` fresh to `0.0` fully spoiled. |
| `saturationMultiplier` | `float` | Recommended multiplier for reducing saturation from FoodSpoiling freshness. |
| `foodGroup` | `string` | The Spice of Life food group identifier from its food group config. |
| `modifier` | `float` | The Spice of Life nutritional value multiplier; `1.0` means unchanged, `0.5` means 50%. |
| `countsTowardsAllTime` | `bool` | Whether an inserted Spice of Life history entry increments the player's all-time eaten counter. |

Returned `IData` maps use plain string keys:

| Map shape | Keys |
| --- | --- |
| Nutrition value map | Dynamic nutrient ids such as `dairy`, `fruit`, `grain`, `protein`, and `vegetable`; each value is a `float`. |
| Nutrition nutrient info | `name`, `color`, `icon`, `decay`, `visible`, `oreDict`, `foodItemCount`. |
| Food value map | `hunger`, `saturationModifier`, `saturationIncrement`. |
| Item map | `empty`, `commandString`, `displayName`, `amount`, `metadata`. |
| Spice of Life history | `historyLength`, `historySize`, `totalFoodsEatenAllTime`, `wasGivenFoodJournal`, `ticksActive`, `distinctFoodGroups`, `lastEatenFood`. |
| Spice of Life history entry | `food`, `foodValues`, `worldTimeEaten`, `playerTimeEaten`, `foodGroups`. |
| Spice of Life food data | `modifier`, `count`, `containsFoodOrItsFoodGroups`, `foodGroups`, `totalFoodValues`. |
| Spice of Life food group info | `identifier`, `name`, `enabled`, `blacklist`, `formula`, `color`, `hidden`. |
| Carrot Edition progress | `eatenFoodCount`, `foodsEaten`, `milestonesAchieved`, `nextMilestone`, `foodsUntilNextMilestone`, `hasReachedMax`, `healthModifier`, `config`. |
| Carrot Edition config | `milestones`, `baseHearts`, `heartsPerMilestone`, `shouldShowUneatenFoods`, `minimumFoodValue`, `blacklist`, `whitelist`, `hasWhitelist`. |
| Carrot Edition food data | `hasEaten`, `shouldCount`, `isAllowed`, `isHearty`, `progress`. |
| FoodSpoiling data | `rotState`, `canSpoil`, `doesNotRot`, `hasExpiration`, `expirationDays`, `baseTicksToRot`, `ticksToRot`, `remainingTicks`, `elapsedTicks`, `spoilage`, `freshness`, `saturationMultiplier`, `lifetimeFactor`, `hasCreationTime`, `creationTime`, `hasRemainingLifetime`, `remainingLifetime`, `hasLastLifetimeFactor`, `lastLifetimeFactor`, `hasID`, `id`. |
| TAN drink data | `matched`, `source`, `thirst`, `hydration`, `poisonChance`. |
| SD food data | `thirst`, `temperature`; each nested map reports whether a matching SD JSON entry exists. |

Useful imports for scripts:

```zenscript
import mods.hungertweaker.Nutrition;
import mods.hungertweaker.SpiceOfLife;
import mods.hungertweaker.SpiceOfLifeCarrotEdition;
import mods.hungertweaker.FoodSpoiling;
import mods.hungertweaker.FoodEffects;
import mods.hungertweaker.HungerOverhaul;
import mods.hungertweaker.Sanity;
import mods.hungertweaker.ToughAsNails;
import mods.hungertweaker.SimpleDifficulty;
import mods.hungertweaker.events.HungerEvents;
import mods.hungertweaker.events.NutritionFoodEatenEvent;
import mods.hungertweaker.events.SpiceOfLifeFoodEatenEvent;
import mods.hungertweaker.events.SpiceOfLifeCarrotFoodEatenEvent;
import mods.hungertweaker.events.FoodSpoilingFoodValuesEvent;
import mods.hungertweaker.events.ToughAsNailsDrinkEvent;
import mods.hungertweaker.events.SimpleDifficultyDrinkEvent;
import mods.hungertweaker.events.SanityFoodEatenEvent;
```

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

| Method pattern | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `getDairy(player)`, `getFruit(player)`, `getGrain(player)`, `getProtein(player)`, `getVegetable(player)` | `IPlayer player` | `float` | Reads one of Nutrition's five default nutrients. Equivalent to `getNutrient(player, "dairy")`, etc. |
| `setDairy(player, value)`, `setFruit(player, value)`, `setGrain(player, value)`, `setProtein(player, value)`, `setVegetable(player, value)` | `IPlayer player`, `float value` | `void` | Sets one default nutrient to an absolute value. |
| `addDairy(player, amount)`, `addFruit(player, amount)`, `addGrain(player, amount)`, `addProtein(player, amount)`, `addVegetable(player, amount)` | `IPlayer player`, `float amount` | `void` | Adds a relative amount to one default nutrient. |
| `resetDairy(player)`, `resetFruit(player)`, `resetGrain(player)`, `resetProtein(player)`, `resetVegetable(player)` | `IPlayer player` | `void` | Resets one default nutrient. |

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
import mods.hungertweaker.events.HungerEvents;
import mods.hungertweaker.events.NutritionFoodEatenEvent;

HungerEvents.onNutritionFoodEaten(function(event as NutritionFoodEatenEvent) {
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
import mods.hungertweaker.events.HungerEvents;
import mods.hungertweaker.events.SpiceOfLifeFoodEatenEvent;

HungerEvents.onSpiceOfLifeFoodEaten(function(event as SpiceOfLifeFoodEatenEvent) {
    if (event.modifier < 0.25) {
        print("Low Spice of Life value for " ~ event.food);
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
import mods.hungertweaker.events.HungerEvents;
import mods.hungertweaker.events.SpiceOfLifeCarrotFoodEatenEvent;

HungerEvents.onSOLCarrotFoodEaten(function(event as SpiceOfLifeCarrotFoodEatenEvent) {
    if (event.shouldCount) {
        print("Foods until next Carrot milestone: " ~ event.foodsUntilNextMilestone);
    }
});
```

### FoodSpoiling

Zen class: `mods.hungertweaker.FoodSpoiling`

Spoilage and saturation helpers:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `isLoaded()` | none | `bool` | Whether FoodSpoiling is loaded. |
| `getRotState(food)` | `IItemStack food` | `string` | Raw FoodSpoiling state: `SUCCESS` means the food can spoil, `PASS` means explicitly does not rot, and `FAIL` means not tracked by FoodSpoiling. |
| `canSpoil(food)` | `IItemStack food` | `bool` | Whether FoodSpoiling actively tracks and rots the food. |
| `doesNotRot(food)` | `IItemStack food` | `bool` | Whether the food is explicitly configured as non-rotting. |
| `getExpirationDays(food)` | `IItemStack food` | `double` | Configured lifetime in FoodSpoiling days, or `NaN` if the food is not configured. |
| `getBaseTicksToRot(food)` | `IItemStack food` | `int` | Lifetime in ticks before container multipliers. Returns `-1` when the food cannot spoil. |
| `getTicksToRot(food)` | `IItemStack food` | `int` | Lifetime in ticks without player/container context. |
| `getTicksToRot(player, food)` | `IPlayer player`, `IItemStack food` | `int` | Lifetime in ticks with the player's current container lifetime factor applied; `-1` means spoilage is paused or not applicable. |
| `getRemainingTicks(player, food)` | `IPlayer player`, `IItemStack food` | `int` | Estimated ticks left before the food fully spoils in the current player/container context. |
| `getSpoilage(player, food)` | `IPlayer player`, `IItemStack food` | `float` | Rot progress from `0.0` fresh to `1.0` fully spoiled. |
| `getFreshness(player, food)` | `IPlayer player`, `IItemStack food` | `float` | Freshness from `1.0` fresh to `0.0` fully spoiled. |
| `getSaturationMultiplier(player, food)` | `IPlayer player`, `IItemStack food` | `float` | Same as freshness; useful for scaling `saturationModifier`. |
| `getSpoiledSaturationModifier(player, food, saturationModifier)` | `IPlayer player`, `IItemStack food`, `float saturationModifier` | `float` | Returns `saturationModifier * freshness`. |
| `getLifetimeFactor(player, food)` | `IPlayer player`, `IItemStack food` | `double` | FoodSpoiling container lifetime factor for the player's current open container. Higher means slower spoilage; negative means paused. |
| `hasCreationTime(food)` | `IItemStack food` | `bool` | Whether the stack has FoodSpoiling's `CreationTime` tag. |
| `getCreationTime(food)` | `IItemStack food` | `long` | Stored FoodSpoiling creation world time. |
| `hasRemainingLifetime(food)` | `IItemStack food` | `bool` | Whether the stack stores paused `RemainingLifetime` instead of active creation time. |
| `getRemainingLifetime(food)` | `IItemStack food` | `int` | Stored paused lifetime in base ticks. |
| `getFoodSpoilage(player, food)` | `IPlayer player`, `IItemStack food` | `IData` map | Full FoodSpoiling data map for the stack. |

Example:

```zenscript
import mods.hungertweaker.events.HungerEvents;
import mods.hungertweaker.events.FoodSpoilingFoodValuesEvent;

HungerEvents.onFoodSpoilingSaturation(function(event as FoodSpoilingFoodValuesEvent) {
    if (event.canSpoil) {
        event.applySpoilageToSaturation();
    }
});
```

### Sanity

Zen class: `mods.hungertweaker.Sanity`

This class is registered only when Sanity is loaded. The capability methods operate on the Sanity capability attached to the target player. `recoverSanity` and `consumeSanity` take non-negative amounts; use `addSanity` when one value should support both positive recovery and negative loss. Food rules are CT-side additions and are applied by the Sanity food-eaten bridge after AppleCore reports that a food was eaten. They do not rewrite Sanity's original config files.

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `isLoaded()` | none | `bool` | Whether Sanity is loaded and its CT class can be used. This is the only method safe to call when Sanity may be absent. |
| `getSanity(player)` | `IPlayer player` | `float` | Reads the player's current sanity. |
| `getMaxSanity(player)` | `IPlayer player` | `float` | Reads the player's maximum sanity. |
| `setSanity(player, value)` | `IPlayer player`, `double value` | `void` | Sets an absolute sanity value. |
| `addSanity(player, amount)` | `IPlayer player`, `double amount` | `void` | Adds sanity when `amount` is positive, or consumes sanity when it is negative. |
| `recoverSanity(player, amount)` | `IPlayer player`, `double amount >= 0` | `void` | Recovers the specified amount of sanity. |
| `consumeSanity(player, amount)` | `IPlayer player`, `double amount >= 0` | `void` | Removes the specified amount of sanity. |
| `isEnabled(player)` | `IPlayer player` | `bool` | Whether Sanity is currently enabled for this player and dimension. |
| `setDefaultFoodValue(value)` | `double value` | `void` | Sets the CT Sanity value used for foods without a matching rule. Positive values recover sanity; negative values consume it. |
| `getDefaultFoodValue()` | none | `double` | Reads the CT default food value. It returns `0` until a default is configured. |
| `setFoodValue(food, value)` | `IIngredient food`, `double value` | `void` | Replaces all existing CT rules for the ingredient with one Sanity value. |
| `addFoodValue(food, value)` | `IIngredient food`, `double value` | `void` | Adds a CT Sanity value for every matching food. Multiple matching rules are added together. |
| `clearFoodValue(food)` | `IIngredient food` | `void` | Removes CT rules matching the ingredient. |
| `clearFoodValues()` | none | `void` | Removes all CT food rules and resets the CT default food value. |
| `getFoodValue(food)` | `IItemStack food` | `double` | Calculates the configured CT Sanity value for one concrete stack, including matching rules and the default value. |
| `setSpoiledFoodPenalty(amount)` | `double amount >= 0` | `void` | Overrides the Sanity loss when a fully spoiled food is found in inventory or eaten. This is used only when FoodSpoiling is also loaded. |
| `getSpoiledFoodPenalty()` | none | `double` | Reads the explicit CT spoiled-food penalty, or `0` when no CT override was configured. |
| `setNutritionFactors(decreaseFactor, increaseFactor, minimum, maximum)` | `double`, `double`, `float`, `float` | `void` | Overrides Sanity's Nutrition compatibility factors and average range. This takes effect only when Nutrition is also loaded. |

When Sanity and FoodSpoiling are both loaded, the spoiled-food penalty is checked every 20 ticks for each fully spoiled food stack in the player's main inventory. When a configured positive food value is eaten, recovery is multiplied by FoodSpoiling freshness (`1.0` fresh, `0.0` fully spoiled). A fully spoiled food uses the configured spoiled-food penalty instead of recovering sanity. When Sanity and Nutrition are both loaded, the average of visible nutrients is compared with the configured range and the corresponding Sanity increase/decrease factors are applied. Sanity's original config remains active for all behavior not explicitly configured through these CT methods.

Example:

```zenscript
import mods.hungertweaker.Sanity;

if (Sanity.isLoaded()) {
    Sanity.setDefaultFoodValue(0.25);
    Sanity.setFoodValue(<minecraft:golden_apple>, 4.0);
    Sanity.setFoodValue(<minecraft:rotten_flesh>, -2.0);
    Sanity.setSpoiledFoodPenalty(1.5);
    Sanity.setNutritionFactors(0.8, 1.25, 20, 80);
}
```

### Tough As Nails

Zen class: `mods.hungertweaker.ToughAsNails`

Every method other than `isLoaded()` requires Tough As Nails to be loaded. `player` is a CT `IPlayer`; numeric thirst values are TAN thirst points, hydration and exhaustion are TAN's native floating-point values, and temperature is the raw TAN scale from `0` to `25` in the reference version.

Status and configuration:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `isLoaded()` | none | `bool` | Whether TAN is loaded. |
| `isThirstEnabled()` | none | `bool` | TAN `Enable Thirst` gameplay switch. |
| `isTemperatureEnabled()` | none | `bool` | TAN `Enable Body Temperature` gameplay switch. |
| `isPeacefulEnabled()` | none | `bool` | TAN `Enable Peaceful` gameplay switch. |
| `isWorldDrinkingEnabled()` | none | `bool` | Whether drinking from water blocks is enabled. |
| `isRainDrinkingEnabled()` | none | `bool` | Whether drinking from rain is enabled. |

Player thirst:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `getThirst(player)` | `IPlayer player` | `int` | Current TAN thirst points. |
| `setThirst(player, thirst)` | `IPlayer player`, `int thirst` | `void` | Sets thirst to an absolute value. |
| `addThirst(player, amount)` | `IPlayer player`, `int amount` | `void` | Adds a relative thirst amount. |
| `getHydration(player)` | `IPlayer player` | `float` | Current hydration buffer. |
| `setHydration(player, hydration)` | `IPlayer player`, `float hydration` | `void` | Sets the hydration buffer. |
| `addHydration(player, amount)` | `IPlayer player`, `float amount` | `void` | Adds to the hydration buffer. |
| `getThirstExhaustion(player)` | `IPlayer player` | `float` | Current TAN thirst exhaustion. |
| `setThirstExhaustion(player, exhaustion)` | `IPlayer player`, `float exhaustion` | `void` | Sets thirst exhaustion. |
| `addThirstExhaustion(player, amount)` | `IPlayer player`, `float amount` | `void` | Adds thirst exhaustion. |
| `addThirstStats(player, thirst, hydration)` | `IPlayer player`, `int thirst`, `float hydration` | `void` | Calls TAN's native combined stat update. |
| `getThirstChangeTime(player)` | `IPlayer player` | `int` | TAN thirst change timer. |
| `setThirstChangeTime(player, ticks)` | `IPlayer player`, `int ticks` | `void` | Sets the thirst change timer. |
| `isThirsty(player)` | `IPlayer player` | `bool` | Whether TAN considers the player below maximum thirst. |
| `getThirstData(player)` | `IPlayer player` | `IData` map | Map with `thirst`, `hydration`, `exhaustion`, and `changeTime`. |

Player temperature:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `getTemperature(player)` | `IPlayer player` | `int` | Current raw body temperature. |
| `setTemperature(player, temperature)` | `IPlayer player`, `int temperature` | `void` | Sets raw body temperature after clamping to TAN's scale. |
| `addTemperature(player, amount)` | `IPlayer player`, `int amount` | `void` | Adds a signed temperature difference. |
| `getPlayerTargetTemperature(player)` | `IPlayer player` | `int` | TAN's calculated target temperature. |
| `getTemperatureChangeTime(player)` | `IPlayer player` | `int` | TAN temperature change timer. |
| `setTemperatureChangeTime(player, ticks)` | `IPlayer player`, `int ticks` | `void` | Sets the temperature change timer. |
| `applyTemperatureModifier(player, name, amount, rate, duration)` | `IPlayer player`, `string name`, `int amount`, `int rate`, `int duration` | `void` | Adds or refreshes TAN's named external modifier. |
| `hasTemperatureModifier(player, name)` | `IPlayer player`, `string name` | `bool` | Whether the named external modifier exists. |
| `getTemperatureModifiers(player)` | `IPlayer player` | `IData` map | Map of modifier name to `name`, `amount`, `rate`, and `endTime`. |
| `getTemperatureData(player)` | `IPlayer player` | `IData` map | Map with `temperature`, `range`, `targetTemperature`, `changeTime`, and `modifiers`. |

Temperature ranges and drinks:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `clampTemperature(temperature)` | `int temperature` | `int` | Clamps a raw temperature to TAN's current scale. |
| `getTemperatureScaleTotal()` | none | `int` | Maximum raw scale value. |
| `getTemperatureScaleMidpoint()` | none | `int` | Midpoint of the raw scale. |
| `getTemperatureRanges()` | none | `string[]` | Names such as `ICY`, `COOL`, `MILD`, `WARM`, and `HOT`. |
| `getTemperatureRange(temperature)` | `int temperature` | `string` | Range name for a raw temperature, or `UNKNOWN` outside the scale. |
| `getTemperatureRangeInfo(rangeName)` | `string rangeName` | `IData` map | Range map with `name`, `lowerBound`, `upperBound`, `middle`, and `size`. |
| `getWaterTypes()` | none | `string[]` | TAN water types, including `NORMAL`, `PURIFIED`, and `RAIN`. |
| `getWaterTypeInfo(typeName)` | `string typeName` | `IData` map | Water drink map with `thirst`, `hydration`, and `poisonChance`. |
| `getDrinkData(food)` | `IItemStack food` | `IData` map | Reads matching TAN item/config/potion drink data. `source` is `item`, `config`, `potion`, or `none`. |
| `getFoodData(food)` | `IItemStack food` | `IData` map | Alias for `getDrinkData`; useful from shared food events. |
| `drink(player, thirst, hydration)` | `IPlayer player`, `int thirst`, `float hydration` | `void` | Calls TAN's `IThirst.addStats`; it does not apply poison effects. |

Example with explicit imports and parameters. When TAN is part of the modpack, the standard
form is to call the API directly:

```zenscript
import mods.hungertweaker.ToughAsNails;

ToughAsNails.addThirst(player, -2); // player: IPlayer, amount: int
ToughAsNails.addHydration(player, 0.25); // amount: float
print("TAN temperature = " ~ ToughAsNails.getTemperature(player));
print("TAN range = " ~ ToughAsNails.getTemperatureRange(ToughAsNails.getTemperature(player)));
```

### SimpleDifficulty

Zen class: `mods.hungertweaker.SimpleDifficulty`

Every method other than `isLoaded()` requires SimpleDifficulty to be loaded. When SD is part of
the modpack, call these methods directly using the normal `mods.hungertweaker.SimpleDifficulty`
namespace. Use `isLoaded()` only when one script must also run without SD. SD thirst levels are
integer thirst points, while saturation and exhaustion use SD's native floating-point values.
Temperature levels are clamped to SD's `0..25` scale. `IData properties` parameters are string
maps, for example `{"burning": "true"}`.

Status and player capabilities:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `isLoaded()` | none | `bool` | Whether SD is loaded. |
| `isThirstEnabled()` | none | `bool` | SD server thirst switch. |
| `isTemperatureEnabled()` | none | `bool` | SD server temperature switch. |
| `getThirstLevel(player)`, `setThirstLevel(player, thirst)`, `addThirstLevel(player, amount)` | `IPlayer player`; setters also take `int thirst` or `int amount` | `int` / `void` | Reads or changes the thirst level. |
| `getThirstSaturation(player)`, `setThirstSaturation(player, saturation)`, `addThirstSaturation(player, amount)` | `IPlayer player`; setters also take `float saturation` or `float amount` | `float` / `void` | Reads or changes thirst saturation. |
| `getThirstExhaustion(player)`, `setThirstExhaustion(player, exhaustion)`, `addThirstExhaustion(player, amount)` | `IPlayer player`; setters also take `float exhaustion` or `float amount` | `float` / `void` | Reads or changes thirst exhaustion. |
| `getThirstTickTimer(player)`, `setThirstTickTimer(player, ticks)`, `addThirstTickTimer(player, ticks)` | `IPlayer player`; setters also take `int ticks` | `int` / `void` | Reads or changes SD's thirst tick timer. |
| `getThirstDamageCounter(player)`, `setThirstDamageCounter(player, value)`, `addThirstDamageCounter(player, value)` | `IPlayer player`; setters also take `int value` | `int` / `void` | Reads or changes the thirst damage counter. |
| `isThirsty(player)` | `IPlayer player` | `bool` | SD capability's `isThirsty()` result. |
| `getThirstData(player)` | `IPlayer player` | `IData` map | Map with `level`, `saturation`, `exhaustion`, `tickTimer`, `damageCounter`, and `isThirsty`. |

Temperature and temporary modifiers:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `getTemperatureLevel(player)`, `setTemperatureLevel(player, temperature)`, `addTemperatureLevel(player, amount)` | `IPlayer player`; setters also take `int temperature` or `int amount` | `int` / `void` | Reads or changes the clamped SD temperature level. |
| `getTemperatureTickTimer(player)`, `setTemperatureTickTimer(player, ticks)`, `addTemperatureTickTimer(player, ticks)` | `IPlayer player`; setters also take `int ticks` | `int` / `void` | Reads or changes the temperature tick timer. |
| `getTemperatureDamageCounter(player)`, `setTemperatureDamageCounter(player, value)`, `addTemperatureDamageCounter(player, value)` | `IPlayer player`; setters also take `int value` | `int` / `void` | Reads or changes the temperature damage counter. |
| `getTemperatureEnum(temperature)` | `int temperature` | `string` | Returns `FREEZING`, `COLD`, `NORMAL`, `HOT`, or `BURNING`. |
| `getTemperatureEnumInfo(name)` | `string name` | `IData` map | Range map with `name`, `lowerBound`, `upperBound`, and `middle`. |
| `getTemperatureEnums()` | none | `string[]` | All SD temperature category names. |
| `getPlayerTargetTemperature(player)` | `IPlayer player` | `int` | Calculated target temperature, not the current capability level. |
| `getWorldTemperature(player)` | `IPlayer player` | `int` | World temperature at the player's position. |
| `getWorldTemperature(player, x, y, z)` | `IPlayer player`, `int x`, `int y`, `int z` | `int` | World temperature at a coordinate in the player's dimension. |
| `getTemperatureData(player)` | `IPlayer player` | `IData` map | Map with `level`, `enum`, `tickTimer`, `damageCounter`, and `temporaryModifiers`. |
| `getTemporaryModifiers(player)` | `IPlayer player` | `IData` map | Map of modifier name to `temperature` and `duration`. |
| `setTemporaryModifier(player, name, temperature, duration)` | `IPlayer player`, `string name`, `float temperature`, `int duration` | `void` | Adds or replaces one SD temporary modifier. |
| `clearTemporaryModifiers(player)` | `IPlayer player` | `void` | Removes all temporary modifiers. |
| `setArmorTemperature(stack, temperature)` | `IItemStack stack`, `float temperature` | `void` | Writes SD's armor temperature NBT tag. |
| `getArmorTemperature(stack)` | `IItemStack stack` | `float` | Reads the armor temperature tag, or SD's missing-tag default. |
| `removeArmorTemperature(stack)` | `IItemStack stack` | `void` | Removes SD's armor temperature tag. |

Thirst types and configured consumables:

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `getThirstTypes()` | none | `string[]` | Names such as `NORMAL`, `SALT`, `RAIN`, `POTION`, and `PURIFIED`. |
| `getThirstTypeInfo(name)` | `string name` | `IData` map | Type map with `name`, `id`, `thirst`, `saturation`, and `thirstyChance`. |
| `takeDrink(player, thirst, saturation, thirstyChance)` | `IPlayer player`, `int thirst`, `float saturation`, `float thirstyChance` | `void` | Calls SD's native drink helper with all values. Chance is `0.0..1.0`. |
| `takeDrink(player, thirst, saturation)` | `IPlayer player`, `int thirst`, `float saturation` | `void` | Calls SD's drink helper with no dirty/thirsty chance. |
| `takeDrink(player, thirstType)` | `IPlayer player`, `string thirstType` | `void` | Uses a named SD `ThirstEnum`; names are case-insensitive. |
| `getConsumableThirst(food)` | `IItemStack food` | `IData` map | Reads the first matching SD consumable thirst JSON entry. |
| `getConsumableTemperature(food)` | `IItemStack food` | `IData` map | Reads the first matching SD consumable temperature JSON entry. |
| `getFoodData(food)` | `IItemStack food` | `IData` map | Combined map with nested `thirst` and `temperature` config data. |

Runtime `JsonConfig` registration:

`registerConsumableThirst` can register thirst values for **any registered item**, not only
SimpleDifficulty's built-in drinks. The item is matched by registry name, with optional
metadata and NBT restrictions. The registration is applied when the item finishes a normal
item-use action (`LivingEntityUseItemEvent.Finish`), so registering a non-consumable item does
not automatically make it edible or usable. Give that item its own food/drink/use behavior if
it cannot already be consumed.

| Method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `registerArmorTemperature(stack, temperature)` | `IItemStack stack`, `float temperature` | `void` | Registers armor temperature using the stack's item/metadata. |
| `registerArmorTemperatureByName(registryName, temperature[, metadata[, nbt]])` | `string registryName`, `float temperature`, optional `int metadata`, optional `string nbt` | `void` | Registers armor temperature by registry name. Omitted metadata is `-1`; omitted NBT is unrestricted. |
| `registerBlockTemperature(registryName, temperature)` | `string registryName`, `float temperature` | `bool` | Registers a block temperature without block properties. The boolean is SD's replacement result. |
| `registerBlockTemperatureWithProperties(registryName, temperature, properties)` | `string registryName`, `float temperature`, `IData properties` | `bool` | Registers a block temperature for an exact property map. |
| `registerFluidTemperature(fluidName, temperature)` | `string fluidName`, `float temperature` | `void` | Registers a fluid temperature. |
| `registerConsumableTemperature(group, food, temperature, duration)` | `string group`, `IItemStack food`, `float temperature`, `int duration` | `void` | Registers a consumable temporary temperature effect. |
| `registerConsumableTemperatureByName(group, registryName, temperature, duration[, metadata[, nbt]])` | `string group`, `string registryName`, `float temperature`, `int duration`, optional `int metadata`, optional `string nbt` | `void` | Registry-name version of the consumable temperature registration. |
| `registerConsumableThirst(food, amount, saturation, thirstyChance)` | `IItemStack food`, `int amount`, `float saturation`, `float thirstyChance` | `void` | Registers thirst for any registered item stack. The item must still be usable/consumable to trigger SD's finish event. |
| `registerConsumableThirstByName(registryName, amount, saturation, thirstyChance[, metadata[, nbt]])` | `string registryName`, `int amount`, `float saturation`, `float thirstyChance`, optional `int metadata`, optional `string nbt` | `void` | Registers thirst for any item by registry name, optionally restricted by metadata and NBT. |
| `registerHeldItem(stack, temperature)` | `IItemStack stack`, `float temperature` | `void` | Registers temperature for a held item. |
| `registerHeldItemByName(registryName, temperature[, metadata[, nbt]])` | `string registryName`, `float temperature`, optional `int metadata`, optional `string nbt` | `void` | Registry-name version of the held-item registration. |
| `registerDimensionTemperature(dimension, temperature)` | `int dimension`, `float temperature` | `void` | Registers a dimension temperature by numeric dimension id. |
| `registerDimensionTemperatureByName(dimension, temperature)` | `string dimension`, `float temperature` | `void` | String form of dimension registration. |

Example with SD JSON registration parameters:

```zenscript
import mods.hungertweaker.SimpleDifficulty;

// Any item can be registered; this example makes an apple restore thirst.
SimpleDifficulty.registerConsumableThirst(<minecraft:apple>, 4, 0.5, 0.0);
SimpleDifficulty.registerConsumableThirstByName(
    "minecraft:milk_bucket", 8, 0.5, 0.0, -1, null
);
SimpleDifficulty.registerConsumableTemperatureByName("drink", "minecraft:milk_bucket", -1.0, 1200, -1);
SimpleDifficulty.registerBlockTemperatureWithProperties(
    "minecraft:campfire", 6.0, {"burning": "true"}
);
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
| `onFoodSpoilingFoodValues(handler)` | `mods.hungertweaker.events.FoodSpoilingFoodValuesEvent` | AppleCore `GetPlayerFoodValues` fires and FoodSpoiling is loaded. | FoodSpoiling rot data plus writable `hunger` and `saturationModifier`. |
| `onFoodSpoilingSaturation(handler)` | Same as above | Alias for FoodSpoiling saturation handling. | Same as above. |
| `onToughAsNailsDrink(handler)` | `mods.hungertweaker.events.ToughAsNailsDrinkEvent` | A server-side `LivingEntityUseItemEvent.Finish` matches a TAN drink and TAN is loaded. | Finished `food`, `player`, TAN `drink` data, and current thirst stats. |
| `onTANDrink(handler)` | Same as above | Alias for TAN drink handling. | Same as above. |
| `onSimpleDifficultyDrink(handler)` | `mods.hungertweaker.events.SimpleDifficultyDrinkEvent` | A server-side `LivingEntityUseItemEvent.Finish` matches an SD drink and SD is loaded. | Finished `food`, `player`, SD `drink` data, and current thirst stats. |
| `onSDDrink(handler)` | Same as above | Alias for SD drink handling. | Same as above. |
| `onSanityFoodEaten(handler)` | `mods.hungertweaker.events.SanityFoodEatenEvent` | AppleCore `FoodEaten` fires and Sanity is loaded. | CT Sanity food value, current sanity, freshness, and sanity mutation methods. |

Each registration method takes one `handler` parameter. In ZenScript, pass a function with one event argument:

```zenscript
import mods.hungertweaker.events.HungerEvents;
import mods.hungertweaker.events.NutritionFoodEatenEvent;

HungerEvents.onNutritionFoodEaten(function(event as NutritionFoodEatenEvent) {
    // Use event getters and methods here.
});
```

Inheritance note: `NutritionFoodEatenEvent`, `SpiceOfLifeFoodEatenEvent`, and `SpiceOfLifeCarrotFoodEatenEvent` all inherit the shared `FoodEatenEvent` getters, while `FoodSpoilingFoodValuesEvent` inherits the shared `GetFoodValuesEvent` getters. `ToughAsNailsDrinkEvent` and `SimpleDifficultyDrinkEvent` inherit `player` and `food` from the shared drink event base. The event-specific tables below only apply to that event family.

The Nutrition, The Spice of Life, and Carrot Edition food-eaten compatibility events extend `FoodEatenEvent`, so they also have the normal food eaten getters:

| Getter | Parameters | Type | Meaning |
| --- | --- | --- | --- |
| `event.player` | none | `IPlayer` | Player who ate the food. |
| `event.food` | none | `IItemStack` | Item stack that was eaten. |
| `event.hunger` | none | `int` | Food hunger value used by AppleCore. |
| `event.saturationModifier` | none | `float` | Food saturation modifier used by AppleCore. |
| `event.hungerAdded` | none | `int` | Hunger points actually added after Minecraft's food logic. |
| `event.saturationAdded` | none | `float` | Saturation points actually added after Minecraft's food logic. |

`FoodSpoilingFoodValuesEvent` extends `GetFoodValuesEvent`, so it fires before the food is eaten and can modify the food values:

| Getter or setter | Parameters | Type | Meaning |
| --- | --- | --- | --- |
| `event.player` | none | `IPlayer` | Player whose food values are being calculated. |
| `event.food` | none | `IItemStack` | Food item stack being calculated. |
| `event.hunger` | none, or `int hunger` when assigned | `int` | Writable hunger value for this food calculation. |
| `event.saturationModifier` | none, or `float saturationModifier` when assigned | `float` | Writable saturation modifier; this is the main value to change for spoilage-based saturation loss. |
| `event.unmodifiedHunger` | none | `int` | Original hunger value before this event's edits. |
| `event.unmodifiedSaturationModifier` | none | `float` | Original saturation modifier before this event's edits. |

`NutritionFoodEatenEvent` extra API:

| Getter or method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `event.foodNutrition` | none | `IData` map | Nutrition gain calculated for `event.food` and `event.player`. |
| `event.playerNutrition` | none | `IData` map | Current player nutrient values. |
| `event.getNutrient(nutrientName)` | `string nutrientName` | `float` | Reads one nutrient from the player. |
| `event.setNutrient(nutrientName, value)` | `string nutrientName`, `float value` | `void` | Sets one player nutrient to an absolute value. |
| `event.addNutrient(nutrientName, amount)` | `string nutrientName`, `float amount` | `void` | Adds a relative amount to one player nutrient. |
| `event.resetNutrient(nutrientName)` | `string nutrientName` | `void` | Resets one player nutrient. |
| `event.dairy`, `event.fruit`, `event.grain`, `event.protein`, `event.vegetable` | none | `float` | Shortcut getters for Nutrition's five default nutrients. |
| `event.dairy = value`, `event.fruit = value`, `event.grain = value`, `event.protein = value`, `event.vegetable = value` | `float value` | `void` | Shortcut setters for Nutrition's five default nutrients. |

`SpiceOfLifeFoodEatenEvent` extra API:

| Getter or method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `event.modifier` | none | `float` | Current Spice of Life diminishing returns multiplier for `event.food`. |
| `event.history` | none | `IData` map | Player's Spice of Life history summary. |
| `event.foodHistory` | none | `IData` map | Spice of Life data for `event.food`, including modifier, count, food groups, and total food values. |
| `event.getFoodCount()` | none | `int` | Count of `event.food` in the current Spice of Life history. |
| `event.getFoodGroupCount(foodGroup)` | `string foodGroup` | `int` | Count of `event.food` or matching foods in that food group. |
| `event.getFoodGroups()` | none | `string[]` | Food groups that match `event.food`. |
| `event.resetFoodHistory()` | none | `void` | Clears Spice of Life history and all-time counters for `event.player`. |
| `event.syncFoodHistory()` | none | `void` | Syncs Spice of Life history to the client on server side. |

`SpiceOfLifeCarrotFoodEatenEvent` extra API:

| Getter or method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `event.hasEaten` | none | `bool` | Whether the player's current Carrot Edition food list contains `event.food`. |
| `event.shouldCount` | none | `bool` | Whether `event.food` passes Carrot Edition config rules for milestone progress. |
| `event.eatenFoodCount` | none | `int` | Unique foods stored in the player's Carrot food list. |
| `event.foodsEatenForMilestones` | none | `int` | Unique foods that currently count toward milestones after filtering. |
| `event.milestonesAchieved` | none | `int` | Number of milestone thresholds reached. |
| `event.nextMilestone` | none | `int` | Food count required for the next milestone, or `-1` if maxed. |
| `event.foodsUntilNextMilestone` | none | `int` | Remaining count until the next milestone, or a negative value if maxed. |
| `event.progress` | none | `IData` map | Full Carrot progress map for the player. |
| `event.foodProgress` | none | `IData` map | Carrot data for `event.food`, including `hasEaten`, `shouldCount`, `isAllowed`, `isHearty`, and `progress`. |
| `event.addFood()` | none | `bool` | Adds `event.food` to the Carrot food list, then updates max health and syncs. |
| `event.clearFoods()` | none | `void` | Clears the player's Carrot food list, updates max health, and syncs. |
| `event.updateMaxHealth()` | none | `bool` | Reapplies the Carrot max-health modifier. Returns whether the modifier changed. |
| `event.syncFoodList()` | none | `void` | Syncs the Carrot food list to the client on server side. |

`SanityFoodEatenEvent` extra API:

| Getter or method | Parameters | Type or Returns | Meaning |
| --- | --- | --- | --- |
| `event.foodValue` | none | `double` | CT Sanity value calculated for the eaten stack. Positive values recover sanity; negative values consume it. |
| `event.sanity` | none | `float` | Current sanity when the getter is read. |
| `event.freshness` | none | `float` | FoodSpoiling freshness for the eaten stack, or `1.0` when FoodSpoiling is not loaded. |
| `event.setSanity(value)` | `double value` | `void` | Sets the player's sanity. |
| `event.addSanity(amount)` | `double amount` | `void` | Adds or consumes sanity depending on the sign. |
| `event.recoverSanity(amount)` | `double amount >= 0` | `void` | Recovers sanity. |
| `event.consumeSanity(amount)` | `double amount >= 0` | `void` | Consumes sanity. |
| `event.applyFoodValue()` | none | `void` | Applies the configured CT food value again. The normal bridge has already applied it before publishing this event, so call this only when deliberately reapplying the value. |

`FoodSpoilingFoodValuesEvent` extra API:

| Getter or method | Parameters | Returns | Meaning |
| --- | --- | --- | --- |
| `event.rotState` | none | `string` | Raw FoodSpoiling rot state: `SUCCESS`, `PASS`, or `FAIL`. |
| `event.canSpoil` | none | `bool` | Whether `event.food` is actively tracked by FoodSpoiling. |
| `event.saturationMultiplier` | none | `float` | Same as `event.freshness`; intended for saturation scaling. |
| `event.ticksToRot` | none | `int` | Total ticks before rot in this player/container context. |
| `event.remainingTicks` | none | `int` | Estimated ticks left before full spoilage. |
| `event.applySpoilageToSaturation()` | none | `void` | Sets `event.saturationModifier` to `event.saturationModifier * event.saturationMultiplier`. |
| `event.multiplySaturation(multiplier)` | `float multiplier` | `void` | Multiplies the current `event.saturationModifier` by a custom value. |

The normal `FoodEatenEvent` and `GetFoodValuesEvent` also expose these optional compatibility getters:

| Getter | Type | Meaning |
| --- | --- | --- |
| `event.nutrition` | `IData` map | Food nutrition gain map, or empty map when Nutrition is not loaded. |
| `event.playerNutrition` | `IData` map | Player nutrition values, or empty map when Nutrition is not loaded. |
| `event.spiceOfLife` | `IData` map | Spice of Life food/history data for this food, or empty map when not loaded. |
| `event.spiceOfLifeModifier` | `float` | Spice of Life modifier for this food, or `1.0` when not loaded. |
| `event.spiceOfLifeCarrot` | `IData` map | Carrot Edition food/progress data for this food, or empty map when not loaded. |
| `event.solCarrot` | `IData` map | Alias of `event.spiceOfLifeCarrot`. |
| `event.foodSpoiling` | `IData` map | FoodSpoiling rot data for this food, or empty map when not loaded. |
| `event.foodSpoilage` | `IData` map | Alias of `event.foodSpoiling`. |
| `event.spoilage` | `float` | FoodSpoiling rot progress, or `0.0` when not loaded. |
| `event.freshness` | `float` | FoodSpoiling freshness, or `1.0` when not loaded. |
| `event.toughAsNails` | `IData` map | TAN drink data for this food, or empty map when TAN is not loaded. |
| `event.simpleDifficulty` | `IData` map | SD configured thirst and temperature data for this food, or empty map when SD is not loaded. |

`ToughAsNailsDrinkEvent` extra API:

| Getter | Type | Meaning |
| --- | --- | --- |
| `event.player` | `IPlayer` | Player who finished using the drink. |
| `event.food` | `IItemStack` | Item stack from the finished-use event. |
| `event.drink` | `IData` map | TAN drink map with `matched`, `source`, `thirst`, `hydration`, and `poisonChance`. |
| `event.thirst` | `int` | Current TAN thirst after the finish event. |
| `event.hydration` | `float` | Current TAN hydration after the finish event. |
| `event.exhaustion` | `float` | Current TAN thirst exhaustion after the finish event. |

`SimpleDifficultyDrinkEvent` extra API:

| Getter | Type | Meaning |
| --- | --- | --- |
| `event.player` | `IPlayer` | Player who finished using the drink. |
| `event.food` | `IItemStack` | Item stack from the finished-use event. |
| `event.drink` | `IData` map | Matching SD consumable thirst map with `matched`, `thirst`, `saturation`, `thirstyChance`, and `identity`. |
| `event.thirstLevel` | `int` | Current SD thirst level after the finish event. |
| `event.saturation` | `float` | Current SD thirst saturation after the finish event. |
| `event.exhaustion` | `float` | Current SD thirst exhaustion after the finish event. |

Drink event example:

```zenscript
import mods.hungertweaker.events.HungerEvents;
import mods.hungertweaker.events.ToughAsNailsDrinkEvent;
import mods.hungertweaker.events.SimpleDifficultyDrinkEvent;

HungerEvents.onToughAsNailsDrink(function(event as ToughAsNailsDrinkEvent) {
    print("TAN drink source = " ~ event.drink.source);
});

HungerEvents.onSimpleDifficultyDrink(function(event as SimpleDifficultyDrinkEvent) {
    if (event.drink.matched) {
        print("SD thirst after drink = " ~ event.thirstLevel);
    }
});
```
