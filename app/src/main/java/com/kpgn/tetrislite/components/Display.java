package com.kpgn.tetrislite.components;

import com.kpgn.tetrislite.R;
import com.kpgn.tetrislite.activity.GameActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Display extends Component {

    private Paint paint;
    private int gridRowBorder;
    private int gridColumnBorder;
    private int squaresize;
    private int rowOffset;
    private int rows;
    private int columnOffset;
    private int columns;
    private boolean landscapeInitialized;
    private int prev_top;
    private int prev_bottom;
    private int prev_left;
    private int prev_right;

    public Display(GameActivity ga) {
        super(ga);
        landscapeInitialized = false;
        paint = new Paint();

        rows = host.getResources().getInteger(R.integer.zeilen);
        columns = host.getResources().getInteger(R.integer.spalten);
        rowOffset = host.getResources().getInteger(R.integer.zeilenoffset);
        columnOffset = host.getResources().getInteger(R.integer.spaltenoffset);

        squaresize = 1;
        prev_top = 1;
        prev_bottom = 1;
        prev_left = 1;
        prev_right = 1;
    }

    public void doDraw(Canvas c) {
        if (c == null) {
            return;
        }

        if (!landscapeInitialized) {
            host.game.getBoard().invalidate();
            landscapeInitialized = true;
            squaresize = (((c.getHeight() - 1) - 2 * rowOffset) / rows);
            int size2 = (((c.getHeight() - 1) - 2 * columnOffset) / (columns + 4 + host.getResources().getInteger(R.integer.padding_columns)));
            if (size2 < squaresize) {
                squaresize = size2;
                rowOffset = (((c.getHeight() - 1) - squaresize * rows) / 2);
            } else {
                columnOffset = (((c.getWidth() - 1) - squaresize * (host.getResources().getInteger(R.integer.padding_columns) + 4 + columns)) / 2);
            }
            gridRowBorder = rowOffset + squaresize * rows;
            gridColumnBorder = columnOffset + squaresize * columns;
            prev_top = rowOffset;
            prev_bottom = rowOffset + 4 * squaresize;
            prev_left = gridColumnBorder + host.getResources().getInteger(R.integer.padding_columns) * squaresize;
            prev_right = prev_left + 4 * squaresize;
        }

        c.drawColor(Color.argb(0, 0, 0, 0), android.graphics.PorterDuff.Mode.CLEAR);

        host.game.getBoard().draw(columnOffset, rowOffset, squaresize, c);
        drawActive(columnOffset, rowOffset, squaresize, c);
        drawGrid(columnOffset, rowOffset, gridColumnBorder, gridRowBorder, c);
        drawPreview(prev_left, prev_top, prev_right, prev_bottom, c);
        host.updateScore();
    }

    private void drawGrid(int x, int y, int xBorder, int yBorder, Canvas c) {

        paint.setColor(host.getResources().getColor(R.color.colorPrimary));
        for (int zeilePixel = 0; zeilePixel <= rows; zeilePixel++) {
            c.drawLine(x, y + zeilePixel * squaresize, xBorder, y + zeilePixel * squaresize, paint);
        }
        for (int spaltePixel = 0; spaltePixel <= columns; spaltePixel++) {
            c.drawLine(x + spaltePixel * squaresize, y, x + spaltePixel * squaresize, yBorder, paint);
        }

        //draw Border
        paint.setColor(host.getResources().getColor(R.color.colorPrimary));
        c.drawLine(x, y, x, yBorder, paint);
        c.drawLine(x, y, xBorder, y, paint);
        c.drawLine(xBorder, yBorder, xBorder, y, paint);
        c.drawLine(xBorder, yBorder, x, yBorder, paint);
    }

    private void drawPreview(int left, int top, int right, int bottom, Canvas c) {
        // Piece
        drawPreview(left, top, squaresize, c);

        // Grid Lines
        paint.setColor(host.getResources().getColor(R.color.colorPrimary));
        for (int zeilePixel = 0; zeilePixel <= 4; zeilePixel++) {
            c.drawLine(left, top + zeilePixel * squaresize, right, top + zeilePixel * squaresize, paint);
        }
        for (int spaltePixel = 0; spaltePixel <= 4; spaltePixel++) {
            c.drawLine(left + spaltePixel * squaresize, top, left + spaltePixel * squaresize, bottom, paint);
        }

        // Border
        paint.setColor(host.getResources().getColor(R.color.colorPrimary));
        c.drawLine(left, top, right, top, paint);
        c.drawLine(left, top, left, bottom, paint);
        c.drawLine(right, bottom, right, top, paint);
        c.drawLine(right, bottom, left, bottom, paint);
    }

    private void drawActive(int spaltenOffset, int zeilenOffset, int spaltenAbstand, Canvas c) {
        host.game.getActivePiece().drawOnBoard(spaltenOffset, zeilenOffset, spaltenAbstand, c);
    }

    private void drawPreview(int spaltenOffset, int zeilenOffset, int spaltenAbstand, Canvas c) {
        host.game.getPreviewPiece().drawOnPreview(spaltenOffset, zeilenOffset, spaltenAbstand, c);
    }

}