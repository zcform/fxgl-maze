package com.zc.view;

import cn.hutool.core.collection.CollUtil;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGLForKtKt;
import com.almasb.fxgl.dsl.components.WaypointMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.maze.Maze;
import com.almasb.fxgl.pathfinding.maze.MazeCell;
import com.almasb.fxgl.time.LocalTimer;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.zc.view.EntityType.*;
import static com.zc.view.constant.*;

public class ZcApplication extends GameApplication {
    private Maze map;
    private MazeMapComponent mapComponent;
    private MazePathfinder pathfinder;
    private Entity se;
    private Entity ee;

    private Entity player;
    private PlayerComponent playerComponent;
    private WaypointMoveComponent waypointMoveComponent;

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
        settings.setWidth((int) WIDTH + 1);
        settings.setHeight((int) HEIGHT + 1);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("num", 0);
    }

    @Override
    protected void initInput() {
        //getInput().addAction(new UserAction("R") {
        //    @Override
        //    protected void onActionEnd() {
        //        refreshMaze();
        //    }
        //}, KeyCode.R);
        //
        //onKeyUp(KeyCode.Q, () -> createSE());
        //
        //getInput().addAction(new UserAction("PRIMARY") {
        //    @Override
        //    protected void onActionEnd() {
        //        Point2D point2D = getInput().getMousePositionWorld();
        //        findPath();
        //    }
        //}, MouseButton.PRIMARY);
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

        refreshSE();
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0.0, 0.0);
    }

    private void refreshMaze() {
        if (notRefresh()) return;

        map = mapComponent.refresh();
        pathfinder = new MazePathfinder(map);

        refreshSE();
    }

    private void refreshSE() {
        if (notRefresh()) return;

        getGameWorld().getEntitiesByType(PLAYER, PATH, S_CELL, E_CELL).forEach(Entity::removeFromWorld);

        MazeCell scell = map.getRandomCell();
        MazeCell ecell = map.getRandomCell();

        se = spawn("scell", scell.getX() * cellWidth, scell.getY() * cellHeight);
        ee = spawn("ecell", ecell.getX() * cellWidth, ecell.getY() * cellHeight);

        player = spawn("player", scell.getX() * cellWidth, scell.getY() * cellHeight);

        playerComponent = player.getComponent(PlayerComponent.class);
        waypointMoveComponent = player.getComponent(WaypointMoveComponent.class);
    }

    private boolean notRefresh() {
        return player != null && !(player.distance(se) == 0 || player.distance(ee) == 0);
    }

    private void findPath() {
        if (se.distance(player) != 0) return;

        int sx = (int) (se.getX() / cellWidth);
        int sy = (int) (se.getY() / cellHeight);

        int ex = (int) (ee.getX() / cellWidth);
        int ey = (int) (ee.getY() / cellHeight);

        path = pathfinder.findPath(sx, sy, ex, ey);

        playerComponent.move(new ArrayList<>(path));
    }

    @Override
    protected void onUpdate(double tpf) {
        if (CollUtil.isNotEmpty(path) && intrTimer.elapsed(Duration.millis(80))) {
            MazeCell remove = path.remove(0);

            double px = remove.getX() * cellWidth;
            double py = remove.getY() * cellHeight;

            spawn("pcell", px, py);

            intrTimer.capture();
        }
    }

    @Override
    protected void initUI() {
        double x = getAppWidth() - describeIntr + leftIntr;

        Line line = new Line(x, 0, x, FXGLForKtKt.getAppHeight());
        line.setStrokeWidth(3);
        line.setStroke(Color.GOLD);
        line.setOpacity(.4);

        Button bt_maze = getButton(x, 30, "maze");
        Button bt_se = getButton(x, bt_maze.getTranslateY() + 22 + 16, "S-E");
        Button bt_path = getButton(x, bt_se.getTranslateY() + 22 + 16, "path");

        bt_maze.setOnMouseClicked(e -> refreshMaze());
        bt_se.setOnMouseClicked(e -> refreshSE());
        bt_path.setOnMouseClicked(e -> findPath());

        addUINode(line);
        addUINode(bt_maze);
        addUINode(bt_se);
        addUINode(bt_path);
    }

    private static Button getButton(double x, double y, String name) {
        Button button = new Button(name);

        button.setFont(new Font(10));
        button.setMinWidth(42);
        button.setMaxWidth(42);
        button.setMinHeight(22);
        button.setMaxHeight(22);
        button.setTranslateX((getAppWidth() - x) / 2 - 21 + x);
        button.setTranslateY(y);

        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}