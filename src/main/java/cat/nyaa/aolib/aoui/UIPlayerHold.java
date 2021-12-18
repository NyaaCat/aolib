package cat.nyaa.aolib.aoui;

import org.bukkit.entity.Player;

public class UIPlayerHold {
    private int stateId = 0;
    private final IBaseUI holdUI;
    private final Player player;

    public UIPlayerHold(IBaseUI holdUI, Player player) {
        this.holdUI = holdUI;
        this.player = player;
    }

    public void setStateId(int id) {//setItem,initializeContents
        this.stateId = id;
    }

    public int getStateId() {
        return stateId;
    }

    public int incrementStateId() {
        this.stateId = this.stateId + 1 & 32767;
        return this.stateId;
    }

    public IBaseUI getHoldUI() {
        return holdUI;
    }

    public Player getPlayer() {
        return player;
    }
}
