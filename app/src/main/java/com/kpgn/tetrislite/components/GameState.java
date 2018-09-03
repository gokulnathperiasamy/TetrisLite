package com.kpgn.tetrislite.components;

import com.kpgn.tetrislite.utility.PieceGenerator;
import com.kpgn.tetrislite.R;
import com.kpgn.tetrislite.activity.GameActivity;
import com.kpgn.tetrislite.pieces.IPiece;
import com.kpgn.tetrislite.pieces.JPiece;
import com.kpgn.tetrislite.pieces.LPiece;
import com.kpgn.tetrislite.pieces.OPiece;
import com.kpgn.tetrislite.pieces.Piece;
import com.kpgn.tetrislite.pieces.SPiece;
import com.kpgn.tetrislite.pieces.TPiece;
import com.kpgn.tetrislite.pieces.ZPiece;


public class GameState extends Component {

    public final static int state_startable = 0;
    public final static int state_running = 1;
    public final static int state_paused = 2;
    public final static int state_finished = 3;

    private static GameState instance;

    private PieceGenerator rng;
    public Board board;

    private int activeIndex;
    private int previewIndex;
    private Piece[] activePieces;
    private Piece[] previewPieces;
    private boolean scheduleSpawn;
    private long spawnTime;
    private int stateOfTheGame;
    private long score;
    private int clearedLines;
    private int level;
    private int maxLevel;
    private long gameTime;
    private long currentTime;
    private long nextDropTime;
    private long nextPlayerDropTime;
    private long nextPlayerMoveTime;
    private int[] dropIntervals;
    private long playerDropInterval;
    private long playerMoveInterval;
    private int singleLineScore;
    private int doubleLineScore;
    private int trippleLineScore;
    private int multiTetrisScore;
    private boolean multitetris;
    private int quadLineScore;
    private int hardDropBonus;
    private int softDropBonus;
    private int spawn_delay;
    private int piece_start_x;
    private long popupTime;
    private int popupAttack;
    private int popupSustain;
    private int popupDecay;
    private int softDropDistance;

    private GameState(GameActivity ga) {
        super(ga);
        board = new Board(host);

        dropIntervals = host.getResources().getIntArray(R.array.intervals);
        singleLineScore = host.getResources().getInteger(R.integer.singleLineScore);
        doubleLineScore = host.getResources().getInteger(R.integer.doubleLineScore);
        trippleLineScore = host.getResources().getInteger(R.integer.trippleLineScore);
        multiTetrisScore = host.getResources().getInteger(R.integer.multiTetrisScore);
        quadLineScore = host.getResources().getInteger(R.integer.quadLineScore);
        hardDropBonus = host.getResources().getInteger(R.integer.hardDropBonus);
        softDropBonus = host.getResources().getInteger(R.integer.softDropBonus);
        softDropDistance = 0;
        spawn_delay = host.getResources().getInteger(R.integer.spawn_delay);
        piece_start_x = host.getResources().getInteger(R.integer.piece_start_x);
        popupAttack = host.getResources().getInteger(R.integer.popup_attack);
        popupSustain = host.getResources().getInteger(R.integer.popup_sustain);
        popupDecay = host.getResources().getInteger(R.integer.popup_decay);
        popupTime = -(popupAttack + popupSustain + popupDecay);
        clearedLines = 0;
        level = 0;
        score = 0;
        maxLevel = host.getResources().getInteger(R.integer.levels);

        nextDropTime = host.getResources().getIntArray(R.array.intervals)[0];

        playerDropInterval = (int) (1000.0f / 60);
        playerMoveInterval = (int) (1000.0f / 60);
        nextPlayerDropTime = (int) (1000.0f / 60);
        nextPlayerMoveTime = (int) (1000.0f / 60);

        gameTime = 0;
        rng = new PieceGenerator(PieceGenerator.STRAT_RANDOM);

        // Initialize Pieces
        activePieces = new Piece[7];
        previewPieces = new Piece[7];

        activePieces[0] = new IPiece(host);
        activePieces[1] = new JPiece(host);
        activePieces[2] = new LPiece(host);
        activePieces[3] = new OPiece(host);
        activePieces[4] = new SPiece(host);
        activePieces[5] = new TPiece(host);
        activePieces[6] = new ZPiece(host);

        previewPieces[0] = new IPiece(host);
        previewPieces[1] = new JPiece(host);
        previewPieces[2] = new LPiece(host);
        previewPieces[3] = new OPiece(host);
        previewPieces[4] = new SPiece(host);
        previewPieces[5] = new TPiece(host);
        previewPieces[6] = new ZPiece(host);

        // starting pieces
        activeIndex = rng.next();
        previewIndex = rng.next();
        activePieces[activeIndex].setActive(true);

        stateOfTheGame = state_startable;
        scheduleSpawn = false;
        spawnTime = 0;
    }

    public Board getBoard() {
        return board;
    }

    public int getAutoDropInterval() {
        return dropIntervals[Math.min(level, maxLevel)];
    }

