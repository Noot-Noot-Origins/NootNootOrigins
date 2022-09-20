package morgan.noot.noot.origins.mixin.entity.passive;

import morgan.noot.noot.origins.origins.powers.NootNootOriginsPowers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityChangeTradePriceMixin
        extends MerchantEntity
        implements InteractionObserver,
        VillagerDataContainer {

    public VillagerEntityChangeTradePriceMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "prepareOffersFor",at = @At("HEAD"))
    public void prepareOffersFor(PlayerEntity player, CallbackInfo ci) {
        int i = NootNootOriginsPowers.getTradeChangePrice(player);
        if (i != 0) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.increaseSpecialPrice(MathHelper.floor((float) i * tradeOffer.getPriceMultiplier()));
            }
        }
    }
}
