package cat.nyaa.aolib.i18n.tree;

public class I18nEmptyNode extends AbstractI18nEndNode<Void> {
    public I18nEmptyNode(String NodeName) {
        super(NodeName);
    }

    @Override
    public Void getValue() {
        return null;
    }


    @Override
    public String nodeInfo() {
        return getName()+":[EMPTY]";
    }
}
