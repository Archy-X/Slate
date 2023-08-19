package com.archyx.slate.lore.parser;

import com.archyx.slate.lore.LoreLine;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public interface LoreParser {

    LoreLine parse(ConfigurationNode config) throws SerializationException;

}
