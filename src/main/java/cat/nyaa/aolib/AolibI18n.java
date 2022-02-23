package cat.nyaa.aolib;

import cat.nyaa.nyaacore.LanguageRepository;
import org.bukkit.plugin.Plugin;

public class AolibI18n extends LanguageRepository {
    private final AoLibPlugin plugin;
    private final String lang;

    public AolibI18n(AoLibPlugin plugin, String lang) {
        this.plugin = plugin;
        this.lang = lang;
        load();
    }

    @Override
    protected Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    protected String getLanguage() {
        return this.lang;
    }
}
