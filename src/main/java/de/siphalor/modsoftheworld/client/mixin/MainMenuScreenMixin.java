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
import org.lwjgl.opengl.GL11;
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

	protected MainMenuScreenMixin(Text text_1) {
		super(text_1);
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
			GLFW.glfwSetWindowTitle(client.getWindow().getHandle(), "Minecraft " + SharedConstants.getGameVersion().getName() + " - " + MOTWClient.getLogos().get(modsOfTheWorld_currentLogo).modName + " Edition");
			splashText = MOTWClient.getLogos().get(modsOfTheWorld_currentLogo).splashProvider.get();
		}
		modsOfTheWorld_logoTime %= MOTWClient.WHOLE_TIME;
		float[] color = new float[4];
		GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, color);
		if(color[3] == 1.0F) {
			float alpha = 1.0F;
			if (modsOfTheWorld_logoTime < MOTWClient.FADE_TIME) alpha = modsOfTheWorld_logoTime / MOTWClient.FADE_TIME;
			if (modsOfTheWorld_logoTime >= MOTWClient.WHOLE_TIME - MOTWClient.FADE_TIME)
				alpha = 1.0F - (modsOfTheWorld_logoTime - MOTWClient.WHOLE_TIME + MOTWClient.FADE_TIME) / MOTWClient.FADE_TIME;
			RenderSystem.color4f(1F, 1F, 1F, alpha);
		}

		matrices.push();

		Logo logoTexture = MOTWClient.getLogos().get(modsOfTheWorld_currentLogo);
		client.getTextureManager().bindTexture(logoTexture.identifier);

		float scaleFactor = (float) height / (float) logoTexture.height;
		matrices.translate(x + 39F - logoTexture.width * scaleFactor, y, 0F);
		matrices.scale(scaleFactor, scaleFactor, 1F);
		drawTexture(matrices, 0, 0, 0.0F, 0.0F, logoTexture.width, logoTexture.height, logoTexture.width, logoTexture.height);

        if(color[3] == 1.0F)
        	RenderSystem.color4f(1F, 1F, 1F, 1F);
		matrices.pop();
	}
}
