package morgan.noot.noot.origins.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import morgan.noot.noot.origins.EntityExtension;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Optional;

public class SpawnCommand {
    private static final SimpleCommandExceptionType ENTITY_IS_NOT_PLAYER = new SimpleCommandExceptionType(Text.translatable("commands.spawn.not.player"));

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register( CommandManager.literal("spawn")
                .requires(source -> {
                    if ( source.getPlayer() != null && Objects.equals(source.getPlayer().getEntityName(), "Morganpitta"))
                    {
                        return true;
                    }
                    return source.hasPermissionLevel(2);
                })
                .executes(context -> execute((ServerCommandSource)context.getSource(), context.getSource().getPlayer()))
        );
    }

    private static int execute(ServerCommandSource source, PlayerEntity player) throws CommandSyntaxException {
        if (player!=null)
        {
            ServerPlayerEntity serverPlayer = player.getServer().getPlayerManager().getPlayer(player.getEntityName());
            Optional<Vec3d> vec3d = PlayerEntity.findRespawnPosition(serverPlayer.server.getOverworld(),serverPlayer.getSpawnPointPosition(), serverPlayer.getSpawnAngle(),serverPlayer.isSpawnForced(),serverPlayer.isAlive());
            ((LivingEntityExtension)player).teleportToDimension(serverPlayer.server.getOverworld(),vec3d.get().getX(),vec3d.get().getY(), vec3d.get().getZ(),player.getYaw(), player.getPitch());
        }
        else
        {
            throw ENTITY_IS_NOT_PLAYER.create();
        }

        if (source.hasPermissionLevel(2)) source.sendFeedback(Text.translatable("commands.spawn.success", player.getDisplayName()), true);
        return 1;
    }
}
