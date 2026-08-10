package coolsquid.hungertweaker.ct.events;

import crafttweaker.api.event.IPlayerEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import stanhebben.zenscript.annotations.ZenGetter;

public abstract class CTDrinkEvent implements IPlayerEvent {

	private final LivingEntityUseItemEvent.Finish internal;

	protected CTDrinkEvent(LivingEntityUseItemEvent.Finish internal) {
		this.internal = internal;
	}

	@ZenGetter("player")
	@Override
	public IPlayer getPlayer() {
		return CraftTweakerMC.getIPlayer((EntityPlayer) this.internal.getEntityLiving());
	}

	@ZenGetter("food")
	public IItemStack getFood() {
		return CraftTweakerMC.getIItemStack(this.internal.getItem());
	}

	EntityPlayer internalPlayer() {
		return (EntityPlayer) this.internal.getEntityLiving();
	}

	IItemStack internalFood() {
		return this.getFood();
	}
}
