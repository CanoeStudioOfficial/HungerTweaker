package coolsquid.hungertweaker.ct.events;

import coolsquid.hungertweaker.ct.compat.CTSpiceOfLifeCarrotEdition;
import crafttweaker.api.data.IData;
import crafttweaker.api.minecraft.CraftTweakerMC;
import squeek.applecore.api.food.FoodEvent.FoodEaten;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.hungertweaker.events.SpiceOfLifeCarrotFoodEatenEvent")
public class CTSpiceOfLifeCarrotFoodEatenEvent extends CTFoodEatenEvent {

	private final FoodEaten internal;

	public CTSpiceOfLifeCarrotFoodEatenEvent(FoodEaten internal) {
		super(internal);
		this.internal = internal;
	}

	@ZenGetter("hasEaten")
	public boolean hasEaten() {
		return CTSpiceOfLifeCarrotEdition.hasEaten(this.getPlayer(), CraftTweakerMC.getIItemStack(this.internal.food));
	}

	@ZenGetter("shouldCount")
	public boolean shouldCount() {
		return CTSpiceOfLifeCarrotEdition.shouldCount(this.getPlayer(),
				CraftTweakerMC.getIItemStack(this.internal.food));
	}

	@ZenGetter("eatenFoodCount")
	public int getEatenFoodCount() {
		return CTSpiceOfLifeCarrotEdition.getEatenFoodCount(this.getPlayer());
	}

	@ZenGetter("foodsEatenForMilestones")
	public int getFoodsEatenForMilestones() {
		return CTSpiceOfLifeCarrotEdition.getFoodsEatenForMilestones(this.getPlayer());
	}

	@ZenGetter("milestonesAchieved")
	public int getMilestonesAchieved() {
		return CTSpiceOfLifeCarrotEdition.getMilestonesAchieved(this.getPlayer());
	}

	@ZenGetter("nextMilestone")
	public int getNextMilestone() {
		return CTSpiceOfLifeCarrotEdition.getNextMilestone(this.getPlayer());
	}

	@ZenGetter("foodsUntilNextMilestone")
	public int getFoodsUntilNextMilestone() {
		return CTSpiceOfLifeCarrotEdition.getFoodsUntilNextMilestone(this.getPlayer());
	}

	@ZenGetter("progress")
	public IData getProgress() {
		return CTSpiceOfLifeCarrotEdition.progressData(this.internal.player);
	}

	@ZenGetter("foodProgress")
	public IData getFoodProgress() {
		return CTSpiceOfLifeCarrotEdition.foodDataForPlayer(this.internal.player, this.internal.food);
	}

	@ZenMethod
	public boolean addFood() {
		return CTSpiceOfLifeCarrotEdition.addFood(this.getPlayer(), CraftTweakerMC.getIItemStack(this.internal.food));
	}

	@ZenMethod
	public void clearFoods() {
		CTSpiceOfLifeCarrotEdition.clearFoods(this.getPlayer());
	}

	@ZenMethod
	public boolean updateMaxHealth() {
		return CTSpiceOfLifeCarrotEdition.updateMaxHealth(this.getPlayer());
	}

	@ZenMethod
	public void syncFoodList() {
		CTSpiceOfLifeCarrotEdition.syncFoodList(this.getPlayer());
	}
}
