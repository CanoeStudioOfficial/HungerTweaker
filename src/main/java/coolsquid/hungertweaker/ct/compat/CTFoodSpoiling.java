package coolsquid.hungertweaker.ct.compat;

import java.util.LinkedHashMap;
import java.util.Map;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import mod.acgaming.foodspoiling.config.FSConfig;
import mod.acgaming.foodspoiling.logic.FSData;
import mod.acgaming.foodspoiling.logic.FSLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.fml.common.Loader;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.hungertweaker.FoodSpoiling")
public class CTFoodSpoiling {

	public static final String MODID = "foodspoiling";

	private CTFoodSpoiling() {
	}

	@ZenMethod
	public static boolean isLoaded() {
		return Loader.isModLoaded(MODID);
	}

	@ZenMethod
	public static String getRotState(IItemStack food) {
		requireLoaded();
		return getRotState(getStack(food)).name();
	}

	@ZenMethod
	public static boolean canSpoil(IItemStack food) {
		requireLoaded();
		return canSpoil(getStack(food));
	}

	@ZenMethod
	public static boolean doesNotRot(IItemStack food) {
		requireLoaded();
		return getRotState(getStack(food)) == EnumActionResult.PASS;
	}

	@ZenMethod
	public static double getExpirationDays(IItemStack food) {
		requireLoaded();
		return getExpirationDays(getStack(food));
	}

	@ZenMethod
	public static int getBaseTicksToRot(IItemStack food) {
		requireLoaded();
		return getBaseTicksToRot(getStack(food));
	}

	@ZenMethod
	public static int getTicksToRot(IItemStack food) {
		requireLoaded();
		return getTicksToRot(null, getStack(food));
	}

	@ZenMethod
	public static int getTicksToRot(IPlayer player, IItemStack food) {
		requireLoaded();
		return getTicksToRot(getPlayer(player), getStack(food));
	}

	@ZenMethod
	public static int getRemainingTicks(IPlayer player, IItemStack food) {
		requireLoaded();
		return getRemainingTicks(getPlayer(player), getStack(food));
	}

	@ZenMethod
	public static float getSpoilage(IPlayer player, IItemStack food) {
		requireLoaded();
		return getSpoilage(getPlayer(player), getStack(food));
	}

	@ZenMethod
	public static float getFreshness(IPlayer player, IItemStack food) {
		requireLoaded();
		return getFreshness(getPlayer(player), getStack(food));
	}

	@ZenMethod
	public static float getSaturationMultiplier(IPlayer player, IItemStack food) {
		requireLoaded();
		return getSaturationMultiplier(getPlayer(player), getStack(food));
	}

	@ZenMethod
	public static float getSpoiledSaturationModifier(IPlayer player, IItemStack food, float saturationModifier) {
		requireLoaded();
		return saturationModifier * getSaturationMultiplier(getPlayer(player), getStack(food));
	}

	@ZenMethod
	public static double getLifetimeFactor(IPlayer player, IItemStack food) {
		requireLoaded();
		return getLifetimeFactor(getPlayer(player), getStack(food));
	}

	@ZenMethod
	public static boolean hasCreationTime(IItemStack food) {
		requireLoaded();
		return FSData.hasCreationTime(getStack(food));
	}

	@ZenMethod
	public static long getCreationTime(IItemStack food) {
		requireLoaded();
		return FSData.getCreationTime(getStack(food));
	}

	@ZenMethod
	public static boolean hasRemainingLifetime(IItemStack food) {
		requireLoaded();
		return FSData.hasRemainingLifetime(getStack(food));
	}

	@ZenMethod
	public static int getRemainingLifetime(IItemStack food) {
		requireLoaded();
		return FSData.getRemainingLifetime(getStack(food));
	}

	@ZenMethod
	public static IData getFoodSpoilage(IPlayer player, IItemStack food) {
		requireLoaded();
		return foodSpoilingData(getPlayer(player), getStack(food));
	}

