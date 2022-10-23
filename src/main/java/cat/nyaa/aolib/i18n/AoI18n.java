package cat.nyaa.aolib.i18n;

import cat.nyaa.aolib.i18n.tree.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AoI18n {
    private static final Gson gson = new GsonBuilder().create();

    private static final I18nRootNode tree = new I18nRootNode();
    //root->local->plugin->mid->text

    public static void load(Plugin plugin) {
        try {
            for (Locale availableLocale : Locale.getAvailableLocales()) {
                load(plugin, availableLocale);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load(Plugin plugin, java.util.Locale locale) throws IOException {
        List<String> languageTags = Lists.newArrayList(locale.toLanguageTag(), locale.toLanguageTag().replace('-', '_'));
        for (String languageTag : languageTags) {
            var res = plugin.getResource("lang/" + languageTag + ".json");
            if (res != null) {
                put(plugin, locale, readJson(res).toArray(new II18nNode[0]));
                return;
            }
        }
        for (String languageTag : languageTags) {
            var res = plugin.getResource("lang/" + languageTag + ".yml");
            if (res == null) {
                res = plugin.getResource("lang/" + languageTag + ".yaml");
            }
            if (res != null) {
                put(plugin, locale, readYaml(res).toArray(new II18nNode[0]));
                return;
            }
        }
    }

    private static List<II18nNode> readYaml(InputStream res) throws IOException {
        String str = new String(res.readAllBytes());
        var yaml = new Yaml();
        var i18nYml = yaml.load(str);
        if (i18nYml instanceof Map<?, ?> map) {
            return readMap(map);
        }
        return Lists.newArrayList();
    }

    private static List<II18nNode> readMap(Map<?, ?> map) {
        List<II18nNode> result = new ArrayList<>();
        map.forEach((k, v) -> {
            if (k instanceof String subNodeName) {
                if (v instanceof Map<?, ?> subMap) {
                    var node = new I18nMidNode(subNodeName);
                    readMap(subMap).forEach(node::put);
                    result.add(node);
                } else if (v instanceof String text) {
                    result.add(new I18nTextNode(subNodeName, text));
                } else {
                    result.add(new I18nEmptyNode(subNodeName));
                }
            }
        });
        return result;
    }

    private static List<II18nNode> readJson(InputStream res) throws IOException {
        return readMap(gson.fromJson(new String(res.readAllBytes()), Map.class));
    }

    private static void put(Plugin plugin, java.util.Locale locale, II18nNode... node) {
        I18nLocaleNode localeNode = tree.getChild(locale.toLanguageTag());
        if (localeNode == null) {
            localeNode = new I18nLocaleNode(locale);
            tree.put(localeNode);
        }
        var pluginNode = localeNode.getChild(plugin.getName());
        if (pluginNode == null || pluginNode.getPlugin() != plugin) {
            pluginNode = new I18nPluginNode(plugin);
            localeNode.put(pluginNode);
        }
        for (II18nNode i18nNode : node) {
            pluginNode.put(i18nNode);
        }
    }

    public static String treeInfo() {
        return tree.treeInfo();
    }

    public static String sort() {
        tree.sort();
        return "success";
    }
}
