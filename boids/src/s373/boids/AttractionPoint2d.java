package s373.boids;

public class AttractionPoint2d {
	public float x, y, force, sensorDist;

	AttractionPoint2d() {
	}

	AttractionPoint2d(float _x, float _y, float _f, float _sensor) {
		x = _x;
		y = _y;
		force = _f;
		sensorDist = _sensor;
	}
}
