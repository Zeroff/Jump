package com.bitdecay.jump.leveleditor.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bitdecay.jump.BitBody;
import com.bitdecay.jump.collision.BitWorld;
import com.bitdecay.jump.BodyType;
import com.bitdecay.jump.collision.ContactListener;
import com.bitdecay.jump.geom.BitPointInt;
import com.bitdecay.jump.geom.BitRectangle;
import com.bitdecay.jump.geom.GeomUtils;
import com.bitdecay.jump.level.builder.LevelBuilder;
import com.bitdecay.jump.leveleditor.render.mouse.BaseMouseMode;
import com.bitdecay.jump.leveleditor.render.mouse.MouseButton;
import com.bitdecay.jump.leveleditor.tools.BitColors;

public class CreateStaticMouseMode extends BaseMouseMode {

    private BitWorld world;

    public CreateStaticMouseMode(LevelBuilder builder, BitWorld world) {
        super(builder);
        this.world = world;
    }

    public CreateStaticMouseMode(LevelBuilder builder) {
        super(builder);
    }

    @Override
    public void mouseDown(BitPointInt point, MouseButton button) {
        startPoint = GeomUtils.snap(point, builder.tileSize);
    }

    @Override
    public void mouseDragged(BitPointInt point) {
        super.mouseDragged(point);
        endPoint = GeomUtils.snap(point, builder.tileSize);
    }

    @Override
    public void mouseUpLogic(BitPointInt point, MouseButton button) {
        endPoint = GeomUtils.snap(point, builder.tileSize);
        BitBody staticBody = new BitBody();
        staticBody.bodyType = BodyType.STATIC;
        staticBody.aabb = new BitRectangle(startPoint.x, startPoint.y, endPoint.x - startPoint.x, endPoint.y - startPoint.y);
        staticBody.getContactListeners().add(new ContactListener() {
            @Override
            public void contactStarted(BitBody other) {
                System.out.println("IT STARTED");
            }

            @Override
            public void contactEnded(BitBody other) {
                System.out.println("IT ENDED");
            }

            @Override
            public void crushed() {
                System.out.println("CRUSHED");
            }
        });
        world.addBody(staticBody);
    }

    @Override
    public void render(ShapeRenderer shaper, SpriteBatch spriteBatch) {
        if (startPoint != null && endPoint != null) {
            shaper.setColor(BitColors.NEW);
            shaper.rect(startPoint.x, startPoint.y, endPoint.x - startPoint.x, endPoint.y - startPoint.y);
        }
    }

    @Override
    public String getToolTip() {
        return "Create";
    }
}