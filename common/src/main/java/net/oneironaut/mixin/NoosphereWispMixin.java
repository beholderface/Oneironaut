package net.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import net.minecraft.world.tick.Tick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ram.talia.hexal.api.HexalAPI;
import ram.talia.hexal.api.config.HexalConfig;
import ram.talia.hexal.common.entities.BaseCastingWisp;
import ram.talia.hexal.common.entities.BaseWisp;
import ram.talia.hexal.common.entities.TickingWisp;


//none of this works and I don't know why, so I have removed it from oneironaut-common.mixins.json
@SuppressWarnings("ConstantConditions")
@Mixin(value = BaseCastingWisp.class)
public abstract class NoosphereWispMixin
{
    private final BaseCastingWisp wisp = (BaseCastingWisp) (Object) this;
    private final int baseUpkeep = HexalConfig.getServer().getTickingWispUpkeepPerTick();
    @Redirect(method = "deductMedia",
            at = @At(value = "INVOKE",
            target="Lram/talia/hexal/common/entities/BaseCastingWisp;getNormalCostPerTick(Lram/talia/hexal/common/entities/BaseCastingWisp;)I",
                    remap = false),
            remap = false)
    public int freeIfNoosphere1(){
        String worldName = wisp.getEntityWorld().getRegistryKey().getValue().toString();
        if (worldName.equals("oneironaut:noosphere")){
            return wisp.wispNumContainedPlayers() < 1 ? 0 : baseUpkeep;
        } else {
            return baseUpkeep;
        }
    }
    @Redirect(method = "deductMedia",
            at = @At(value = "INVOKE",
                    target="Lram/talia/hexal/common/entities/BaseCastingWisp;getUntriggeredCostPerTick(Lram/talia/hexal/common/entities/BaseCastingWisp;)I",
                    remap = false),
            remap = false)
    public int freeIfNoosphere2(){
        double discount = HexalConfig.getServer().getUntriggeredWispUpkeepDiscount();
        int discountedUpkeep = (int) (baseUpkeep * discount);
        String worldName = wisp.getEntityWorld().getRegistryKey().getValue().toString();
        if (worldName.equals("oneironaut:noosphere")){
            return wisp.wispNumContainedPlayers() < 1 ? 0 : discountedUpkeep;
        } else {
            return discountedUpkeep;
        }
    }
}
