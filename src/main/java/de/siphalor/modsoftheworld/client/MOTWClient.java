package de.siphalor.modsoftheworld.client;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.lwjgl.opengl.GL32;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class MOTWClient {
	public static final String MOD_ID = "modsoftheworld";

	public static final Identifier LOGO_KEY = new Identifier(MOD_ID, "logo");
	public static final Identifier SPLASHES_KEY = new Identifier(MOD_ID, "splashes");
	public static final Pattern FABRIC_PATTERN = Pattern.compile("fabric-api-base|fabric-.*-v\\d");
	public static final float SHOW_TIME = 15;
	public static final float FADE_TIME = 10;
	public static final float WHOLE_TIME = SHOW_TIME + 2 * FADE_TIME;
	public static final Random RANDOM = new Random();

	private static ArrayList<Logo> modLogos = null;

	public static ArrayList<Logo> getLogos() {
		if (modLogos != null) return modLogos;
		modLogos = new ArrayList<>();
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			ModMetadata modMetadata = mod.getMetadata();

			Optional<String> iconPath = modMetadata.getIconPath(8);

			if (
				FABRIC_PATTERN.matcher(modMetadata.getId()).find()
				|| modMetadata.containsCustomValue("modmenu:api")
				&& (
					!modMetadata.containsCustomValue(LOGO_KEY.toString())
					&& !modMetadata.containsCustomValue(SPLASHES_KEY.toString())
				)
			) {
				continue;
			}

			SplashProvider splashProvider = loadSplashes(modMetadata);

			if(modMetadata.containsCustomValue(LOGO_KEY.toString())) {
				if(loadLogo(Identifier.tryParse(modMetadata.getCustomValue(LOGO_KEY.toString()).getAsString()), modMetadata.getName(), splashProvider, true))
					continue;
			}

			try {
				iconPath.ifPresent(s -> {
					s = s.toLowerCase(Locale.ENGLISH);
					loadLogo(new Identifier(s.replace("assets/", "").replaceFirst("/", ":")), modMetadata.getName(), splashProvider, true);
				});
			} catch (InvalidIdentifierException e) {
				System.err.println("[MOTW] Found invalid icon identifier \"" + iconPath.get() + "\" for mod " + modMetadata.getName());
			}
		}
		Collections.shuffle(modLogos);
		loadLogo(new Identifier(MOD_ID, "java.png"), "Java", SplashProvider.DEFAULT, false);

		return modLogos;
	}

	public static boolean loadLogo(Identifier logoId, String modName, SplashProvider splashProvider, boolean pushBack) {
		ResourceTexture resourceTexture = new ResourceTexture(logoId);
		try {
			ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
			Resource resource = resourceManager.getResource(logoId);
			NativeImage nativeImage = NativeImage.read(resource.getInputStream());
			resourceTexture.load(resourceManager);
			Logo logo = new Logo(
					resourceTexture, logoId, nativeImage.getWidth(), nativeImage.getHeight(), modName, splashProvider
			);
			modLogos.add(
					pushBack ? modLogos.size() : 0,
					logo
			);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static SplashProvider loadSplashes(ModMetadata modMetadata) {
		if(modMetadata.containsCustomValue(SPLASHES_KEY.toString())) {
			try {
				Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(Identifier.tryParse(modMetadata.getCustomValue(SPLASHES_KEY.toString()).getAsString()));
				if(resource != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
					String[] splashes = reader.lines().map(String::trim).toArray(String[]::new);

					reader.close();
					resource.close();

					return new SplashProvider() {
						@Override
						public String get() {
							return splashes[RANDOM.nextInt(splashes.length)];
						}
					};
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(modMetadata.getDescription().length() > 0) {
			String[] splashes = Arrays.stream(modMetadata.getDescription().split("(?<=[.!?]|$)(?=\\s|$)")).map(s -> {
				s = s.trim();
				if(s.length() == 0) return null;
				return s;
			}).toArray(String[]::new);
			return new SplashProvider() {
				@Override
				public String get() {
					return splashes[RANDOM.nextInt(splashes.length)];
				}
			};
		} else {
			return SplashProvider.DEFAULT;
		}
	}

	public static int getCurrentTexWidth() {
		return GL32.glGetTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_WIDTH);
	}

	public static int getCurrentTexHeight() {
		return GL32.glGetTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_HEIGHT);
	}
}
