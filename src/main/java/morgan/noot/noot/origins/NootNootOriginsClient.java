package morgan.noot.noot.origins;

import morgan.noot.noot.origins.client.render.entity.HookEntityRenderer;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.entity.NootNootOriginsEntityType;
import morgan.noot.noot.origins.entity.projectile.HookEntity;
import morgan.noot.noot.origins.network.packet.NootNootOriginsPacketsInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.entity.projectile.FishingBobberEntity;

@Environment(EnvType.CLIENT)
public class NootNootOriginsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //Double Jump Packets
        ClientPlayNetworking.registerGlobalReceiver(NootNootOriginsPacketsInit.DOUBLE_JUMP_PACKET_ID, (client, handler, buf, responseSender) -> {
            int d = buf.readInt();
            client.execute(() -> {
                // Everything in this lambda is run on the render thread
                ((LivingEntityExtension)client.player).setDoubleJumps(d);
            });
        });

        //Hook ID Packets
        ClientPlayNetworking.registerGlobalReceiver(NootNootOriginsPacketsInit.HOOK_ID_PACKET_ID, (client, handler, buf, responseSender) -> {
            int d = buf.readInt();
            client.execute(() -> {
                // Everything in this lambda is run on the render thread
                ((LivingEntityExtension)client.player).setHook((HookEntity) client.world.getEntityById(d));
            });
        });

        //Register HookEntity render, temporary please remove morgan when updating
        EntityRendererRegistry.register(NootNootOriginsEntityType.HookEntityType, (context) ->
                new HookEntityRenderer(context));
    }
}
