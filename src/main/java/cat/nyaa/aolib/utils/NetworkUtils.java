package cat.nyaa.aolib.utils;

import cat.nyaa.nyaacore.utils.EntityUtils;
import org.bukkit.entity.EntityType;

import java.util.Optional;

public class NetworkUtils {
    public static final double ACC_MAGICAL_QUANTIZATION = 8000.0D;
    public static final double ACC_LIMIT = 3.9D;

    public static int power2acceleration(double power) { // FOR NETWORK PACKET
        //[-3.9,3.9]
        return (int) (Math.min(Math.max(power, -ACC_LIMIT), ACC_LIMIT) * ACC_MAGICAL_QUANTIZATION);
    }

    public static int rot2int(float rot) {// FOR NETWORK PACKET
        return (int)Math.floor(rot * 256.0F / 360.0F);
    }
    public static byte rot2byte(float rot) {// FOR NETWORK PACKET
        return (byte) rot2int(rot);
    }
    public static int tryToGetEntityTypeId(EntityType entityType) throws RuntimeException{
        Optional<Integer> typeID = EntityUtils.getEntityTypeId(entityType);
        if (typeID.isEmpty()) throw new RuntimeException("can not get type id by: " + entityType.name());
        return typeID.get();
    }
}
