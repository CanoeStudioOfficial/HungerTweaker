package coolsquid.hungertweaker.ct.events;

import coolsquid.hungertweaker.ct.compat.CTSpiceOfLife;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.minecraft.CraftTweakerMC;
import squeek.applecore.api.food.FoodEvent.FoodEaten;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.hungertweaker.events.SpiceOfLifeFoodEatenEvent")
public class CTSpiceOfLifeFoodEatenEvent extends CTFoodEatenEvent {

	private final FoodEaten internal;

	public CTSpiceOfLifeFoodEatenEvent(FoodEaten internal) {
		super(internal);
		this.internal = internal;
	}

	@ZenGetter("modifier")
	public float getModifier() {
		return CTSpiceOfLife.getFoodModifier(this.getPlayer(), CraftTweakerMC.getIItemStack(this.internal.food));
	}

	@ZenGetter("history")
	public IData getHistory() {
		return CTSpiceOfLife.foodHistoryData(this.internal.player);
	}

	@ZenGetter("foodHistory")
	public IData getFoodHistory() {
		return CTSpiceOfLife.foodDataForPlayer(this.internal.player, this.internal.food);
	}

	@ZenMethod
	public int getFoodCount() {
		return CTSpiceOfLife.getFoodCount(this.getPlayer(), CraftTweakerMC.getIItemStack(this.internal.food));
	}

	@ZenMethod
	public int getFoodGroupCount(String foodGroup) {
		return CTSpiceOfLife.getFoodGroupCount(this.getPlayer(), CraftTweakerMC.getIItemStack(this.internal.food),
				foodGroup);
	}

	@ZenMethod
	public String[] getFoodGroups() {
		return CTSpiceOfLife.getFoodGroupsForFood(CraftTweakerMC.getIItemStack(this.internal.food));
	}

	@ZenMethod
	public void resetFoodHistory() {
		CTSpiceOfLife.resetFoodHistory(this.getPlayer());
	}

	@ZenMethod
	public void syncFoodHistory() {
		CTSpiceOfLife.syncFoodHistory(this.getPlayer());
	}
}
