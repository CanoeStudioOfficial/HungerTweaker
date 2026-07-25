package coolsquid.hungertweaker.ct.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import squeek.applecore.api.food.FoodValues;
import squeek.spiceoflife.foodtracker.FoodEaten;
import squeek.spiceoflife.foodtracker.FoodHistory;
import squeek.spiceoflife.foodtracker.FoodModifier;
import squeek.spiceoflife.foodtracker.FoodTracker;
import squeek.spiceoflife.foodtracker.foodgroups.FoodGroup;
import squeek.spiceoflife.foodtracker.foodgroups.FoodGroupRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.hungertweaker.SpiceOfLife")
public class CTSpiceOfLife {

	public static final String MODID = "spiceoflife";

	private CTSpiceOfLife() {
	}

	@ZenMethod
	public static boolean isLoaded() {
		return Loader.isModLoaded(MODID);
	}

	@ZenMethod
	public static float getFoodModifier(IPlayer player, IItemStack food) {
		requireLoaded();
		return FoodModifier.getFoodModifier(getPlayer(player), getStack(food));
	}

	@ZenMethod
	public static float getFoodGroupModifier(IPlayer player, IItemStack food, String foodGroup) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		return FoodModifier.getFoodGroupModifier(FoodHistory.get(mcPlayer), getStack(food),
				(FoodGroup) getFoodGroup(foodGroup));
	}

	@ZenMethod
	public static IData getModifiedFoodValues(int hunger, float saturationModifier, float modifier) {
		requireLoaded();
		return CTCompatData.foodValues(FoodModifier.getModifiedFoodValues(new FoodValues(hunger, saturationModifier),
				modifier));
	}

	@ZenMethod
	public static IData getModifiedFoodValuesForPlayer(IPlayer player, IItemStack food) {
		requireLoaded();
		ItemStack stack = getStack(food);
		FoodValues values = FoodValues.get(stack, getPlayer(player));
		if (values == null) {
			values = new FoodValues(0, 0);
		}
		return CTCompatData.foodValues(FoodModifier.getModifiedFoodValues(values,
				FoodModifier.getFoodModifier(getPlayer(player), stack)));
	}

	@ZenMethod
	public static IData getFoodHistory(IPlayer player) {
		requireLoaded();
		return foodHistoryData(getPlayer(player));
	}

	@ZenMethod
	public static IData getHistoryFoods(IPlayer player) {
		requireLoaded();
		List<IData> data = new ArrayList<>();
		for (FoodEaten foodEaten : FoodHistory.get(getPlayer(player)).getHistory()) {
			data.add(foodEatenData(foodEaten));
		}
		return CTCompatData.list(data);
	}

	@ZenMethod
	public static int getHistoryLength(IPlayer player) {
		requireLoaded();
		return FoodHistory.get(getPlayer(player)).getHistoryLengthInRelevantUnits();
	}

	@ZenMethod
	public static int getTotalFoodsEatenAllTime(IPlayer player) {
		requireLoaded();
		return FoodHistory.get(getPlayer(player)).totalFoodsEatenAllTime;
	}

	@ZenMethod
	public static long getTicksActive(IPlayer player) {
		requireLoaded();
		return FoodHistory.get(getPlayer(player)).ticksActive;
	}

	@ZenMethod
	public static IItemStack getLastEatenFood(IPlayer player) {
		requireLoaded();
		FoodEaten foodEaten = FoodHistory.get(getPlayer(player)).getLastEatenFood();
		return foodEaten == null ? null : CraftTweakerMC.getIItemStack(foodEaten.itemStack);
	}

	@ZenMethod
	public static int getFoodCount(IPlayer player, IItemStack food) {
		requireLoaded();
		return FoodHistory.get(getPlayer(player)).getFoodCountIgnoringFoodGroups(getStack(food));
	}

	@ZenMethod
	public static int getFoodGroupCount(IPlayer player, IItemStack food, String foodGroup) {
		requireLoaded();
		return FoodHistory.get(getPlayer(player)).getFoodCountForFoodGroup(getStack(food),
				(FoodGroup) getFoodGroup(foodGroup));
	}

	@ZenMethod
	public static boolean containsFoodOrItsFoodGroups(IPlayer player, IItemStack food) {
		requireLoaded();
		return FoodHistory.get(getPlayer(player)).containsFoodOrItsFoodGroups(getStack(food));
	}

	@ZenMethod
	public static IData getTotalFoodValues(IPlayer player, IItemStack food) {
		requireLoaded();
		return CTCompatData.foodValues(FoodHistory.get(getPlayer(player)).getTotalFoodValuesIgnoringFoodGroups(
				getStack(food)));
	}

	@ZenMethod
	public static IData getTotalFoodValuesForFoodGroup(IPlayer player, IItemStack food, String foodGroup) {
		requireLoaded();
		return CTCompatData.foodValues(FoodHistory.get(getPlayer(player)).getTotalFoodValuesForFoodGroup(
				getStack(food), (FoodGroup) getFoodGroup(foodGroup)));
	}

	@ZenMethod
	public static String[] getFoodGroups() {
		requireLoaded();
		return groupNames(FoodGroupRegistry.getFoodGroups()).toArray(new String[0]);
	}

	@ZenMethod
	public static String[] getFoodGroupsForFood(IItemStack food) {
		requireLoaded();
		return groupNames(FoodGroupRegistry.getFoodGroupsForFood(getStack(food))).toArray(new String[0]);
	}

	@ZenMethod
	public static IData getFoodGroupInfo(String foodGroup) {
		requireLoaded();
		return foodGroupData(getFoodGroup(foodGroup));
	}

	@ZenMethod
	public static IData getFoodGroupInfoForFood(IItemStack food) {
		requireLoaded();
		Map<String, IData> data = new LinkedHashMap<>();
		for (FoodGroup foodGroup : FoodGroupRegistry.getFoodGroupsForFood(getStack(food))) {
			data.put(foodGroup.identifier, foodGroupData(foodGroup));
		}
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static boolean isFoodBlacklisted(IItemStack food) {
		requireLoaded();
		return FoodGroupRegistry.isFoodBlacklisted(getStack(food));
	}

	@ZenMethod
	public static boolean addFoodToHistory(IPlayer player, IItemStack food) {
		return addFoodToHistory(player, food, true);
	}

	@ZenMethod
	public static boolean addFoodToHistory(IPlayer player, IItemStack food, boolean countsTowardsAllTime) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		ItemStack stack = getStack(food);
		FoodEaten foodEaten = new FoodEaten(stack, mcPlayer);
		FoodValues foodValues = FoodValues.get(stack, mcPlayer);
		foodEaten.foodValues = foodValues == null ? FoodEaten.dummyFoodValues : foodValues;
		boolean added = FoodHistory.get(mcPlayer).addFood(foodEaten, countsTowardsAllTime);
		sync(mcPlayer);
		return added;
	}

	@ZenMethod
	public static void resetFoodHistory(IPlayer player) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		FoodHistory.get(mcPlayer).reset();
		sync(mcPlayer);
	}

	@ZenMethod
	public static void validateFoodHistory(IPlayer player) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		FoodHistory.get(mcPlayer).validate();
		sync(mcPlayer);
	}

	@ZenMethod
	public static void syncFoodHistory(IPlayer player) {
		requireLoaded();
		sync(getPlayer(player));
	}

	public static IData foodHistoryData(EntityPlayer player) {
		requireLoaded();
		FoodHistory history = FoodHistory.get(player);
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("historyLength", CTCompatData.integer(history.getHistoryLengthInRelevantUnits()));
		data.put("historySize", CTCompatData.integer(history.getHistory().size()));
		data.put("totalFoodsEatenAllTime", CTCompatData.integer(history.totalFoodsEatenAllTime));
		data.put("wasGivenFoodJournal", CTCompatData.bool(history.wasGivenFoodJournal));
		data.put("ticksActive", CTCompatData.longValue(history.ticksActive));
		data.put("distinctFoodGroups", CTCompatData.strings(groupNames(history.getDistinctFoodGroups())));
		FoodEaten last = history.getLastEatenFood();
		data.put("lastEatenFood", last == null ? CTCompatData.emptyMap() : foodEatenData(last));
		return CTCompatData.map(data);
	}

	public static IData foodDataForPlayer(EntityPlayer player, ItemStack food) {
		requireLoaded();
		Map<String, IData> data = new LinkedHashMap<>();
		FoodHistory history = FoodHistory.get(player);
		data.put("modifier", CTCompatData.floating(FoodModifier.getFoodModifier(history, food)));
		data.put("count", CTCompatData.integer(history.getFoodCountIgnoringFoodGroups(food)));
		data.put("containsFoodOrItsFoodGroups", CTCompatData.bool(history.containsFoodOrItsFoodGroups(food)));
		data.put("foodGroups", CTCompatData.strings(groupNames(FoodGroupRegistry.getFoodGroupsForFood(food))));
		data.put("totalFoodValues", CTCompatData.foodValues(history.getTotalFoodValuesIgnoringFoodGroups(food)));
		return CTCompatData.map(data);
	}

	private static IData foodEatenData(Object eatenFood) {
		FoodEaten foodEaten = (FoodEaten) eatenFood;
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("food", CTCompatData.itemStack(foodEaten.itemStack));
		data.put("foodValues", CTCompatData.foodValues(foodEaten.foodValues));
		data.put("worldTimeEaten", CTCompatData.longValue(foodEaten.worldTimeEaten));
		data.put("playerTimeEaten", CTCompatData.longValue(foodEaten.playerTimeEaten));
		data.put("foodGroups", CTCompatData.strings(groupNames(foodEaten.getFoodGroups())));
		return CTCompatData.map(data);
	}

	private static IData foodGroupData(Object group) {
		FoodGroup foodGroup = (FoodGroup) group;
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("identifier", CTCompatData.string(foodGroup.identifier));
		data.put("name", CTCompatData.string(foodGroup.name));
		data.put("enabled", CTCompatData.bool(foodGroup.enabled));
		data.put("blacklist", CTCompatData.bool(foodGroup.blacklist));
		data.put("formula", CTCompatData.string(foodGroup.formula));
		data.put("color", CTCompatData.string(foodGroup.color));
		data.put("hidden", CTCompatData.bool(foodGroup.hidden()));
		return CTCompatData.map(data);
	}

	private static List<String> groupNames(Collection<?> foodGroups) {
		List<String> names = new ArrayList<>();
		for (Object foodGroup : foodGroups) {
			if (foodGroup != null) {
				names.add(((FoodGroup) foodGroup).identifier);
			}
		}
		return names;
	}

	private static Object getFoodGroup(String identifier) {
		FoodGroup foodGroup = FoodGroupRegistry.getFoodGroup(identifier);
		if (foodGroup != null) {
			return foodGroup;
		}
		throw new IllegalArgumentException("Unknown Spice of Life food group '" + identifier + "'.");
	}

	private static ItemStack getStack(IItemStack food) {
		return CraftTweakerMC.getItemStack(food);
	}

	private static EntityPlayer getPlayer(IPlayer player) {
		return CraftTweakerMC.getPlayer(player);
	}

	private static void sync(EntityPlayer player) {
		if (!player.world.isRemote && player instanceof EntityPlayerMP) {
			FoodTracker.syncFoodHistory(FoodHistory.get(player));
		}
	}

	private static void requireLoaded() {
		if (!isLoaded()) {
			throw new IllegalStateException("The Spice of Life is not loaded.");
		}
	}
}
