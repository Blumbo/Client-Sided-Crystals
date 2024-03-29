package io.github.blumbo.clientsidedcrystals.mixin.server;

import io.github.blumbo.clientsidedcrystals.FastEndCrystalEntity;
import io.github.blumbo.clientsidedcrystals.ClientSidedCrystals;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin {

    private ItemUsageContext context;

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void getContext(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        this.context = context;
    }

    @ModifyArg(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private Entity setCrystalEntity(Entity entity) {
        Integer lastId = ClientSidedCrystals.lastPlacementId;
        if (lastId == null) return entity;

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) context.getPlayer();
        FastEndCrystalEntity fastEndCrystal = new FastEndCrystalEntity((EndCrystalEntity) entity, serverPlayer, lastId);
        ClientSidedCrystals.lastPlacementSucceeded = true;

        return fastEndCrystal;
    }

}
