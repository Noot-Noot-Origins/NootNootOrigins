package morgan.noot.noot.origins.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.NootNootOriginsComponents;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class StarCommand {
    private static final SimpleCommandExceptionType ENTITY_IS_NOT_PLAYER = new SimpleCommandExceptionType(Text.translatable("commands.spawn.not.player"));
    private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.teleport.invalidPosition"));


    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register( CommandManager.literal("star")
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
        ServerWorld world;
        if (player!=null)
        {
            ServerPlayerEntity serverPlayer = player.getServer().getPlayerManager().getPlayer(player.getEntityName());
            NootNootOrigins.LOGGER.info(player.world.getRegistryKey().getValue().getPath());
            if (Objects.equals(player.world.getRegistryKey().getValue().toString(), "nootnoot:star"))
            {
                if (!((LivingEntityExtension)player).leaveStarDimension())
                {
                    throw INVALID_POSITION_EXCEPTION.create();
                }
            }
            else
            {
                if (!((LivingEntityExtension)player).enterStarDimension())
                {
                    throw INVALID_POSITION_EXCEPTION.create();
                }
            }
        }
        else
        {
            throw ENTITY_IS_NOT_PLAYER.create();
        }

        if (source.hasPermissionLevel(2)) source.sendFeedback(Text.translatable("commands.star.success", player.getDisplayName(),player.getWorld().getRegistryKey().getValue().getPath()), true);
        return 1;
    }
}
