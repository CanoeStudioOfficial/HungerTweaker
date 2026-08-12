package coolsquid.hungertweaker.ct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotionEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** Adds the food-effect rules requested by EditableEdibles without requiring that mod. */
@ZenRegister
@ZenClass("mods.hungertweaker.FoodEffects")
public class CTFoodEffects {

	private static final List<EffectRule> EFFECTS = new ArrayList<>();
	private static final List<CureRule> CURES = new ArrayList<>();
	private static final List<IIngredient> CANCEL_DEFAULT_EFFECTS = new ArrayList<>();
	private static final Map<UUID, PendingFoodUse> PENDING_USES = new HashMap<>();

	private CTFoodEffects() {
	}

	@ZenMethod
	public static void addEffect(IIngredient food, IPotionEffect effect, float chance) {
		addEffect(food, effect, chance, false, -1, false, -1);
	}

	@ZenMethod
	public static void addEffect(IIngredient food, IPotionEffect effect, float chance,
			boolean additiveDuration, int maxDuration, boolean additiveAmplifier, int maxAmplifier) {
		PotionEffect potionEffect = CraftTweakerMC.getPotionEffect(effect);
		if (potionEffect == null) {
			throw new IllegalArgumentException("Food effect cannot be null.");
		}
		if (chance < 0) {
			throw new IllegalArgumentException("Food effect chance cannot be negative.");
		}
		EFFECTS.add(new EffectRule(food, potionEffect, chance, additiveDuration, maxDuration,
				additiveAmplifier, maxAmplifier));
	}

	@ZenMethod
	public static void addCureEffect(IIngredient food, IPotionEffect effect, float chance) {
		PotionEffect potionEffect = CraftTweakerMC.getPotionEffect(effect);
		if (potionEffect == null) {
			throw new IllegalArgumentException("Food cure effect cannot be null.");
		}
		if (chance < 0) {
			throw new IllegalArgumentException("Food cure chance cannot be negative.");
		}
		CURES.add(CureRule.effect(food, potionEffect, Math.min(1, chance)));
	}

