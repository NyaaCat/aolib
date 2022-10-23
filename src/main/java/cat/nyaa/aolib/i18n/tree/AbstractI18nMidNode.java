package cat.nyaa.aolib.i18n.tree;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractI18nMidNode<T extends II18nNode> implements II18nNode {
    private final String nodeName;
    private final Class<T> childType;
    private final List<T> children = Lists.newArrayList();
    private final Set<String> childNames = new HashSet<>();

    protected AbstractI18nMidNode(String nodeName, Class<T> childType) {
        this.nodeName = nodeName;
        this.childType = childType;
    }

    private String treeInfoPrefix(List<Integer> layers) {
        StringBuilder builder = new StringBuilder();
        for (int i = layers.size() - 1; i >= 0; i--) {
            var depth = layers.get(i);
            if (i == 0 && depth == 1) {
                builder.append(" `");
            } else if (depth >= 1) {
                builder.append(" |");
            } else {
                builder.append("  ");
            }
        }

        if (layers.size() != 0) {
            builder.append("--");
        } else {
            builder.append("  ");
        }
        return builder.toString();
    }

    public String treeInfo() {
        return this.nodeInfo() + "\n" + treeInfo(Lists.newLinkedList());
    }

    private String treeInfo(LinkedList<Integer> layers) {
        StringBuilder builder = new StringBuilder();
        layers.push(getChildren().length);
        for (II18nNode node : getChildren()) {
            builder.append(treeInfoPrefix(layers)).append(node.nodeInfo()).append("\n");
            layers.push(layers.pop() - 1);
            if (node instanceof AbstractI18nMidNode<?> midNode) {//child node
                builder.append(midNode.treeInfo(layers));
            }
        }
        layers.pop();
        return builder.toString();
    }

    @Override
    public String nodeInfo() {
        return "[" + getName() + "]";
    }

    public void sort() {
        sortByName(String::compareTo);
    }

    public void sortByName(Comparator<String> c) {
        children.forEach(child -> {
            if (child instanceof AbstractI18nMidNode<?> node) node.sortByName(c);
        });
        children.sort((v1, v2) -> c.compare(v1.getName(), v2.getName()));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void put(T node) {
        String nodeName = node.getName();
        if (this.equals(node)) {
            throw new RuntimeException("can't contain itself");
        }
        if (this.contains(node)) {
            throw new RuntimeException("node:" + node.getName() + " already exists in " + this.getName());
        }
        if (childNames.contains(nodeName)) {
            if (this.getChild(nodeName) instanceof AbstractI18nMidNode<?> subNode && node instanceof AbstractI18nMidNode<?> sub2) {
                if (subNode.getChildType().equals(sub2.getChildType())) {
                    subNode.marge((AbstractI18nMidNode) sub2);
                    return;
                }
            }
            throw new RuntimeException("can not marge node:" + node.getName() + " type not allowed in " + this.getName());
        } else {
            children.add(node);
            childNames.add(nodeName);
        }
    }

    public void marge(AbstractI18nMidNode<T> parentNode) {
        parentNode.children.forEach(this::put);
    }

    public II18nNode[] getChildren() {
        return children.toArray(new II18nNode[]{});
    }

    public boolean containsName(String otherNodeName) {
        return getAllChildNames().contains(otherNodeName);
    }

    public boolean contains(T otherNode) {
        return children.contains(otherNode);
    }

    public @Nullable T getChild(String name) {
        if (children.size() > 12) {
            if (!containsName(name)) return null;
        }
        for (T child : children) {
            if (Objects.equals(child.getName(), name)) return child;
        }
        return null;
    }

    public Set<String> getAllChildNames() {
        return new HashSet<>(childNames);
    }

    public Class<T> getChildType() {
        return childType;
    }

    @Override
    public String getName() {
        return this.nodeName;
    }
}
