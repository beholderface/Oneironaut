package net.beholderface.oneironaut;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexItems;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import net.beholderface.oneironaut.block.blockentity.HoverElevatorBlockEntity;
import net.beholderface.oneironaut.casting.IdeaInscriptionManager;
import net.beholderface.oneironaut.item.BottomlessMediaItem;
import net.beholderface.oneironaut.recipe.OneironautRecipeSerializer;
import net.beholderface.oneironaut.recipe.OneironautRecipeTypes;
import net.beholderface.oneironaut.registry.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ConcurrentModificationException;

import static net.beholderface.oneironaut.MiscAPIKt.getItemTagKey;
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
        //Registry.register(Registry.RECIPE_SERIALIZER, OneironautRecipeSerializer.)
        OneironautRecipeSerializer.registerSerializers(OneironautRecipeTypes.Companion.bind(Registry.RECIPE_SERIALIZER));
        OneironautRecipeTypes.registerTypes(OneironautRecipeTypes.Companion.bind(Registry.RECIPE_TYPE));

        //Registry.register(Registry.CHUNK_GENERATOR, new Identifier(MOD_ID, "noosphere"))

        //LOGGER.info(OneironautAbstractions.getConfigDirectory().toAbsolutePath().normalize().toString());
        LifecycleEvent.SERVER_STARTED.register((startedserver) ->{
            IdeaInscriptionManager ideaState = IdeaInscriptionManager.getServerState(startedserver);
            IdeaInscriptionManager.cleanMap(startedserver, ideaState);
            ideaState.markDirty();
            //SentinelTracker sentinelState = SentinelTracker.getServerState(startedserver);
        });

        TickEvent.SERVER_PRE.register((server) -> {
            BottomlessMediaItem.time = server.getOverworld().getTime();
        });

        LOGGER.info("Registering server-side hoverlift processor.");
        TickEvent.SERVER_POST.register((server)->{
            try {
                HoverElevatorBlockEntity.processHover(true, server.getOverworld().getTime());
            } catch (ConcurrentModificationException exception){
                LOGGER.error("Oopsie server-side hoverlift exception " + exception.getMessage());
            }
        });

        ItemStack fakeStaffStack = HexItems.STAFF_OAK.getDefaultStack();
        TagKey<Item> realStaffTag = getItemTagKey(new Identifier("hexcasting:staves"));
        TagKey<Item> fakeStaffTag = getItemTagKey(new Identifier("oneironaut:datapack_staves"));
        InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> {
            ItemStack heldStack = player.getStackInHand(hand);
            if (heldStack.isIn(fakeStaffTag) && !(heldStack.getItem() instanceof ItemStaff)){
                if (heldStack.isIn(realStaffTag)){
                    fakeStaffStack.use(player.world, player, hand);
                    player.swingHand(hand);
                } else {
                    LOGGER.info(player.getName().getString() + " has right-clicked an item tagged as a datapacked staff, but that item does not have the normal staff tag, which is necessary for the datapack staff functionality to work.");
                }
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
                    boolean planeshift = OneironautConfig.getServer().getPlaneShiftOtherPlayers();
                    int lifetime = OneironautConfig.getServer().getIdeaLifetime();
                    context.getSource().sendFeedback(Text.of("Idea Inscription lifetime: " + (double)lifetime / 20.0 + " seconds\n" +
                            "Permission to use Noetic Gateway on other players: " + planeshift), false);
                    return 1;
                })
        )));
        //IdeaInscriptionManager ideaState = IdeaInscriptionManager.getServerState()
    }

    //for easily toggling whether several things should be logged without having to search through the whole file
    public static void boolLogger(String str, boolean bool){
        if (bool){
            LOGGER.info(str);
        }
    }

    /**
     * Shortcut for identifiers specific to this mod.
     */
    public static Identifier id(String string) {
        return new Identifier(MOD_ID, string);
    }
}
