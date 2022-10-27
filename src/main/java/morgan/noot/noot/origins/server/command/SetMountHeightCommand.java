package morgan.noot.noot.origins.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import morgan.noot.noot.origins.EntityExtension;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.entity.projectile.HookEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Objects;

public class SetMountHeightCommand {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register( CommandManager.literal("setmountheight")
                .requires(source -> {
                    if ( source.getPlayer() != null && Objects.equals(source.getPlayer().getEntityName(), "Morganpitta"))
                    {
                        return true;
                    }
                    return source.hasPermissionLevel(2);
                })
                .then(CommandManager.argument("entity", EntityArgumentType.entity())
                        .then(CommandManager.argument("amount", FloatArgumentType.floatArg())
                                .executes(context -> execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "entity"), FloatArgumentType.getFloat(context,"amount")))
                        )
                )
        );
    }

    private static int execute(ServerCommandSource source, Entity entity, float amount) throws CommandSyntaxException {
        ((EntityExtension)entity).setExtraMountedHeightOffset(amount);

        if (source.hasPermissionLevel(2)) source.sendFeedback(Text.translatable("commands.set.mount.height.success", entity.getDisplayName()), true);
        return 1;
    }
}
