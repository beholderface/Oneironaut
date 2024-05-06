package net.oneironaut.casting;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class GlowingAmbitEffect extends StatusEffect {
    //this class is only necessary because StatusEffect's constructor method is protected for some reason, the actual functionality of the effect is in a couple mixins
    //the glow is in GlowAmbitEffectGlowMixin, and the ambit is in GlowAmbitEffectAmbitMixin
    public GlowingAmbitEffect() {
        super(StatusEffectCategory.NEUTRAL, 0x7355ff);
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier){
        return false;
    }
}
