package bitDecayJump.level;

import bitDecayJump.geom.*;

public class MovingObject extends LevelObject implements Updatable {

	private BitPath path;
	private float speed;

	public MovingObject(BitRectangle rect, BitPath path, float speed) {
		super(rect);
		this.path = path;
	}

	@Override
	public void update(float delta) {
		rect.xy = path.update(delta, speed);
	}
}