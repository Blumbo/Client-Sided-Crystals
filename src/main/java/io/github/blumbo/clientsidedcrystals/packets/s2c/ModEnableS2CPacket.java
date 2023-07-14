package io.github.blumbo.clientsidedcrystals.packets.s2c;

import io.github.blumbo.clientsidedcrystals.client.ClientSidedCrystalsClient;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class ModEnableS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ClientSidedCrystalsClient.serverHasMod = true;
    }

}
