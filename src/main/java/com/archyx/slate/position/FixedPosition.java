package com.archyx.slate.position;

import fr.minuskube.inv.content.SlotPos;

import java.util.Collection;

public class FixedPosition implements PositionProvider {

    private final SlotPos pos;

    public FixedPosition(SlotPos pos) {
        this.pos = pos;
    }

    public SlotPos getPos() {
        return pos;
    }

    @Override
    public SlotPos getPosition(Collection<PositionProvider> positionData) {
        return pos;
    }
}
