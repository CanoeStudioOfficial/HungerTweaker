package coolsquid.hungertweaker;

import coolsquid.hungertweaker.ct.CTFoodValues;
import coolsquid.hungertweaker.ct.CTHunger;
import coolsquid.hungertweaker.ct.CTHungerOverhaul;
import coolsquid.hungertweaker.ct.CTFoodEffects;
import coolsquid.hungertweaker.ct.CTStarvation;
import coolsquid.hungertweaker.ct.compat.CTFoodSpoiling;
import coolsquid.hungertweaker.ct.compat.CTNutrition;
import coolsquid.hungertweaker.ct.compat.CTSpiceOfLife;
import coolsquid.hungertweaker.ct.compat.CTSpiceOfLifeCarrotEdition;
import coolsquid.hungertweaker.ct.events.CTAllowExhaustionEvent;
import coolsquid.hungertweaker.ct.events.CTAllowRegenEvent;
import coolsquid.hungertweaker.ct.events.CTAllowSaturatedRegenEvent;
import coolsquid.hungertweaker.ct.events.CTAllowStarvationEvent;
import coolsquid.hungertweaker.ct.events.CTExhaustedEvent;
import coolsquid.hungertweaker.ct.events.CTExhaustingActionEvent;
import coolsquid.hungertweaker.ct.events.CTFoodEatenEvent;
import coolsquid.hungertweaker.ct.events.CTFoodSpoilingFoodValuesEvent;
import coolsquid.hungertweaker.ct.events.CTFoodStatsAdditionEvent;
import coolsquid.hungertweaker.ct.events.CTGetFoodValuesEvent;
import coolsquid.hungertweaker.ct.events.CTGetMaxExhaustionEvent;
import coolsquid.hungertweaker.ct.events.CTGetMaxHungerEvent;
import coolsquid.hungertweaker.ct.events.CTGetRegenTickPeriodEvent;
import coolsquid.hungertweaker.ct.events.CTGetSaturatedRegenTickPeriodEvent;
import coolsquid.hungertweaker.ct.events.CTGetStarveTickPeriodEvent;
import coolsquid.hungertweaker.ct.events.CTNutritionFoodEatenEvent;
import coolsquid.hungertweaker.ct.events.CTPeacefulHungerRegenEvent;
import coolsquid.hungertweaker.ct.events.CTPeacefulRegenEvent;
import coolsquid.hungertweaker.ct.events.CTRegenEvent;
import coolsquid.hungertweaker.ct.events.CTSaturatedRegenEvent;
import coolsquid.hungertweaker.ct.events.CTSpiceOfLifeCarrotFoodEatenEvent;
import coolsquid.hungertweaker.ct.events.CTSpiceOfLifeFoodEatenEvent;
import coolsquid.hungertweaker.ct.events.CTStarveEvent;
import coolsquid.hungertweaker.ct.events.HungerEventManager;
import coolsquid.hungertweaker.ct.exhaustion.CTExhaustingAction;
import coolsquid.hungertweaker.ct.exhaustion.CTExhaustion;
import coolsquid.hungertweaker.ct.regen.CTPeacefulRegen;
import coolsquid.hungertweaker.ct.regen.CTRegen;
import coolsquid.hungertweaker.ct.regen.CTSaturatedRegen;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import squeek.applecore.api.food.FoodEvent;
import squeek.applecore.api.food.FoodValues;
import squeek.applecore.api.hunger.ExhaustionEvent;
import squeek.applecore.api.hunger.HealthRegenEvent;
import squeek.applecore.api.hunger.HungerEvent;
import squeek.applecore.api.hunger.HungerRegenEvent;
import squeek.applecore.api.hunger.StarvationEvent;

