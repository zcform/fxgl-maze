package com.zc.view;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.pathfinding.maze.Maze;
import com.almasb.fxgl.pathfinding.maze.MazeCell;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Maze 绘制
 * 2024-01-23
 * zhangxl
 */
public class MazeMapComponent extends Component {
    private Maze maze;
    private int w_num;
    private int h_num;
    private double cellWidth;
    private double cellHeight;

    private Canvas onScreenCanvas = new Canvas(getAppWidth(), getAppHeight());
    private GraphicsContext g = onScreenCanvas.getGraphicsContext2D();

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(onScreenCanvas);
    }

    public MazeMapComponent(int w_num, int h_num) {
        refresh(w_num, h_num, getAppWidth() / w_num, getAppHeight() / h_num);
    }

    public MazeMapComponent(int w_num, int h_num, double cellWidth, double cellHeight) {
        refresh(w_num, h_num, cellWidth, cellHeight);
    }

    public Maze refresh() {
        this.maze = new Maze(w_num, h_num);
        creatMap();
        return this.maze;
    }

    private void refresh(int w_num, int h_num, double cellWidth, double cellHeight) {
        this.w_num = w_num;
        this.h_num = h_num;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;

        refresh();
    }

    public Maze getMaze() {
        return this.maze;
    }

    private void creatMap() {
        g.clearRect(0, 0, getAppWidth(), getAppHeight());

        List<MazeCell> cells = maze.getCells();

        for (MazeCell cell : cells) {
            double x = cell.getX() * cellWidth;
            double y = cell.getY() * cellHeight;

            //writeLine(g, x, y, x + cellWidth, y, Color.GRAY);

            if (cell.hasTopWall()) {
                writeLine(g, x, y, x + cellWidth, y, Color.GOLD);
            }

            if (cell.hasLeftWall()) {
                writeLine(g, x, y, x, y + cellHeight, Color.GOLD);
            }
        }

        int ws = w_num;
        int hs = h_num;

        writeLine(g, ws * cellWidth, 0, ws * cellWidth, hs * cellHeight, Color.GOLD);
        writeLine(g, 0, hs * cellHeight, ws * cellWidth, hs * cellHeight, Color.GOLD);
    }

    private void writeLine(GraphicsContext g, double sx, double sy, double ex, double ey, Color color) {
        g.setStroke(color);
        g.setLineWidth(1);
        g.strokeLine(sx, sy, ex, ey);
    }
}
