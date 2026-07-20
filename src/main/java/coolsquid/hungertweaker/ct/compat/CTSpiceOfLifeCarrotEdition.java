package coolsquid.hungertweaker.ct.compat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cazsius.solcarrot.api.SOLCarrotAPI;
import com.cazsius.solcarrot.tracking.FoodInstance;
import com.cazsius.solcarrot.tracking.FoodList;
import com.cazsius.solcarrot.tracking.MaxHealthHandler;
import com.cazsius.solcarrot.tracking.ProgressInfo;
import com.cazsius.solcarrot.tracking.ProgressInfo.ConfigInfo;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.hungertweaker.SpiceOfLifeCarrotEdition")
public class CTSpiceOfLifeCarrotEdition {

	public static final String MODID = "solcarrot";

	private CTSpiceOfLifeCarrotEdition() {
	}

	@ZenMethod
	public static boolean isLoaded() {
		return Loader.isModLoaded(MODID);
	}

	@ZenMethod
	public static int getEatenFoodCount(IPlayer player) {
		requireLoaded();
		return SOLCarrotAPI.getFoodCapability(getPlayer(player)).getEatenFoodCount();
	}

	@ZenMethod
	public static int getFoodsEatenForMilestones(IPlayer player) {
		requireLoaded();
		return FoodList.get(getPlayer(player)).getProgressInfo().foodsEaten;
	}

	@ZenMethod
	public static boolean hasEaten(IPlayer player, IItemStack food) {
		requireLoaded();
		return SOLCarrotAPI.getFoodCapability(getPlayer(player)).hasEaten(getStack(food));
	}

	@ZenMethod
	public static boolean shouldCount(IPlayer player, IItemStack food) {
		requireLoaded();
		return FoodList.get(getPlayer(player)).getConfigInfo().shouldCount(getStack(food));
	}

	@ZenMethod
	public static boolean isAllowed(IPlayer player, IItemStack food) {
		requireLoaded();
		return FoodList.get(getPlayer(player)).getConfigInfo().isAllowed(getStack(food));
	}

	@ZenMethod
	public static boolean isHearty(IPlayer player, IItemStack food) {
		requireLoaded();
		return FoodList.get(getPlayer(player)).getConfigInfo().isHearty(getStack(food));
	}

	@ZenMethod
	public static int getMilestonesAchieved(IPlayer player) {
		requireLoaded();
		return FoodList.get(getPlayer(player)).getProgressInfo().milestonesAchieved();
	}

	@ZenMethod
	public static int getNextMilestone(IPlayer player) {
		requireLoaded();
		return FoodList.get(getPlayer(player)).getProgressInfo().nextMilestone();
	}

	@ZenMethod
	public static int getFoodsUntilNextMilestone(IPlayer player) {
		requireLoaded();
		return FoodList.get(getPlayer(player)).getProgressInfo().foodsUntilNextMilestone();
	}

	@ZenMethod
	public static boolean hasReachedMax(IPlayer player) {
		requireLoaded();
		return FoodList.get(getPlayer(player)).getProgressInfo().hasReachedMax();
	}

	@ZenMethod
	public static int getHealthModifier(IPlayer player) {
		requireLoaded();
		ProgressInfo progress = FoodList.get(getPlayer(player)).getProgressInfo();
		return 2 * (progress.configInfo.baseHearts - 10)
				+ progress.milestonesAchieved() * 2 * progress.configInfo.heartsPerMilestone;
	}

	@ZenMethod
	public static IData getProgress(IPlayer player) {
		requireLoaded();
		return progressData(getPlayer(player));
	}

	@ZenMethod
	public static IData getEatenFoods(IPlayer player) {
		requireLoaded();
		List<IData> data = new ArrayList<>();
		for (FoodInstance food : FoodList.get(getPlayer(player)).getEatenFoods()) {
			data.add(CTCompatData.itemStack(food.getItemStack()));
		}
		return CTCompatData.list(data);
	}

