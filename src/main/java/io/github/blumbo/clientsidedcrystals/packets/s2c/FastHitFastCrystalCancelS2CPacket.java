package io.github.blumbo.clientsidedcrystals.packets.s2c;

import io.github.blumbo.clientsidedcrystals.client.ClientSidedCrystalsClient;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class FastHitFastCrystalCancelS2CPacket {

    public int ownerCrystalId;

    public FastHitFastCrystalCancelS2CPacket(int ownerCrystalId) {
        this.ownerCrystalId = ownerCrystalId;
    }

    public FastHitFastCrystalCancelS2CPacket(PacketByteBuf buf) {
        this.ownerCrystalId = buf.readVarInt();
    }

    public PacketByteBuf write() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(ownerCrystalId);
        return buf;
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        FastHitFastCrystalCancelS2CPacket packet = new FastHitFastCrystalCancelS2CPacket(buf);
        ClientSidedCrystalsClient.fastHitCrystalIds.remove(packet.ownerCrystalId);
    }

}
