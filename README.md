# NPC-Lib
Minecraft NPC library for 1.8-1.16 servers.
This Library does only support the latest patch release of a supported version (for example 1.13.2).
Issues with older patch versions (for example 1.13.1) won't be fixed.

## Requirements
This library can only be used on spigot servers higher or on version 1.8.8. 
The plugin [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) is required on your server.

Add ProtocolLib as dependency to your plugin.yml. It could look like this:
```yml
name: Hub
version: 1.0-SNAPSHOT
api-version: "1.13"
depend: [ProtocolLib]
author: EazyFTW
main: com.github.eazyftw.server.hub.ServerHub
```
Now you're all set! You can start by creating an instance of the 
[NPCPool](https://github.com/juliarn/NPC-Lib/blob/master/src/main/java/com/github/juliarn/npc/NPCPool.java) and the 
[NPC.Builder](https://github.com/juliarn/NPC-Lib/blob/master/src/main/java/com/github/juliarn/npc/NPC.java).
