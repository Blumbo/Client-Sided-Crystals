package io.github.blumbo.clientsidedcrystals.packets.s2c;

import io.github.blumbo.clientsidedcrystals.FastEndCrystalEntity;
import io.github.blumbo.clientsidedcrystals.client.ClientSidedCrystalsClient;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

public class PlaceFastCrystalSuccessS2CPacket extends EntitySpawnS2CPacket {

    public int ownerCrystalId;

    public PlaceFastCrystalSuccessS2CPacket(FastEndCrystalEntity fastEndCrystal) {
        super(fastEndCrystal);
        this.ownerCrystalId = fastEndCrystal.ownerCrystalId;
    }

    public PlaceFastCrystalSuccessS2CPacket(PacketByteBuf buf) {
        super(buf);
        this.ownerCrystalId = buf.readVarInt();
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        PlaceFastCrystalSuccessS2CPacket packet = new PlaceFastCrystalSuccessS2CPacket(buf);
        ClientSidedCrystalsClient.removeCrystal(handler.getWorld(), packet.ownerCrystalId);
        if (!ClientSidedCrystalsClient.fastHitCrystalIds.remove(packet.ownerCrystalId)) {
            try {
                packet.apply(handler);
            } catch (OffThreadException ignored) {}
        }
    }

    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeVarInt(ownerCrystalId);
    }

}
