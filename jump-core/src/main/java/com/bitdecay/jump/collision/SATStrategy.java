package com.bitdecay.jump.collision;

import com.bitdecay.jump.*;
import com.bitdecay.jump.geom.BitPoint;
import com.bitdecay.jump.geom.BitRectangle;
import com.bitdecay.jump.geom.GeomUtils;
import com.bitdecay.jump.geom.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monday on 9/27/2015.
 */
public class SATStrategy extends BitResolution {

    public SATStrategy(BitBody body) {
        super(body);
    }

    /**
     * Determines appropriate resolution for the body based on the collisions found. This method will
     * deactivate the body if opposing collisions are found.
     * @param world
     */
    @Override
    public void satisfy(BitWorld world) {
        // TODO: This method can probably made more efficient if needed down the road
        List<BitPoint> directionsResolved = new ArrayList<>();
        for (BitCollision collision : collisions) {
            SATResolution satRes = SATCollisions.getCollision(resolvedPosition, collision.otherBody.aabb);
            if (satRes != null) {
                satResolve(resolvedPosition, satRes, body, collision.otherBody);
                if (satRes.axis.equals(GeomUtils.ZERO_AXIS)) {

                    continue;
                }
                BitPoint resAxis = satRes.result.normalize();
                for (BitPoint otherAxis : directionsResolved) {
                    // this will only check exact opposites, may need to change if we move away from rectangle tiles
                    if (resAxis.scale(-1).equals(otherAxis)) {
                        // reset any pending resolutions.
                        // don't bother moving a dead body
                        // short circuit the resolution
                        // and return
                        resolvedPosition.set(body.aabb);
                        body.active = false;
                        body.velocity.set(0, 0);
                        return;
                    }
                }
                directionsResolved.add(resAxis);
                postResolve(world, body, collision.otherBody, satRes);
            }
        }
    }

    private void satResolve(BitRectangle resolvedPosition, SATResolution satRes, BitBody body, BitBody otherBody) {
        satRes.compute(body, otherBody, resolvedPosition);
        if (satRes.axis.x != 0 && satRes.axis.y > 0) {
            // this is logic to make it so the player doesn't move slower when running uphill. Likewise, we will need logic to 'glue' the player to the ground when running downhill.
            // atan is our angle of resolution
            double atan = Math.atan(satRes.axis.y / satRes.axis.x);

            if (Math.abs(atan - MathUtils.PI_OVER_TWO) <= Math.toRadians(30)) {
                // currently we impose a hard limit of 30 degree angle 'walkability'
                if (atan > 0) {
                    double angleToUpright;
                    angleToUpright = MathUtils.PI_OVER_TWO - atan;
                    double straightUp = satRes.distance / Math.cos(angleToUpright);
                    resolvedPosition.xy.add(0, (float) straightUp);
                } else {
                    double angleToUpright;
                    angleToUpright = -MathUtils.PI_OVER_TWO - atan;
                    double straightUp = satRes.distance / Math.cos(angleToUpright);
                    resolvedPosition.xy.add(0, (float) straightUp);
                }
                return;
            }
        }

        resolvedPosition.xy.add(satRes.axis.x * satRes.distance, satRes.axis.y * satRes.distance);
    }


    private void postResolve(BitWorld world, BitBody body, BitBody otherBody, SATResolution satRes) {
        if (BodyType.KINETIC.equals(otherBody.bodyType)) {
            if (body.parent == null) {
                // Attach as child if we were resolved by the kinetic object in the direction it is moving
                if (satRes.result.dot(otherBody.currentAttempt) > 0) {
                    body.parent = otherBody;
                    otherBody.children.add(body);
                }
                // attach if we were resolved against gravity (aka we are standing on it)
                if (satRes.axis.dot(world.gravity.x, world.gravity.y) < 0) {
                    body.parent = otherBody;
                    otherBody.children.add(body);
                }
            }
        }
    }



}
