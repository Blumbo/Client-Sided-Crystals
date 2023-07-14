package io.github.blumbo.clientsidedcrystals.mixin.server;

import io.github.blumbo.clientsidedcrystals.FastEndCrystalEntity;
import io.github.blumbo.clientsidedcrystals.packets.ModPackets;
import io.github.blumbo.clientsidedcrystals.packets.s2c.PlaceFastCrystalSuccessS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin<T> {

    @Shadow @Final private Entity entity;
    ServerPlayerEntity receiver;

    @Inject(method = "startTracking", at = @At("HEAD"))
    private void getPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        receiver = player;
    }

    @Redirect(method = "sendPackets", at = @At(value = "INVOKE", ordinal = 0, target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
    private void  getSender(Consumer<T> sender, T packet) {
        if (!(entity instanceof FastEndCrystalEntity crystal)) {
            sender.accept(packet);
            return;
        }
        if (crystal.crystalOwner != receiver) {
            sender.accept(packet);
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        new PlaceFastCrystalSuccessS2CPacket(crystal).write(buf);
        ServerPlayNetworking.send(receiver, ModPackets.PLACE_FAST_CRYSTAL_SUCCESS_ID, buf);
    }

}
