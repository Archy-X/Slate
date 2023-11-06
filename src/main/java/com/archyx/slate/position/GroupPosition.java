package com.archyx.slate.position;

import com.archyx.slate.context.ContextGroup;
import com.archyx.slate.context.GroupAlign;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GroupPosition implements PositionProvider {

    private final ContextGroup group;
    private final int order;

    public GroupPosition(ContextGroup group, int order) {
        this.group = group;
        this.order = order;
    }

    public ContextGroup getGroup() {
        return group;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public SlotPos getPosition(Collection<PositionProvider> positionData) {
        // Get the group providers in the same group
        List<GroupPosition> providers = positionData.stream()
                .filter(p -> p instanceof GroupPosition)
                .map(p -> (GroupPosition) p)
                .filter(p -> {
                    Bukkit.getLogger().info("Checking if group " + p.getGroup().hashCode() + " equals " + group.hashCode());
                    return p.getGroup().equals(group);
                })
                .sorted(Comparator.comparingInt(GroupPosition::getOrder)) // Sort by ascending order
                .collect(Collectors.toList());

        int size = providers.size();
        Bukkit.getLogger().info("Filtered provider size: " + size);
        int startRow = group.getStart().getRow();
        int startCol = group.getStart().getColumn();
        int endRow = group.getEnd().getRow();
        int endCol = group.getEnd().getColumn();

        // Get the index of the current group in the list of providers
        int index = providers.size();
        for (int i = 0; i < providers.size(); i ++) {
            if (providers.get(i).getOrder() == order) {
                index = i;
            }
        }

        GroupUtil groupUtil = new GroupUtil(size, startRow, startCol, endRow, endCol);
        if (group.getAlign() == GroupAlign.CENTER) {
            List<SlotPos> slots = groupUtil.getCenterSlots();
            if (index < slots.size()) {
                Bukkit.getLogger().info("Returning with index " + index);
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
