package cat.nyaa.aolib.i18n.tree;

import java.util.Objects;

public abstract class AbstractI18nEndNode<T> implements II18nNode {


    private final String NodeName;

    public AbstractI18nEndNode(String NodeName) {
        this.NodeName = NodeName;
    }

    public String getName() {
        return this.NodeName;
    }


    public abstract T getValue();


}
