package dev.latvian.kubejs.entity;

import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 * @author LatvianModder
 */
public class LivingEntityDeathEventJS extends LivingEntityEventJS
{
	public final DamageSourceJS source;

	public LivingEntityDeathEventJS(LivingDeathEvent event)
	{
		super(event.getEntity());
		source = new DamageSourceJS(world, event.getSource());
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}
}