# NPC-Lib
Minecraft NPC library for Paper 1.12.2+ servers.

## Requirements
This library can only be used on PaperSpigot servers higher or on version 1.12.2. 
The plugin [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) is required on your server.

## How to use
Add the repository and dependency to your plugin:


Maven

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.realPanamo</groupId>
    <artifactId>NP-Lib</artifactId>
    <version>1.6-SNAPSHOT</version>
</dependency>
```
Gradle

```xml
repositories {
    maven {
        name = 'jitpack.io'
        url 'https://jitpack.io'
    }
}

dependencies {
        compileOnly 'com.github.realPanamo:NPC-Lib:1.6-SNAPSHOT'
}
```

Add ProtocolLib as dependency to your plugin.yml. It could look like this:
```yml
name: Hub
version: 1.0-SNAPSHOT
api-version: "1.13"
depend: [ProtocolLib]
author: Panamo
main: de.panamo.server.hub.ServerHub
```
Now you're all set! You can start by creating an instance of the 
[NPCPool](https://github.com/realPanamo/NPC-Lib/blob/master/src/main/java/com/github/realpanamo/npc/NPCPool.java) and the 
[NPC.Builder](https://github.com/realPanamo/NPC-Lib/blob/master/src/main/java/com/github/realpanamo/npc/NPC.java).
