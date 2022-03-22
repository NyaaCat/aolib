package cat.nyaa.aolib.utils;

import cat.nyaa.nyaacore.utils.EntityUtils;

import java.util.Optional;

public class EntityDataUtils {
    public static int PLAYER_ENTITY_DATA_PLAYER_MODE_CUSTOMISATION_ID;

    public static void init() {
        Optional<Integer> optionalInteger;
        optionalInteger = EntityUtils.getPlayerEntityDataId("DATA_PLAYER_MODE_CUSTOMISATION");
        if (optionalInteger.isEmpty())
            throw new RuntimeException("can not get entity data:" + "DATA_PLAYER_MODE_CUSTOMISATION");
        PLAYER_ENTITY_DATA_PLAYER_MODE_CUSTOMISATION_ID = optionalInteger.get();
    }
}
