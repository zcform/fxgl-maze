package com.zc.view;

import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.components.WaypointMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.zc.maze.MazeMapComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.zc.view.EntityType.*;
import static com.zc.view.constant.*;

/**
 * Spawns Factory
 * 2024-01-08
 * zhangxl
 */
public class ZcFactory implements EntityFactory {
    @Spawns("maze")
    public Entity maze(SpawnData data) {
        return entityBuilder()
                .at(leftIntr, topIntr)
                .with(new MazeMapComponent(w_num, h_num, cellWidth, cellHeight, Color.GOLD))
                .zIndex(10)
                .opacity(.8)
                .neverUpdated()
                .build();
    }

    @Spawns("scell")
    public Entity scell(SpawnData data) {
        return createCell(data, Color.RED, S_CELL);
    }

    @Spawns("ecell")
    public Entity ecell(SpawnData data) {
        return createCell(data, Color.BLUE, E_CELL);
    }

    @Spawns("pcell")
    public Entity pcell(SpawnData data) {
        Entity cell = createCell(data, Color.GRAY, PATH);
        cell.setZIndex(0);

        return cell;
    }

    @Spawns("player")
    public Entity player(SpawnData data) {
        Entity build = entityBuilder()
                .at(data.getX() + leftIntr, data.getY() + topIntr)
                .with("cell", data.get("cell"))
                .type(PLAYER)
                .with(new WaypointMoveComponent(0, new ArrayList<>()))
                .with(new PlayerComponent())
                .zIndex(100)
                .build();

        return build;
    }

    private Entity createCell(SpawnData data, Color color, EntityType type) {
        Entity build = cpoint(data, color)
                .type(type)
                .with("cell", data.get("cell"))
                .build();

        build.translate(leftIntr, topIntr);

        return build;
    }

    private EntityBuilder cpoint(SpawnData data, Color color) {
        Rectangle rectangle = new Rectangle(cellWidth, cellHeight, color);

        return entityBuilder(data)
                .view(rectangle)
                .zIndex(5)
                .neverUpdated();
    }
}
