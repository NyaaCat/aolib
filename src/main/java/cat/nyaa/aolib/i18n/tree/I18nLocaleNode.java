package cat.nyaa.aolib.i18n.tree;

import java.util.Locale;

public class I18nLocaleNode extends AbstractI18nMidNode<I18nPluginNode> {
    final java.util.Locale locale;

    public I18nLocaleNode(Locale locale) {
        super(locale.toLanguageTag(), I18nPluginNode.class);
        this.locale = locale;
    }

    @Override
    public String getName() {
        return locale.toLanguageTag();
    }


}
