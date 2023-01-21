package net.blumbo.clientsidedcrystals.mixin.server;

import net.blumbo.clientsidedcrystals.ClientSidedCrystals;
import net.blumbo.clientsidedcrystals.FastEndCrystalEntity;
import net.blumbo.clientsidedcrystals.packets.ModPackets;
import net.blumbo.clientsidedcrystals.packets.c2s.FastHitCrystalC2SPacket;
import net.blumbo.clientsidedcrystals.packets.c2s.FastHitFastCrystalC2SPacket;
import net.blumbo.clientsidedcrystals.packets.c2s.PlaceFastCrystalC2SPacket;
import net.blumbo.clientsidedcrystals.packets.s2c.FastHitFastCrystalCancelS2CPacket;
import net.blumbo.clientsidedcrystals.packets.s2c.PlaceFastCrystalCancelS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onPlayerInteractBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
    private void setPlaceCrystalId(PlayerInteractBlockC2SPacket packet, CallbackInfo ci) {
        if (!(packet instanceof PlaceFastCrystalC2SPacket placeCrystalPacket)) {
            ClientSidedCrystals.lastPlacementId = null;
            return;
        }

        ClientSidedCrystals.lastPlacementId = placeCrystalPacket.ownerCrystalId;
        ClientSidedCrystals.lastPlacementSucceeded = false;
    }

    @Inject(method = "onPlayerInteractBlock", at = @At(value = "INVOKE", shift= At.Shift.AFTER, target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
    private void sendPlaceCrystalCancel(PlayerInteractBlockC2SPacket packet, CallbackInfo ci) {
        if (!(packet instanceof PlaceFastCrystalC2SPacket placeCrystaPacket)) return;
        if (ClientSidedCrystals.lastPlacementSucceeded) return;

        ServerPlayNetworking.send(player, ModPackets.PLACE_FAST_CRYSTAL_CANCEL_ID,
                new PlaceFastCrystalCancelS2CPacket(placeCrystaPacket.ownerCrystalId).write());
    }

    @Inject(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;handle(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket$Handler;)V"))
    private void resetHitSuccessBoolean(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        if (isFastHitPacket(packet)) {
            ClientSidedCrystals.lastHitId = packet.entityId;
            ClientSidedCrystals.lastHitSucceeded = false;
        } else {
            ClientSidedCrystals.lastHitSucceeded = null;
        }
    }

    @Inject(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;handle(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket$Handler;)V"))
    private void sendEpicHitFailure(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        if (isFastHitPacket(packet) && !ClientSidedCrystals.lastHitSucceeded) {
            Entity entity = packet.getEntity(player.getWorld());
            if (entity instanceof EndCrystalEntity crystal) {
                ClientSidedCrystals.sendCrystalPacket(player, crystal);
            }
        }
    }

    private boolean shouldCancel = false;

    @Redirect(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;getEntity(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/entity/Entity;"))
    private Entity getHitEntity(PlayerInteractEntityC2SPacket instance, ServerWorld world) {
        shouldCancel = false;
        if (!(instance instanceof FastHitFastCrystalC2SPacket)) return instance.getEntity(world);

        Entity entity = getFastCrystal((FastHitFastCrystalC2SPacket) instance);
        if (entity == null) shouldCancel = true;

        return entity;
    }

    private Entity getFastCrystal(FastHitFastCrystalC2SPacket packet) {
        if (!ClientSidedCrystals.fastEndCrystals.containsKey(player.getUuid())) return null;

        FastEndCrystalEntity crystal = ClientSidedCrystals.fastEndCrystals.get(player.getUuid()).get(packet.ownerCrystalId);
        if (crystal == null) return null;

        packet.entityId = crystal.getId();
        Entity entity = packet.getEntity(player.getWorld());
        if (!(entity instanceof EndCrystalEntity)) return null;

        return entity;
    }

    @Inject(method = "onPlayerInteractEntity", cancellable = true,
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;getEntity(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/entity/Entity;"))
    private void cancelHitFast(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        if (shouldCancel) {
            if (packet instanceof FastHitFastCrystalC2SPacket hitFastPacket) {
                ServerPlayNetworking.send(player, ModPackets.FAST_HIT_FAST_CRYSTAL_CANCEL_ID,
                        new FastHitFastCrystalCancelS2CPacket(hitFastPacket.ownerCrystalId).write());
            }
            ci.cancel();
        }
    }

    private boolean isFastHitPacket(PlayerInteractEntityC2SPacket packet) {
        return packet instanceof FastHitCrystalC2SPacket || packet instanceof FastHitFastCrystalC2SPacket;
    }
}
