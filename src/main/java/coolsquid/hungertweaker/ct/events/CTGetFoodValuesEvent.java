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
import squeek.applecore.api.food.FoodEvent.GetPlayerFoodValues;
import squeek.applecore.api.food.FoodValues;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass("mods.hungertweaker.events.GetFoodValuesEvent")
public class CTGetFoodValuesEvent implements IPlayerEvent {

	private final GetPlayerFoodValues internal;

	public CTGetFoodValuesEvent(GetPlayerFoodValues internal) {
		this.internal = internal;
	}

	@ZenGetter("hunger")
	public int getHunger() {
		return this.internal.foodValues.hunger;
	}

	@ZenSetter("hunger")
	public void setHunger(int hunger) {
		this.internal.foodValues = new FoodValues(hunger, this.getSaturationModifier());
	}

	@ZenGetter("saturationModifier")
	public float getSaturationModifier() {
		return this.internal.foodValues.saturationModifier;
	}

	@ZenSetter("saturationModifier")
	public void setSaturationModifier(float saturationModifier) {
		this.internal.foodValues = new FoodValues(this.getHunger(), saturationModifier);
	}

	@ZenGetter("unmodifiedHunger")
	public int getUnmodifiedHunger() {
		return this.internal.unmodifiedFoodValues.hunger;
	}

	@ZenGetter("unmodifiedSaturationModifier")
	public float getUnmodifiedSaturationModifier() {
		return this.internal.unmodifiedFoodValues.saturationModifier;
	}

	@ZenGetter("food")
	public IItemStack getFood() {
		return CraftTweakerMC.getIItemStack(this.internal.food);
	}

	@ZenGetter("nutrition")
	public IData getNutrition() {
		return CTNutrition.isLoaded() ? CTNutrition.foodNutritionData(this.internal.food, this.internal.player)
				: DataMap.EMPTY;
	}

	@ZenGetter("playerNutrition")
	public IData getPlayerNutrition() {
		return CTNutrition.isLoaded() ? CTNutrition.playerNutritionData(this.internal.player) : DataMap.EMPTY;
	}

	@ZenGetter("spiceOfLife")
	public IData getSpiceOfLife() {
		return CTSpiceOfLife.isLoaded() ? CTSpiceOfLife.foodDataForPlayer(this.internal.player, this.internal.food)
				: DataMap.EMPTY;
	}

	@ZenGetter("spiceOfLifeModifier")
	public float getSpiceOfLifeModifier() {
		return CTSpiceOfLife.isLoaded() ? CTSpiceOfLife.getFoodModifier(this.getPlayer(), this.getFood()) : 1;
	}

	@ZenGetter("spiceOfLifeCarrot")
	public IData getSpiceOfLifeCarrot() {
		return CTSpiceOfLifeCarrotEdition.isLoaded()
				? CTSpiceOfLifeCarrotEdition.foodDataForPlayer(this.internal.player, this.internal.food)
				: DataMap.EMPTY;
	}

	@ZenGetter("solCarrot")
	public IData getSOLCarrot() {
		return this.getSpiceOfLifeCarrot();
	}

	@ZenGetter("foodSpoiling")
	public IData getFoodSpoiling() {
		return CTFoodSpoiling.isLoaded() ? CTFoodSpoiling.foodSpoilingData(this.internal.player, this.internal.food)
				: DataMap.EMPTY;
	}

	@ZenGetter("foodSpoilage")
	public IData getFoodSpoilage() {
		return this.getFoodSpoiling();
	}

	@ZenGetter("spoilage")
	public float getSpoilage() {
		return CTFoodSpoiling.isLoaded() ? CTFoodSpoiling.getSpoilage(this.internal.player, this.internal.food) : 0;
	}

	@ZenGetter("freshness")
	public float getFreshness() {
		return CTFoodSpoiling.isLoaded() ? CTFoodSpoiling.getFreshness(this.internal.player, this.internal.food) : 1;
	}

	@Override
	public IPlayer getPlayer() {
		return CraftTweakerMC.getIPlayer(this.internal.player);
	}
}
