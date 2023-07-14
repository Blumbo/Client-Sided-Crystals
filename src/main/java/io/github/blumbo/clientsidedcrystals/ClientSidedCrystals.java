package io.github.blumbo.clientsidedcrystals;

import io.github.blumbo.clientsidedcrystals.packets.ModPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ClientSidedCrystals implements ModInitializer {

    public static final String MOD_ID = "clientsidedcrystals";
    public static HashMap<UUID, HashMap<Integer, FastEndCrystalEntity>> fastEndCrystals = new HashMap<>();

    public static Integer lastPlacementId = null;
    public static boolean lastPlacementSucceeded = true;

    public static Integer lastHitId = null;
    public static Boolean lastHitSucceeded = null;

    public static HashSet<UUID> disabledPlayers = new HashSet<>();

    @Override
    public void onInitialize() {
        ModPackets.registerC2S();
    }

    public static void sendCrystalPacket(ServerPlayerEntity player, EndCrystalEntity crystal) {
        EntitySpawnS2CPacket crystalSpawnPacket = new EntitySpawnS2CPacket(crystal);
        player.networkHandler.sendPacket(crystalSpawnPacket);
        if (crystal.getDataTracker().getChangedEntries() != null) {
            player.networkHandler.sendPacket(
                    new EntityTrackerUpdateS2CPacket(crystal.getId(), crystal.getDataTracker().getChangedEntries()));
        }
    }

    // These are for anyone using this mod as a library

    public static boolean enableOnJoin = true;

    public static void enableForPlayer(ServerPlayerEntity player) {
        disabledPlayers.remove(player.getUuid());
        ServerPlayNetworking.send(player, ModPackets.MOD_ENABLE_ID, PacketByteBufs.create());
    }

    public static void disableForPlayer(ServerPlayerEntity player) {
        disabledPlayers.add(player.getUuid());
        ServerPlayNetworking.send(player, ModPackets.MOD_DISABLE_ID, PacketByteBufs.create());
    }

}
