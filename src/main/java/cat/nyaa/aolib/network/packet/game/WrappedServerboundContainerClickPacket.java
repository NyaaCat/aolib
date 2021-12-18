package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.data.DataClickType;
import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.inventory.ItemStack;

public class WrappedServerboundContainerClickPacket  extends AbstractWrappedPacket {

    public final static PacketType PACKET_TYPE = PacketType.Play.Client.WINDOW_CLICK;

    public WrappedServerboundContainerClickPacket(PacketContainer handle) {
        super(handle,PACKET_TYPE);
    }

    public int getContainerId() {
        return getPacket().getIntegers().read(0);
    }

    public int getStateId() {
        return getPacket().getIntegers().read(1);
    }

    public int getSlotNum() {
        return getPacket().getIntegers().read(2);
    }

    public int getButtonNum() {
        return getPacket().getIntegers().read(3);
    }

    public DataClickType getClickType() {
        return getPacket().getEnumModifier(DataClickType.class, 4).read(0);
    }

    public ItemStack getCarriedItem() {
        return  getPacket().getItemModifier().read(5);
    }

//    public Int2ObjectMap<ItemStack> getChangedSlots() {
//        return getPacket().getModifier().withParamType(MinecraftReflection.getInt2ObjectMapClass(), new EquivalentConverter<Int2ObjectMap<ItemStack>>() {
//
//            @Override
//            public Object getGeneric(Int2ObjectMap<ItemStack> specific) {
//                return null;
//            }
//
//            @Override
//            public Int2ObjectMap<ItemStack> getSpecific(Object generic) {
//                return null;
//            }
//
//            @Override
//            public Class<Int2ObjectMap<ItemStack>> getSpecificType() {
//                return null;
//            }
//        },MinecraftReflection.getItemStackClass()).read(0);
//    }
}
