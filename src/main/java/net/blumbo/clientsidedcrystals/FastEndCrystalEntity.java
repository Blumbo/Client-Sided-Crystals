package net.blumbo.clientsidedcrystals;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

public class FastEndCrystalEntity extends EndCrystalEntity {

    public ServerPlayerEntity crystalOwner;
    public int ownerCrystalId;

    public FastEndCrystalEntity(World world, double x, double y, double z, ServerPlayerEntity crystalOwner, int ownerCrystalId) {
        super(world, x, y, z);
        this.crystalOwner = crystalOwner;
        this.ownerCrystalId = ownerCrystalId;

        ClientSidedCrystals.fastEndCrystals.get(crystalOwner.getUuid()).put(ownerCrystalId, this);
    }

    public FastEndCrystalEntity(EndCrystalEntity og, ServerPlayerEntity crystalOwner, int ownerCrystalId) {
        this(og.getWorld(), og.getX(), og.getY(), og.getZ(), crystalOwner, ownerCrystalId);
        this.setShowBottom(og.shouldShowBottom());
    }

    public void setRemoved(Entity.RemovalReason reason) {
        UUID ownerUUID = crystalOwner.getUuid();
        if (ClientSidedCrystals.fastEndCrystals.containsKey(ownerUUID)) {
            ClientSidedCrystals.fastEndCrystals.get(ownerUUID).remove(ownerCrystalId);
        }
        super.setRemoved(reason);
    }

}
