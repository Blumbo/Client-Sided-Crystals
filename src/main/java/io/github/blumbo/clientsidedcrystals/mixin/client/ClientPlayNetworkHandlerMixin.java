package io.github.blumbo.clientsidedcrystals.mixin.client;

import io.github.blumbo.clientsidedcrystals.client.ClientSidedCrystalsClient;
import io.github.blumbo.clientsidedcrystals.packets.s2c.PlaceFastCrystalSuccessS2CPacket;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    private boolean isCrystalSuccessPacket;

    @Inject(method = "onEntitySpawn", at = @At("HEAD"))
    private void getPacketType(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        isCrystalSuccessPacket = packet instanceof PlaceFastCrystalSuccessS2CPacket;
    }

    @ModifyArg(method = "onEntitySpawn", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addEntity(ILnet/minecraft/entity/Entity;)V"))
    private Entity setEntityAge(Entity entity) {
        if (entity instanceof EndCrystalEntity crystal && isCrystalSuccessPacket) {
            crystal.endCrystalAge = ClientSidedCrystalsClient.lastSuccessCrystalAge;
        }
        return entity;
    }

    @Inject(method = "onGameJoin", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V"))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        ClientSidedCrystalsClient.serverHasMod = false;
    }

}
