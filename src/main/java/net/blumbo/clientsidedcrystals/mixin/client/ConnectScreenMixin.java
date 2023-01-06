package net.blumbo.clientsidedcrystals.mixin.client;

import net.blumbo.clientsidedcrystals.client.ClientSidedCrystalsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {

    @Inject(method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;)V",
            at = @At("HEAD"))
    private void startConnect(MinecraftClient client, ServerAddress address, CallbackInfo ci) {
        ClientSidedCrystalsClient.serverHasMod = false;
    }
}
