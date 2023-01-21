# Client-Sided Crystals

Client-Sided Crystals is a Fabric mod that allows players to interact with End Crystals in real-time independent from ping. Placing crystals will instantly make them appear to the player, and hitting crystals will instantly make them dissapear for the player. This means that players will no longer have junky crystal cooldown, making Crystal PvP more enjoyable and less ping based.  
  
Note that this mod does not remove any actual delay from crystals, it just lets players interact with crystals faster without having to wait for packets from the server.  
  
This mod must be installed on both the client and server for its features to work. However, you (or players on your server) can still play the game normally without it installed on either the client or server.

### For server developers
You can use this mod as a library to enable and disable the mod for certain players at any time. You can also toggle whether the mod is immediately enabled or not for any player joining your server. You should find these functionalities in the `ClientSidedCrystals` class (as of writing this). Might be useful if you want to make separate modes with this mod enabled.
