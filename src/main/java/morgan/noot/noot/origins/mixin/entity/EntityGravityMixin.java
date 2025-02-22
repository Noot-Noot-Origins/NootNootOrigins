package morgan.noot.noot.origins.mixin.entity;

import dev.onyxstudios.cca.api.v3.component.ComponentAccess;
import morgan.noot.noot.origins.entity.EntityExtension;
import morgan.noot.noot.origins.NootNootOriginsComponents;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityGravityMixin implements Nameable,
        EntityLike,
        CommandOutput,
        ComponentAccess,
        EntityExtension {
    @Shadow public World world;

    public void setEntityGravity(float gravity){
        this.getComponent(NootNootOriginsComponents.ENTITY_GRAVITY).setValue(gravity);
    }

    public float getEntityGravity(){
        return this.getComponent(NootNootOriginsComponents.ENTITY_GRAVITY).getValue()*0.08f;
    }

    public float getFullGravity(){
        return this.getEntityGravity()*this.world.getComponent(NootNootOriginsComponents.WORLD_GRAVITY).getValue();
    }
}
