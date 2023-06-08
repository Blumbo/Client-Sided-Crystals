package net.blumbo.clientsidedcrystals.packets.c2s;

import net.blumbo.clientsidedcrystals.ClientSidedCrystals;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class FastHitCrystalC2SPacket extends PlayerInteractEntityC2SPacket {

    public FastHitCrystalC2SPacket(int entityId, boolean playerSneaking) {
        super(entityId, playerSneaking, PlayerInteractEntityC2SPacket.ATTACK);
    }

    public FastHitCrystalC2SPacket(PacketByteBuf buf) {
        super(buf);
    }

    public void write(PacketByteBuf buf) {
        super.write(buf);
    }

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        FastHitCrystalC2SPacket packet = new FastHitCrystalC2SPacket(buf);
        Entity entity = packet.getEntity(player.getServerWorld());

        if (!(entity instanceof EndCrystalEntity crystal)) {
            return;
        }

        if (ClientSidedCrystals.disabledPlayers.contains(player.getUuid())) {
            ClientSidedCrystals.sendCrystalPacket(player, crystal);
            return;
        }

        try {
            handler.onPlayerInteractEntity(packet);
        } catch (OffThreadException ignored) {}
    }

}
