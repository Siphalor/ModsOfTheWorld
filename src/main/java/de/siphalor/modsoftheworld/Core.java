package de.siphalor.modsoftheworld;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class Core {
	public static final String MOD_ID = "modsoftheworld";

	public static final Identifier LOGO_KEY = new Identifier(MOD_ID, "logo");
	public static final float SHOW_TIME = 15;
	public static final float FADE_TIME = 10;
	public static final float WHOLE_TIME = SHOW_TIME + 2 * FADE_TIME;

	private static ArrayList<LogoTexture> modLogos = null;

	public static ArrayList<LogoTexture> getLogos() {
		if(modLogos != null) return modLogos;
		modLogos = new ArrayList<>();
        loadTexture(new Identifier(MOD_ID, "java.png"));
		for(ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			String modId = mod.getMetadata().getId();
            Optional<String> iconPath = mod.getMetadata().getIconPath(8);
			if(mod.getMetadata().containsCustomElement(LOGO_KEY.toString())) {
				if(loadTexture(Identifier.ofNullable(JsonHelper.asString(mod.getMetadata().getCustomElement(LOGO_KEY.toString()), modId + "'s logo identifier"))))
					continue;
			}
			iconPath.ifPresent(s -> loadTexture(new Identifier(s.replace("assets/", "").replaceFirst("/", ":"))));
		}

		return modLogos;
	}

	public static boolean loadTexture(Identifier logoId) {
		ResourceTexture.TextureData data = ResourceTexture.TextureData.load(MinecraftClient.getInstance().getResourceManager(), logoId);
		try {
			data.checkException();
			modLogos.add(new LogoTexture(logoId, data.getImage().getWidth(), data.getImage().getHeight()));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
