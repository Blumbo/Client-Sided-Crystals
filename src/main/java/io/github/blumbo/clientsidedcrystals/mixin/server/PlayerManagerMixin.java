package io.github.blumbo.clientsidedcrystals.mixin.server;

import io.github.blumbo.clientsidedcrystals.packets.ModPackets;
import io.github.blumbo.clientsidedcrystals.ClientSidedCrystals;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow public abstract MinecraftServer getServer();

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void sendModPacket(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (ClientSidedCrystals.enableOnJoin) {
            ServerPlayNetworking.send(player, ModPackets.MOD_ENABLE_ID, PacketByteBufs.create());
        } else {
            ClientSidedCrystals.disabledPlayers.add(player.getUuid());
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void removePlayer(ServerPlayerEntity player, CallbackInfo ci) {
        ClientSidedCrystals.fastEndCrystals.remove(player.getUuid());
        ClientSidedCrystals.disabledPlayers.remove(player.getUuid());
    }

}
