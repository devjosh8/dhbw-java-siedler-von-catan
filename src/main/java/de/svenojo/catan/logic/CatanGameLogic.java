package de.svenojo.catan.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.svenojo.catan.logic.events.BuildCityEvent;
import de.svenojo.catan.logic.events.BuildSettlementEvent;
import de.svenojo.catan.logic.events.BuildStreetEvent;
import de.svenojo.catan.logic.events.EndTurnEvent;
import de.svenojo.catan.player.Player;
import de.svenojo.catan.screen.ui.GameUI;
import de.svenojo.catan.world.Edge;
import de.svenojo.catan.world.WorldMap;
import de.svenojo.catan.world.building.Building;
import de.svenojo.catan.world.building.BuildingType;
import de.svenojo.catan.world.building.NodeBuilding;
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
    // in dieser Phase wird der Klick auf Nodes abgefangen -> dann überprüfen ob
    // Node einen hafen hat & dem Spieler gehört
    // dann kann er traden. die Phase kann mit einem Button übersprungen werden;
    // bitte hinzufügen ~ josh

    public enum GameState {
        PRE_GAME,
        SETTLE_PLAYERS,
        GAME,
        GAME_ENDING
    }

    public enum RoundPhase {
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

    private final WorldMap worldMap;
    private final GameUI gameUI;
    private final EventBus gameScreenEventBus;

    private final DiceRoll diceRoll = new DiceRoll();

    @Getter
    private GameState currentGameState;
    @Getter
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

    public CatanGameLogic(List<Player> players, WorldMap worldMap, GameUI gameUI, EventBus gameScreenEventBus) {
        this.players = players;
        this.worldMap = worldMap;
        this.gameUI = gameUI;
        this.gameScreenEventBus = gameScreenEventBus;

        gameScreenEventBus.register(this);

        Gdx.app.log("DEBUG", "CatanGameLogic initialized with players: " + players.size());

        this.currentRoundPhase = null; // Will be set in playGameState()
        this.currentPlayerIndex = 0;
        this.rolledNumber = -1; // No dice rolled yet

        // This will start the game in the PRE_GAME state and the render will pick it up
        this.currentGameState = GameState.PRE_GAME;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % players.size();
        gameUI.updateCurrentPlayer(getCurrentPlayer().getName(), getCurrentPlayer().getColorString());
        gameUI.updateMaterials(getCurrentPlayer().getMaterials());
    }

    private boolean firstSettleRoundComplete = false;

    /**
     * 
     * @return true if the next player was set, false if the game state changed
     */
    public boolean nextPlayerDuringSettlementPhase() {
        if (!firstSettleRoundComplete && (currentPlayerIndex < (players.size() - 1))) {
            nextPlayer();
            return true;
        }
        if (!firstSettleRoundComplete && (currentPlayerIndex == (players.size() - 1))) {

            firstSettleRoundComplete = true;
            // Last player can settle again
            return true;
        }
        if (firstSettleRoundComplete && currentPlayerIndex == 0) {
            // First player settled before, so we start the game now
            currentGameState = GameState.GAME;
            playGameState();
            return false;
        }
        currentPlayerIndex = (currentPlayerIndex - 1);
        gameUI.updateCurrentPlayer(getCurrentPlayer().getName(), getCurrentPlayer().getColorString());
        return true;
    }

    public void nextRoundPhase() {
        NextPhaseResult result = this.currentRoundPhase.next();
        currentRoundPhase = result.getNextPhase();
        if (result.isFullRoundCompleted())
            nextPlayer();
        gameUI.getEndTurnButton().setVisible(false); // Hide the button after the round is done
        gameUI.getButtonTable().setVisible(false);
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
        if (playerPlacingRobber) {
            if (worldMap.getCurrentlyHighlightedTile().isPresent()) {
                playerPlacingRobber = false;
                worldMap.setHighlightingType(HighlightingType.NONE);
                Tile destinedTile = worldMap.getCurrentlyHighlightedTile().get();
                destinedTile.setRobberPlaced(true);
                worldMap.placeBandit(destinedTile);
            }
            nextRoundPhase();
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

            if (building instanceof BuildingStreet) {
                Edge edge = worldMap.getCurrentlyHighlightedEdge().get();
                if (!worldMap.canStreetBePlacedOnEdge(edge)) {
                    Gdx.app.log("DEBUG", "Cannot place street on edge: " + edge);
                    return;
                }
                // TODO: check if street connects to current players buildings
            }
            if (building instanceof NodeBuilding) {
                NodeBuilding nodeBuilding = (NodeBuilding) building;
                if (!worldMap.canNodeBuildingBePlaced(nodeBuilding)) {
                    Gdx.app.log("DEBUG", "Cannot place building on node: " + nodeBuilding);
                    return;
                }
            }

            worldMap.placeBuilding(getCurrentPlayer(), building);
            boolean finishedBuilding = letCurrentPlayerPlaceNextBuilding();
            if (finishedBuilding && currentGameState.equals(GameState.SETTLE_PLAYERS)) {
                boolean shouldLetSettle = nextPlayerDuringSettlementPhase();
                if (shouldLetSettle)
                    letCurrentPlayerSettle();
            }
        }
    }

    private void letCurrentPlayerSettle() {
        letCurrentPlayerPlaceBuilding(BuildingType.SETTLEMENT);
        letCurrentPlayerPlaceBuilding(BuildingType.STREET);
    }

    public void tradeWithBank(Player player, MaterialType typeToGive, MaterialType typeToReceive) {
        if (player.getMaterialCount(typeToGive) >= 4) {
            player.addMaterial(typeToGive, -4);
            player.addMaterial(typeToReceive, 1);
        }
    }

    @Subscribe
    public void onEndTurnEvent(EndTurnEvent event) {
        Gdx.app.log("DEBUG", "EndTurnEvent received");
        if (!buildingQueue.isEmpty() || playerPlacingBuilding) {
            return; // Player still has buildings to place
        }
        nextRoundPhase();
    }

    @Subscribe
    public void onBuildSettlementEvent(BuildSettlementEvent event) {
        handleTryToBuildEvent(BuildingType.SETTLEMENT);
    }

    @Subscribe
    public void onBuildCityEvent(BuildCityEvent event) {
        handleTryToBuildEvent(BuildingType.CITY);
    }

    @Subscribe
    public void onBuildStreetEvent(BuildStreetEvent event) {
        handleTryToBuildEvent(BuildingType.STREET);
    }

    public void handleTryToBuildEvent(BuildingType type) {
        Player currentPlayer = getCurrentPlayer();
        if (!currentPlayer.canAfford(type)) {
            Gdx.app.log("DEBUG", "Player " + currentPlayer.getName() + " cannot afford building: " + type);
            return;
        }
        if (type == BuildingType.CITY && currentPlayer.getCityAmount() >= 4) {
            Gdx.app.log("DEBUG", "Player " + currentPlayer.getName() + " cannot build more than 4 cities");
            return;
        }
        if (type == BuildingType.SETTLEMENT && currentPlayer.getSettlementAmount() >= 5) {
            Gdx.app.log("DEBUG", "Player " + currentPlayer.getName() + " cannot build more than 5 settlements");
            return;
        }
        if (type == BuildingType.STREET && currentPlayer.getStreetAmount() >= 15) {
            Gdx.app.log("DEBUG", "Player " + currentPlayer.getName() + " cannot build more than 15 streets");
            return;
        }

        currentPlayer.removeMaterialForBuilding(type);
        gameUI.updateMaterials(getCurrentPlayer().getMaterials());

        letCurrentPlayerPlaceBuilding(type);
    }

    /**
     * Plays the current round phase of the game logic.
     * Once complete with the round phase, a method will change the current round
     * phase and the {@link de.svenojo.catan.screen.GameScreen#render(float) Game
     * Screen} will play the new round phase in the render method.
     */
    public void playRoundPhase() {
        switch (currentRoundPhase) {
            case DICE_ROLL:
                this.rolledNumber = diceRoll.rollBothDice();
                gameUI.updateRolledNumber(this.rolledNumber);
                nextRoundPhase();
                break;
            case MATERIAL_DISTRIBUTION:
                if (this.rolledNumber == 7) {
                    nextRoundPhase();
                    break;
                }
                // Annahme rolled number ist zahl => herausfinden, welches Feld diese zahl hat
                for (Tile tile : worldMap.getMapTiles()) {
                    if (tile.getNumberValue() != this.rolledNumber)
                        continue;
                    if (tile.isRobberPlaced())
                        continue;

                    MaterialType materialToGivePlayers = tile.getWorldTileType().getMaterialType();

                    List<NodeBuilding> buildingsOnTile = worldMap.getNodeBuildingsOnTile(tile);
                    for (NodeBuilding building : buildingsOnTile) {
                        Player player = building.getPlayer();
                        int amount = switch (building.getBuildingType()) {
                            case SETTLEMENT -> 1;
                            case CITY -> 2;
                            default -> 0;
                        };
                        Gdx.app.log("DEBUG", "Giving " + amount + " " + materialToGivePlayers.name() + " to player: "
                                + player.getName());
                        player.addMaterial(materialToGivePlayers, amount);
                    }
                }
                gameUI.updateMaterials(getCurrentPlayer().getMaterials());
                nextRoundPhase();
                break;
            case ROBBER:
                if (this.rolledNumber != 7) {
                    nextRoundPhase();
                    break;
                }
                letCurrentPlayerPlaceRobber();
                break;
            case BUILD:
                gameUI.getButtonTable().setVisible(true);
                gameUI.getEndTurnButton().setVisible(true);
                break;

            default:
                System.err.println("The current RoundPhase " + currentRoundPhase.toString() + " cannot be played.");
                break;
        }
    }

    /**
     * Plays the current Game phase of the game logic depending on the State of the
     * game.
     * Once complete with the tasks of the game state, a method will change the
     * current Game state and the
     * {@link de.svenojo.catan.screen.GameScreen#render(float) Game
     * Screen} will play the new game state in the render method.
     */

    public void playGameState() {
        switch (currentGameState) {
            case PRE_GAME:
                // Players could be shuffled or try to roll higher than each other
                gameUI.updateCurrentPlayer(getCurrentPlayer().getName(), getCurrentPlayer().getColorString());
                currentGameState = GameState.SETTLE_PLAYERS;
                break;
            case SETTLE_PLAYERS:
                // In this state, players are placing their first buildings
                letCurrentPlayerSettle();
                break;
            case GAME:
                // Starts the round loop
                currentRoundPhase = RoundPhase.DICE_ROLL;
                break;
            case GAME_ENDING:
            default:
                System.err.println("The current GameState " + currentGameState.toString() + " cannot be played.");
                break;
        }
    }
}
