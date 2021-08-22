package de.siphalor.modsoftheworld.client;

import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;

public class Logo {
	private final ResourceTexture texture;
	private final Identifier identifier;
	private final String modName;
	private final SplashProvider splashProvider;
    private final int width;
    private final int height;

	public Logo(ResourceTexture texture, Identifier identifier, int width, int height, String modName, SplashProvider splashProvider) {
		this.texture = texture;
		this.identifier = identifier;
		this.modName = modName;
		this.splashProvider = splashProvider;
		this.width = width;
		this.height = height;
	}

	public ResourceTexture getTexture() {
		return texture;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public String getModName() {
		return modName;
	}

	public SplashProvider getSplashProvider() {
		return splashProvider;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
