package net.blumbo.clientsidedcrystals.packets.c2s;

import net.blumbo.clientsidedcrystals.ClientSidedCrystals;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

import java.util.HashMap;

public class PlaceFastCrystalC2SPacket extends PlayerInteractBlockC2SPacket {

    public int ownerCrystalId;

    public PlaceFastCrystalC2SPacket(Hand hand, BlockHitResult blockHitResult, int sequence, int ownerCrystalId) {
        super(hand, blockHitResult, sequence);
        this.ownerCrystalId = ownerCrystalId;
    }

    public PlaceFastCrystalC2SPacket(PacketByteBuf buf) {
        super(buf);
        ownerCrystalId = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeVarInt(ownerCrystalId);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        super.apply(serverPlayPacketListener);
    }

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (!ClientSidedCrystals.fastEndCrystals.containsKey(player.getUuid())) {
            ClientSidedCrystals.fastEndCrystals.put(player.getUuid(), new HashMap<>());
        }

        PlaceFastCrystalC2SPacket packet = new PlaceFastCrystalC2SPacket(buf);
        try {
            packet.apply(handler);
        } catch (OffThreadException ignored) {}
    }
}
