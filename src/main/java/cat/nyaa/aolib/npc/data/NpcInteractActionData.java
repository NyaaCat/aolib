package cat.nyaa.aolib.npc.data;

import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record NpcInteractActionData(@Nullable actionEnum action, @Nullable handEnum hand, @Nullable Vector position) {


     public  static NpcInteractActionData create(@NotNull WrappedEnumEntityUseAction wrappedEnumEntityUseAction) {
        switch (wrappedEnumEntityUseAction.getAction()) {
            case INTERACT -> {
                return new NpcInteractActionData(actionEnum.INTERACT, handEnum.valueOf(wrappedEnumEntityUseAction.getHand().name()), null);
            }
            case ATTACK -> {
                return new NpcInteractActionData(actionEnum.ATTACK, null, null);
            }
            case INTERACT_AT -> {
                return new NpcInteractActionData(actionEnum.INTERACT_AT, handEnum.valueOf(wrappedEnumEntityUseAction.getHand().name()), wrappedEnumEntityUseAction.getPosition());
            }
            default -> {
                return new NpcInteractActionData(null, null, null);
            }
        }
    }

    @Nullable
    public actionEnum getAction() {
        return action;
    }

    @Nullable
    public handEnum getHand() {
        return hand;
    }

    @Nullable
    public Vector getPosition() {
        return position;
    }

    enum actionEnum {
        INTERACT,
        ATTACK,
        INTERACT_AT
    }

    public enum handEnum {
        MAIN_HAND,
        OFF_HAND
    }
}
