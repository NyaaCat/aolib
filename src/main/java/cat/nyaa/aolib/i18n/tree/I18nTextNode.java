package cat.nyaa.aolib.i18n.tree;

public class I18nTextNode extends AbstractI18nEndNode<String> {

    private final String text;

    public I18nTextNode(String name, String text) {
        super(name);
        this.text = text;
    }


    public String getValue() {
        return text;
    }

    @Override
    public String nodeInfo() {
        return getName()+":"+getValue();
    }
}
