package net.oneironaut.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.oneironaut.Oneironaut;
import net.oneironaut.OneironautConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.oneironaut.registry.OneironautBlockRegistry.ETERNAL;

//my mega chorus on hexxycraft may have been griefed, but its spirit shall live on
@Mixin(ChorusFlowerBlock.class)
public class EternalChorusMixin {

    @Shadow @Final public static IntProperty AGE;
    @Unique private static final boolean debugMessages = false;

    @Inject(method = "appendProperties", at = @At(value = "HEAD", remap = true), remap = true)
    public void addEternal(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci){
        builder.add(ETERNAL);
    }
    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/ChorusFlowerBlock;grow(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;I)V",
    remap = true), remap = true)
    private void growForever(ChorusFlowerBlock instance, World world, BlockPos pos, int age, Operation<Void> original, @Local(ordinal = 0) BlockState state){
        boolean allowEternal = OneironautConfig.getServer().getInfusionEternalChorus();
        boolean isEternal = state.get(ETERNAL);
        if (!isEternal || !allowEternal){
            Oneironaut.boolLogger("not eternal", debugMessages);
            original.call(instance, world, pos, age);
        } else {
            Oneironaut.boolLogger("eternal!", debugMessages);
            growEternally(instance, world, pos);
            /*world.setBlockState(pos, (BlockState)instance.getDefaultState().with(AGE, 0).with(ETERNAL, true), 2);
            world.syncWorldEvent(1033, pos, 0);*/
        }
    }

    //cutting down a true mega chorus results in weird floating bits for some reason, but at least they destroy themselves when they try to grow
    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/ChorusFlowerBlock;die(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V",
            remap = true, ordinal = 0), remap = true)
    private void stopDying1(ChorusFlowerBlock instance, World world, BlockPos pos, Operation<Void> original, @Local(ordinal = 0) BlockState state){
        boolean allowEternal = OneironautConfig.getServer().getInfusionEternalChorus();
        boolean isEternal = state.get(ETERNAL);
        if (!isEternal || !allowEternal){
            Oneironaut.boolLogger("not eternal", debugMessages);
            original.call(instance, world, pos);
        } else {
            //do nothing
            Oneironaut.boolLogger("eternal!", debugMessages);
        }
    }

    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/ChorusFlowerBlock;die(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V",
            remap = true, ordinal = 1), remap = true)
    private void stopDying2(ChorusFlowerBlock instance, World world, BlockPos pos, Operation<Void> original, @Local(ordinal = 0) BlockState state){
        boolean allowEternal = OneironautConfig.getServer().getInfusionEternalChorus();
        boolean isEternal = state.get(ETERNAL);
        if (!isEternal || !allowEternal){
            Oneironaut.boolLogger("not eternal", debugMessages);
            original.call(instance, world, pos);
        } else {
            Oneironaut.boolLogger("eternal!", debugMessages);
            growEternally(instance, world, pos);
        }
    }

    @ModifyReceiver(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"))
    public BlockState notUsuallyEternal(BlockState instance, Property property, Comparable comparable){
        return instance.with(ETERNAL, false).with(AGE, 0);
    }

    @Unique private void growEternally(ChorusFlowerBlock instance, World world, BlockPos pos){
        world.setBlockState(pos, (BlockState)instance.getDefaultState().with(AGE, 0).with(ETERNAL, true), 2);
        world.syncWorldEvent(1033, pos, 0);

    }

}
