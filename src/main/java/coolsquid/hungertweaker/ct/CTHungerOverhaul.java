package coolsquid.hungertweaker.ct;

import java.util.ArrayList;
import java.util.List;

import coolsquid.hungertweaker.ModEventHandler;
import coolsquid.hungertweaker.ct.exhaustion.CTExhaustion;
import coolsquid.hungertweaker.util.Expression;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotionEffect;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import squeek.applecore.api.food.FoodEvent;
import squeek.applecore.api.food.FoodValues;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** CT-configurable food and hunger rules based on Hunger Overhaul's AppleCore hooks. */
@ZenRegister
@ZenClass("mods.hungertweaker.HungerOverhaul")
public class CTHungerOverhaul {

	public static Expression foodHungerDivider;
	public static Expression foodSaturationDivider;
	public static Expression foodHungerToSaturationDivider;
	public static Expression eatingDuration;
	public static Expression eatingDurationMultiplier;
	public static Expression foodHealDivider;
	public static Expression wellFedDuration;
	public static Expression wellFedDurationMultiplier;
	public static Expression damageOnStarve;

	public static boolean modifyFoodValues;
	public static boolean modifyEatingSpeed;
	public static boolean foodRegensHealth;
	public static boolean requireMinimumHungerToHeal;
	public static boolean disableHealingHungerDrain;
	public static boolean difficultyScalingHunger;
	public static boolean difficultyScalingHealing;
	public static boolean difficultyScalingEffects;
	public static boolean difficultyScalingRespawnHunger;
	public static boolean enableRespawnHunger;
	public static boolean instantStarvation;
	public static boolean modifyRegenRateOnLowHealth;
	public static boolean peacefulExhaustionHungerLoss;
	public static boolean modifyAnimalDelays;
	public static boolean modifyCropGrowth;
	public static boolean modifyBonemeal;
	public static boolean modifyBonemealGrowth;
	public static boolean difficultyScalingBoneMeal;
	public static boolean cropGrowthDaylightOnly;
	public static boolean cropGrowthNeedsSky;

	public static float hungerLossRatePercentage = 100;
	public static float healthRegenRatePercentage = 100;
	public static float wellFedEffectiveness;
	public static float wellFedSaturationEffectiveness;
	public static float lowHealthRegenRateModifier = 0;
	public static float eggTimeoutMultiplier = 1;
	public static float breedingTimeoutMultiplier = 1;
	public static float childDurationMultiplier = 1;
	public static float cropGrowthMultiplier = 1;
	public static float cropGrowthNoSkyMultiplier;
	public static float bonemealEffectiveness = 1;
	public static int minHungerToHeal = 6;
	public static int respawnHunger = 20;
	public static int respawnHungerDifficultyModifier = 4;
	public static int foodStackSizeMultiplier = 1;
	public static boolean modifyFoodStackSize;
	public static float constantHungerLoss;
	public static boolean starvationDamageConfigured;

	private static PotionEffect wellFedEffect;
	private static final List<LowStatRule> LOW_STAT_RULES = new ArrayList<>();

	private CTHungerOverhaul() {
	}

	@ZenMethod
	public static void setModifyFoodValues(boolean value) {
		modifyFoodValues = value;
	}

	@ZenMethod
	public static void setFoodHungerDivider(IData value) {
		foodHungerDivider = Expression.parse(value);
		modifyFoodValues = true;
	}

	@ZenMethod
	public static void setFoodSaturationDivider(IData value) {
		foodSaturationDivider = Expression.parse(value);
		modifyFoodValues = true;
	}

	@ZenMethod
	public static void setFoodHungerToSaturationDivider(IData value) {
		foodHungerToSaturationDivider = Expression.parse(value);
		modifyFoodValues = true;
	}

	@ZenMethod
	public static void setEatingDuration(IData value) {
		eatingDuration = Expression.parse(value);
		modifyEatingSpeed = true;
	}

	@ZenMethod
	public static void setEatingDurationMultiplier(IData value) {
		eatingDurationMultiplier = Expression.parse(value);
		modifyEatingSpeed = true;
	}

	@ZenMethod
	public static void setFoodRegensHealth(boolean value) {
		foodRegensHealth = value;
	}

	@ZenMethod
	public static void setFoodHealDivider(IData value) {
		foodHealDivider = Expression.parse(value);
		foodRegensHealth = true;
	}

	@ZenMethod
	public static void setWellFedEffect(IPotionEffect effect) {
		wellFedEffect = CraftTweakerMC.getPotionEffect(effect);
	}

	@ZenMethod
	public static void setWellFedDuration(IData value) {
		wellFedDuration = Expression.parse(value);
	}

