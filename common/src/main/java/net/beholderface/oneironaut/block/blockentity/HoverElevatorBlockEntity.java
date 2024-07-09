package net.beholderface.oneironaut.block.blockentity;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.common.misc.PlayerPositionRecorder;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import kotlin.Triple;
import net.beholderface.oneironaut.MiscAPIKt;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.block.HoverElevatorBlock;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.tag.TagKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;

public class HoverElevatorBlockEntity extends BlockEntity {

    private static final Map<LivingEntity, Integer> HOVER_MAP = new HashMap<>();
    private static final DirectionProperty FACING = HoverElevatorBlock.FACING;
    private static final int color = new FrozenColorizer(HexItems.DYE_COLORIZERS.get(DyeColor.PURPLE).getDefaultStack(), Util.NIL_UUID).getColor(0f, Vec3d.ZERO);

    private Box pairCuboid = null;
    private final Box defaultCuboid;

    public HoverElevatorBlockEntity(BlockPos pos, BlockState state) {
        super(OneironautBlockRegistry.HOVER_ELEVATOR_ENTITY.get(), pos, state);
        this.defaultCuboid = new Box(pos);
    }

    public void tick(World world, BlockPos pos, BlockState state){
        Random rand = world.random;
        Direction dir = state.get(FACING);
        Vec3i dirVec = dir.getVector();
        Vec3d dirVec3d = new Vec3d(dirVec.getX(), dirVec.getY(), dirVec.getZ());
        if (state.get(HoverElevatorBlock.POWERED)){
            if (this.pairCuboid == null || world.getTime() % 20 == 0){
                //search for paired elevator
                this.pairCuboid = findPair(world, pos, state);
                /*if (!this.pairCuboid.equals(this.defaultCuboid)){
                    Oneironaut.LOGGER.info("Found paired elevator");
                }*/
            }
            List<LivingEntity> detectedEntities = new ArrayList<>();
            for (Entity e : world.getOtherEntities(null, pairCuboid, (entity)-> {return true;})){
                if (e instanceof LivingEntity le){
                    detectedEntities.add(le);
                    /*if (world.getTime() % 20 == 0 && !world.isClient){
                        Oneironaut.LOGGER.info("Found living entity " + le.getDisplayName());
                    }*/
                }
            }
            int axialBit = switch (state.get(FACING).getAxis()){
                case X -> 1;
                case Y -> 2;
                case Z -> 4;
            };
            for (LivingEntity livingEntity : detectedEntities){
                if (livingEntity instanceof PlayerEntity player && player.isSpectator()){
                    continue;
                }
                int found = HOVER_MAP.getOrDefault(livingEntity, 0);
                HOVER_MAP.put(livingEntity, found | axialBit);
                Vec3d entityPos = livingEntity.getPos().add(livingEntity.getBoundingBox().getXLength() / 2, 0, livingEntity.getBoundingBox().getZLength() / 2);
                Vec3d entityVel = livingEntity.getVelocity();
                if (world instanceof ClientWorld clientWorld){
                    clientWorld.addParticle(new ConjureParticleOptions(livingEntity instanceof PlayerEntity player ?
                                    IXplatAbstractions.INSTANCE.getColorizer(player).getColor(world.getTime(), player.getPos()) : color, true),
                            entityPos.x + (((rand.nextGaussian() * 2) - 1) / 5), entityPos.y + (((rand.nextGaussian() * 2) - 1) / 5) + (rand.nextBetween(0, (int) (livingEntity.getHeight() * 20f)) / 20f),
                            entityPos.z + (((rand.nextGaussian() * 2) - 1) / 5), entityVel.x, entityVel.y + 0.1, entityVel.z);
                }
            }
            if (world instanceof ClientWorld clientWorld && !pairCuboid.equals(defaultCuboid)){
                Vec3d particleCenter = Vec3d.ofCenter(new Vec3i(pos.getX(), pos.getY(), pos.getZ())).add(dirVec3d.multiply(0.5));
                Vec3d dirVelVec = dirVec3d.multiply(0.25);
                clientWorld.addParticle(new ConjureParticleOptions(color, true),
                        particleCenter.x + (((rand.nextGaussian() * 2) - 1) / 5), particleCenter.y + (((rand.nextGaussian() * 2) - 1) / 5),
                        particleCenter.z + (((rand.nextGaussian() * 2) - 1) / 5), dirVelVec.x, dirVelVec.y, dirVelVec.z);
            }
        }
    }