	@ZenMethod
	public static boolean addFood(IPlayer player, IItemStack food) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		boolean added = FoodList.get(mcPlayer).addFood(getStack(food));
		syncAndUpdate(mcPlayer);
		return added;
	}

	@ZenMethod
	public static void clearFoods(IPlayer player) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		FoodList.get(mcPlayer).clearFood();
		syncAndUpdate(mcPlayer);
	}

	@ZenMethod
	public static void updateProgressInfo(IPlayer player) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		FoodList.get(mcPlayer).updateProgressInfo();
		syncAndUpdate(mcPlayer);
	}

	@ZenMethod
	public static boolean updateMaxHealth(IPlayer player) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		boolean changed = !mcPlayer.world.isRemote && MaxHealthHandler.updateFoodHPModifier(mcPlayer);
		sync(mcPlayer);
		return changed;
	}

	@ZenMethod
	public static void syncFoodList(IPlayer player) {
		requireLoaded();
		sync(getPlayer(player));
	}

	public static IData progressData(EntityPlayer player) {
		requireLoaded();
		FoodList foodList = FoodList.get(player);
		ProgressInfo progress = foodList.getProgressInfo();
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("eatenFoodCount", CTCompatData.integer(foodList.getEatenFoodCount()));
		data.put("foodsEaten", CTCompatData.integer(progress.foodsEaten));
		data.put("milestonesAchieved", CTCompatData.integer(progress.milestonesAchieved()));
		data.put("nextMilestone", CTCompatData.integer(progress.nextMilestone()));
		data.put("foodsUntilNextMilestone", CTCompatData.integer(progress.foodsUntilNextMilestone()));
		data.put("hasReachedMax", CTCompatData.bool(progress.hasReachedMax()));
		data.put("healthModifier", CTCompatData.integer(2 * (progress.configInfo.baseHearts - 10)
				+ progress.milestonesAchieved() * 2 * progress.configInfo.heartsPerMilestone));
		data.put("config", configData(progress.configInfo));
		return CTCompatData.map(data);
	}

	public static IData foodDataForPlayer(EntityPlayer player, ItemStack food) {
		requireLoaded();
		FoodList foodList = FoodList.get(player);
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("hasEaten", CTCompatData.bool(foodList.hasEaten(food)));
		data.put("shouldCount", CTCompatData.bool(foodList.getConfigInfo().shouldCount(food)));
		data.put("isAllowed", CTCompatData.bool(foodList.getConfigInfo().isAllowed(food)));
		data.put("isHearty", CTCompatData.bool(foodList.getConfigInfo().isHearty(food)));
		data.put("progress", progressData(player));
		return CTCompatData.map(data);
	}

	private static IData configData(ConfigInfo config) {
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("milestones", CTCompatData.ints(config.milestones));
		data.put("baseHearts", CTCompatData.integer(config.baseHearts));
		data.put("heartsPerMilestone", CTCompatData.integer(config.heartsPerMilestone));
		data.put("shouldShowUneatenFoods", CTCompatData.bool(config.shouldShowUneatenFoods));
		data.put("minimumFoodValue", CTCompatData.integer(config.minimumFoodValue));
		data.put("blacklist", CTCompatData.strings(asList(config.blacklist)));
		data.put("whitelist", CTCompatData.strings(asList(config.whitelist)));
		data.put("hasWhitelist", CTCompatData.bool(config.hasWhitelist()));
		return CTCompatData.map(data);
	}

	private static List<String> asList(String[] values) {
		List<String> result = new ArrayList<>();
		for (String value : values) {
			result.add(value);
		}
		return result;
	}

	private static ItemStack getStack(IItemStack food) {
		return CraftTweakerMC.getItemStack(food);
	}

	private static EntityPlayer getPlayer(IPlayer player) {
		return CraftTweakerMC.getPlayer(player);
	}

	private static void syncAndUpdate(EntityPlayer player) {
		if (!player.world.isRemote) {
			MaxHealthHandler.updateFoodHPModifier(player);
		}
		sync(player);
	}

	private static void sync(EntityPlayer player) {
		if (!player.world.isRemote && player instanceof EntityPlayerMP) {
			SOLCarrotAPI.syncFoodList(player);
		}
	}

	private static void requireLoaded() {
		if (!isLoaded()) {
			throw new IllegalStateException("Spice of Life: Carrot Edition is not loaded.");
		}
	}
}
