package io.github.blumbo.clientsidedcrystals.packets.s2c;

import io.github.blumbo.clientsidedcrystals.client.ClientSidedCrystalsClient;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class PlaceFastCrystalCancelS2CPacket {

    public int ownerCrystalId;

    public PlaceFastCrystalCancelS2CPacket(int ownerCrystalId) {
        this.ownerCrystalId = ownerCrystalId;
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        PlaceFastCrystalCancelS2CPacket packet = new PlaceFastCrystalCancelS2CPacket(buf);
        ClientSidedCrystalsClient.removeCrystal(handler.getWorld(), packet.ownerCrystalId);
    }

    public PacketByteBuf write() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(this.ownerCrystalId);
        return buf;
    }

    public PlaceFastCrystalCancelS2CPacket(PacketByteBuf buf) {
        this.ownerCrystalId = buf.readVarInt();
    }

}