	@ZenMethod
	public static void setWellFedDurationMultiplier(IData value) {
		wellFedDurationMultiplier = Expression.parse(value);
	}

	@ZenMethod
	public static void setWellFedEffectiveness(float value) {
		wellFedEffectiveness = Math.max(0, Math.min(1, value));
	}

	@ZenMethod
	public static void setWellFedSaturationEffectiveness(float value) {
		wellFedSaturationEffectiveness = Math.max(0, Math.min(1, value));
	}

	@ZenMethod
	public static void setRequireMinimumHungerToHeal(boolean value) {
		requireMinimumHungerToHeal = value;
	}

	@ZenMethod
	public static void setMinimumHungerToHeal(int value) {
		minHungerToHeal = Math.max(0, value);
		requireMinimumHungerToHeal = true;
	}

	@ZenMethod
	public static void setDisableHealingHungerDrain(boolean value) {
		disableHealingHungerDrain = value;
	}

	@ZenMethod
	public static void setHungerLossRate(float percentage) {
		if (percentage < 0) {
			throw new IllegalArgumentException("Hunger loss rate cannot be negative.");
		}
		hungerLossRatePercentage = percentage;
	}

	@ZenMethod
	public static void setHealthRegenRate(float percentage) {
		if (percentage < 0) {
			throw new IllegalArgumentException("Health regeneration rate cannot be negative.");
		}
		healthRegenRatePercentage = percentage;
	}

	@ZenMethod
	public static void setDifficultyScalingHunger(boolean value) {
		difficultyScalingHunger = value;
	}

	@ZenMethod
	public static void setDifficultyScalingHealing(boolean value) {
		difficultyScalingHealing = value;
	}

	@ZenMethod
	public static void setDifficultyScalingEffects(boolean value) {
		difficultyScalingEffects = value;
	}

	@ZenMethod
	public static void setModifyRegenRateOnLowHealth(boolean value) {
		modifyRegenRateOnLowHealth = value;
	}

	@ZenMethod
	public static void setLowHealthRegenRateModifier(float value) {
		lowHealthRegenRateModifier = Math.max(0, value);
		modifyRegenRateOnLowHealth = true;
	}

	@ZenMethod
	public static void setRespawnHunger(int value, int difficultyModifier, boolean difficultyScaling) {
		respawnHunger = value;
		respawnHungerDifficultyModifier = difficultyModifier;
		enableRespawnHunger = true;
		difficultyScalingRespawnHunger = difficultyScaling;
	}

	@ZenMethod
	public static void setInstantStarvation(boolean value) {
		instantStarvation = value;
	}

	@ZenMethod
	public static void setDamageOnStarve(IData value) {
		damageOnStarve = Expression.parse(value);
		starvationDamageConfigured = true;
	}

	@ZenMethod
	public static void setPeacefulExhaustionHungerLoss(boolean value) {
		peacefulExhaustionHungerLoss = value;
	}

	@ZenMethod
	public static void setConstantHungerLoss(boolean value) {
		constantHungerLoss = value ? 0.01F : 0;
		if (value) {
			ensureTickHandler();
		} else {
			releaseTickHandlerIfUnused();
		}
	}

