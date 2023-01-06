package net.blumbo.clientsidedcrystals.mixin.client;

import net.blumbo.clientsidedcrystals.client.ClientFastEndCrystalEntity;
import net.blumbo.clientsidedcrystals.client.ClientSidedCrystalsClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin {

    private static int id = -1;

    @Inject(method = "useOnBlock", at = @At(value = "RETURN", ordinal = 3))
    private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getPlayer() == null) return;
        if (!context.getWorld().isClient) return;

        context.getPlayer().sendMessage(Text.of("Server has mod: " + ClientSidedCrystalsClient.serverHasMod), false);
        if (ClientSidedCrystalsClient.serverHasMod) {
            placeClientCrystal(context);
        }
    }

    private static void placeClientCrystal(ItemUsageContext context) {
        ClientWorld world = (ClientWorld) context.getWorld();
        BlockPos crystalPos = context.getBlockPos().up();
        double x = crystalPos.getX() + 0.5;
        double y = crystalPos.getY();
        double z = crystalPos.getZ() + 0.5;

        ClientFastEndCrystalEntity endCrystalEntity = new ClientFastEndCrystalEntity(world, x, y, z, ClientSidedCrystalsClient.newCrystalId());
        endCrystalEntity.setShowBottom(false);
        world.addEntity(id--, endCrystalEntity);
    }

}
