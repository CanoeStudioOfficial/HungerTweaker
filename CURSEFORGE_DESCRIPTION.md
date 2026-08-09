# HungerTweaker Community Edition

HungerTweaker Community Edition is an unofficial community-maintained continuation of the original HungerTweaker project for Minecraft 1.12.2. It exposes the AppleCore hunger API to CraftTweaker scripts, allowing modpack developers to configure food values and react to hunger, exhaustion, starvation, and regeneration events.

This project is maintained by CanoeStudio. It is not affiliated with or endorsed by the original author or the original HungerTweaker project.

## Original Project Credit

This project is based on the original HungerTweaker project:

- Original repository: https://github.com/coolsquid/HungerTweaker
- Original Wiki and API documentation: https://github.com/coolsquid/HungerTweaker/wiki

Credit goes to coolsquid and the original HungerTweaker contributors for the original project, API design, and implementation. The original API documentation has been migrated into this repository's README so the Community Edition can provide a self-contained reference.

## How This Project Differs

HungerTweaker Community Edition keeps the original HungerTweaker and AppleCore CraftTweaker API while adding:

- Nutrition compatibility APIs for the five default nutrients (`dairy`, `fruit`, `grain`, `protein`, and `vegetable`), custom nutrient maps, player nutrition values, food nutrition calculation, and Nutrition food-eaten events.
- The Spice of Life compatibility APIs for diminishing-return modifiers, food history, food groups, statistics, and food-eaten events.
- Spice of Life: Carrot Edition compatibility APIs for unique-food progress, milestones, health modifiers, configuration checks, synchronization, and food-eaten events.
- FoodSpoiling compatibility APIs for rot state, expiration, remaining lifetime, spoilage, freshness, and spoilage-based saturation adjustment events.
- Optional-mod compatibility handling so the integrations only activate when their corresponding mod is loaded, including compatibility with either Spice of Life variant when installed alone.
- A complete in-repository CraftTweaker reference with imports, method signatures, parameter meanings, return values, event timing, and examples.

The original core API paths remain unchanged. Compatibility integrations are optional and do not replace the configuration systems of the supported mods. For Nutrition, the supported branch or fork must preserve the expected `ca.wescook.nutrition` package layout and public API.

## License

The original project and this Community Edition are released under the [Unlicense](https://github.com/CanoeStudioOfficial/HungerTweaker/blob/1.12.2/LICENSE). The Unlicense is a public-domain dedication intended to release the work to the public domain, with a permissive fallback license in jurisdictions where a public-domain dedication is not recognized. See also the [Unlicense text](https://unlicense.org/).

## Main Features

- Modify food hunger and saturation values through `IIngredient.foodValues`.
- Configure exhaustion, starvation, normal regeneration, saturated regeneration, peaceful regeneration, and the AppleCore food HUD.
- Register CraftTweaker handlers for core AppleCore hunger events.
- Extend food events with optional Nutrition, Spice of Life, Carrot Edition, and FoodSpoiling data when those mods are present.
