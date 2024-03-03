package com.archyx.slate.position;

import fr.minuskube.inv.content.SlotPos;

import java.util.Collection;

public interface PositionProvider {

    SlotPos getPosition(Collection<PositionProvider> positionData);

}
