# Mods of the World

![logo](src/main/resources/assets/modsoftheworld/icon_large.png)

A mod to end all title screen conflicts and Minecraft rebrandings.

This mod contains custom changing logos in the edition part of the Minecraft title screen, an accordingly changing window title and splash texts.

Logos will fallback to the mod icon. Splashes will default to the sentences of the mod description.

## Add support for MotW
In the `fabric.mod.json` add:
```json
"custom": {
	"modsoftheworld:logo": "modid:path/to/logo.png",
	"modsoftheworld:splashes": "modid:path/to/splashes.txt"
}
```

## License

This mod is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
