package morgan.noot.noot.origins;

import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.network.packet.NootNootOriginsPacketsInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.RenderLayer;

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
    }
}
