package net.blumbo.clientsidedcrystals.client;

import net.blumbo.clientsidedcrystals.packets.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.HashSet;

@Environment(EnvType.CLIENT)
public class ClientSidedCrystalsClient implements ClientModInitializer {

    public static boolean serverHasMod = false;
    private static int currentCrystalId = 0;
    public static HashMap<Integer, ClientFastEndCrystalEntity> clientCrystals = new HashMap<>();
    public static HashSet<Integer> fastHitCrystalIds = new HashSet<>();
    public static int lastSuccessCrystalAge = 0;

    public static int newCrystalId() {
        return ++currentCrystalId;
    }

    public static int currentCrystalId() {
        return currentCrystalId;
    }

    public static void removeCrystal(ClientWorld world, int ownerCrystalId) {
        ClientFastEndCrystalEntity endCrystal = ClientSidedCrystalsClient.clientCrystals.get(ownerCrystalId);
        if (endCrystal != null) {
            ClientSidedCrystalsClient.clientCrystals.remove(ownerCrystalId);
            world.removeEntity(endCrystal.getId(), Entity.RemovalReason.KILLED);
            ClientSidedCrystalsClient.lastSuccessCrystalAge = endCrystal.endCrystalAge;
        }
    }

    @Override
    public void onInitializeClient() {
        ModPackets.registerS2C();
    }

}
