package cat.nyaa.aolib.i18n.tree;

import org.bukkit.plugin.Plugin;

public class I18nPluginNode extends AbstractI18nMidNode<II18nNode> {
    private final Plugin plugin;

    public I18nPluginNode(Plugin plugin) {
        super(plugin.getName(), II18nNode.class);
        this.plugin = plugin;
    }


    public Plugin getPlugin() {
        return plugin;
    }
}
