package morgan.noot.noot.origins;

import morgan.noot.noot.origins.entity.NootNootOriginsEntityType;
import morgan.noot.noot.origins.network.packet.NootNootOriginsPackets;
import morgan.noot.noot.origins.origins.powers.NootNootOriginsPowers;
import morgan.noot.noot.origins.server.command.NootNootOriginsCommands;
import morgan.noot.noot.origins.tags.NootNootOriginsTags;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NootNootOrigins implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("NootNootOrigins");

	public static final String MODID = "nootnoot";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		NootNootOriginsTags.init();
		NootNootOriginsPowers.init();
		NootNootOriginsEntityType.init();
		NootNootOriginsCommands.init();
		NootNootOriginsPackets.registerC2SPackets();

		LOGGER.info("Noot Noot Origins has been initialized!");
	}
}
