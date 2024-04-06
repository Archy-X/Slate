package com.archyx.slate.position;

import com.archyx.slate.context.ContextGroup;
import com.archyx.slate.context.GroupAlign;
import fr.minuskube.inv.content.SlotPos;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public record GroupPosition(ContextGroup group, int order) implements PositionProvider {

    @Override
    public SlotPos getPosition(Collection<PositionProvider> positionData) {
        // Get the group providers in the same group
        List<GroupPosition> providers = positionData.stream()
                .filter(p -> p instanceof GroupPosition)
                .map(p -> (GroupPosition) p)
                .filter(p -> p.group().equals(group))
                .sorted(Comparator.comparingInt(GroupPosition::order)) // Sort by ascending order
                .toList();

        int size = providers.size();
        int startRow = group.getStart().getRow();
        int startCol = group.getStart().getColumn();
        int endRow = group.getEnd().getRow();
        int endCol = group.getEnd().getColumn();

        // Get the index of the current group in the list of providers
        int index = providers.size();
        for (int i = 0; i < providers.size(); i++) {
            if (providers.get(i).order() == order) {
                index = i;
            }
        }

        GroupUtil groupUtil = new GroupUtil(size, startRow, startCol, endRow, endCol);
        if (group.getAlign() == GroupAlign.CENTER) {
            List<SlotPos> slots = groupUtil.getCenterSlots();
            if (index < slots.size()) {
                return slots.get(index);
            }
        } else if (group.getAlign() == GroupAlign.LEFT) {
            List<SlotPos> slots = groupUtil.getLeftSlots();
            if (index < slots.size()) {
                return slots.get(index);
            }
        } else if (group.getAlign() == GroupAlign.RIGHT) {
            List<SlotPos> slots = groupUtil.getRightSlots();
            if (index < slots.size()) {
                return slots.get(index);
            }
        }
        return SlotPos.of(startRow, startCol);
    }


}
