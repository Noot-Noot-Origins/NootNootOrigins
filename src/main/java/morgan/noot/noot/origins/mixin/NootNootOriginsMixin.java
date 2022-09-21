package morgan.noot.noot.origins.mixin;

import morgan.noot.noot.origins.NootNootOrigins;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class NootNootOriginsMixin {
	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
		NootNootOrigins.LOGGER.info("Noot Noot Origins Mixins has been initialized!");
	}
}
