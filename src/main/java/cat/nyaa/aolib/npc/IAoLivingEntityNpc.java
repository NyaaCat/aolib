package cat.nyaa.aolib.npc;

public interface IAoLivingEntityNpc extends IAoEntityNpc {
    @Override
    default float getYHeadRot() {
        return this.getYRot();
    }
}
