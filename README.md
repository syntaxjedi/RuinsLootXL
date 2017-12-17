# RuinsLootXL
Auto refillable ruins chests.
------
This plugin adds support for lootable chests in the world. It has three types of default chests: Common, Uncommon, and Legendary. All chests across the server will refresh after a given interval with the loot defined in the config. XL has the added ability to randomly spawn chests around the world that disappear when users find them. The main driver for splitting off RuinsLoot and RuinsLootXL is for eventual support of random spawning ruins/camps/bandit hideouts along with quests and bounties.  

Commands
------
* /lootchest: gives info about the plugin (current version, author, commands, etc...)  
* /lootchest <common|uncommon|legendary> : gives the player a lootchest of the given type  
* /lootchest <common|uncommon|legendary> [name] : gives the player a lootchest of the given type with a custom name  
* /lootchest random : Toggles random spawning across the world/worlds (defined in config)  
* /lootchest reload : reloads the config  
* /lootchest random chest : Toggles between spawning random chest rarity (common, uncommon, legendary) or the default chest rarity (defined in config)  
* /lootchest clear : Clears the current iteration of randomly spawned chest  
* /lootchest random next : Clears the current iteration of randomly spawned chests and spawns the next iteration
* /lootchest fill : fills all the chests in the world/worlds (defined in config)  

Coming Soon
------
* spawn ruins/camps/bandit hideouts
* custom questline/tips on where chests spawned
* obtainable keys that let players spawn a given, or random, chest/number of chests as a reward