	@ZenMethod
	public static void addCureType(IIngredient food, String cureType, float chance) {
		if (cureType == null || chance < 0) {
			throw new IllegalArgumentException("Food cure type cannot be null and chance cannot be negative.");
		}
		try {
			CURES.add(CureRule.type(food, CureType.valueOf(cureType.toUpperCase()), Math.min(1, chance)));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unknown food cure type '" + cureType
					+ "'. Use ALL, POSITIVE, or NEGATIVE.", e);
		}
	}

	/** Convenience form of <code>food.foodValues.alwaysEdible = value</code>. */
	@ZenMethod
	public static void setAlwaysEdible(IIngredient food, boolean value) {
		CTFoodValues.get(food).setAlwaysEdible(value);
	}

	/**
	 * Restores potion effects present before eating after the food's vanilla effect
	 * hook has run. This does not intercept arbitrary custom onFoodEaten side effects.
	 */
	@ZenMethod
	public static void setCancelDefaultEffects(IIngredient food, boolean cancel) {
		removeMatchingIngredients(CANCEL_DEFAULT_EFFECTS, food);
		if (cancel) {
			CANCEL_DEFAULT_EFFECTS.add(food);
		}
	}

	@ZenMethod
	public static void clearEffects(IIngredient food) {
		removeMatching(EFFECTS, food);
		removeMatching(CURES, food);
		removeMatchingIngredients(CANCEL_DEFAULT_EFFECTS, food);
	}

	@ZenMethod
	public static void clearAll() {
		EFFECTS.clear();
		CURES.clear();
		CANCEL_DEFAULT_EFFECTS.clear();
		PENDING_USES.clear();
	}

	public static void trackFoodUse(EntityPlayer player, ItemStack food) {
		if (player == null || food == null || food.isEmpty() || !matchesCancelRule(food)) {
			return;
		}
		Map<Potion, PotionEffect> effects = new HashMap<>();
		for (PotionEffect effect : player.getActivePotionEffects()) {
			effects.put(effect.getPotion(), new PotionEffect(effect));
		}
		PENDING_USES.put(player.getUniqueID(), new PendingFoodUse(food.copy(), effects));
	}

	public static void clearFoodUse(EntityPlayer player) {
		if (player != null) {
			PENDING_USES.remove(player.getUniqueID());
		}
	}

	public static void restoreDefaultEffects(EntityPlayer player, ItemStack food) {
		if (player == null || food == null || food.isEmpty()) {
			return;
		}
		PendingFoodUse pending = PENDING_USES.remove(player.getUniqueID());
		if (pending == null || !pending.food.isItemEqual(food)
				|| !matchesCancelRule(food)) {
			return;
		}
		List<Potion> current = new ArrayList<>();
		for (PotionEffect effect : player.getActivePotionEffects()) {
			current.add(effect.getPotion());
		}
		for (Potion potion : current) {
			PotionEffect before = pending.effects.get(potion);
			if (before == null) {
				player.removePotionEffect(potion);
			} else {
				PotionEffect after = player.getActivePotionEffect(potion);
				if (after == null || after.getDuration() != before.getDuration()
						|| after.getAmplifier() != before.getAmplifier()) {
					player.removePotionEffect(potion);
					player.addPotionEffect(new PotionEffect(before));
				}
			}
		}
	}

	public static void apply(EntityPlayer player, ItemStack food) {
		if (player == null || food == null || food.isEmpty() || player.world.isRemote) {
			return;
		}
		IItemStack foodStack = CraftTweakerMC.getIItemStack(food);
		for (EffectRule rule : EFFECTS) {
			if (!rule.weighted && rule.food.matches(foodStack) && rule.roll(player)) {
				applyEffect(player, rule);
			}
		}
		applyWeightedEffect(player, foodStack);
		for (CureRule rule : CURES) {
			if (rule.food.matches(foodStack) && player.getRNG().nextFloat() < rule.chance) {
				if (rule.type == null) {
					cureEffect(player, rule.effect);
				} else {
					cureType(player, rule.type);
				}
			}
		}
	}

	private static boolean matchesCancelRule(ItemStack food) {
		IItemStack stack = CraftTweakerMC.getIItemStack(food);
		for (IIngredient ingredient : CANCEL_DEFAULT_EFFECTS) {
			if (ingredient.matches(stack)) {
				return true;
			}
		}
		return false;
	}

	private static void applyWeightedEffect(EntityPlayer player, IItemStack food) {
		float totalWeight = 0;
		for (EffectRule rule : EFFECTS) {
			if (rule.weighted && rule.food.matches(food)) {
				totalWeight += rule.chance;
			}
		}
		if (totalWeight <= 0) {
			return;
		}
		float target = player.getRNG().nextFloat() * totalWeight;
		for (EffectRule rule : EFFECTS) {
			if (rule.weighted && rule.food.matches(food)) {
				target -= rule.chance;
				if (target < 0) {
					applyEffect(player, rule);
					return;
				}
			}
		}
	}

	private static void applyEffect(EntityPlayer player, EffectRule rule) {
		PotionEffect effect = rule.effect;
		if (effect.getPotion().isInstant()) {
			effect.getPotion().affectEntity(player, player, player, effect.getAmplifier(), 1.0D);
			return;
		}
		int duration = effect.getDuration();
		int amplifier = effect.getAmplifier();
		PotionEffect current = player.getActivePotionEffect(effect.getPotion());
		if (current != null && rule.additiveDuration) {
			duration += current.getDuration();
		}
		if (current != null && rule.additiveAmplifier) {
			amplifier += current.getAmplifier() + 1;
		}
		if (rule.maxDuration >= 0) {
			duration = Math.min(duration, rule.maxDuration);
		}
		if (rule.maxAmplifier >= 0) {
			amplifier = Math.min(amplifier, rule.maxAmplifier);
		}
		player.addPotionEffect(new PotionEffect(effect.getPotion(), Math.max(1, duration), Math.max(0, amplifier),
				effect.getIsAmbient(), effect.doesShowParticles()));
	}

	private static void cureEffect(EntityPlayer player, PotionEffect cure) {
		PotionEffect active = player.getActivePotionEffect(cure.getPotion());
		if (active == null) {
			return;
		}
		boolean durationMatches = cure.getDuration() < 0 || cure.getDuration() >= active.getDuration();
		boolean amplifierMatches = cure.getAmplifier() < 0 || cure.getAmplifier() >= active.getAmplifier();
		if (durationMatches && amplifierMatches) {
			player.removePotionEffect(cure.getPotion());
		}
	}

	private static void cureType(EntityPlayer player, CureType type) {
		if (type == CureType.ALL) {
			player.clearActivePotions();
			return;
		}
		List<Potion> toRemove = new ArrayList<>();
		for (PotionEffect active : player.getActivePotionEffects()) {
			boolean positive = !active.getPotion().isBadEffect();
			if ((type == CureType.POSITIVE && positive) || (type == CureType.NEGATIVE && !positive)) {
				toRemove.add(active.getPotion());
			}
		}
		for (Potion potion : toRemove) {
			player.removePotionEffect(potion);
		}
	}

	private static <T> void removeMatching(List<T> rules, IIngredient food) {
		Iterator<T> iterator = rules.iterator();
		while (iterator.hasNext()) {
			Object rule = iterator.next();
			IIngredient ingredient = rule instanceof EffectRule ? ((EffectRule) rule).food : ((CureRule) rule).food;
			if (ingredient == food || (ingredient != null && food != null
					&& ingredient.toCommandString().equals(food.toCommandString()))) {
				iterator.remove();
			}
		}
	}

	private static void removeMatchingIngredients(List<IIngredient> rules, IIngredient food) {
		Iterator<IIngredient> iterator = rules.iterator();
		while (iterator.hasNext()) {
			IIngredient ingredient = iterator.next();
			if (ingredient == food || (ingredient != null && food != null
					&& ingredient.toCommandString().equals(food.toCommandString()))) {
				iterator.remove();
			}
		}
	}

	private static class PendingFoodUse {
		private final ItemStack food;
		private final Map<Potion, PotionEffect> effects;

		private PendingFoodUse(ItemStack food, Map<Potion, PotionEffect> effects) {
			this.food = food;
			this.effects = effects;
		}
	}

	private static class EffectRule {
		private final IIngredient food;
		private final PotionEffect effect;
		private final float chance;
		private final boolean additiveDuration;
		private final int maxDuration;
		private final boolean additiveAmplifier;
		private final int maxAmplifier;
		private final boolean weighted;

		private EffectRule(IIngredient food, PotionEffect effect, float chance, boolean additiveDuration,
				int maxDuration, boolean additiveAmplifier, int maxAmplifier) {
			this.food = food;
			this.effect = effect;
			this.additiveDuration = additiveDuration;
			this.maxDuration = maxDuration;
			this.additiveAmplifier = additiveAmplifier;
			this.maxAmplifier = maxAmplifier;
			this.weighted = chance > 1;
			if (this.weighted) {
				this.chance = chance;
			} else {
				this.chance = Math.max(0, Math.min(1, chance));
			}
		}

		private boolean roll(EntityPlayer player) {
			return player.getRNG().nextFloat() < chance;
		}
	}

	private enum CureType {
		ALL, POSITIVE, NEGATIVE
	}

	private static class CureRule {
		private final IIngredient food;
		private final PotionEffect effect;
		private final CureType type;
		private final float chance;

		private CureRule(IIngredient food, PotionEffect effect, CureType type, float chance) {
			this.food = food;
			this.effect = effect;
			this.type = type;
			this.chance = chance;
		}

		private static CureRule effect(IIngredient food, PotionEffect effect, float chance) {
			return new CureRule(food, effect, null, chance);
		}

		private static CureRule type(IIngredient food, CureType type, float chance) {
			return new CureRule(food, null, type, chance);
		}
	}
}