    public long getMoveInterval() {
        return playerMoveInterval;
    }

    public long getSoftDropInterval() {
        return playerDropInterval;
    }

    public void setRunning(boolean b) {
        if (b) {
            currentTime = System.currentTimeMillis();
            if (stateOfTheGame != state_finished)
                stateOfTheGame = state_running;
        } else {
            if (stateOfTheGame == state_running)
                stateOfTheGame = state_paused;
        }
    }

    public void clearLines(boolean playerHardDrop, int hardDropDistance) {
        if (host == null) {
            return;
        }

        activePieces[activeIndex].place(board);
        int cleared = board.clearLines(activePieces[activeIndex].getDim());
        clearedLines += cleared;
        long addScore;

        switch (cleared) {
            case 1:
                addScore = singleLineScore;
                multitetris = false;
                popupTime = gameTime;
                break;
            case 2:
                addScore = doubleLineScore;
                multitetris = false;
                popupTime = gameTime;
                break;
            case 3:
                addScore = trippleLineScore;
                multitetris = false;
                popupTime = gameTime;
                break;
            case 4:
                if (multitetris) {
                    addScore = multiTetrisScore;
                } else {
                    addScore = quadLineScore;
                }
                multitetris = true;
                popupTime = gameTime;
                break;
            default:
                addScore = 0;
                if ((gameTime - popupTime) < (popupAttack + popupSustain)) {
                    popupTime = gameTime - (popupAttack + popupSustain);
                }
                break;
        }

        if (cleared > 0) {
            if (playerHardDrop) {
                addScore += hardDropDistance * hardDropBonus;
            } else {
                addScore += softDropDistance * softDropBonus;
            }
        }
        score += addScore;
    }

    public void pieceTransition(boolean eventVibrationEnabled) {
        if (host == null) {
            return;
        }

        scheduleSpawn = true;

        if (eventVibrationEnabled) {
            spawnTime = gameTime + spawn_delay;
        } else {
            spawnTime = gameTime;
        }

        activePieces[activeIndex].reset(host);
        activeIndex = previewIndex;
        previewIndex = rng.next();
        activePieces[activeIndex].reset(host);
    }

    public void finishTransition() {
        if (host == null) {
            return;
        }

        scheduleSpawn = false;
        activePieces[activeIndex].setActive(true);
        setNextDropTime(gameTime + dropIntervals[Math.min(level, maxLevel)]);
        setNextPlayerDropTime(gameTime);
        setNextPlayerMoveTime(gameTime);
        softDropDistance = 0;

        if (!activePieces[activeIndex].setPosition(piece_start_x, 0, false, board)) {
            stateOfTheGame = state_finished;
            host.gameOver();
        }
    }

    public boolean isResumable() {
        return (stateOfTheGame != state_finished);
    }

    public String getScoreString() {
        return "" + score;
    }

    public long getScore() {
        return score;
    }

    public Piece getActivePiece() {
        return activePieces[activeIndex];
    }

    public boolean cycle(long tempTime) {
        if (stateOfTheGame != state_running)
            return false;

        gameTime += (tempTime - currentTime);
        currentTime = tempTime;

        // Instant Placement
        if (scheduleSpawn) {
            if (gameTime >= spawnTime) {
                finishTransition();
            }
            return false;
        }
        return true;
    }

    public String getLevelString() {
        return "" + level;
    }

    @Override
    public void reconnect(GameActivity ga) {
        super.reconnect(ga);

        playerDropInterval = (int) (1000.0f / 60);
        playerMoveInterval = (int) (1000.0f / 60);

        rng = new PieceGenerator(PieceGenerator.STRAT_RANDOM);
        board.reconnect(ga);
        setRunning(true);
    }

    public void disconnect() {
        setRunning(false);
        board.disconnect();
        super.disconnect();
    }

    public Piece getPreviewPiece() {
        return previewPieces[previewIndex];
    }

    public long getTime() {
        return gameTime;
    }

    public void nextLevel() {
        level++;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getClearedLines() {
        return clearedLines;
    }

    public void setNextPlayerDropTime(long time) {
        nextPlayerDropTime = time;
    }

    public void setNextPlayerMoveTime(long time) {
        nextPlayerMoveTime = time;
    }

    public void setNextDropTime(long l) {
        nextDropTime = l;
    }

    public long getNextPlayerDropTime() {
        return nextPlayerDropTime;
    }

    public long getNextDropTime() {
        return nextDropTime;
    }

    public long getNextPlayerMoveTime() {
        return nextPlayerMoveTime;
    }

    public static GameState getNewInstance(GameActivity ga) {
        instance = new GameState(ga);
        return instance;
    }

    public void setLevel(int int1) {
        level = int1;
        nextDropTime = host.getResources().getIntArray(R.array.intervals)[int1];
        clearedLines = 10 * int1;
    }

    public void incSoftDropCounter() {
        softDropDistance++;
    }
}