    private Box findPair(World world, BlockPos pos, BlockState state){
        Direction dir = state.get(FACING);
        Vec3i dirVec = dir.getVector();
        BlockPos current;
        Box output = this.defaultCuboid;
        int i = 1;
        int range = dir.getAxis() == Direction.Axis.Y ? 128 : 64;
        for (; i <= range; i++){
            current = pos.add(dirVec.multiply(i));
            BlockState checkedState = world.getBlockState(current);
            TagKey<Block> breakImmune = MiscAPIKt.getBlockTagKey(Identifier.tryParse("oneironaut:hexbreakimmune"));
            TagKey<Block> rayImmune = MiscAPIKt.getBlockTagKey(Identifier.tryParse("oneironaut:blocksraycast"));
            if (checkedState.getBlock() == OneironautBlockRegistry.HOVER_ELEVATOR.get()){
                if (checkedState.get(HoverElevatorBlock.POWERED)){
                    if(checkedState.get(FACING).getOpposite().equals(dir)){
                        output = new Box(pos, current.add(1, 1, 1));
                        break;
                    } else if (checkedState.get(FACING).equals(dir)) {
                        //not sure if there would be any use case for >   << but it feels weird to let you do that
                        break;
                    }
                }
            } else if (checkedState.isIn(breakImmune) || checkedState.isIn(rayImmune)){
                break;
            }
        }
        return output;
    }

    private static double vecProximity(Vec3d a, Vec3d b){
        double output = a.subtract(b).length();
        return output;
    }
    private static double vecProximity(Direction a, Vec3d b){
        return vecProximity(Vec3d.of(a.getVector()), b);
    }

    public static void processHover(){
        double threshold = 0.75;
        for (LivingEntity entity : HOVER_MAP.keySet()){
            Vec3d hoverVec = Vec3d.ZERO;
            Vec3d look = entity.getRotationVector();
            int axesNum = HOVER_MAP.get(entity);
            int divisor = 20;
            boolean counterVerticalMomentum = true;
            if (!entity.isSneaking()){
                if ((axesNum & 1) == 1){
                    double eastScore = vecProximity(Direction.EAST, look);
                    double westScore = vecProximity(Direction.WEST, look);
                    if ((eastScore <= threshold || westScore <= threshold) && Math.abs(hoverVec.x) < Math.abs(look.x / divisor)){
                        hoverVec = hoverVec.add(look.x * (1.0 / divisor), 0.0, 0.0);
                    }
                }
                if ((axesNum & 2) == 2){
                    double upScore = vecProximity(Direction.UP, look);
                    double downScore = vecProximity(Direction.DOWN, look);
                    if ((upScore <= threshold || downScore <= threshold) && Math.abs(hoverVec.y) < Math.abs(look.y / divisor)){
                        hoverVec = hoverVec.add(0.0, look.y * (1.0 / divisor), 0.0);
                        counterVerticalMomentum = false;
                    }
                }
                if ((axesNum & 4) == 4){
                    double southScore = vecProximity(Direction.SOUTH, look);
                    double northScore = vecProximity(Direction.NORTH, look);
                    if ((southScore <= threshold || northScore <= threshold) && Math.abs(hoverVec.z) < Math.abs(look.z / divisor)){
                        hoverVec = hoverVec.add(0.0, 0.0, look.z * (1.0 / divisor));
                    }
                }
            }
            Vec3d velocity = entity.getVelocity();
            if (entity instanceof ServerPlayerEntity serverPlayerEntity){
                velocity = PlayerPositionRecorder.getMotion(serverPlayerEntity);
            }
            double antigravNum = 0.08;
            if (counterVerticalMomentum){
                if (velocity.y < -0.1){
                    hoverVec = hoverVec.add(0.0, 0.005, 0.0);
                } else if (velocity.y > 0.01){
                    //WHY U NO STOP
                    hoverVec = hoverVec.add(0.0, -0.1, 0.0);
                }
            }
            hoverVec = hoverVec.add(new Vec3d(0.0, antigravNum, 0.0));
            entity.addVelocity(hoverVec.x, hoverVec.y, hoverVec.z);
        }
        HOVER_MAP.clear();
    }
}
