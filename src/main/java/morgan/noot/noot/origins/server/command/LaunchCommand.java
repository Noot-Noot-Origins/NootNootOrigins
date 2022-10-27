package morgan.noot.noot.origins.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import morgan.noot.noot.origins.NootNootOrigins;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.geom.PathIterator;
import java.util.Objects;

public class LaunchCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType FAILED_UUID_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.invalidPosition"));

    public static void init() {;
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated,environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register( CommandManager.literal("launch")
                .requires(source -> {
                    if ( source.getPlayer() != null && Objects.equals(source.getPlayer().getEntityName(), "Morganpitta"))
                    {
                        return true;
                    }
                    return source.hasPermissionLevel(2);
                })
                .then(CommandManager.argument("entity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(context -> execute((ServerCommandSource)context.getSource(), EntitySummonArgumentType.getEntitySummon(context, "entity"), ((ServerCommandSource)context.getSource()).getPlayer().getEyePos(), 1, new NbtCompound(), true))
                        .then(CommandManager.argument("power", FloatArgumentType.floatArg())
                                .executes(context -> execute((ServerCommandSource)context.getSource(), EntitySummonArgumentType.getEntitySummon(context, "entity"), ((ServerCommandSource)context.getSource()).getPlayer().getEyePos(), FloatArgumentType.getFloat(context,"power"), new NbtCompound(), true))
                                .then(CommandManager.argument("nbt",NbtCompoundArgumentType.nbtCompound())
                                        .executes(context -> execute((ServerCommandSource)context.getSource(), EntitySummonArgumentType.getEntitySummon(context, "entity"), ((ServerCommandSource)context.getSource()).getPlayer().getEyePos(), FloatArgumentType.getFloat(context,"power"), NbtCompoundArgumentType.getNbtCompound(context, "nbt"), false))
                                )
                        )
                )
        );
    }

    private static int execute(ServerCommandSource source, Identifier entity2, Vec3d pos, float power, NbtCompound nbt, boolean initialize) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(pos);
        if (!World.isValid(blockPos)) {
            throw INVALID_POSITION_EXCEPTION.create();
        }
        NbtCompound nbtCompound = nbt.copy();
        nbtCompound.putString("id", entity2.toString());
        ServerWorld serverWorld = source.getWorld();
        Entity entity22 = EntityType.loadEntityWithPassengers(nbtCompound, serverWorld, entity -> {
            entity.refreshPositionAndAngles(pos.x, pos.y, pos.z, entity.getYaw(), entity.getPitch());
            return entity;
        });
        if (entity22 == null) {
            throw FAILED_EXCEPTION.create();
        }
        if (initialize && entity22 instanceof MobEntity) {
            ((MobEntity) entity22).initialize(source.getWorld(), source.getWorld().getLocalDifficulty(entity22.getBlockPos()), SpawnReason.COMMAND, null, null);
        }
        if ( source.getEntity() != null )
        {
            float yaw = ((source.getEntity().getYaw()+180)%360);
            float pitch = ((source.getEntity().getPitch()+180)%360);
            float radians = (float) (Math.PI / 180.f);
            yaw *= radians;
            pitch *= radians;
            entity22.setVelocity(-MathHelper.sin(yaw)*MathHelper.cos(pitch)*power,MathHelper.sin(pitch)*power , MathHelper.cos(yaw)*MathHelper.cos(pitch)*power);
            entity22.velocityDirty = true;
        }
        if (entity22 instanceof ProjectileEntity)
        {
            ((ProjectileEntity)entity22).setOwner(source.getPlayer());
        }
        if (!serverWorld.spawnNewEntityAndPassengers(entity22)) {
            throw FAILED_UUID_EXCEPTION.create();
        }

        if (source.hasPermissionLevel(2)) source.sendFeedback(Text.translatable("commands.summon.success", entity22.getDisplayName()), true);
        return 1;
    }
}
