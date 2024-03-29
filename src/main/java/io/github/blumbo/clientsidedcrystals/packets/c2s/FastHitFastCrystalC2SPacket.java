package io.github.blumbo.clientsidedcrystals.packets.c2s;

import io.github.blumbo.clientsidedcrystals.packets.ModPackets;
import io.github.blumbo.clientsidedcrystals.packets.s2c.FastHitFastCrystalCancelS2CPacket;
import io.github.blumbo.clientsidedcrystals.ClientSidedCrystals;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class FastHitFastCrystalC2SPacket extends PlayerInteractEntityC2SPacket {

    public int ownerCrystalId;

    public FastHitFastCrystalC2SPacket(boolean playerSneaking, int ownerCrystalId) {
        super(0, playerSneaking, PlayerInteractEntityC2SPacket.ATTACK);
        this.ownerCrystalId = ownerCrystalId;
    }

    public FastHitFastCrystalC2SPacket(PacketByteBuf buf) {
        super(buf);
        ownerCrystalId = buf.readVarInt();
    }

    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeVarInt(ownerCrystalId);
    }

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        FastHitFastCrystalC2SPacket packet = new FastHitFastCrystalC2SPacket(buf);

        if (ClientSidedCrystals.disabledPlayers.contains(player.getUuid())) {
            ServerPlayNetworking.send(player, ModPackets.FAST_HIT_FAST_CRYSTAL_CANCEL_ID,
                    new FastHitFastCrystalCancelS2CPacket(packet.ownerCrystalId).write());
        }

        try {
            handler.onPlayerInteractEntity(packet);
        } catch (OffThreadException ignored) {}
    }

}
