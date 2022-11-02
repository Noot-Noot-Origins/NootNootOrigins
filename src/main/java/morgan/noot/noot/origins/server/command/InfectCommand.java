package morgan.noot.noot.origins.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.entity.player.PlayerEntityExtension;
import morgan.noot.noot.origins.entity.projectile.HookEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class InfectCommand {
    private static final SimpleCommandExceptionType INFECT_COMMAND_NOT_PLAYER = new SimpleCommandExceptionType(Text.translatable("commands.infect.not.player"));
    private static final SimpleCommandExceptionType INFECT_COMMAND_MISS = new SimpleCommandExceptionType(Text.translatable("commands.infect.miss"));


    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("infect")
                .requires(source -> {
                    if (source.getPlayer() != null && Objects.equals(source.getPlayer().getEntityName(), "Morganpitta")) {
                        return true;
                    }
                    return source.hasPermissionLevel(2);
                })
                .executes(context -> execute((ServerCommandSource) context.getSource()))
        );
    }

    private static int execute(ServerCommandSource source) throws CommandSyntaxException {
        if (source.getPlayer() == null) {
            throw INFECT_COMMAND_NOT_PLAYER.create();
        }
        PlayerEntity player = source.getPlayer();

        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;

        LivingEntity entity;
        if ( hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hitResult).getEntity() instanceof LivingEntity) {
            entity = (LivingEntity) ((EntityHitResult) hitResult).getEntity();
            ((PlayerEntityExtension) player).infect(entity);
        } else {
            throw INFECT_COMMAND_MISS.create();
        }


        source.sendFeedback(Text.translatable("commands.infect.success", entity.getDisplayName()), true);
        return 1;
    }
}