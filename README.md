# Necklace Totem

A Fabric mod for Minecraft 26.3.

- **Totems stack to 8.** The Totem of Undying's max stack size is raised from 1 to 8.
- **Necklace Totem.** Craft it with 3 string + 1 Totem of Undying (shapeless).
- **Necklace slot.** A new inventory slot above the offhand slot that only accepts the Necklace Totem.
- **Works like a totem.** If you would die with a Necklace Totem in the slot, it is consumed and saves you
  (1 heart, Regeneration II, Absorption II, Fire Resistance, pop animation). Void damage and `/kill` still kill you.

Shift-click moves a Necklace Totem between your inventory and the slot.

## Requirements

- Minecraft 26.3
- Fabric Loader 0.19.5+
- Fabric API (0.162.0+26.3 or newer)
- Java 25

Install on both the client and the server.

## Building

```
gradle wrapper
./gradlew build
```

The jar ends up in `build/libs/`. Use the one without `-sources` in its name.

If Gradle fails, generate a fresh 26.3 project from the Fabric template
(https://fabricmc.net/develop/template/) and copy this repo's `src/` folder into it.

## Notes

- The necklace is kept when you die (it is saved with the player).
- The slot is hidden in creative mode.
- Not yet compile-tested against 26.3. Open an issue with the error output if the build fails.

## License

MIT, see [LICENSE](LICENSE).
