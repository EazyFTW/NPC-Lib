# NPC-Lib
Minecraft NPC library for 1.8-1.15 servers.
This Library does only support the latest patch release of a supported version (for example 1.13.2).
Issues with older patch versions (for example 1.13.1) won't be fixed.

## Requirements
This library can only be used on spigot servers higher or on version 1.8.8. 
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
    <groupId>com.github.juliarn</groupId>
    <artifactId>npc-lib</artifactId>
    <version>2.3-RELEASE</version>
</dependency>
```

Gradle
```groovy
maven {
    name 'jitpack.io'
    url 'https://jitpack.io'
}

compile group: 'com.github.juliarn', name: 'npc-lib', version: '2.3-RELEASE'
```

Add ProtocolLib as dependency to your plugin.yml. It could look like this:
```yml
name: Hub
version: 1.0-SNAPSHOT
api-version: "1.13"
depend: [ProtocolLib]
author: juliarn
main: com.github.juliarn.server.hub.ServerHub
```
Now you're all set! You can start by creating an instance of the 
[NPCPool](https://github.com/juliarn/NPC-Lib/blob/master/src/main/java/com/github/juliarn/npc/NPCPool.java) and the 
[NPC.Builder](https://github.com/juliarn/NPC-Lib/blob/master/src/main/java/com/github/juliarn/npc/NPC.java).
