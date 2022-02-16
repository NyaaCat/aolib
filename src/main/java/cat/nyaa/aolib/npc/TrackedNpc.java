package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.network.packet.game.WrappedClientboundMoveEntityPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundRotateHeadPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundTeleportEntityPacket;
import cat.nyaa.aolib.utils.NetworkUtils;

import java.lang.reflect.InvocationTargetException;

public class TrackedNpc {
    private final int updateInterval;
    private final NpcManager npcManager;
    private final IAoEntityNpc aoNpc;
    private long xp;
    private long yp;
    private long zp;
    private int yRotp;
    private int xRotp;
    private int yHeadRotp;
    private boolean wasOnGround;
    private int tickCount;

    TrackedNpc(int updateInterval, NpcManager npcManager, IAoEntityNpc aoNpc) {
        this.updateInterval = updateInterval;
        this.npcManager = npcManager;
        this.aoNpc = aoNpc;
        updateSentPos();
        this.yRotp = NetworkUtils.rot2int(aoNpc.getYRot());
        this.xRotp = NetworkUtils.rot2int(aoNpc.getXRot());
        this.yHeadRotp = NetworkUtils.rot2int(aoNpc.getYHeadRot());
        this.wasOnGround = aoNpc.isOnGround();
    }

    public void sendChanges() {
        // todo itemframe -map
        if (this.tickCount % 60 != 0 && this.tickCount % updateInterval != 0) {
            return;
        }
//        boolean forceFlash = false;
//        if (tickCount % 60 == 0) {
//            forceFlash = true;
//        }
        //todo force Flash
        int yrotp_ = NetworkUtils.rot2int(this.getAoNpc().getYRot());
        int xrotp_ = NetworkUtils.rot2int(this.getAoNpc().getYRot());
        boolean sendRot = Math.abs(yrotp_ - this.yRotp) >= 1 || Math.abs(xrotp_ - this.xRotp) >= 1;
        long xp_ = WrappedClientboundMoveEntityPacket.entityToPacket(aoNpc.getX());
        long yp_ = WrappedClientboundMoveEntityPacket.entityToPacket(aoNpc.getY());
        long zp_ = WrappedClientboundMoveEntityPacket.entityToPacket(aoNpc.getZ());
        long i = xp_ - xp;
        long j = yp_ - yp;
        long k = zp_ - zp;
        boolean flag = i < -32768L || i > 32767L || j < -32768L || j > 32767L || k < -32768L || k > 32767L;
        boolean sendPos = i != 0 || j != 0 || k != 0;
        if (flag) {
            npcManager.onNpcTeleport(this);
            npcManager.getNpcPlayerMap().getNpcHoldOnlinePlayer(aoNpc.getUniqueNpcName()).forEach(p ->
            {
                try {
                    new WrappedClientboundTeleportEntityPacket(aoNpc).sendServerPacket(p);
                } catch (InvocationTargetException ignored) {
                }
            });
        } else if (sendPos && sendRot) {
            npcManager.onNpcMove(this, true, true);
            npcManager.getNpcPlayerMap().getNpcHoldOnlinePlayer(aoNpc.getUniqueNpcName())
                    .forEach(player -> {
                        try {
                            new WrappedClientboundMoveEntityPacket.PosRot(aoNpc.getEntityId(), (short) xp_, (short) yp_, (short) zp_, (byte) yrotp_, (byte) xrotp_, aoNpc.isOnGround())
                                    .sendServerPacket(player);
                        } catch (InvocationTargetException ignored) {
                        }
                    });
            updateSentPos();
            this.yRotp = yrotp_;
            this.xRotp = xrotp_;
            this.wasOnGround = aoNpc.isOnGround();
        } else if (sendPos) {
            npcManager.onNpcMove(this, true, false);
            npcManager.getNpcPlayerMap().getNpcHoldOnlinePlayer(aoNpc.getUniqueNpcName()).forEach(
                    player -> {
                        try {
                            new WrappedClientboundMoveEntityPacket.Pos(aoNpc.getEntityId(), (short) xp_, (short) yp_, (short) zp_, aoNpc.isOnGround())
                                    .sendServerPacket(player);
                        } catch (InvocationTargetException ignored) {
                        }
                    }
            );
            updateSentPos();
            this.wasOnGround = aoNpc.isOnGround();
        } else if (sendRot) {
            npcManager.onNpcMove(this, false, true);
            npcManager.getNpcPlayerMap().getNpcHoldOnlinePlayer(aoNpc.getUniqueNpcName()).forEach(
                    player -> {
                        try {
                            new WrappedClientboundMoveEntityPacket.Rot(aoNpc.getEntityId(), (byte) yrotp_, (byte) xrotp_, aoNpc.isOnGround())
                                    .sendServerPacket(player);
                        } catch (InvocationTargetException ignored) {
                        }
                    }
            );
            this.yRotp = yrotp_;
            this.xRotp = xrotp_;
            this.wasOnGround = aoNpc.isOnGround();
        }

        int yHeadRotp_ = NetworkUtils.rot2int(this.aoNpc.getYHeadRot());
        if (Math.abs(yHeadRotp_ - this.yHeadRotp) >= 1) {
            npcManager.getNpcPlayerMap().getNpcHoldOnlinePlayer(aoNpc.getUniqueNpcName()).forEach(
                    player -> {
                        try {
                            new WrappedClientboundRotateHeadPacket(this.aoNpc.getEntityId(), (byte) yHeadRotp_).sendServerPacket(player);
                        } catch (InvocationTargetException ignored) {
                        }
                    }
            );
            this.yHeadRotp = yHeadRotp_;
        }
        this.tickCount++;

    }

    public IAoEntityNpc getAoNpc() {
        return aoNpc;
    }

    private void updateSentPos() {
        this.xp = WrappedClientboundMoveEntityPacket.entityToPacket(this.aoNpc.getX());
        this.yp = WrappedClientboundMoveEntityPacket.entityToPacket(this.aoNpc.getY());
        this.zp = WrappedClientboundMoveEntityPacket.entityToPacket(this.aoNpc.getZ());
    }
}
