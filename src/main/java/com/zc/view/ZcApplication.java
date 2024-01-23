package com.zc.view;

import cn.hutool.core.collection.CollUtil;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.pathfinding.maze.Maze;
import com.almasb.fxgl.pathfinding.maze.MazeCell;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;
import static com.zc.view.EntityType.*;
import static com.zc.view.constant.*;

public class ZcApplication extends GameApplication {
    private Maze map;
    private MazeMapComponent mapComponent;
    private MazePathfinder pathfinder;
    private Entity se;
    private Entity ee;

    private LocalTimer intrTimer;

    private List<MazeCell> path = new ArrayList<>();

    public Maze getMap() {
        return this.map;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle(" ");
        settings.setVersion(" ");
        settings.setAppIcon("zc.png");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("num", 0);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("R") {
            @Override
            protected void onActionEnd() {
                if (CollUtil.isNotEmpty(path)) {
                    return;
                }

                map = mapComponent.refresh();
                pathfinder = new MazePathfinder(map);

                createSE();
            }
        }, KeyCode.R);

        onKeyUp(KeyCode.Q, () -> createSE());

        getInput().addAction(new UserAction("PRIMARY") {
            @Override
            protected void onActionEnd() {
                Point2D point2D = getInput().getMousePositionWorld();
                findPath();
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        intrTimer = newLocalTimer();
        intrTimer.capture();

        getGameWorld().addEntityFactory(new ZcFactory());
        getGameScene().setBackgroundColor(Color.BLACK);

        Entity maze = spawn("maze");

        mapComponent = maze.getComponent(MazeMapComponent.class);
        map = mapComponent.getMaze();
        pathfinder = new MazePathfinder(map);

        createSE();
    }

    private void createSE() {
        if (CollUtil.isNotEmpty(path)) {
            return;
        }

        getGameWorld().getEntitiesByType(PATH, S_CELL, E_CELL).forEach(Entity::removeFromWorld);

        MazeCell scell = map.getRandomCell();
        se = spawn("scell", scell.getX() * cellWidth, scell.getY() * cellHeight);

        MazeCell ecell = map.getRandomCell();
        ee = spawn("ecell", ecell.getX() * cellWidth, ecell.getY() * cellHeight);
    }

    private void findPath() {
        if (CollUtil.isNotEmpty(path) || getGameWorld().getEntitiesByType(PATH).size() != 0) {
            return;
        }

        int sx = (int) (se.getX() / cellWidth);
        int sy = (int) (se.getY() / cellHeight);

        int ex = (int) (ee.getX() / cellWidth);
        int ey = (int) (ee.getY() / cellHeight);

        path = pathfinder.findPath(sx, sy, ex, ey);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (CollUtil.isNotEmpty(path) && intrTimer.elapsed(Duration.millis(30))) {
            MazeCell remove = path.remove(0);

            double px = remove.getX() * cellWidth;
            double py = remove.getY() * cellHeight;

            spawn("pcell", px, py);

            intrTimer.capture();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}