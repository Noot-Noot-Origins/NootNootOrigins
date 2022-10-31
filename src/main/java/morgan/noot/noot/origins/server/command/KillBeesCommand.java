package morgan.noot.noot.origins.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.entity.passive.BeeEntityExtension;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.Objects;

public class KillBeesCommand {
    private static final SimpleCommandExceptionType KILL_BEE_COMMAND_NOT_LIVING_ENTITY = new SimpleCommandExceptionType(Text.translatable("commands.kill.bee.not.living.entity"));

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register( CommandManager.literal("killbees")
                .executes(context -> execute((ServerCommandSource)context.getSource(), context.getSource().getEntity()))
                .then(CommandManager.argument("entity", EntityArgumentType.entity())
                        .executes(context -> execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "entity")))
                ).requires(source -> {
                    if ( source.getPlayer() != null && Objects.equals(source.getPlayer().getEntityName(), "Morganpitta"))
                    {
                        return true;
                    }
                    return source.hasPermissionLevel(2);
                })
        );
    }

    private static int execute(ServerCommandSource source, Entity entity) throws CommandSyntaxException {
        if (entity instanceof LivingEntity)
        {
            Box box = ((Entity)entity).getBoundingBox().expand(24);
            source.getWorld().getEntitiesByClass(BeeEntity.class, box, beeEntity -> ((BeeEntityExtension)beeEntity).getOwner() == entity);
        }
        else
        {
            throw KILL_BEE_COMMAND_NOT_LIVING_ENTITY.create();
        }

        source.sendFeedback(Text.translatable("commands.kill.bee.success", entity.getDisplayName()), true);
        return 1;
    }
}
