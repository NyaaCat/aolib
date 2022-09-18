package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.MethodAccessor;
import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedRegistry;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;

public class WrappedClientboundOpenScreenPacket extends AbstractWrappedPacket { //todo need test
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.OPEN_WINDOW;

    private static final MethodAccessor ID_GET;
    private static final Class<?> regClass;
    private static final Object regHandle;

    static {
        regClass = MinecraftReflection.getIRegistry();
        FuzzyReflection fuzzy = FuzzyReflection.fromClass(regClass, false);
        ID_GET = Accessors.getMethodAccessor(fuzzy.getMethod(FuzzyMethodContract
                .newBuilder()
                .parameterCount(1)
                .returnDerivedOf(Object.class)
                .requireModifier(Modifier.ABSTRACT)
                .parameterExactType(int.class)
                .build()));
        try {
            var obj = WrappedRegistry.getRegistry(getMenuTypeClass());
            var handleField = obj.getClass().getField("handle");
            handleField.setAccessible(true);
            regHandle = obj.getClass().getField("handle").get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected WrappedClientboundOpenScreenPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundOpenScreenPacket(int windowId, int type, WrappedChatComponent title) {
        this(createPacket(windowId, type, title));
    }

    private static @NotNull PacketContainer createPacket(int windowId, int type, WrappedChatComponent title) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);
        packetContainer.getIntegers().write(0, windowId);
        // Containers
        packetContainer.getModifier().write(1,ID_GET.invoke(regHandle,type)); //todo fix it
        packetContainer.getChatComponents().write(0, title);
        return packetContainer;

    }

    private static Class<?> getMenuTypeClass() {
        return MinecraftReflection.getNullableNMS("net.minecraft.world.inventory.MenuType", "net.minecraft.world.inventory.Containers", "MenuType", "Containers");
    }

}
