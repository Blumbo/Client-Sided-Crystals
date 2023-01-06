package net.blumbo.clientsidedcrystals.packets.s2c;

import net.blumbo.clientsidedcrystals.client.ClientSidedCrystalsClient;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class ModExistenceS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ClientSidedCrystalsClient.serverHasMod = true;
    }

}
