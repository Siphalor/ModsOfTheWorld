package de.siphalor.modsoftheworld.client;

import net.minecraft.util.Identifier;

public class Logo {
	public Identifier identifier;
	public String modName;
	public SplashProvider splashProvider;
    public int width;
    public int height;

	Logo(Identifier identifier, int width, int height, String modName, SplashProvider splashProvider) {
		this.identifier = identifier;
		this.modName = modName;
		this.splashProvider = splashProvider;
		this.width = width;
		this.height = height;
	}
}
