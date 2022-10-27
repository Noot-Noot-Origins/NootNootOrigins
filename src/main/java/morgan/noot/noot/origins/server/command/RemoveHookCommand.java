package morgan.noot.noot.origins.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Objects;

public class RemoveHookCommand {
    private static final SimpleCommandExceptionType REMOVE_HOOK_COMMAND_NOT_LIVING_ENTITY = new SimpleCommandExceptionType(Text.translatable("commands.remove.hook.not.living.entity"));

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register( CommandManager.literal("removehook")
                .requires(source -> {
                    if ( source.getPlayer() != null && Objects.equals(source.getPlayer().getEntityName(), "Morganpitta"))
                    {
                        return true;
                    }
                    return source.hasPermissionLevel(2);
                })
                .executes(context -> execute((ServerCommandSource)context.getSource(), context.getSource().getEntity()))
                .then(CommandManager.argument("entity", EntityArgumentType.entity())
                        .executes(context -> execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "entity")))
                )
        );
    }

    private static int execute(ServerCommandSource source, Entity entity) throws CommandSyntaxException {
        if (entity instanceof LivingEntity)
        {
            ((LivingEntityExtension)entity).setHook(null);
        }
        else
        {
            throw REMOVE_HOOK_COMMAND_NOT_LIVING_ENTITY.create();
        }

        if (source.hasPermissionLevel(2)) source.sendFeedback(Text.translatable("commands.remove.hook.success", entity.getDisplayName()), true);
        return 1;
    }
}
