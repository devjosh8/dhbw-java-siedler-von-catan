package de.svenojo.catan.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import de.svenojo.catan.player.Player;
import de.svenojo.catan.world.WorldMap;
import de.svenojo.catan.world.building.Building;
import de.svenojo.catan.world.building.BuildingType;
import de.svenojo.catan.world.building.buildings.BuildingCity;
import de.svenojo.catan.world.building.buildings.BuildingSettlement;
import de.svenojo.catan.world.building.buildings.BuildingStreet;
import de.svenojo.catan.world.material.MaterialType;
import de.svenojo.catan.world.tile.Tile;
import de.svenojo.catan.world.util.HighlightingType;
import lombok.Getter;
import lombok.Setter;

public class CatanGameLogic {


    // TODO: Phase hinzufügen, bei der mit Häfen getauscht werden kann
    // in dieser Phase wird der Klick auf Nodes abgefangen -> dann überprüfen ob Node einen hafen hat & dem Spieler gehört
    // dann kann er traden. die Phase kann mit einem Button übersprungen werden; bitte hinzufügen ~ josh


    enum GameState {
        PRE_GAME,
        SETTLE_PLAYERS,
        GAME,
        GAME_ENDING
    }

    enum RoundPhase {
        DICE_ROLL,
        MATERIAL_DISTRIBUTION,
        ROBBER,
        BUILD;

        public NextPhaseResult next() {
            int nextOrdinal = this.ordinal() + 1;
            RoundPhase[] phases = RoundPhase.values();

            if (nextOrdinal < phases.length) {
                return new NextPhaseResult(phases[nextOrdinal], false);
            } else {
                return new NextPhaseResult(phases[0], true);
            }
        }
    }

    private WorldMap worldMap;

    private final DiceRoll diceRoll = new DiceRoll();

    private GameState currentGameState;
    private RoundPhase currentRoundPhase;

    @Getter
    @Setter
    private boolean playerPlacingBuilding = false;
    @Getter
    private boolean playerPlacingRobber = false;
    private BuildingType playerPlacingBuildingType = null;
    private Queue<BuildingType> buildingQueue = new LinkedList<>();

    private List<Player> players;
    private int currentPlayerIndex;

    private int rolledNumber;

    public CatanGameLogic(List<Player> players, WorldMap worldMap) {
        this.players = players;
        this.worldMap = worldMap;
        this.currentGameState = GameState.PRE_GAME;
        this.currentRoundPhase = RoundPhase.DICE_ROLL;
        this.currentPlayerIndex = 0;
        this.rolledNumber = -1; // No dice rolled yet

        letCurrentPlayerPlaceRobber();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % players.size();
    }

    public void nextRoundPhase() {
        NextPhaseResult result = this.currentRoundPhase.next();
        currentRoundPhase = result.getNextPhase();
        if (result.isFullRoundCompleted())
            nextPlayer();
    }

    public void letCurrentPlayerPlaceRobber() {
        worldMap.setHighlightingType(HighlightingType.TILE);
        playerPlacingRobber = true;

        worldMap.getBandit().getPosition().ifPresent(previousTile -> previousTile.setRobberPlaced(false));
    }

    public void letCurrentPlayerPlaceBuilding(BuildingType buildingType) {
        buildingQueue.add(buildingType);
        if (!playerPlacingBuilding) {
            letCurrentPlayerPlaceNextBuilding();
        }
    }

    /**
     * 
     * @return true -> finished all buildings, false -> still has to build
     */
    private boolean letCurrentPlayerPlaceNextBuilding() {
        if (buildingQueue.isEmpty()) {
            playerPlacingBuilding = false;
            playerPlacingBuildingType = null;
            worldMap.setHighlightingType(HighlightingType.NONE);
            return true;
        }
        playerPlacingBuilding = true;
        playerPlacingBuildingType = buildingQueue.poll();
        worldMap.setHighlightingType(HighlightingType.fromBuildingType(playerPlacingBuildingType));
        return false;
    }

    public void onMouseTouchDown() {
        if(playerPlacingRobber) {
            if(worldMap.getCurrentlyHighlightedTile().isPresent()) {
                playerPlacingRobber = false;
                worldMap.setHighlightingType(HighlightingType.NONE);
                Tile destinedTile = worldMap.getCurrentlyHighlightedTile().get();
                destinedTile.setRobberPlaced(true);
                worldMap.placeBandit(destinedTile);
            }
            return;
        }
        if (playerPlacingBuilding) {
            Building building = switch (playerPlacingBuildingType) {
                case SETTLEMENT ->
                    new BuildingSettlement(getCurrentPlayer(), worldMap.getCurrentlyHighlightedNode().get());
                case CITY -> new BuildingCity(getCurrentPlayer(), worldMap.getCurrentlyHighlightedNode().get());
                case STREET -> new BuildingStreet(getCurrentPlayer(), worldMap.getCurrentlyHighlightedEdge().get());
                default -> null;
            };
            if (building == null)
                throw new Error("Building type " + playerPlacingBuildingType.toString() + "is not handled");

            worldMap.placeBuilding(getCurrentPlayer(), building);
            boolean finishedBuilding = letCurrentPlayerPlaceNextBuilding();
            if (finishedBuilding) {
                //Continue game loop
            }
        }
    }
    

    public void tradeWithBank(Player player, MaterialType typeToGive, MaterialType typeToReceive) {
        if(player.getMaterialCount(typeToGive) >= 4) {
            player.addMaterial(typeToGive, -4);
            player.addMaterial(typeToReceive, 1);
        }
    }
    

    public void playRoundPhase() {
        switch (currentRoundPhase) {
            case DICE_ROLL:
                this.rolledNumber = diceRoll.rollBothDice();
                break;
            case MATERIAL_DISTRIBUTION:
                if (this.rolledNumber == 7)
                    break;

                // Annahme rolled number ist zahl => herausfinden, welches feld diese zahl hat
                for(Tile tile : worldMap.getMapTiles()) {
                    if(tile.getNumberValue() == this.rolledNumber) {
                        MaterialType materialToGivePlayers = tile.getWorldTileType().getMaterialType();
                        for(Player p : players) {
                            p.addMaterial(materialToGivePlayers, 1);
                        }
                        break;
                    }
                }
                break;
            case ROBBER:
                if (this.rolledNumber != 7)
                    break;

                break;
            case BUILD:

                break;

            default:
                System.err.println("The current RoundPhase " + currentRoundPhase.toString() + " cannot be played.");
                break;
        }
    }
}
