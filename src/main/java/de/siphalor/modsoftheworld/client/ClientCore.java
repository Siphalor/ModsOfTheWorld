package de.siphalor.modsoftheworld.client;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public class ClientCore {
	public static final String MOD_ID = "modsoftheworld";

	public static final Identifier LOGO_KEY = new Identifier(MOD_ID, "logo");
	public static final float SHOW_TIME = 15;
	public static final float FADE_TIME = 10;
	public static final float WHOLE_TIME = SHOW_TIME + 2 * FADE_TIME;

	private static ArrayList<Logo> modLogos = null;

	public static ArrayList<Logo> getLogos() {
		if(modLogos != null) return modLogos;
		modLogos = new ArrayList<>();
		for(ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			String modId = mod.getMetadata().getId();
            Optional<String> iconPath = mod.getMetadata().getIconPath(8);
			if(mod.getMetadata().containsCustomElement(LOGO_KEY.toString())) {
				if(loadLogo(Identifier.ofNullable(JsonHelper.asString(mod.getMetadata().getCustomElement(LOGO_KEY.toString()), modId + "'s logo identifier")), mod.getMetadata().getName(), true))
					continue;
			}
			iconPath.ifPresent(s -> loadLogo(new Identifier(s.replace("assets/", "").replaceFirst("/", ":")), mod.getMetadata().getName(), true));
		}
		Collections.shuffle(modLogos);
		loadLogo(new Identifier(MOD_ID, "java.png"), "Java", false);

		return modLogos;
	}

	public static boolean loadLogo(Identifier logoId, String modName, boolean pushBack) {
		ResourceTexture.TextureData data = ResourceTexture.TextureData.load(MinecraftClient.getInstance().getResourceManager(), logoId);
		try {
			data.checkException();
			modLogos.add(pushBack ? modLogos.size() : 0, new Logo(logoId, data.getImage().getWidth(), data.getImage().getHeight(), modName));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
