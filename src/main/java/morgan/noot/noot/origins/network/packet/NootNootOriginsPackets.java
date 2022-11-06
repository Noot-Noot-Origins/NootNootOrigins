package morgan.noot.noot.origins.network.packet;

import morgan.noot.noot.origins.entity.EntityExtension;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.entity.player.PlayerEntityExtension;
import morgan.noot.noot.origins.entity.projectile.HookEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class NootNootOriginsPackets {
    public static final Identifier DOUBLE_JUMP_PACKET_ID = new Identifier("morgan", "double_jump");
    public static final Identifier HOOK_ID_PACKET_ID = new Identifier("morgan", "hook_id");
    public static final Identifier ON_INFECTED_ENTITY_PACKET_ID = new Identifier("morgan", "on_infected_entity");
    public static final Identifier ON_UNINFECTED_ENTITY_PACKET_ID = new Identifier("morgan", "on_uninfected_entity");

    public static void registerC2SPackets()
    {

    }

    public static void registerS2CPackets() {
        //Double Jump Packets
        ClientPlayNetworking.registerGlobalReceiver(NootNootOriginsPackets.DOUBLE_JUMP_PACKET_ID, (client, handler, buf, responseSender) -> {
            int d = buf.readInt();
            client.execute(() -> {
                // Everything in this lambda is run on the render thread
                ((LivingEntityExtension)client.player).setDoubleJumps(d);
            });
        });

        //Hook ID Packets
        ClientPlayNetworking.registerGlobalReceiver(NootNootOriginsPackets.HOOK_ID_PACKET_ID, (client, handler, buf, responseSender) -> {
            int d = buf.readInt();
            client.execute(() -> {
                // Everything in this lambda is run on the render thread
                ((LivingEntityExtension)client.player).setHook((HookEntity) client.world.getEntityById(d));
            });
        });

        //On Infected Entity Packets
        ClientPlayNetworking.registerGlobalReceiver(NootNootOriginsPackets.ON_INFECTED_ENTITY_PACKET_ID, (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            Entity entity = client.world.getEntityById(entityId);
            client.execute(() -> {
                // Everything in this lambda is run on the render thread
                ((PlayerEntityExtension)client.player).infect((LivingEntity) entity);
            });
        });

        //On UnInfected Entity Packets
        ClientPlayNetworking.registerGlobalReceiver(NootNootOriginsPackets.ON_UNINFECTED_ENTITY_PACKET_ID, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                // Everything in this lambda is run on the render thread
                ((PlayerEntityExtension)client.player).unInfect();
            });
        });
    }
}
