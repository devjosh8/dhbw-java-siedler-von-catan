package de.svenojo.util;

import java.util.List;

import de.svenojo.catan.player.Player;

public class PlayerOptions {
    private List<Player> playerList;

      public PlayerOptions(List<Player> playerList) {
        this.playerList = playerList;
    }

    public List<Player> getplayerList() {
        return playerList;
    }
}
