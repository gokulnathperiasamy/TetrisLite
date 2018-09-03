package com.kpgn.tetrislite.components;

import com.kpgn.tetrislite.pieces.Piece;

import com.kpgn.tetrislite.R;
import com.kpgn.tetrislite.activity.GameActivity;

public class Controls extends Component {

    private int[] lineThresholds;

    private boolean playerSoftDrop;
    private boolean clearPlayerSoftDrop;
    private boolean playerHardDrop;
    private boolean leftMove;
    private boolean rightMove;
    private boolean continuousSoftDrop;
    private boolean continuousLeftMove;
    private boolean continuousRightMove;
    private boolean clearLeftMove;
    private boolean clearRightMove;
    private boolean leftRotation;
    private boolean rightRotation;
    private int initialHIntervalFactor;
    private int initialVIntervalFactor;

    public Controls(GameActivity ga) {
        super(ga);

        lineThresholds = host.getResources().getIntArray(R.array.line_thresholds);
        initialHIntervalFactor = 1;
        initialVIntervalFactor = 1;
        playerSoftDrop = false;
        leftMove = false;
        rightMove = false;
        leftRotation = false;
        rightRotation = false;
        clearLeftMove = false;
        clearRightMove = false;
        clearPlayerSoftDrop = false;
        continuousSoftDrop = false;
        continuousLeftMove = false;
        continuousRightMove = false;
    }


    public void rotateLeftPressed() {
        leftRotation = true;
    }

    public void rotateRightPressed() {
        rightRotation = true;
    }

    public void dropButtonPressed() {
        if (!host.game.getActivePiece().isActive())
            return;

        playerHardDrop = true;
    }

    public void leftButtonReleased() {
        clearLeftMove = true;
    }

    public void leftButtonPressed() {
        clearLeftMove = false;
        leftMove = true;
        rightMove = false;
        host.game.setNextPlayerMoveTime(host.game.getTime());
    }

    public void rightButtonReleased() {
        clearRightMove = true;
    }

    public void rightButtonPressed() {
        clearRightMove = false;
        rightMove = true;
        leftMove = false;
        host.game.setNextPlayerMoveTime(host.game.getTime());
    }

    public void cycle(long tempTime) {
        long gameTime = host.game.getTime();
        Piece active = host.game.getActivePiece();
        Board board = host.game.getBoard();
        int maxLevel = host.game.getMaxLevel();

        // Left Rotation
        if (leftRotation) {
            leftRotation = false;
            active.turnLeft(board);
        }

        // Right Rotation
        if (rightRotation) {
            rightRotation = false;
            active.turnRight(board);
        }

        // Reset Move Time
        if ((!leftMove && !rightMove) && (!continuousLeftMove && !continuousRightMove))
            host.game.setNextPlayerMoveTime(gameTime);

        // Left Move
        if (leftMove) {
            continuousLeftMove = false;
            leftMove = false;
            if (active.moveLeft(board)) { // successful move
                host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + initialHIntervalFactor * host.game.getMoveInterval());
            } else { // failed move
                host.game.setNextPlayerMoveTime(gameTime);
            }

        } else if (continuousLeftMove) {
            if (gameTime >= host.game.getNextPlayerMoveTime()) {
                if (active.moveLeft(board)) { // successful move
                    host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + host.game.getMoveInterval());
                } else { // failed move
                    host.game.setNextPlayerMoveTime(gameTime);
                }
            }

            if (clearLeftMove) {
                continuousLeftMove = false;
                clearLeftMove = false;
            }
        }

        // Right Move
        if (rightMove) {
            continuousRightMove = false;
            rightMove = false;
            if (active.moveRight(board)) { // successful move
                host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + initialHIntervalFactor * host.game.getMoveInterval());
            } else { // failed move
                host.game.setNextPlayerMoveTime(gameTime); // first interval is doubled!
            }
        } else if (continuousRightMove) {
            if (gameTime >= host.game.getNextPlayerMoveTime()) {
                if (active.moveRight(board)) { // successful move
                    host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + host.game.getMoveInterval());
                } else { // failed move
                    host.game.setNextPlayerMoveTime(gameTime);
                }
            }

            if (clearRightMove) {
                continuousRightMove = false;
                clearRightMove = false;
            }
        }


        // Hard Drop
        if (playerHardDrop) {
            board.interruptClearAnimation();
            int hardDropDistance = active.hardDrop(false, board);
            host.game.clearLines(true, hardDropDistance);
            host.game.pieceTransition(false);
            board.invalidate();
            playerHardDrop = false;

            if ((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(), maxLevel - 1)]))
                host.game.nextLevel();
            host.game.setNextDropTime(gameTime + host.game.getAutoDropInterval());
            host.game.setNextPlayerDropTime(gameTime);

            // Initial Soft Drop
        } else if (playerSoftDrop) {
            playerSoftDrop = false;
            continuousSoftDrop = true;
            if (!active.drop(board)) {
                // piece finished
                host.game.clearLines(false, 0);
                host.game.pieceTransition(false);
                board.invalidate();
            } else {
                host.game.incSoftDropCounter();
            }
            if ((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(), maxLevel - 1)]))
                host.game.nextLevel();
            host.game.setNextDropTime(host.game.getNextPlayerDropTime() + host.game.getAutoDropInterval());
            host.game.setNextPlayerDropTime(host.game.getNextPlayerDropTime() + initialVIntervalFactor * host.game.getSoftDropInterval());

            // Continuous Soft Drop
        } else if (continuousSoftDrop) {
            if (gameTime >= host.game.getNextPlayerDropTime()) {
                if (!active.drop(board)) {
                    // piece finished
                    host.game.clearLines(false, 0);
                    host.game.pieceTransition(false);
                    board.invalidate();
                } else {
                    host.game.incSoftDropCounter();
                }
                if ((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(), maxLevel - 1)]))
                    host.game.nextLevel();
                host.game.setNextDropTime(host.game.getNextPlayerDropTime() + host.game.getAutoDropInterval());
                host.game.setNextPlayerDropTime(host.game.getNextPlayerDropTime() + host.game.getSoftDropInterval());

                // Autodrop if faster than playerDrop
            } else if (gameTime >= host.game.getNextDropTime()) {
                if (!active.drop(board)) {
                    // piece finished
                    host.game.clearLines(false, 0);
                    host.game.pieceTransition(false);
                    board.invalidate();
                }
                if ((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(), maxLevel - 1)]))
                    host.game.nextLevel();
                host.game.setNextDropTime(host.game.getNextDropTime() + host.game.getAutoDropInterval());
                host.game.setNextPlayerDropTime(host.game.getNextDropTime() + host.game.getSoftDropInterval());
            }

            /* Cancel continuous SoftDrop */
            if (clearPlayerSoftDrop) {
                continuousSoftDrop = false;
                clearPlayerSoftDrop = false;
            }

            // Autodrop if no playerDrop
        } else if (gameTime >= host.game.getNextDropTime()) {
            if (!active.drop(board)) {
                // piece finished
                host.game.clearLines(false, 0);
                host.game.pieceTransition(false);
                board.invalidate();
            }
            if ((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(), maxLevel - 1)]))
                host.game.nextLevel();
            host.game.setNextDropTime(host.game.getNextDropTime() + host.game.getAutoDropInterval());
            host.game.setNextPlayerDropTime(host.game.getNextDropTime());

        } else {
            host.game.setNextPlayerDropTime(gameTime);
        }
    }

    @Override
    public void reconnect(GameActivity cont) {
        super.reconnect(cont);
    }

    @Override
    public void disconnect() {
        super.disconnect();
    }
}
