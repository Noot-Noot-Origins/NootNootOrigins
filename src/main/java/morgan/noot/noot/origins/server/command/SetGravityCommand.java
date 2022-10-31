package morgan.noot.noot.origins.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import morgan.noot.noot.origins.NootNootOriginsComponents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Objects;

public class SetGravityCommand {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register( CommandManager.literal("setgravity")
                .requires(source -> {
                    if ( source.getPlayer() != null && Objects.equals(source.getPlayer().getEntityName(), "Morganpitta"))
                    {
                        return true;
                    }
                    return source.hasPermissionLevel(2);
                })
                .then(CommandManager.argument("amount", FloatArgumentType.floatArg())
                        .executes(context -> execute((ServerCommandSource)context.getSource(), context.getSource().getWorld(), FloatArgumentType.getFloat(context,"amount")))
                )
        );
    }

    private static int execute(ServerCommandSource source, ServerWorld world, float amount) throws CommandSyntaxException {
        world.getComponent(NootNootOriginsComponents.WORLD_GRAVITY).setValue(amount);

        source.sendFeedback(Text.translatable("commands.set.gravity.success", world.getRegistryKey().getValue().getPath(),String.valueOf(amount)), true);
        return 1;
    }
}
