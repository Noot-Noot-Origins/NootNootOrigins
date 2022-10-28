package morgan.noot.noot.origins.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.NootNootOriginsComponents;
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

    private static void teleport(ServerCommandSource source, Entity target, ServerWorld world, double x, double y, double z,  float yaw, float pitch) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(x, y, z);
        if (!World.isValid(blockPos)) {
            throw INVALID_POSITION_EXCEPTION.create();
        }
        float f = MathHelper.wrapDegrees(yaw);
        float g = MathHelper.wrapDegrees(pitch);
        if (target instanceof ServerPlayerEntity) {
            ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
            world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, target.getId());
            target.stopRiding();
            if (((ServerPlayerEntity)target).isSleeping()) {
                ((ServerPlayerEntity)target).wakeUp(true, true);
            }
            if (world == target.world) {
                ((ServerPlayerEntity)target).networkHandler.requestTeleport(x, y, z, f, g);
            } else {
                ((ServerPlayerEntity)target).teleport(world, x, y, z, f, g);
            }
            target.setHeadYaw(f);
        } else {
            float h = MathHelper.clamp(g, -90.0f, 90.0f);
            if (world == target.world) {
                target.refreshPositionAndAngles(x, y, z, f, h);
                target.setHeadYaw(f);
            } else {
                target.detach();
                Entity entity = target;
                target = entity.getType().create(world);
                if (target != null) {
                    target.copyFrom(entity);
                    target.refreshPositionAndAngles(x, y, z, f, h);
                    target.setHeadYaw(f);
                    entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                    world.onDimensionChanged(target);
                } else {
                    return;
                }
            }
        }
        if (!(target instanceof LivingEntity) || !((LivingEntity)target).isFallFlying()) {
            target.setVelocity(target.getVelocity().multiply(1.0, 0.0, 1.0));
            target.setOnGround(true);
        }

        if (target instanceof PathAwareEntity) {
            ((PathAwareEntity)target).getNavigation().stop();
        }
    }

    private static int execute(ServerCommandSource source, PlayerEntity player) throws CommandSyntaxException {
        ServerWorld world;
        if (player!=null)
        {
            ServerPlayerEntity serverPlayer = player.getServer().getPlayerManager().getPlayer(player.getEntityName());
            NootNootOrigins.LOGGER.info(player.world.getRegistryKey().getValue().getPath());
            if (Objects.equals(player.world.getRegistryKey().getValue().toString(), "nootnoot:star"))
            {
                Vec3d position = player.getComponent(NootNootOriginsComponents.STAR_POSITION).getValue();
                world = player.getServer().getWorld(player.getComponent(NootNootOriginsComponents.STAR_WORLD_KEY).getValue());
                teleport(source, player, world, position.x, position.y, position.z, player.getYaw(), player.getPitch() );
            }
            else
            {
                player.getComponent(NootNootOriginsComponents.STAR_POSITION).setValue(player.getPos());
                player.getComponent(NootNootOriginsComponents.STAR_WORLD_KEY).setValue(player.world.getRegistryKey());
                world = player.getServer().getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier("nootnoot","star")));

                int i = 0;
                int j = 62;
                int k = 0;
                BlockPos.iterate(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach(pos -> world.setBlockState((BlockPos)pos, Blocks.AIR.getDefaultState()));
                BlockPos.iterate(i - 2, j, k - 2, i + 2, j, k + 2).forEach(pos -> world.setBlockState((BlockPos)pos, Blocks.OBSIDIAN.getDefaultState()));

                teleport(source, player, world, 0.5,64,0.5, player.getYaw(), player.getPitch() );
            }
        }
        else
        {
            throw ENTITY_IS_NOT_PLAYER.create();
        }

        if (source.hasPermissionLevel(2)) source.sendFeedback(Text.translatable("commands.star.success", player.getDisplayName(),world.getRegistryKey().getValue().getPath()), true);
        return 1;
    }
}
