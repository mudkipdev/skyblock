package net.skyblock;

import net.skyblock.command.commands.ItemCommand;
import net.skyblock.command.commands.TestCommand;
import net.skyblock.item.SkyblockItemProcessor;
import net.skyblock.item.component.ComponentContainer;
import net.skyblock.item.component.components.ArtOfPeaceComponent;
import net.skyblock.item.component.components.HotPotatoBookComponent;
import net.skyblock.item.component.components.RarityComponent;
import net.skyblock.listener.*;
import skyblock.listeners.*;
import net.skyblock.player.SkyblockPlayer;
import net.skyblock.registry.ItemRegistry;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.anvil.AnvilLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for the Skyblock server implementation.
 * This class handles server initialization, registry loading, and world setup.
 * It serves as the entry point for the entire Skyblock server application.
 */
public class Skyblock {
    private static Skyblock instance;
    private static Logger logger;
    private final MinecraftServer server;
    private final SkyblockItemProcessor processor;
    private final InstanceContainer hubInstance;

    // Registries
    private final ItemRegistry itemRegistry;

    /**
     * Main entry point for the Skyblock server.
     *
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        Skyblock skyblock = new Skyblock();
        logger = LoggerFactory.getLogger(Skyblock.class);
        skyblock.start("0.0.0.0", 25565);
    }

    /**
     * Constructs a new Skyblock server instance.
     * Initializes the server, registers event listeners, loads registries,
     * creates the hub world.
     */
    public Skyblock() {
        instance = this;
        this.server = MinecraftServer.init();

        MojangAuth.init();
        MinecraftServer.getConnectionManager().setPlayerProvider(SkyblockPlayer::new);

        // Set up world instances
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        this.hubInstance = loadHub(instanceManager);

        // Register event listeners before registries to ensure proper registration order
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        registerListeners(eventHandler);

        // Initialize item and component registries
        this.itemRegistry = new ItemRegistry();
        initRegistries();
        this.processor = initProcessor();

        // Register commands
        CommandManager cmdManager = MinecraftServer.getCommandManager();
        registerCommands(cmdManager);
    }

    /**
     * Starts the server on the specified address and port.
     *
     * @param address The address to bind to
     * @param port The port to listen on
     */
    public void start(String address, int port) {
        server.start(address, port);
    }

    /**
     * Registers all event listeners required for the Skyblock server.
     *
     * @param eventHandler The global event handler
     */
    private void registerListeners(GlobalEventHandler eventHandler) {
        eventHandler.addListener(new AsyncPlayerConfigurationListener(hubInstance, new Pos(-2, 71, -68).withYaw(-180F)));
        eventHandler.addListener(new PlayerSwapItemListener());
        eventHandler.addListener(new InventoryPreClickListener());
        eventHandler.addListener(new PlayerChangeHeldSlotListener());
        eventHandler.addListener(new PlayerUseItemListener());
        ComponentContainer.addListener(new StatModifierSyncListener());
    }

    /**
     * Initializes all registries needed for the Skyblock server.
     * This includes the item registry and component registry which handle
     * custom items and their associated components/behaviors.
     */
    private void initRegistries() {
        itemRegistry.init();
    }

    /**
     * Registers all default components to processor
     *
     * @return The initialized SkyblockItemProcessor
     */
    private SkyblockItemProcessor initProcessor() {
        SkyblockItemProcessor processor = new SkyblockItemProcessor();
        processor.registerDeserializer(HotPotatoBookComponent::read);
        processor.registerDeserializer(RarityComponent::read);
        processor.registerDeserializer(ArtOfPeaceComponent::read);
        return processor;
    }

    /**
     * Creates and loads the hub world instance that players will initially spawn in.
     * Uses an AnvilLoader to load chunk data from the "hub" directory.
     *
     * @param instanceManager The Minestom instance manager used to create world instances
     * @return The created hub instance container
     */
    private InstanceContainer loadHub(InstanceManager instanceManager) {
        InstanceContainer hubInstance = instanceManager.createInstanceContainer();
        hubInstance.setChunkLoader(new AnvilLoader("hub"));
        return hubInstance;
    }

    /**
     * Registers all the commands
     *
     * @param cmdManager The Minestom command manager used to register commands.
     */
    private void registerCommands(CommandManager cmdManager) {
        cmdManager.register(new ItemCommand());
        cmdManager.register(new TestCommand());
    }

    /**
     * Returns the server's logger for Skyblock-related logging.
     *
     * @return The SLF4J logger for this class
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Returns the item processor with default components registered
     *
     * @return The Skyblock Item Processor
     */
    public SkyblockItemProcessor getProcessor() {
        return processor;
    }

    /**
     * Returns the item registry with default items registered
     *
     * @return The Item Registry
     */
    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    /**
     * Returns the skyblock instance
     *
     * @return The skyblock instance
     */
    public static Skyblock getInstance() {
        return instance;
    }
}