# Slate

A configurable and concise inventory GUI framework for Bukkit.

![Maven Central Version](https://img.shields.io/maven-central/v/dev.aurelium/slate?style=flat-square)

## Overview

Slate is a comprehensive GUI menu framework that uses a front-end YAML configuration file backed by code to define functionality.

The API is designed around:
- Separation of appearance and functionality
- Full configurability on the individual servers your plugin runs on
- Rapid building and iteration to save development time
- Writing the least amount of code needed to implement functionality
## Features

### Configuration Language

These features are natively supported through Slate's YAML-based configuration language:
- Full item meta support
- MiniMessage formatting
- PlaceholderAPI support
- Click actions to execute commands
- Customizing the title and size of the menu
- Full protection of items so they cannot be taken out of menus

### API Features

The API is designed to be declarative and hierarchical. Code is structured using lambdas and builders to match the layout of the configuration file. API features include:

- Running code when an item is clicked, including for specific mouse buttons
- Placeholder system for inserting player-dependent data into menus at runtime
- Page system
- Templates for defining multiple instances of a similar item
- Components for reusing pieces of lore across items
- Custom configuration options with automatic updating for user configs

## Documentation

[Documentation can be found on the wiki](https://wiki.aurelium.dev/slate) (under construction).

## Compatibility

**Slate requires at least Java 17**, and has been tested on Minecraft 1.18 - 1.20. However, older server versions may work if they run Java 17 and 1.14.4 at minimum.

## Installation

Release versions of the API are published to the Maven central repository.

### Gradle

**Kotlin DSL:**
```Gradle Kotlin DSL
repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.aurelium:slate:1.0.5")
}
```

**Groovy DSL:**
```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'dev.aurelium:slate:1.0.5'
}
```

### Maven

```xml
<dependency>
    <groupId>dev.aurelium</groupId>
    <artifactId>slate</artifactId>
    <version>1.0.5</version>
</dependency>
```

You should use the Gradle [Shadow](https://github.com/johnrengelman/shadow) or Maven [Shade](https://maven.apache.org/plugins/maven-shade-plugin/usage.html) plugin to include Slate in your plugin jar. It is highly recommended to relocate the API packages to avoid conflict with plugins that may use conflicting Slate versions.

> Slate bundles the [Adventure](https://github.com/KyoriPowered/adventure) and MiniMessage APIs as dependencies. If you are only developing for Paper, which includes Adventure in the server, you can exclude the `adventure-platform-bukkit` and `adventure-text-minimessage` modules in the `net.kyori` package to reduce plugin jar size.

## Getting Started

This basic example shows a menu with a single item that closes the menu when clicked.

Create a YAML configuration file for your menu in a sub-folder in your project resources folder, such as `menus/my_menu.yml`.

```yaml
size: 3
items:
  close:
    material: barrier
    pos: 0,0
    display_name: <red>Close
```

Create a Slate instance by passing in your Plugin instance and options. This example assumes you put this in the `onEnable` of your main class. After getting the instance, use it to build the functionality, generate the config file if missing, and load menus.

```java
Slate slate = new Slate(this, SlateOptions.builder()
        .mainDirectory(new File(this.getDataFolder(), "menus"))
        .build());

slate.buildMenu("my_menu", menu -> {
    menu.item("close", item -> {
        item.onClick(c -> c.player().closeInventory());
    });
});

slate.generateFiles();
slate.loadMenus();
```

Finally, show the menu to a player where you need to, like in a command:

```java
slate.openMenu(player, "my_menu");
```

It's recommended to store the Slate instance as a field in your main class so you can access it elsewhere in your plugin through dependency injection. For example:

```java
public class MyPlugin extends JavaPlugin {

    private Slate slate;

    @Override
    public void onEnable() {
        slate = ... // Instantiation shown above
    }

    public Slate getSlate() {
        return slate;
    }
}
```

### Other Examples

Complex examples can be seen in [AuraSkills](https://github.com/Archy-X/AuraSkills), a popular plugin that uses Slate for all menus.
- [Skills menu](https://github.com/Archy-X/AuraSkills/blob/master/bukkit/src/main/java/dev/aurelium/auraskills/bukkit/menus/SkillsMenu.java)
- [Stats menu](https://github.com/Archy-X/AuraSkills/blob/master/bukkit/src/main/java/dev/aurelium/auraskills/bukkit/menus/StatsMenu.java)
- [Where menus are registered](https://github.com/Archy-X/AuraSkills/blob/master/bukkit/src/main/java/dev/aurelium/auraskills/bukkit/menus/MenuRegistrar.java)
- [SlateOptions used and custom meta parsers](https://github.com/Archy-X/AuraSkills/blob/master/bukkit/src/main/java/dev/aurelium/auraskills/bukkit/menus/MenuOptions.java)