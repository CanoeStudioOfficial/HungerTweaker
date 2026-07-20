package coolsquid.hungertweaker.ct.events;

import coolsquid.hungertweaker.ct.compat.CTFoodSpoiling;
import coolsquid.hungertweaker.ct.compat.CTNutrition;
import coolsquid.hungertweaker.ct.compat.CTSpiceOfLife;
import coolsquid.hungertweaker.ct.compat.CTSpiceOfLifeCarrotEdition;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.api.event.IPlayerEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import squeek.applecore.api.food.FoodEvent.FoodEaten;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenRegister
@ZenClass("mods.hungertweaker.events.FoodEatenEvent")
public class CTFoodEatenEvent implements IPlayerEvent {

	private final FoodEaten internal;

	public CTFoodEatenEvent(FoodEaten internal) {
		this.internal = internal;
	}

	@ZenGetter
	public int hunger() {
		return this.internal.foodValues.hunger;
	}

	@ZenGetter
	public float saturationModifier() {
		return this.internal.foodValues.saturationModifier;
	}

	@ZenGetter
	public int hungerAdded() {
		return this.internal.hungerAdded;
	}

	@ZenGetter
	public float saturationAdded() {
		return this.internal.saturationAdded;
	}

	@ZenGetter
	public IItemStack food() {
		return CraftTweakerMC.getIItemStack(this.internal.food);
	}

	@ZenGetter("nutrition")
	public IData nutrition() {
		return CTNutrition.isLoaded() ? CTNutrition.foodNutritionData(this.internal.food, this.internal.player)
				: DataMap.EMPTY;
	}

	@ZenGetter("playerNutrition")
	public IData playerNutrition() {
		return CTNutrition.isLoaded() ? CTNutrition.playerNutritionData(this.internal.player) : DataMap.EMPTY;
	}

	@ZenGetter("spiceOfLife")
	public IData spiceOfLife() {
		return CTSpiceOfLife.isLoaded() ? CTSpiceOfLife.foodDataForPlayer(this.internal.player, this.internal.food)
				: DataMap.EMPTY;
	}

	@ZenGetter("spiceOfLifeModifier")
	public float spiceOfLifeModifier() {
		return CTSpiceOfLife.isLoaded() ? CTSpiceOfLife.getFoodModifier(this.getPlayer(), this.food()) : 1;
	}

	@ZenGetter("spiceOfLifeCarrot")
	public IData spiceOfLifeCarrot() {
		return CTSpiceOfLifeCarrotEdition.isLoaded()
				? CTSpiceOfLifeCarrotEdition.foodDataForPlayer(this.internal.player, this.internal.food)
				: DataMap.EMPTY;
	}

	@ZenGetter("solCarrot")
	public IData solCarrot() {
		return this.spiceOfLifeCarrot();
	}

	@ZenGetter("foodSpoiling")
	public IData foodSpoiling() {
		return CTFoodSpoiling.isLoaded() ? CTFoodSpoiling.foodSpoilingData(this.internal.player, this.internal.food)
				: DataMap.EMPTY;
	}

	@ZenGetter("foodSpoilage")
	public IData foodSpoilage() {
		return this.foodSpoiling();
	}

	@ZenGetter("spoilage")
	public float spoilage() {
		return CTFoodSpoiling.isLoaded() ? CTFoodSpoiling.getSpoilage(this.internal.player, this.internal.food) : 0;
	}

	@ZenGetter("freshness")
	public float freshness() {
		return CTFoodSpoiling.isLoaded() ? CTFoodSpoiling.getFreshness(this.internal.player, this.internal.food) : 1;
	}

	@Override
	public IPlayer getPlayer() {
		return CraftTweakerMC.getIPlayer(this.internal.player);
	}
}
