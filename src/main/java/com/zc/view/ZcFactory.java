package com.zc.view;

import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;
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
                .with(new MazeMapComponent(w_num, h_num, cellWidth, cellHeight))
                .zIndex(10)
                .neverUpdated()
                .build();
    }

    @Spawns("scell")
    public Entity scell(SpawnData data) {
        return createCell(data, Color.RED, EntityType.S_CELL);
    }

    @Spawns("ecell")
    public Entity ecell(SpawnData data) {
        return createCell(data, Color.BLUE, EntityType.E_CELL);
    }

    @Spawns("pcell")
    public Entity pcell(SpawnData data) {
        Entity cell = createCell(data, Color.GRAY, EntityType.PATH);
        cell.setZIndex(0);

        return cell;
    }

    private Entity createCell(SpawnData data, Color color, EntityType type) {
        Entity build = cpoint(data, color).build();
        build.translate(leftIntr, topIntr);
        build.setType(type);

        return build;
    }

    private EntityBuilder cpoint(SpawnData data, Color color) {
        Rectangle rectangle = new Rectangle(cellWidth, cellHeight, color);

        return entityBuilder(data)
                .view(rectangle)
                .zIndex(5);
    }
}
