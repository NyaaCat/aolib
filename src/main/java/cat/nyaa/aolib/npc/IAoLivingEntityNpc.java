package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundAddMobPacket;
import cat.nyaa.aolib.npc.data.NpcEquipmentData;

public interface IAoLivingEntityNpc extends IAoEntityNpc {
    @Override
    default float getYHeadRot() {
        return this.getYRot();
    }

    default AbstractWrappedPacket getAddEntityPacket() {
        return new WrappedClientboundAddMobPacket(this);
    }

    default NpcEquipmentData getNpcEquipmentData() {
        return new NpcEquipmentData();
    }
}
