package dev.aurelium.slate.position;

import dev.aurelium.slate.inv.content.SlotPos;

import java.util.Collection;

public interface PositionProvider {

    SlotPos getPosition(Collection<PositionProvider> positionData);

}
