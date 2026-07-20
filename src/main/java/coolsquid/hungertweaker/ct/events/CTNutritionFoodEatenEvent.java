package coolsquid.hungertweaker.ct.events;

import coolsquid.hungertweaker.ct.compat.CTNutrition;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import squeek.applecore.api.food.FoodEvent.FoodEaten;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass("mods.hungertweaker.events.NutritionFoodEatenEvent")
public class CTNutritionFoodEatenEvent extends CTFoodEatenEvent {

	private final FoodEaten internal;

	public CTNutritionFoodEatenEvent(FoodEaten internal) {
		super(internal);
		this.internal = internal;
	}

	@ZenGetter("foodNutrition")
	public IData getFoodNutrition() {
		return CTNutrition.foodNutritionData(this.internal.food, this.internal.player);
	}

	@ZenGetter("playerNutrition")
	public IData getPlayerNutrition() {
		return CTNutrition.playerNutritionData(this.internal.player);
	}

	@ZenMethod
	public float getNutrient(String nutrientName) {
		return CTNutrition.getNutrient(this.getPlayer(), nutrientName);
	}

	@ZenMethod
	public void setNutrient(String nutrientName, float value) {
		CTNutrition.setNutrient(this.getPlayer(), nutrientName, value);
	}

	@ZenMethod
	public void addNutrient(String nutrientName, float amount) {
		CTNutrition.addNutrient(this.getPlayer(), nutrientName, amount);
	}

	@ZenMethod
	public void resetNutrient(String nutrientName) {
		CTNutrition.resetNutrient(this.getPlayer(), nutrientName);
	}

	@ZenGetter("dairy")
	public float getDairy() {
		return CTNutrition.getDairy(this.getPlayer());
	}

	@ZenSetter("dairy")
	public void setDairy(float value) {
		CTNutrition.setDairy(this.getPlayer(), value);
	}

	@ZenGetter("fruit")
	public float getFruit() {
		return CTNutrition.getFruit(this.getPlayer());
	}

	@ZenSetter("fruit")
	public void setFruit(float value) {
		CTNutrition.setFruit(this.getPlayer(), value);
	}

	@ZenGetter("grain")
	public float getGrain() {
		return CTNutrition.getGrain(this.getPlayer());
	}

	@ZenSetter("grain")
	public void setGrain(float value) {
		CTNutrition.setGrain(this.getPlayer(), value);
	}

	@ZenGetter("protein")
	public float getProtein() {
		return CTNutrition.getProtein(this.getPlayer());
	}

	@ZenSetter("protein")
	public void setProtein(float value) {
		CTNutrition.setProtein(this.getPlayer(), value);
	}

	@ZenGetter("vegetable")
	public float getVegetable() {
		return CTNutrition.getVegetable(this.getPlayer());
	}

	@ZenSetter("vegetable")
	public void setVegetable(float value) {
		CTNutrition.setVegetable(this.getPlayer(), value);
	}
}
