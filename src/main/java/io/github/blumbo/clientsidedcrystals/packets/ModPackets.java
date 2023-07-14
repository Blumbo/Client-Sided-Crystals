package io.github.blumbo.clientsidedcrystals.packets;

import io.github.blumbo.clientsidedcrystals.ClientSidedCrystals;
import io.github.blumbo.clientsidedcrystals.packets.c2s.FastHitCrystalC2SPacket;
import io.github.blumbo.clientsidedcrystals.packets.c2s.FastHitFastCrystalC2SPacket;
import io.github.blumbo.clientsidedcrystals.packets.c2s.PlaceFastCrystalC2SPacket;
import io.github.blumbo.clientsidedcrystals.packets.s2c.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets {

    public static Identifier MOD_ENABLE_ID = createIdentifier("mod_enable");
    public static Identifier MOD_DISABLE_ID = createIdentifier("mod_disable");

    public static Identifier PLACE_FAST_CRYSTAL_SUCCESS_ID = createIdentifier("place_fast_crystal_success");
    public static Identifier PLACE_FAST_CRYSTAL_CANCEL_ID = createIdentifier("place_fast_crystal_cancel");
    public static Identifier FAST_HIT_FAST_CRYSTAL_CANCEL_ID = createIdentifier("fast_hit_fast_crystal_cancel");

    public static Identifier PLACE_FAST_CRYSTAL_ID = createIdentifier("place_fast_crystal");
    public static Identifier FAST_HIT_CRYSTAL_ID = createIdentifier("fast_hit_crystal");
    public static Identifier FAST_HIT_FAST_CRYSTAL_ID = createIdentifier("fast_hit_fast_crystal");

    private static Identifier createIdentifier(String name) {
        return new Identifier(ClientSidedCrystals.MOD_ID + ":" + name);
    }

    public static void registerS2C() {
        ClientPlayNetworking.registerGlobalReceiver(MOD_ENABLE_ID, ModEnableS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(MOD_DISABLE_ID, ModDisableS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(PLACE_FAST_CRYSTAL_SUCCESS_ID, PlaceFastCrystalSuccessS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(PLACE_FAST_CRYSTAL_CANCEL_ID, PlaceFastCrystalCancelS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(FAST_HIT_FAST_CRYSTAL_CANCEL_ID, FastHitFastCrystalCancelS2CPacket::receive);
    }

    public static void registerC2S() {
        ServerPlayNetworking.registerGlobalReceiver(PLACE_FAST_CRYSTAL_ID, PlaceFastCrystalC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(FAST_HIT_CRYSTAL_ID, FastHitCrystalC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(FAST_HIT_FAST_CRYSTAL_ID, FastHitFastCrystalC2SPacket::receive);
    }

}
