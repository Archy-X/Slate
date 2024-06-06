package dev.aurelium.slate.fill;

import dev.aurelium.slate.inv.content.SlotPos;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;

public class SlotParser {

    public SlotPos[] parse(ConfigurationNode section, int size) {
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
        } else if (!section.node("shape").virtual()) {
            String shape = section.node("shape").getString("border");
            switch (shape) {
                case "border" -> {
                    List<SlotPos> slots = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        slots.add(SlotPos.of(0, i));
                        slots.add(SlotPos.of(size - 1, i));
                    }
                    for (int r = 1; r < size - 1; r++) {
                        slots.add(SlotPos.of(r, 0));
                        slots.add(SlotPos.of(r, 8));
                    }
                    return slots.toArray(new SlotPos[0]);
                }
                case "top_row" -> {
                    SlotPos[] slots = new SlotPos[9];
                    for (int i = 0; i < 9; i++) {
                        slots[i] = SlotPos.of(0, i);
                    }
                    return slots;
                }
                case "bottom_row" -> {
                    SlotPos[] slots = new SlotPos[9];
                    for (int i = 0; i < 9; i++) {
                        slots[i] = SlotPos.of(size - 1, i);
                    }
                    return slots;
                }
                case "left_column" -> {
                    SlotPos[] slots = new SlotPos[size];
                    for (int i = 0; i < size; i++) {
                        slots[i] = SlotPos.of(i, 0);
                    }
                    return slots;
                }
                case "right_column" -> {
                    SlotPos[] slots = new SlotPos[size];
                    for (int i = 0; i < size; i++) {
                        slots[i] = SlotPos.of(i, 8);
                    }
                    return slots;
                }
            }
        }
        return null;
    }

}
