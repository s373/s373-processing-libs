package s373.boids;

public class AttractionPoint3d {
	public float x, y, z, force, sensorDist;

	AttractionPoint3d() {
	}

	AttractionPoint3d(float _x, float _y, float _z, float _f, float _sensor) {
		x = _x;
		y = _y;
		z = _z;
		force = _f;
		sensorDist = _sensor;
	}
}