	public static IData foodSpoilingData(EntityPlayer player, ItemStack food) {
		requireLoaded();
		Map<String, IData> data = new LinkedHashMap<>();
		EnumActionResult rotState = getRotState(food);
		double expirationDays = getExpirationDays(food);
		int baseTicksToRot = getBaseTicksToRot(food);
		int ticksToRot = getTicksToRot(player, food);
		int remainingTicks = getRemainingTicks(player, food);
		float spoilage = getSpoilage(player, food);
		double lifetimeFactor = player == null ? 1 : getLifetimeFactor(player, food);

		data.put("rotState", CTCompatData.string(rotState.name()));
		data.put("canSpoil", CTCompatData.bool(rotState == EnumActionResult.SUCCESS));
		data.put("doesNotRot", CTCompatData.bool(rotState == EnumActionResult.PASS));
		data.put("hasExpiration", CTCompatData.bool(!Double.isNaN(expirationDays)));
		data.put("expirationDays", CTCompatData.doubleValue(Double.isNaN(expirationDays) ? -1 : expirationDays));
		data.put("baseTicksToRot", CTCompatData.integer(baseTicksToRot));
		data.put("ticksToRot", CTCompatData.integer(ticksToRot));
		data.put("remainingTicks", CTCompatData.integer(remainingTicks));
		data.put("elapsedTicks", CTCompatData.longValue(getElapsedTicks(player, food)));
		data.put("spoilage", CTCompatData.floating(spoilage));
		data.put("freshness", CTCompatData.floating(1 - spoilage));
		data.put("saturationMultiplier", CTCompatData.floating(1 - spoilage));
		data.put("lifetimeFactor", CTCompatData.doubleValue(lifetimeFactor));
		data.put("hasCreationTime", CTCompatData.bool(FSData.hasCreationTime(food)));
		data.put("creationTime", CTCompatData.longValue(FSData.getCreationTime(food)));
		data.put("hasRemainingLifetime", CTCompatData.bool(FSData.hasRemainingLifetime(food)));
		data.put("remainingLifetime", CTCompatData.integer(FSData.getRemainingLifetime(food)));
		data.put("hasLastLifetimeFactor", CTCompatData.bool(FSData.hasLastLifetimeFactor(food)));
		data.put("lastLifetimeFactor", CTCompatData.doubleValue(
				FSData.hasLastLifetimeFactor(food) ? FSData.getLastLifetimeFactor(food) : 1));
		data.put("hasID", CTCompatData.bool(FSData.hasID(food)));
		data.put("id", CTCompatData.integer(FSData.getID(food)));
		return CTCompatData.map(data);
	}

	public static String getRotState(EntityPlayer player, ItemStack food) {
		return getRotState(food).name();
	}

	public static boolean canSpoil(EntityPlayer player, ItemStack food) {
		return canSpoil(food);
	}

	public static float getSpoilage(EntityPlayer player, ItemStack food) {
		if (!canSpoil(food)) {
			return 0;
		}
		int baseTicksToRot = getBaseTicksToRot(food);
		if (baseTicksToRot <= 0) {
			return 0;
		}
		if (FSData.hasRemainingLifetime(food)) {
			return clamp(1 - FSData.getRemainingLifetime(food) / (float) baseTicksToRot);
		}
		if (!FSData.hasCreationTime(food)) {
			return 0;
		}
		int ticksToRot = getTicksToRot(player, food);
		if (ticksToRot <= 0) {
			return 0;
		}
		return clamp(getElapsedTicks(player, food) / (float) ticksToRot);
	}

	public static float getFreshness(EntityPlayer player, ItemStack food) {
		return 1 - getSpoilage(player, food);
	}

	public static float getSaturationMultiplier(EntityPlayer player, ItemStack food) {
		return getFreshness(player, food);
	}

	public static int getTicksToRot(EntityPlayer player, ItemStack food) {
		return FSLogic.getTicksToRot(player, food);
	}

	public static int getRemainingTicks(EntityPlayer player, ItemStack food) {
		if (!canSpoil(food)) {
			return -1;
		}
		if (FSData.hasRemainingLifetime(food)) {
			int remainingLifetime = FSData.getRemainingLifetime(food);
			if (player == null) {
				return remainingLifetime;
			}
			double lifetimeFactor = getLifetimeFactor(player, food);
			return lifetimeFactor > 0 ? (int) (remainingLifetime * lifetimeFactor) : remainingLifetime;
		}
		int ticksToRot = getTicksToRot(player, food);
		if (ticksToRot <= 0) {
			return ticksToRot;
		}
		if (!FSData.hasCreationTime(food)) {
			return ticksToRot;
		}
		return Math.max(0, ticksToRot - (int) getElapsedTicks(player, food));
	}

	public static double getLifetimeFactor(EntityPlayer player, ItemStack food) {
		return player == null ? 1 : FSLogic.getLifetimeFactor(player, food);
	}

	private static EnumActionResult getRotState(ItemStack food) {
		return food == null || food.isEmpty() ? EnumActionResult.FAIL : FSLogic.canRot(food);
	}

	private static boolean canSpoil(ItemStack food) {
		return getRotState(food) == EnumActionResult.SUCCESS;
	}

	private static double getExpirationDays(ItemStack food) {
		return food == null || food.isEmpty() ? Double.NaN : FSLogic.getExpirationDays(food);
	}

	private static int getBaseTicksToRot(ItemStack food) {
		double expirationDays = getExpirationDays(food);
		if (Double.isNaN(expirationDays) || expirationDays < 0) {
			return -1;
		}
		return (int) (expirationDays * FSConfig.GENERAL.dayLengthInTicks);
	}

	private static long getElapsedTicks(EntityPlayer player, ItemStack food) {
		if (player == null || food == null || !FSData.hasCreationTime(food)) {
			return 0;
		}
		return Math.max(0, player.world.getTotalWorldTime() - FSData.getCreationTime(food));
	}

	private static float clamp(float value) {
		return Math.max(0, Math.min(1, value));
	}

	private static ItemStack getStack(IItemStack food) {
		return food == null ? ItemStack.EMPTY : CraftTweakerMC.getItemStack(food);
	}

	private static EntityPlayer getPlayer(IPlayer player) {
		return player == null ? null : CraftTweakerMC.getPlayer(player);
	}

	private static void requireLoaded() {
		if (!isLoaded()) {
			throw new IllegalStateException("FoodSpoiling is not loaded.");
		}
	}
}
