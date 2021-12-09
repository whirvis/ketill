package com.whirvis.kibasan.feature;

import java.util.Objects;

import com.whirvis.kibasan.Direction;

public class Orientation1o {
	
	private Direction direction;
	
	public Orientation1o(Direction direction) {
		this.setDirection(direction);
	}
	
	public Orientation1o() {
		this(Direction.UP);
	}
	
	public Direction getDirection() {
		return this.direction;
	}
	
	public void setDirection(Direction direction) {
		this.direction = Objects.requireNonNull(direction, "direction");
	}
	
}
