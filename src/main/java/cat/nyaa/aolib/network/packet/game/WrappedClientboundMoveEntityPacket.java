package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;


public abstract class WrappedClientboundMoveEntityPacket extends AbstractWrappedPacket {
    protected WrappedClientboundMoveEntityPacket(PacketContainer handle, PacketType packetType) throws IllegalArgumentException {
        super(handle, packetType);
    }

    private WrappedClientboundMoveEntityPacket(PacketContainer handle) {
        super(handle, handle.getType());
    }

    protected WrappedClientboundMoveEntityPacket(int entityId, short xa, short ya, short za, byte yRot, byte xRot, boolean onGround, boolean hasRot, boolean hasPos) throws RuntimeException {
        this(createPacket(entityId, xa, ya, za, yRot, xRot, onGround, hasRot, hasPos));
    }

    public static double packetToEntity(long packetP) {
        return (double) packetP / 4096.0D;
    }

    public static long entityToPacket(double entityP) {
        return (long) Math.floor(entityP * 4096.0D);
    }
    public static @NotNull Vector packetToEntity(long xp, long yp, long zp) {
        return (new Vector((double)xp, (double)yp, (double)zp)).multiply((double)2.4414062E-4F);// x/4096
    }

    private static @NotNull PacketContainer createPacket(int entityId, short xa, short ya, short za, byte yRot, byte xRot, boolean onGround, boolean hasRot, boolean hasPos) throws RuntimeException {
        PacketType packetType;
        if (hasRot && hasPos) {
            packetType = PacketType.Play.Server.REL_ENTITY_MOVE_LOOK;
        } else if (hasRot) {
            packetType = PacketType.Play.Server.ENTITY_LOOK;
        } else if (hasPos) {
            packetType = PacketType.Play.Server.REL_ENTITY_MOVE;
        } else {
            throw new RuntimeException("createPacket error:can not get packet type.(at WrappedClientboundMoveEntityPacket)");
        }
        PacketContainer packetContainer = new PacketContainer(packetType);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getShorts().write(0, xa);
        packetContainer.getShorts().write(1, ya);
        packetContainer.getShorts().write(2, za);
        packetContainer.getBytes().write(0, yRot);
        packetContainer.getBytes().write(1, xRot);
        packetContainer.getBooleans().write(0, onGround);
        return packetContainer;
    }

    public static class Pos extends WrappedClientboundMoveEntityPacket {
        public Pos(int entityId, short xa, short ya, short za, boolean onGround) {
            super(entityId, xa, ya, za, (byte) 0, (byte) 0, onGround, false, true);
        }
    }

    public static class PosRot extends WrappedClientboundMoveEntityPacket {
        public PosRot(int entityId, short xa, short ya, short za, byte yRot, byte xRot, boolean onGround) {
            super(entityId, xa, ya, za, yRot, xRot, onGround, true, true);
        }
    }

    public static class Rot extends WrappedClientboundMoveEntityPacket {
        public Rot(int entityId, byte yRot, byte xRot, boolean onGround) {
            super(entityId, (short) 0, (short) 0, (short) 0, yRot, xRot, onGround, true, false);
        }
    }
}