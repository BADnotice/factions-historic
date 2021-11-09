package io.github.badnotice.factionshistoric.player;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "playerName")
@Data
public final class PlayerHistoric {

    private final String playerName;

    private final String factionTag;
    private final String factionName;
    private final String dateCreated;

}
