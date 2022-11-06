package morgan.noot.noot.origins.entity;

public interface EntityExtension {
    float getExtraMountedHeightOffset();

    void setExtraMountedHeightOffset(float extraMountedHeightOffset);

    void setEntityGravity(float gravity);

    float getEntityGravity();

    float getFullGravity();
}
