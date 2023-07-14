package io.github.blumbo.clientsidedcrystals.client;

import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.world.World;

public class ClientFastEndCrystalEntity extends EndCrystalEntity {

    public int ownerCrystalId;

    public ClientFastEndCrystalEntity(World world, double x, double y, double z, int ownerCrystalId) {
        super(world, x, y, z);
        this.ownerCrystalId = ownerCrystalId;
        ClientSidedCrystalsClient.clientCrystals.put(ownerCrystalId, this);
    }
}
