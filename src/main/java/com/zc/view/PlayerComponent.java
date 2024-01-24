package com.zc.view;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.WaypointMoveComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.pathfinding.maze.Maze;
import com.almasb.fxgl.pathfinding.maze.MazeCell;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.image;
import static com.zc.view.constant.*;

/**
 * TODO
 * 2024-01-24
 * zhangxl
 */
@Required(WaypointMoveComponent.class)
public class PlayerComponent extends Component {
    private WaypointMoveComponent moveComponent;
    private Maze map;

    private AnimatedTexture texture;
    private AnimationChannel animS, animL, animR, animU, animD;

    private double ox;
    private double oy;

    public PlayerComponent() {
        map = FXGL.<ZcApplication>getAppCast().getMap();

        double min = Math.min(cellWidth, cellHeight);

        double ds = .8;

        Image image = image("bug.png", min * 8, min * 8);
        animS = new AnimationChannel(image, 8, (int) min, (int) min, Duration.seconds(1), 6 * 8, 6 * 8);
        animL = new AnimationChannel(image, 8, (int) min, (int) min, Duration.seconds(ds), 0 * 8, 0 * 8 + 7);
        animR = new AnimationChannel(image, 8, (int) min, (int) min, Duration.seconds(ds), 4 * 8, 4 * 8 + 7);
        animU = new AnimationChannel(image, 8, (int) min, (int) min, Duration.seconds(ds), 2 * 8, 2 * 8 + 7);
        animD = new AnimationChannel(image, 8, (int) min, (int) min, Duration.seconds(ds), 6 * 8, 6 * 8 + 7);

        animS = animD;

        texture = new AnimatedTexture(animS);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(texture.getWidth() / 8 / 2, texture.getHeight() / 8 / 2));
        entity.getViewComponent().addChild(texture);

        ox = entity.getX();
        oy = entity.getY();
    }

    public void move(List<MazeCell> path) {
        List<Point2D> point2DS = path.stream().map(p -> new Point2D(p.getX() * cellWidth + leftIntr, p.getY() * cellHeight + topIntr)).collect(Collectors.toList());

        moveComponent.setSpeed(100);
        moveComponent.move(point2DS);
    }

    @Override
    public void onUpdate(double tpf) {
        rotation();
    }

    private void rotation() {
        if (isMoving()) {
            rotationX();
            rotationY();
        } else {
            if (texture.getAnimationChannel() != animS) {
                texture.loopAnimationChannel(animS);
            }
        }

        ox = entity.getX();
        oy = entity.getY();
    }

    private boolean isMoving() {
        double x = entity.getX();
        double y = entity.getY();

        return !(x == ox && y == oy);
    }

    private void rotationX() {
        rotationImg(entity.getX(), ox, animL, animR);
    }

    private void rotationY() {
        rotationImg(entity.getY(), oy, animU, animD);
    }

    private void rotationImg(double xy, double oxy, AnimationChannel animLU, AnimationChannel animUD) {
        if (xy == oxy) {
            return;
        }

        if (xy - oxy < 0) {
            if (texture.getAnimationChannel() != animLU) {
                texture.loopAnimationChannel(animLU);
            }
        } else {
            if (texture.getAnimationChannel() != animUD) {
                texture.loopAnimationChannel(animUD);
            }
        }
    }
}
