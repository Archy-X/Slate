package dev.aurelium.slate.action.condition;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.util.YamlLoader;
import org.spongepowered.configurate.ConfigurationNode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ConditionParser {

    private final Slate slate;

    public ConditionParser(Slate slate) {
        this.slate = slate;
    }

    public List<Condition> parseConditions(ConfigurationNode config, String menuName) {
        List<Condition> conditions = new ArrayList<>();
        int index = 0;
        for (ConfigurationNode condNode : config.childrenList()) {
            try {
                String type = Objects.requireNonNull(detectType(condNode));
                if (type.equals("permission")) {
                    conditions.add(parsePermissionCondition(condNode));
                }
            } catch (RuntimeException e) {
                slate.getPlugin().getLogger().warning("Error parsing condition in menu " + menuName + " at path " + YamlLoader.toDotString(config.path()) + ".[" + index + "], see below for error:");
                e.printStackTrace();
            }
            index++;
        }
        return conditions;
    }

    private PermissionCondition parsePermissionCondition(ConfigurationNode config) {
        return new PermissionCondition(slate,
                Objects.requireNonNull(config.node("permission").getString()),
                config.node("value").getBoolean(true));
    }

    @Nullable
    private String detectType(ConfigurationNode node) {
        String type = node.node("type").getString();
        if (type != null) {
            return type.toLowerCase(Locale.ROOT);
        }
        // Auto detection
        if (!node.node("permission").virtual()) {
            return "permission";
        }
        return null;
    }

}
