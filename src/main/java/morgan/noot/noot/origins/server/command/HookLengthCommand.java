package morgan.noot.noot.origins.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.entity.projectile.HookEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class HookLengthCommand {
    private static final SimpleCommandExceptionType HOOK_LENGTH_COMMAND_NOT_LIVING_ENTITY = new SimpleCommandExceptionType(Text.translatable("commands.hook.length.not.living.entity"));

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register( CommandManager.literal("hooklength")
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

    private static int execute(ServerCommandSource source, Entity entity, float length) throws CommandSyntaxException {
        if (entity instanceof LivingEntity)
        {
            if ( ((LivingEntityExtension)entity).getHook() != null && (((LivingEntityExtension)entity).getHook().getHookLength() + length) > HookEntity.minHookLength )
            {
                ((LivingEntityExtension)entity).getHook().updateHookLength(((LivingEntityExtension)entity).getHook().getHookLength()+length);
            }
        }
        else
        {
            throw HOOK_LENGTH_COMMAND_NOT_LIVING_ENTITY.create();
        }

        source.sendFeedback(Text.translatable("commands.hook.length.success", entity.getDisplayName()), true);
        return 1;
    }
}
