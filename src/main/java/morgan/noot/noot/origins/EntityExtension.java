package morgan.noot.noot.origins;

import morgan.noot.noot.origins.network.packet.NootNootOriginsPacketsInit;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public interface EntityExtension {
    float getExtraMountedHeightOffset();

    void setExtraMountedHeightOffset(float extraMountedHeightOffset);

    void setEntityGravity(float gravity);

    float getEntityGravity();

    float getFullGravity();
}
