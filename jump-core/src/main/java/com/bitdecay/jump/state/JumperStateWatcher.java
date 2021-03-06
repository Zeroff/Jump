package com.bitdecay.jump.state;

import com.bitdecay.jump.BitBody;
import com.bitdecay.jump.Facing;
import com.bitdecay.jump.common.State;

public class JumperStateWatcher extends AbstractStateWatcher {

	public JumperStateWatcher() {
	}

	@Override
	public void update(BitBody body) {
		boolean facingLeft = Facing.LEFT.equals(body.facing);
		State newState = state;
		if (body.grounded) {
			if (Math.abs(body.velocity.x) < .01f) {
				if (body.lastResolution.x != 0) {
					newState = facingLeft ? JumperState.LEFT_GROUNDED_AGAINST_WALL : JumperState.RIGHT_GROUNDED_AGAINST_WALL;
				} else {
					newState = facingLeft ? JumperState.LEFT_STANDING : JumperState.RIGHT_STANDING;
				}
			} else {
				newState = facingLeft ? JumperState.LEFT_RUNNING : JumperState.RIGHT_RUNNING;
			}
		} else {
			if (body.lastResolution.x != 0) {
				newState = facingLeft ? JumperState.LEFT_AIR_AGAINST_WALL : JumperState.RIGHT_AIR_AGAINST_WALL;
			} else if (body.velocity.y > 50) {
				newState = facingLeft ? JumperState.LEFT_JUMPING : JumperState.RIGHT_JUMPING;
			} else if (body.velocity.y < -50) {
				newState = facingLeft ? JumperState.LEFT_FALLING : JumperState.RIGHT_FALLING;
			} else {
				newState = facingLeft ? JumperState.LEFT_APEX : JumperState.RIGHT_APEX;
			}
		}
		if (!newState.equals(state)) {
			state = newState;
			fireListeners(state);
		}
	}
}
