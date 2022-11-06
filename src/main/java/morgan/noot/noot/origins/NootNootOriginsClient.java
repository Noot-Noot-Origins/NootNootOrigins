package morgan.noot.noot.origins;

import morgan.noot.noot.origins.client.render.entity.HookEntityRenderer;
import morgan.noot.noot.origins.entity.EntityExtension;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.entity.NootNootOriginsEntityType;
import morgan.noot.noot.origins.entity.projectile.HookEntity;
import morgan.noot.noot.origins.network.packet.NootNootOriginsPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class NootNootOriginsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NootNootOriginsPackets.registerS2CPackets();

        //Register HookEntity render, temporary please remove morgan when updating
        EntityRendererRegistry.register(NootNootOriginsEntityType.HookEntityType, (context) ->
                new HookEntityRenderer(context));
    }
}
