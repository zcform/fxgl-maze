package com.zc.view;

import cn.hutool.core.collection.CollUtil;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.pathfinding.maze.Maze;
import com.almasb.fxgl.pathfinding.maze.MazeCell;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
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

        while (scell.distance(ecell) == 0) {
            ecell = map.getRandomCell();
        }

        Point2D sep = new Point2D(scell.getX() * cellWidth, scell.getY() * cellHeight);
        Point2D eep = new Point2D(ecell.getX() * cellWidth, ecell.getY() * cellHeight);

        se = spawn("scell", new SpawnData(sep).put("cell", scell));
        ee = spawn("ecell", new SpawnData(eep).put("cell", ecell));
        player = spawn("player", new SpawnData(sep).put("cell", scell));

        //se = spawn("scell", 0 * cellWidth, 0 * cellHeight);
        //ee = spawn("ecell", (w_num - 1) * cellWidth, (h_num - 1) * cellHeight);
        //player = spawn("player", 0 * cellWidth, 0 * cellHeight);

        playerComponent = player.getComponent(PlayerComponent.class);
    }

    private boolean notRefresh() {
        return player != null && !(player.distance(se) == 0 || player.distance(ee) == 0);
    }

    private void findPath() {
        if (se.distance(player) != 0) return;

        MazeCell so = (MazeCell) se.getPropertyOptional("cell").get();
        MazeCell eo = (MazeCell) ee.getPropertyOptional("cell").get();

        path = pathfinder.findPath(so.getX(), so.getY(), eo.getX(), eo.getY());

        playerComponent.move(new ArrayList<>(path));
    }

    @Override
    protected void onUpdate(double tpf) {
        if (CollUtil.isNotEmpty(path) && intrTimer.elapsed(Duration.millis(80))) {
            MazeCell remove = path.remove(0);

            double px = remove.getX() * cellWidth;
            double py = remove.getY() * cellHeight;

            spawn("pcell", new SpawnData(px, py).put("cell", remove));

            intrTimer.capture();
        }
    }

    @Override
    protected void initUI() {
        Line line = new Line(0, topIntr - bottomIntr, getAppWidth(), topIntr - bottomIntr);
        line.setStrokeWidth(3);
        line.setStroke(Color.GOLD);
        line.setOpacity(.4);

        double w2 = getAppWidth() / 2;

        Button bt_maze = getButton(w2 - 22, (topIntr - bottomIntr - 22) / 2, "maze");
        Button bt_se = getButton(w2 - 22 - 15 - 42, (topIntr - bottomIntr - 22) / 2, "S-E");
        Button bt_path = getButton(w2 + 22 + 15, (topIntr - bottomIntr - 22) / 2, "path");

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
        button.setTranslateX(x);
        button.setTranslateY(y);

        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}