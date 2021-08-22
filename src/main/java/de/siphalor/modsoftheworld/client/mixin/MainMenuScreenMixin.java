package de.siphalor.modsoftheworld.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import de.siphalor.modsoftheworld.client.Logo;
import de.siphalor.modsoftheworld.client.MOTWClient;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("WeakerAccess")
@Mixin(TitleScreen.class)
public abstract class MainMenuScreenMixin extends Screen {

	@Shadow
	private String splashText;
	private int modsOfTheWorld_currentLogo = 0;
	private float modsOfTheWorld_logoTime = 0;

	protected MainMenuScreenMixin(Text title) {
		super(title);
	}


	@Inject(method = "render", at = @At("HEAD"))
	public void onRender(MatrixStack matrices, int x, int y, float delta, CallbackInfo callbackInfo) {
		modsOfTheWorld_logoTime += delta / 4;
	}

	@SuppressWarnings("ConstantConditions")
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIFFIIII)V", ordinal = 0))
	public void editionBlitProxy(MatrixStack matrices, int x, int y, float texX, float texY, int width, int height, int texWidth, int texHeight) {
		drawTexture(matrices, x + 39, y, texX + 39, texY, width - 39, height, texWidth, texHeight);

		if(modsOfTheWorld_logoTime > MOTWClient.WHOLE_TIME) {
			modsOfTheWorld_currentLogo = modsOfTheWorld_currentLogo >= MOTWClient.getLogos().size() - 1 ? 0 : modsOfTheWorld_currentLogo + 1;
			GLFW.glfwSetWindowTitle(
					client.getWindow().getHandle(), "Minecraft " + SharedConstants.getGameVersion().getName()
							+ " - " + MOTWClient.getLogos().get(modsOfTheWorld_currentLogo).getModName() + " Edition"
			);
			splashText = MOTWClient.getLogos().get(modsOfTheWorld_currentLogo).getSplashProvider().get();
		}
		modsOfTheWorld_logoTime %= MOTWClient.WHOLE_TIME;
		float[] color = RenderSystem.getShaderColor();
		if(color[3] == 1.0F) {
			float alpha = 1.0F;
			if (modsOfTheWorld_logoTime < MOTWClient.FADE_TIME) alpha = modsOfTheWorld_logoTime / MOTWClient.FADE_TIME;
			if (modsOfTheWorld_logoTime >= MOTWClient.WHOLE_TIME - MOTWClient.FADE_TIME)
				alpha = 1.0F - (modsOfTheWorld_logoTime - MOTWClient.WHOLE_TIME + MOTWClient.FADE_TIME) / MOTWClient.FADE_TIME;
			RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
		}

		matrices.push();

		Logo logo = MOTWClient.getLogos().get(modsOfTheWorld_currentLogo);
		RenderSystem.setShaderTexture(0, logo.getIdentifier());

		float scaleFactor = (float) height / (float) logo.getHeight();
		matrices.translate(x + 39F - logo.getWidth() * scaleFactor, y, 0F);
		matrices.scale(scaleFactor, scaleFactor, 1F);
		drawTexture(
				matrices, 0, 0, 0.0F, 0.0F, logo.getWidth(), logo.getHeight(), logo.getWidth(), logo.getHeight()
		);

		if(color[3] == 1.0F) {
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		}
		matrices.pop();
	}
}
