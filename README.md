# Pronouns
PronounDB in Minecraft!
Just a silly little API for PronounDB

Supports Paper (and Purpur), Sponge and Fabric!

## FAQ
Folia support?
Sorry not yet, when the platform is more stable and is released to the public, only then will I support Folia.

Forge support?
No, I'd rather eat a shoe. Feel free to make a mod to use the API to add Forge support, I'm not interested in Forge.

NeoForge support?
I haven't tried NeoForge out development wise and it's early in development, once it's more stable I will experiment and see if I wanna add it.

Quilt support?
Likely works with the Fabric mod already but I'm not gonna test, someone feel free to DM me on Discord (novampr) and tell me if it works though.

API extensions?
Soon™

Why not support Bukkit/Spigot?
While I can, they are old. Let's face it, Paper is faster and you probably don't have reason to use it. However feel free to fork the project to add support for this.

Velocity support?
Planned, just not now, I haven't had the time yet.

Bungeecord/Waterfall support?
Probably not, not worth the hassle for the few people that may benefit from this. Again, feel free to fork and add, maybe make a PR and I'll consider adding it lol.

You can use the PlaceholderAPI to get the normal or short version of your pronouns<br>
Example:<br>
`%pronouns_get%` = `He/They/It`<br>
`%pronouns_getshort%` = `He/They`

You can also use the `PronounsAPI` class in your own plugin :)<br>
Example (Bukkit):

```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        String personsPronouns = PronounsAPI.getInstance().getPronouns(null); //TODO: Put a functional UUID in, null won't work!
        getLogger().info(personsPronouns);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
```

Example (Sponge):

```java
@Plugin("myplugin")
public class MyPlugin {
    @Inject
    public Logger logger;

    @Listener
    public void onServerStart(final StartedEngineEvent<Server> event) {
        String personsPronouns = PronounsAPI.getInstance().getPronouns(null); //TODO: Put a functional UUID in, null won't work!
        logger.info(personsPronouns);
    }
}
```

Example (Fabric):
```java
public class MyMod implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        String personsPronouns = PronounsAPI.getInstance().getPronouns(null); //TODO: Put a functional UUID in, null won't work!
        logger.info(personsPronouns);
    }
}
```

Example (Velocity):
```java
@Plugin(id="myplugin")
public class MyPlugin {
    @Inject
    public Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        String personsPronouns = PronounsAPI.getInstance().getPronouns(null); //TODO: Put a functional UUID in, null won't work!
        logger.info(personsPronouns);
    }
}
```

Example (Bungee):
```java
public class MyPlugin extends Plugin {
    @Override
    public void onEnable() {
        String personsPronouns = PronounsAPI.getInstance().getPronouns(null); //TODO: Put a functional UUID in, null won't work!
        getLogger().info(personsPronouns);
    }
}
```

Example (Minestom):
```java
public class MyPlugin extends Extension {
    @Override
    public void initialize() {
        String personsPronouns = PronounsAPI.getInstance().getPronouns(null); //TODO: Put a functional UUID in, null won't work!
        getLogger().info(Component.text(personsPronouns));
    }
}
```