public class ModEventHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(FoodEvent.GetFoodValues ie) {
		for (int index = CTFoodValues.LIST.size() - 1; index >= 0; index--) {
			CTFoodValues v = CTFoodValues.LIST.get(index);
			if ((v.hunger != null || v.saturationModifier != null)
					&& v.ingredient.matches(CraftTweakerMC.getIItemStack(ie.food))) {
				int hunger = (int) (v.hunger == null ? ie.foodValues.hunger : v.hunger.eval(ie.foodValues.hunger));
				float saturationModifier = (float) (v.saturationModifier == null ? ie.foodValues.saturationModifier
						: v.saturationModifier.eval(ie.foodValues.saturationModifier));
				ie.foodValues = new FoodValues(hunger, saturationModifier);
				break;
			}
		}
		CTHungerOverhaul.modifyFoodValues(ie);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(FoodEvent.GetPlayerFoodValues ie) {
		if (HungerEventManager.GET_FOOD_VALUES.hasHandlers()) {
			HungerEventManager.GET_FOOD_VALUES.publish(new CTGetFoodValuesEvent(ie));
		}
		if (Loader.isModLoaded(CTFoodSpoiling.MODID)
				&& HungerEventManager.FOOD_SPOILING_FOOD_VALUES.hasHandlers()) {
			HungerEventManager.FOOD_SPOILING_FOOD_VALUES.publish(new CTFoodSpoilingFoodValuesEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(FoodEvent.FoodEaten ie) {
		CTFoodEffects.apply(ie.player, ie.food);
		CTHungerOverhaul.onFoodEaten(ie);
		if (HungerEventManager.FOOD_EATEN.hasHandlers()) {
			HungerEventManager.FOOD_EATEN.publish(new CTFoodEatenEvent(ie));
		}
		if (Loader.isModLoaded(CTNutrition.MODID) && HungerEventManager.NUTRITION_FOOD_EATEN.hasHandlers()) {
			HungerEventManager.NUTRITION_FOOD_EATEN.publish(new CTNutritionFoodEatenEvent(ie));
		}
		if (Loader.isModLoaded(CTSpiceOfLife.MODID) && HungerEventManager.SPICE_OF_LIFE_FOOD_EATEN.hasHandlers()) {
			HungerEventManager.SPICE_OF_LIFE_FOOD_EATEN.publish(new CTSpiceOfLifeFoodEatenEvent(ie));
		}
		if (Loader.isModLoaded(CTSpiceOfLifeCarrotEdition.MODID)
				&& HungerEventManager.SPICE_OF_LIFE_CARROT_FOOD_EATEN.hasHandlers()) {
			HungerEventManager.SPICE_OF_LIFE_CARROT_FOOD_EATEN.publish(new CTSpiceOfLifeCarrotFoodEatenEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(FoodEvent.FoodStatsAddition ie) {
		if (CTHungerOverhaul.shouldDenyFoodStatsAddition()) {
			ie.setCanceled(true);
		}
		if (HungerEventManager.FOOD_STATS_ADDITION.hasHandlers()) {
			HungerEventManager.FOOD_STATS_ADDITION.publish(new CTFoodStatsAdditionEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(ExhaustionEvent.AllowExhaustion ie) {
		if (CTHungerOverhaul.shouldDenyExhaustion()) {
			squeek.applecore.api.AppleCoreAPI.mutator.setHunger(ie.player, 19);
			squeek.applecore.api.AppleCoreAPI.mutator.setSaturation(ie.player, 0);
			squeek.applecore.api.AppleCoreAPI.mutator.setExhaustion(ie.player, 0);
			ie.setResult(Result.DENY);
		}
		if (CTExhaustion.status != Result.DEFAULT) {
			ie.setResult(CTExhaustion.status);
		}
		if (HungerEventManager.ALLOW_EXHAUSTION.hasHandlers()) {
			HungerEventManager.ALLOW_EXHAUSTION.publish(new CTAllowExhaustionEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(ExhaustionEvent.Exhausted ie) {
		if (CTHungerOverhaul.peacefulExhaustionHungerLoss
				&& ie.player.getFoodStats().getSaturationLevel() <= 0) {
			ie.deltaHunger = -1;
		}
		if (CTExhaustion.deltaExhaustion != null) {
			ie.deltaExhaustion = (float) CTExhaustion.deltaExhaustion.eval(ie.deltaExhaustion);
		}
		if (CTExhaustion.deltaHunger != null) {
			ie.deltaHunger = (int) CTExhaustion.deltaHunger.eval(ie.deltaHunger);
		}
		if (CTExhaustion.deltaSaturation != null) {
			ie.deltaSaturation = (float) CTExhaustion.deltaSaturation.eval(ie.deltaSaturation);
		}
		if (HungerEventManager.EXHAUSTED.hasHandlers()) {
			HungerEventManager.EXHAUSTED.publish(new CTExhaustedEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(ExhaustionEvent.ExhaustingAction ie) {
		CTExhaustingAction a = CTExhaustingAction.MAP.get(ie.source);
		if (a.deltaExhaustion != null) {
			ie.deltaExhaustion = (float) a.deltaExhaustion.eval(ie.deltaExhaustion);
		}
		if (HungerEventManager.EXHAUSTING_ACTION.hasHandlers()) {
			HungerEventManager.EXHAUSTING_ACTION.publish(new CTExhaustingActionEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(ExhaustionEvent.GetMaxExhaustion ie) {
		ie.maxExhaustionLevel = CTHungerOverhaul.modifyMaxExhaustion(ie.player, ie.maxExhaustionLevel);
		if (CTExhaustion.maxExhaustionLevel != null) {
			ie.maxExhaustionLevel = (float) CTExhaustion.maxExhaustionLevel.eval(ie.maxExhaustionLevel);
		}
		if (HungerEventManager.GET_MAX_EXHAUSTION.hasHandlers()) {
			HungerEventManager.GET_MAX_EXHAUSTION.publish(new CTGetMaxExhaustionEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(HungerEvent.GetMaxHunger ie) {
		if (CTHunger.maxHunger != null) {
			ie.maxHunger = (int) CTHunger.maxHunger.eval(ie.maxHunger);
		}
		if (HungerEventManager.GET_MAX_HUNGER.hasHandlers()) {
			HungerEventManager.GET_MAX_HUNGER.publish(new CTGetMaxHungerEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(StarvationEvent.AllowStarvation ie) {
		if (CTStarvation.status != Result.DEFAULT) {
			ie.setResult(CTStarvation.status);
		}
		if (HungerEventManager.ALLOW_STARVATION.hasHandlers()) {
			HungerEventManager.ALLOW_STARVATION.publish(new CTAllowStarvationEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(StarvationEvent.GetStarveTickPeriod ie) {
		if (CTStarvation.interval != null) {
			ie.starveTickPeriod = (int) CTStarvation.interval.eval(ie.starveTickPeriod);
		}
		if (HungerEventManager.GET_STARVE_TICK_PERIOD.hasHandlers()) {
			HungerEventManager.GET_STARVE_TICK_PERIOD.publish(new CTGetStarveTickPeriodEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(StarvationEvent.Starve ie) {
		if (CTHungerOverhaul.isInstantStarvation()) {
			ie.starveDamage = Math.max(ie.player.getMaxHealth() * 2F, 1F);
		} else {
			ie.starveDamage = CTHungerOverhaul.modifyStarvationDamage(ie.starveDamage);
		}
		if (CTStarvation.starveDamage != null) {
			ie.starveDamage = (float) CTStarvation.starveDamage.eval(ie.starveDamage);
		}
		if (HungerEventManager.STARVE.hasHandlers()) {
			HungerEventManager.STARVE.publish(new CTStarveEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(HealthRegenEvent.AllowRegen ie) {
		if (CTHungerOverhaul.shouldDenyRegen()) {
			ie.setResult(Result.DENY);
		}
		if (CTHungerOverhaul.requireMinimumHungerToHeal) {
			ie.setResult(CTHungerOverhaul.shouldAllowRegen(ie.player) ? Result.ALLOW : Result.DENY);
		}
		if (CTRegen.status != Result.DEFAULT) {
			ie.setResult(CTRegen.status);
		}
		if (HungerEventManager.ALLOW_REGEN.hasHandlers()) {
			HungerEventManager.ALLOW_REGEN.publish(new CTAllowRegenEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(HealthRegenEvent.AllowSaturatedRegen ie) {
		if (CTHungerOverhaul.shouldDenyRegen()) {
			ie.setResult(Result.DENY);
		}
		if (CTSaturatedRegen.status != Result.DEFAULT) {
			ie.setResult(CTSaturatedRegen.status);
		}
		if (HungerEventManager.ALLOW_SATURATED_REGEN.hasHandlers()) {
			HungerEventManager.ALLOW_SATURATED_REGEN.publish(new CTAllowSaturatedRegenEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(HealthRegenEvent.GetRegenTickPeriod ie) {
		ie.regenTickPeriod = CTHungerOverhaul.modifyRegenPeriod(ie.player, ie.regenTickPeriod);
		if (CTRegen.interval != null) {
			ie.regenTickPeriod = (int) CTRegen.interval.eval(ie.regenTickPeriod);
		}
		if (HungerEventManager.GET_REGEN_TICK_PERIOD.hasHandlers()) {
			HungerEventManager.GET_REGEN_TICK_PERIOD.publish(new CTGetRegenTickPeriodEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(HealthRegenEvent.GetSaturatedRegenTickPeriod ie) {
		if (CTSaturatedRegen.interval != null) {
			ie.regenTickPeriod = (int) CTSaturatedRegen.interval.eval(ie.regenTickPeriod);
		}
		if (HungerEventManager.GET_SATURATED_REGEN_TICK_PERIOD.hasHandlers()) {
			HungerEventManager.GET_SATURATED_REGEN_TICK_PERIOD.publish(new CTGetSaturatedRegenTickPeriodEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(HealthRegenEvent.PeacefulRegen ie) {
		if (CTPeacefulRegen.deltaHealth != null) {
			ie.deltaHealth = (float) CTPeacefulRegen.deltaHealth.eval(ie.deltaHealth);
		}
		if (CTPeacefulRegen.healthStatus == Result.ALLOW) {
			ie.setCanceled(false);
		} else if (CTPeacefulRegen.healthStatus == Result.DENY) {
			ie.setCanceled(true);
		}
		if (HungerEventManager.PEACEFUL_REGEN.hasHandlers()) {
			HungerEventManager.PEACEFUL_REGEN.publish(new CTPeacefulRegenEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(HealthRegenEvent.Regen ie) {
		if (CTHungerOverhaul.isHealingHungerDrainDisabled()) {
			ie.deltaExhaustion = 0;
		}
		if (CTRegen.deltaHealth != null) {
			ie.deltaHealth = (float) CTRegen.deltaHealth.eval(ie.deltaHealth);
		}
		if (CTRegen.deltaExhaustion != null) {
			ie.deltaExhaustion = (float) CTRegen.deltaExhaustion.eval(ie.deltaExhaustion);
		}
		if (HungerEventManager.REGEN.hasHandlers()) {
			HungerEventManager.REGEN.publish(new CTRegenEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(HealthRegenEvent.SaturatedRegen ie) {
		if (CTSaturatedRegen.deltaHealth != null) {
			ie.deltaHealth = (float) CTSaturatedRegen.deltaHealth.eval(ie.deltaHealth);
		}
		if (CTSaturatedRegen.deltaExhaustion != null) {
			ie.deltaExhaustion = (float) CTSaturatedRegen.deltaExhaustion.eval(ie.deltaExhaustion);
		}
		if (HungerEventManager.SATURATED_REGEN.hasHandlers()) {
			HungerEventManager.SATURATED_REGEN.publish(new CTSaturatedRegenEvent(ie));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(HungerRegenEvent.PeacefulRegen ie) {
		if (CTPeacefulRegen.deltaHunger != null) {
			ie.deltaHunger = (int) CTPeacefulRegen.deltaHunger.eval(ie.deltaHunger);
		}
		if (CTPeacefulRegen.hungerStatus == Result.ALLOW) {
			ie.setCanceled(false);
		} else if (CTPeacefulRegen.hungerStatus == Result.DENY) {
			ie.setCanceled(true);
		}
		if (HungerEventManager.PEACEFUL_HUNGER_REGEN.hasHandlers()) {
			HungerEventManager.PEACEFUL_HUNGER_REGEN.publish(new CTPeacefulHungerRegenEvent(ie));
		}
	}

	public static class TickHandler {

		public static TickHandler instance;

		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void on(PlayerTickEvent event) {
			if (event.phase != net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END) {
				return;
			}
			if (CTExhaustion.constantExhaustionIncrease != 0) {
				event.player.getFoodStats().addExhaustion(CTExhaustion.constantExhaustionIncrease);
			}
			if (CTHungerOverhaul.constantHungerLoss != 0 && !event.player.capabilities.isCreativeMode
					&& !event.player.isDead) {
				event.player.addExhaustion(CTHungerOverhaul.constantHungerLoss);
			}
			if (!event.player.world.isRemote && event.player.ticksExisted % 20 == 0) {
				CTHungerOverhaul.applyLowStatEffects(event.player);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(LivingEntityUseItemEvent.Start event) {
		CTFoodEffects.trackFoodUse(event.getEntityLiving() instanceof net.minecraft.entity.player.EntityPlayer
				? (net.minecraft.entity.player.EntityPlayer) event.getEntityLiving() : null, event.getItem());
		if (!CTHungerOverhaul.modifyEatingSpeed
				|| !squeek.applecore.api.AppleCoreAPI.accessor.isFood(event.getItem())) {
			return;
		}
		FoodValues values = FoodValues.get(event.getItem());
		if (values == null || values.hunger <= 0) {
			return;
		}
		int duration = CTHungerOverhaul.eatingDuration == null ? values.hunger * 8 + 8
				: (int) CTHungerOverhaul.eatingDuration.eval(values.hunger);
		if (CTHungerOverhaul.eatingDurationMultiplier != null) {
			duration = (int) (duration * CTHungerOverhaul.eatingDurationMultiplier.eval(values.hunger));
		}
		event.setDuration(Math.max(1, duration));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(PlayerRespawnEvent event) {
		CTHungerOverhaul.onRespawn(event.player);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntityLiving() instanceof net.minecraft.entity.player.EntityPlayer) {
			CTFoodEffects.restoreDefaultEffects((net.minecraft.entity.player.EntityPlayer) event.getEntityLiving(),
					event.getItem());
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(LivingEntityUseItemEvent.Stop event) {
		if (event.getEntityLiving() instanceof net.minecraft.entity.player.EntityPlayer) {
			CTFoodEffects.clearFoodUse((net.minecraft.entity.player.EntityPlayer) event.getEntityLiving());
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(PlayerLoggedInEvent event) {
		CTHungerOverhaul.onInitialLogin(event.player);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(LivingUpdateEvent event) {
		if (event.getEntityLiving().world.isRemote || !CTHungerOverhaul.modifyAnimalDelays
				|| !(event.getEntityLiving() instanceof EntityAnimal)) {
			return;
		}
		EntityAnimal animal = (EntityAnimal) event.getEntityLiving();
		if (animal instanceof EntityAgeable) {
			EntityAgeable ageable = (EntityAgeable) animal;
			int age = ageable.getGrowingAge();
			if (age > 0 && shouldDelay(CTHungerOverhaul.breedingTimeoutMultiplier, animal.getRNG())) {
				ageable.setGrowingAge(age + 1);
			} else if (age < 0 && shouldDelay(CTHungerOverhaul.childDurationMultiplier, animal.getRNG())) {
				ageable.setGrowingAge(age - 1);
			}
		}
		if (animal instanceof EntityChicken) {
			EntityChicken chicken = (EntityChicken) animal;
			if (chicken.timeUntilNextEgg > 0
					&& shouldDelay(CTHungerOverhaul.eggTimeoutMultiplier, animal.getRNG())) {
				chicken.timeUntilNextEgg++;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(CropGrowEvent.Pre event) {
		if (!CTHungerOverhaul.modifyCropGrowth || event.getResult() != Result.DEFAULT
				|| !isSupportedCrop(event.getState().getBlock())) {
			return;
		}
		if (!CTHungerOverhaul.allowCropGrowth(event.getWorld(), event.getPos())) {
			event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void on(BonemealEvent event) {
		if (event.getWorld().isRemote || !CTHungerOverhaul.modifyBonemeal
				|| event.isCanceled() || event.getResult() != Result.DEFAULT
				|| !isSupportedCrop(event.getBlock().getBlock())) {
			return;
		}
		if (event.getWorld().rand.nextFloat() >= CTHungerOverhaul.getBonemealChance(event.getWorld())) {
			event.setResult(Result.ALLOW);
			return;
		}
		if (CTHungerOverhaul.modifyBonemealGrowth) {
			net.minecraft.block.state.IBlockState result = CTHungerOverhaul.modifyBonemealState(event.getBlock());
			if (!result.equals(event.getBlock())) {
				event.getWorld().setBlockState(event.getPos(), result, 3);
				event.setResult(Result.ALLOW);
			}
		}
	}

	private static boolean shouldDelay(float multiplier, java.util.Random random) {
		return multiplier > 1F && random.nextFloat() * multiplier >= 1F;
	}

	private static boolean isSupportedCrop(net.minecraft.block.Block block) {
		return block instanceof BlockCrops || block instanceof BlockBeetroot
				|| block == net.minecraft.init.Blocks.REEDS || block == net.minecraft.init.Blocks.CACTUS
				|| block == net.minecraft.init.Blocks.PUMPKIN_STEM || block == net.minecraft.init.Blocks.MELON_STEM
				|| block == net.minecraft.init.Blocks.COCOA || block == net.minecraft.init.Blocks.NETHER_WART
				|| block == net.minecraft.init.Blocks.SAPLING;
	}
}
