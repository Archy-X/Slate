package com.archyx.slate.fill;

import fr.minuskube.inv.content.SlotPos;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public class SlotParser {

    public SlotPos[] parse(ConfigurationNode section) {
        List<?> slotList = (List<?>) section.node("slots").raw();
        if (slotList != null) {
            SlotPos[] slots = new SlotPos[slotList.size()];
            int index = 0;
            for (Object element : slotList) {
                if (element instanceof Integer) {
                    int value = (int) element;
                    slots[index] = SlotPos.of(value / 9, value % 9);
                } else if (element instanceof String str) {
                    String[] splitStr = str.split(",");
                    if (splitStr.length > 1) {
                        slots[index] = SlotPos.of(Integer.parseInt(splitStr[0]), Integer.parseInt(splitStr[1]));
                    }
                }
                index++;
            }
            return slots;
        } else {
            return null;
        }
    }

}
