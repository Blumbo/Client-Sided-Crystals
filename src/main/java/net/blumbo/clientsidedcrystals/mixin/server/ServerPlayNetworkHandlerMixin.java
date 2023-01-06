package net.blumbo.clientsidedcrystals.mixin.server;

import net.blumbo.clientsidedcrystals.ClientSidedCrystals;
import net.blumbo.clientsidedcrystals.FastEndCrystalEntity;
import net.blumbo.clientsidedcrystals.packets.ModPackets;
import net.blumbo.clientsidedcrystals.packets.c2s.FastHitCrystalC2SPacket;
import net.blumbo.clientsidedcrystals.packets.c2s.FastHitFastCrystalC2SPacket;
import net.blumbo.clientsidedcrystals.packets.c2s.PlaceFastCrystalC2SPacket;
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
        if (packet instanceof PlaceFastCrystalC2SPacket placeCrystalPacket) {
            ClientSidedCrystals.lastPlacementId = placeCrystalPacket.ownerCrystalId;
            ClientSidedCrystals.lastPlacementSucceeded = false;
        } else {
            ClientSidedCrystals.lastPlacementId = null;
        }
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
    private Entity getHitFastEntity(PlayerInteractEntityC2SPacket instance, ServerWorld world) {
        shouldCancel = false;
        if (!(instance instanceof FastHitFastCrystalC2SPacket packet)) return instance.getEntity(world);

        System.out.println("Received packet");

        if (!ClientSidedCrystals.fastEndCrystals.containsKey(player.getUuid())) {
            shouldCancel = true;
            return null;
        }
        System.out.println("Contains key");

        FastEndCrystalEntity crystal = ClientSidedCrystals.fastEndCrystals.get(player.getUuid()).get(packet.ownerCrystalId);
        if (crystal == null) {
            shouldCancel = true;
            return null;
        }
        System.out.println("Crystal is not null");

        packet.entityId = crystal.getId();
        Entity entity = packet.getEntity(player.getWorld());
        if (!(entity instanceof EndCrystalEntity)) {
            shouldCancel = true;
            return null;
        }

        System.out.println("Entity is End Crystal");
        return entity;
    }

    @Inject(method = "onPlayerInteractEntity", cancellable = true,
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;getEntity(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/entity/Entity;"))
    private void cancelHitFast(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        if (shouldCancel) {
            if (packet instanceof FastHitFastCrystalC2SPacket hitFastPacket) {
                hitFastPacket.sendFailure(player);
            }
            ci.cancel();
        }
    }

    private boolean isFastHitPacket(PlayerInteractEntityC2SPacket packet) {
        return packet instanceof FastHitCrystalC2SPacket || packet instanceof FastHitFastCrystalC2SPacket;
    }


    @Inject(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updateLastActionTime()V"))
    private void aaAAAAAAAa(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        System.out.println(1.1);
    }


    @Inject(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;handle(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket$Handler;)V"))
    private void aaAAAAAAAa1(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        System.out.println(1.2);
    }


    @Inject(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;handle(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket$Handler;)V"))
    private void aaAAAAAAAa12(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        System.out.println(1.3);
    }
}
