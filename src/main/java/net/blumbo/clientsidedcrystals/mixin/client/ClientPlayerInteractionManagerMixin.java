package net.blumbo.clientsidedcrystals.mixin.client;

import net.blumbo.clientsidedcrystals.client.ClientFastEndCrystalEntity;
import net.blumbo.clientsidedcrystals.client.ClientSidedCrystalsClient;
import net.blumbo.clientsidedcrystals.packets.ModPackets;
import net.blumbo.clientsidedcrystals.packets.c2s.FastHitCrystalC2SPacket;
import net.blumbo.clientsidedcrystals.packets.c2s.FastHitFastCrystalC2SPacket;
import net.blumbo.clientsidedcrystals.packets.c2s.PlaceFastCrystalC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    private PlayerEntity attackerPlayer;
    private Entity attackedEntity;

    private boolean sendFastCrystalPacket = false;

    @Redirect(method = "interactBlockInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"))
    private ActionResult getActionResult(ItemStack instance, ItemUsageContext context) {
        ActionResult actionResult = instance.useOnBlock(context);

        sendFastCrystalPacket = (actionResult == ActionResult.SUCCESS) && (instance.getItem() == Items.END_CRYSTAL) &&
                (ClientSidedCrystalsClient.serverHasMod);

        return actionResult;
    }

    @Redirect(method = "sendSequencedPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void sendPacket(ClientPlayNetworkHandler handler, Packet<?> packet) {
        if (sendFastCrystalPacket && packet instanceof PlayerInteractBlockC2SPacket interactPacket) {

            PlaceFastCrystalC2SPacket crystalPacket = new PlaceFastCrystalC2SPacket(
                    interactPacket.getHand(), interactPacket.getBlockHitResult(), interactPacket.getSequence(),
                    ClientSidedCrystalsClient.currentCrystalId());

            PacketByteBuf buf = PacketByteBufs.create();
            crystalPacket.write(buf);
            ClientPlayNetworking.send(ModPackets.PLACE_FAST_CRYSTAL_ID, buf);

        } else {
            handler.sendPacket(packet);
        }
    }

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void getVariables(PlayerEntity player, Entity target, CallbackInfo ci) {
        attackerPlayer = player;
        attackedEntity = target;
    }

    @Redirect(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void sendAttackPacket(ClientPlayNetworkHandler instance, Packet<?> packet) {
        if (!ClientSidedCrystalsClient.serverHasMod || !(attackedEntity instanceof EndCrystalEntity crystal)) {
            instance.sendPacket(packet);
            return;
        }

        crystal.remove(Entity.RemovalReason.KILLED);
        PacketByteBuf buf = PacketByteBufs.create();

        if (crystal instanceof ClientFastEndCrystalEntity clientFastCrystal) {
            new FastHitFastCrystalC2SPacket(attackerPlayer.isSneaking(), clientFastCrystal.ownerCrystalId).write(buf);
            ClientSidedCrystalsClient.fastHitCrystalIds.add(clientFastCrystal.ownerCrystalId);
            ClientPlayNetworking.send(ModPackets.FAST_HIT_FAST_CRYSTAL_ID, buf);
        } else {
            new FastHitCrystalC2SPacket(crystal.getId(), attackerPlayer.isSneaking()).write(buf);
            ClientPlayNetworking.send(ModPackets.FAST_HIT_CRYSTAL_ID, buf);
        }
    }

}
