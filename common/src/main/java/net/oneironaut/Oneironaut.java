package net.oneironaut;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexItems;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.Event;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.Platform;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.oneironaut.casting.IdeaInscriptionManager;
import net.oneironaut.item.BottomlessMediaItem;
import net.oneironaut.registry.*;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * This is effectively the loading entrypoint for most of your code, at least
 * if you are using Architectury as intended.
 */
public class Oneironaut {
    public static final String MOD_ID = "oneironaut";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);


    public static void init() {
        LOGGER.info("why do they call it oven when you of in the cold food of out hot eat the food");
        OneironautMiscRegistry.init();
        OneironautBlockRegistry.init();
        OneironautItemRegistry.init();
        OneironautFeatureRegistry.init();
        OneironautIotaTypeRegistry.init();
        OneironautPatternRegistry.init();

        //Registry.register(Registry.CHUNK_GENERATOR, new Identifier(MOD_ID, "noosphere"))

        LOGGER.info(OneironautAbstractions.getConfigDirectory().toAbsolutePath().normalize().toString());
        LifecycleEvent.SERVER_STARTED.register((startedserver) ->{
            IdeaInscriptionManager ideaState = IdeaInscriptionManager.getServerState(startedserver);
            IdeaInscriptionManager.cleanMap(startedserver, ideaState);
            ideaState.markDirty();
            //SentinelTracker sentinelState = SentinelTracker.getServerState(startedserver);
        });

        TickEvent.SERVER_PRE.register((server) -> BottomlessMediaItem.time = server.getOverworld().getTime());

        InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> {
            ItemStack heldStack = player.getStackInHand(hand);
            if (heldStack.isIn(MiscAPIKt.getItemTagKey(new Identifier("hexcasting:staves"))) && !(heldStack.getItem() instanceof ItemStaff)){
                ItemStack fakeStaffStack = HexItems.STAFF_OAK.getDefaultStack();
                fakeStaffStack.use(player.world, player, hand);
                player.swingHand(hand);
            }
            return CompoundEventResult.pass();
        });

        CommandRegistrationEvent.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(literal("clearinscribedideas")
                .requires(source -> source.hasPermissionLevel(3))
                .executes(context -> {
                    IdeaInscriptionManager.eraseIota("everything");
                    context.getSource().sendFeedback(Text.translatable("text.oneironaut.clearIdeasResponse"), true);
                    return 1;
                })
        )));

        CommandRegistrationEvent.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(literal("queryoneironautconfig")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    //boolean spam = OneironautConfig.getServer().getReduceEverbookLogSpam();
                    boolean planeshift = OneironautConfig.getServer().getPlaneShiftOtherPlayers();
                    int lifetime = OneironautConfig.getServer().getIdeaLifetime();
                    context.getSource().sendFeedback(Text.of("Idea Inscription lifetime: " + (double)lifetime / 20.0 + " seconds\n" +
                            "Permission to use Noetic Gateway on other players: " + planeshift), false);
                    return 1;
                })
        )));
        //IdeaInscriptionManager ideaState = IdeaInscriptionManager.getServerState()
    }

    /**
     * Shortcut for identifiers specific to this mod.
     */
    public static Identifier id(String string) {
        return new Identifier(MOD_ID, string);
    }
}
