package com.archyx.slate.lore;

import com.archyx.slate.Slate;
import com.archyx.slate.lore.parser.LoreParser;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LoreFactory {

    private final Slate slate;

    public LoreFactory(Slate slate) {
        this.slate = slate;
    }

    public List<LoreLine> getLore(ConfigurationNode config) {
        List<LoreLine> lines = new ArrayList<>();
        if (!config.isList()) {
            return lines;
        }
        for (ConfigurationNode lineNode : config.childrenList()) {
            LoreLine loreLine = buildLoreLine(lineNode);
            if (loreLine != null) {
                lines.add(loreLine);
            }
        }
        return lines;
    }

    @Nullable
    private LoreLine buildLoreLine(ConfigurationNode config) {
        try {
            LoreType type = getType(config);

            Class<? extends LoreParser> parserClass = type.getParserClass();

            LoreParser parser = parserClass.newInstance();
            return parser.parse(config);
        } catch (Exception e) {
            slate.getPlugin().getLogger().warning("Error building lore line: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private LoreType getType(ConfigurationNode config) {
        if (!config.isMap()) {
            return LoreType.TEXT;
        }
        String type = config.node("type").getString();
        Objects.requireNonNull(type);

        return LoreType.valueOf(type.toUpperCase(Locale.ROOT));
    }

}