	@ZenMethod
	public static void setConstantHungerLossAmount(float amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Constant hunger loss cannot be negative.");
		}
		constantHungerLoss = amount;
		if (amount != 0) {
			ensureTickHandler();
		} else {
			releaseTickHandlerIfUnused();
		}
	}

	@ZenMethod
	public static void setModifyFoodStackSize(boolean value, int multiplier) {
		modifyFoodStackSize = value;
		foodStackSizeMultiplier = Math.max(1, multiplier);
	}

	@ZenMethod
	public static void setEggTimeoutMultiplier(float multiplier) {
		eggTimeoutMultiplier = requireDelayMultiplier(multiplier, "Egg timeout");
		modifyAnimalDelays = true;
	}

	@ZenMethod
	public static void setBreedingTimeoutMultiplier(float multiplier) {
		breedingTimeoutMultiplier = requireDelayMultiplier(multiplier, "Breeding timeout");
		modifyAnimalDelays = true;
	}

	@ZenMethod
	public static void setChildDurationMultiplier(float multiplier) {
		childDurationMultiplier = requireDelayMultiplier(multiplier, "Child duration");
		modifyAnimalDelays = true;
	}

	@ZenMethod
	public static void setAnimalDelayMultipliers(float egg, float breeding, float child) {
		setEggTimeoutMultiplier(egg);
		setBreedingTimeoutMultiplier(breeding);
		setChildDurationMultiplier(child);
	}

	@ZenMethod
	public static void setCropGrowthMultiplier(float multiplier) {
		if (multiplier <= 0) {
			throw new IllegalArgumentException("Crop growth multiplier must be greater than zero.");
		}
		cropGrowthMultiplier = multiplier;
		modifyCropGrowth = true;
	}

	@ZenMethod
	public static void setCropGrowthDaylightOnly(boolean value) {
		cropGrowthDaylightOnly = value;
		modifyCropGrowth = true;
	}

	@ZenMethod
	public static void setCropGrowthNeedsSky(boolean value) {
		cropGrowthNeedsSky = value;
		modifyCropGrowth = true;
	}

	@ZenMethod
	public static void setCropGrowthNoSkyMultiplier(float multiplier) {
		if (multiplier < 0) {
			throw new IllegalArgumentException("No-sky crop growth multiplier cannot be negative.");
		}
		cropGrowthNoSkyMultiplier = multiplier;
		cropGrowthNeedsSky = true;
		modifyCropGrowth = true;
	}

	@ZenMethod
	public static void setBonemealEffectiveness(float value) {
		if (value < 0 || value > 1) {
			throw new IllegalArgumentException("Bonemeal effectiveness must be between 0 and 1.");
		}
		bonemealEffectiveness = value;
		modifyBonemeal = true;
	}

	@ZenMethod
	public static void setModifyBonemealGrowth(boolean value) {
		modifyBonemealGrowth = value;
		modifyBonemeal = true;
	}

	@ZenMethod
	public static void setDifficultyScalingBoneMeal(boolean value) {
		difficultyScalingBoneMeal = value;
		modifyBonemeal = true;
	}

	@ZenMethod
	public static void addLowHungerEffect(IPotionEffect effect, int maximumFoodLevel) {
		addLowStatRule(effect, maximumFoodLevel, false);
	}

	@ZenMethod
	public static void addLowHealthEffect(IPotionEffect effect, float maximumHealthPercent) {
		float threshold = maximumHealthPercent > 1 ? maximumHealthPercent / 100F : maximumHealthPercent;
		addLowStatRule(effect, Math.max(0, Math.min(1, threshold)), true);
	}

	@ZenMethod
	public static void clearLowStatEffects() {
		LOW_STAT_RULES.clear();
		releaseTickHandlerIfUnused();
	}

	public static void modifyFoodValues(FoodEvent.GetFoodValues event) {
		if (!modifyFoodValues) {
			return;
		}
		int hunger = event.foodValues.hunger;
		float saturation = event.foodValues.saturationModifier;
		if (foodHungerDivider != null) {
			double divider = foodHungerDivider.eval(hunger);
			if (divider != 0) {
				hunger = Math.max(1, (int) Math.round(hunger / divider));
			}
		}
		if (foodHungerToSaturationDivider != null) {
			double divider = foodHungerToSaturationDivider.eval(hunger);
			if (divider != 0) {
				saturation = (float) (hunger / divider);
			}
		}
		if (foodSaturationDivider != null) {
			double divider = foodSaturationDivider.eval(saturation);
			if (divider != 0) {
				saturation = (float) (saturation / divider);
			}
		}
		event.foodValues = new FoodValues(hunger, saturation);
	}

	public static void onFoodEaten(FoodEvent.FoodEaten event) {
		EntityPlayer player = event.player;
		if (player.world.isRemote) {
			return;
		}
		if (wellFedEffect != null && player.world.getGameRules().getBoolean("naturalRegeneration")
				&& healthRegenRatePercentage > 0) {
			double baseDuration = Math.pow(event.foodValues.hunger * 100D, 1.2D);
			int duration = wellFedDuration == null ? (int) baseDuration : (int) wellFedDuration.eval(event.foodValues.hunger);
			if (wellFedDurationMultiplier != null) {
				duration = (int) (duration * wellFedDurationMultiplier.eval(event.foodValues.hunger));
			}
			if (duration >= 1) {
				PotionEffect current = player.getActivePotionEffect(wellFedEffect.getPotion());
				if (current != null) {
					duration += current.getDuration();
				}
				player.addPotionEffect(new PotionEffect(wellFedEffect.getPotion(), duration,
						wellFedEffect.getAmplifier(), wellFedEffect.getIsAmbient(), wellFedEffect.doesShowParticles()));
			}
		}
		if (foodRegensHealth && foodHealDivider != null) {
			double divider = foodHealDivider.eval(event.foodValues.hunger);
			if (divider == 0) {
				return;
			}
			float amount = Math.round((float) (event.foodValues.hunger / divider));
			if (amount > 0) {
				player.heal(Math.min(amount, player.getMaxHealth() - player.getHealth()));
			}
		}
	}

	public static float modifyMaxExhaustion(EntityPlayer player, float value) {
		if (hungerLossRatePercentage <= 0) {
			return value;
		}
		float lossRate = hungerLossRatePercentage / 100F;
		if (wellFedEffect != null && wellFedSaturationEffectiveness > 0
				&& player.isPotionActive(wellFedEffect.getPotion())) {
			lossRate *= 1F - wellFedSaturationEffectiveness;
		}
		float result = value / Math.max(0.0001F, lossRate);
		if (difficultyScalingHunger) {
			if (player.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
				result *= 5F / 3F;
			} else if (player.world.getDifficulty() == EnumDifficulty.EASY) {
				result *= 4F / 3F;
			}
		}
		return result;
	}

	public static boolean shouldDenyExhaustion() {
		return hungerLossRatePercentage == 0;
	}

	public static boolean shouldDenyFoodStatsAddition() {
		return hungerLossRatePercentage == 0;
	}

	public static boolean shouldDenyRegen() {
		return healthRegenRatePercentage <= 0;
	}

	public static boolean shouldAllowRegen(EntityPlayer player) {
		return requireMinimumHungerToHeal && player.getFoodStats().getFoodLevel() >= minHungerToHeal
				&& healthRegenRatePercentage > 0 && player.world.getGameRules().getBoolean("naturalRegeneration")
				&& player.shouldHeal();
	}

	public static int modifyRegenPeriod(EntityPlayer player, int period) {
		float difficulty = 1;
		if (difficultyScalingHealing) {
			if (player.world.getDifficulty().getId() <= EnumDifficulty.EASY.getId()) {
				difficulty = 0.75F;
			} else if (player.world.getDifficulty() == EnumDifficulty.HARD) {
				difficulty = 1.5F;
			}
		}
		float wellFed = wellFedEffect != null && player.isPotionActive(wellFedEffect.getPotion())
				? 1F - wellFedEffectiveness : 1F;
		float lowHealth = 1F;
		if (modifyRegenRateOnLowHealth) {
			lowHealth = player.getMaxHealth() - player.getHealth();
			lowHealth *= lowHealthRegenRateModifier / 100F;
			lowHealth *= difficulty;
			lowHealth = (float) Math.pow(lowHealth + 1F, 1.5F);
		}
		float rate = healthRegenRatePercentage <= 0 ? 1 : healthRegenRatePercentage / 100F;
		return Math.max(1, Math.round(period * difficulty * wellFed * lowHealth / rate));
	}

	public static boolean isHealingHungerDrainDisabled() {
		return disableHealingHungerDrain;
	}

	public static void onRespawn(EntityPlayer player) {
		if (!enableRespawnHunger) {
			return;
		}
		applyRespawnHunger(player);
	}

	public static void onInitialLogin(EntityPlayer player) {
		if (!enableRespawnHunger || player.world.isRemote) {
			return;
		}
		if (!player.getEntityData().hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
			player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, new net.minecraft.nbt.NBTTagCompound());
		}
		net.minecraft.nbt.NBTTagCompound persisted = player.getEntityData()
				.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		if (!persisted.getBoolean("HungerTweakerInitialHunger")) {
			applyRespawnHunger(player);
			persisted.setBoolean("HungerTweakerInitialHunger", true);
		}
	}

	private static void applyRespawnHunger(EntityPlayer player) {
		int value = respawnHunger;
		if (difficultyScalingRespawnHunger && player.world.getDifficulty().getId() > EnumDifficulty.EASY.getId()) {
			value -= (player.world.getDifficulty().getId() - 1) * respawnHungerDifficultyModifier;
		}
		value = Math.min(20, Math.max(1, value));
		squeek.applecore.api.AppleCoreAPI.mutator.setHunger(player, value);
		if (player.getFoodStats().getSaturationLevel() > value) {
			squeek.applecore.api.AppleCoreAPI.mutator.setSaturation(player, value);
		}
	}

	public static boolean isInstantStarvation() {
		return instantStarvation;
	}

	public static float modifyStarvationDamage(float value) {
		if (instantStarvation) {
			return value;
		}
		return starvationDamageConfigured && damageOnStarve != null
				? (float) damageOnStarve.eval(value) : value;
	}

	public static boolean allowCropGrowth(World world, net.minecraft.util.math.BlockPos pos) {
		if (!modifyCropGrowth) {
			return true;
		}
		if (cropGrowthDaylightOnly && !world.isDaytime()) {
			return false;
		}
		float chance = 1F / cropGrowthMultiplier;
		if (cropGrowthNeedsSky && !world.canSeeSky(pos)) {
			if (cropGrowthNoSkyMultiplier == 0) {
				return false;
			}
			chance /= cropGrowthNoSkyMultiplier;
		}
		return world.rand.nextFloat() < Math.min(1F, chance);
	}

	public static float getBonemealChance(World world) {
		float chance = bonemealEffectiveness;
		if (difficultyScalingBoneMeal) {
			if (world.getDifficulty() == EnumDifficulty.NORMAL) {
				chance *= 0.5F;
			} else if (world.getDifficulty() == EnumDifficulty.HARD) {
				chance = 0;
			}
		}
		return Math.max(0, Math.min(1, chance));
	}

	public static IBlockState modifyBonemealState(IBlockState state) {
		if (state.getBlock() instanceof BlockCrops) {
			int age = state.getValue(BlockCrops.AGE);
			return state.withProperty(BlockCrops.AGE, Math.min(age + 1, 7));
		}
		if (state.getBlock() instanceof BlockBeetroot) {
			int age = state.getValue(BlockBeetroot.BEETROOT_AGE);
			return state.withProperty(BlockBeetroot.BEETROOT_AGE, Math.min(age + 1, 3));
		}
		return state;
	}

	public static void applyLowStatEffects(EntityPlayer player) {
		for (LowStatRule rule : LOW_STAT_RULES) {
			boolean active = rule.health ? player.getHealth() / player.getMaxHealth() <= rule.threshold
					: player.getFoodStats().getFoodLevel() <= rule.threshold;
			if (active) {
				int amplifier = rule.effect.getAmplifier();
				if (difficultyScalingEffects) {
					amplifier = Math.max(0, amplifier + player.world.getDifficulty().getId() - EnumDifficulty.NORMAL.getId());
				}
				player.addPotionEffect(new PotionEffect(rule.effect.getPotion(), 25, amplifier,
						rule.effect.getIsAmbient(), rule.effect.doesShowParticles()));
			}
		}
	}

	public static void applyFoodStackSizes() {
		if (!modifyFoodStackSize) {
			return;
		}
		for (Item item : Item.REGISTRY) {
			if (!(item instanceof ItemFood)) {
				continue;
			}
			ItemStack stack = new ItemStack(item);
			FoodValues values = FoodValues.get(stack);
			if (values == null) {
				continue;
			}
			int max;
			if (values.hunger <= 2) max = 16 * foodStackSizeMultiplier;
			else if (values.hunger <= 5) max = 8 * foodStackSizeMultiplier;
			else if (values.hunger <= 8) max = 4 * foodStackSizeMultiplier;
			else if (values.hunger <= 11) max = 2 * foodStackSizeMultiplier;
			else max = foodStackSizeMultiplier;
			if (new ResourceLocation("minecraft:rotten_flesh").equals(item.getRegistryName())) {
				max = Math.max(8 * foodStackSizeMultiplier, 40);
			}
			if (item.getItemStackLimit(stack) > max) item.setMaxStackSize(max);
		}
	}

	public static void ensureTickHandler() {
		if (ModEventHandler.TickHandler.instance == null) {
			ModEventHandler.TickHandler.instance = new ModEventHandler.TickHandler();
			MinecraftForge.EVENT_BUS.register(ModEventHandler.TickHandler.instance);
		}
	}

	public static void releaseTickHandlerIfUnused() {
		if (constantHungerLoss == 0 && CTExhaustion.constantExhaustionIncrease == 0 && LOW_STAT_RULES.isEmpty()
				&& ModEventHandler.TickHandler.instance != null) {
			MinecraftForge.EVENT_BUS.unregister(ModEventHandler.TickHandler.instance);
			ModEventHandler.TickHandler.instance = null;
		}
	}

	private static void addLowStatRule(IPotionEffect effect, float threshold, boolean health) {
		PotionEffect converted = CraftTweakerMC.getPotionEffect(effect);
		if (converted == null) throw new IllegalArgumentException("Low-stat effect cannot be null.");
		LOW_STAT_RULES.add(new LowStatRule(converted, threshold, health));
		ensureTickHandler();
	}

	private static float requireDelayMultiplier(float multiplier, String name) {
		if (multiplier < 1) {
			throw new IllegalArgumentException(name + " multiplier must be at least 1.");
		}
		return multiplier;
	}

	private static class LowStatRule {
		private final PotionEffect effect;
		private final float threshold;
		private final boolean health;

		private LowStatRule(PotionEffect effect, float threshold, boolean health) {
			this.effect = effect;
			this.threshold = threshold;
			this.health = health;
		}
	}
}
