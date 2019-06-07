package de.siphalor.modsoftheworld.client;

import net.minecraft.client.MinecraftClient;

import java.util.function.Supplier;

public abstract class SplashProvider implements Supplier<String> {
	public static final SplashProvider DEFAULT = new SplashProvider() {
		@Override
		public String get() {
			return MinecraftClient.getInstance().getSplashTextLoader().get();
		}
	};
}
