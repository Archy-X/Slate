package dev.aurelium.slate.position;

import dev.aurelium.slate.inv.content.SlotPos;

import java.util.Collection;

public record FixedPosition(SlotPos pos) implements PositionProvider {

    @Override
    public SlotPos getPosition(Collection<PositionProvider> positionData) {
        return pos;
    }
}
