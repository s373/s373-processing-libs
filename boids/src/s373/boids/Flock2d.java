package s373.boids;

import java.util.ArrayList;

//import s373.math.s373Math;

public class Flock2d {

	public ArrayList<Boid2d> boids;
	public ArrayList<AttractionPoint2d> attractionPoints;

	// public ArrayList<Team> teams; // superclass flockTeam, etc

	// forces
	protected float separate, align, cohesion;
	protected float distSeparation, distAlign, distCohesion;
	protected float maxTurn, maxSpeed, maxForce;
	// bounds
	protected float minX, minY, maxX, maxY, boundsWidth, boundsHeight;
	protected int boundmode;

	// / construcores
	public Flock2d(int num, float lx, float ly) {
		this(num, lx, ly, 1);
	}

	public Flock2d(int num, float lx, float ly, float dev) {
		boids = new ArrayList<Boid2d>();
		attractionPoints = new ArrayList<AttractionPoint2d>();

		for (int i = 0; i < num; i++) {
			Boid2d b = new Boid2d(this);
			// need to be scattered or else no work
			b.setLoc(lx + RndUtils.random(-dev, dev),
					ly + RndUtils.random(-dev, dev));
			// b.setLoc(lx, ly);
			boids.add(b);
		}

		defaultValues();
	}

	private void defaultValues() {
		boundmode = 0;
		separate = 55.0f;
		align = 12.0f;
		cohesion = 7.0f;
		distSeparation = 50.0f;
		distAlign = 100.0f;
		distCohesion = 75.0f;
		// maxTurn, maxSpeed, maxForce;
		maxSpeed = 2.f;
		maxForce = 10000.0f;
		// minX = 0; minY = 0; maxX = 700; maxY, boundsWidth, boundsHeight;
		setBounds(0, 0, 700, 700);
	}

	// metodos

	public Flock2d add(float lx, float ly) {
		Boid2d b = new Boid2d(this);
		b.setLoc(lx, ly);
		boids.add(b);
		return this;
	}

	public Flock2d setMaxTurn(float mt) {
		maxTurn = mt;
		return this;
	}

	public Flock2d setMaxSpeed(float ms) {
		maxSpeed = ms;
		return this;
	}

	public Flock2d setMaxForce(float mf) {
		maxForce = mf;
		return this;
	}

	public float getMaxTurn() {
		return maxTurn;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public float getMaxForce() {
		return maxForce;
	}

	public float getSeparate() {
		return separate;
	}

	public Flock2d setSeparate(float forceSeparate) {
		this.separate = forceSeparate;
		return this;
	}

	public Flock2d setAlign(float forceAlign) {
		this.align = forceAlign;
		return this;
	}

	public Flock2d setCohesion(float forceCohesion) {
		this.cohesion = forceCohesion;
		return this;
	}

	public float getAlign() {
		return align;
	}

	public float getCohesion() {
		return cohesion;
	}

	/**
	 * @return the distSeparation
	 */
	public float getSeparation() {
		return distSeparation;
	}

	/**
	 * @return the distNeighborDistance
	 */
	public float getDistAlign() {
		return distAlign;
	}

	/**
	 * @return the distNeighborDistance
	 */
	public float getDistCohesion() {
		return distCohesion;
	}

	/**
	 * @param distSeparation
	 *            the distSeparation to set
	 */
	public Flock2d setDistSeparation(float distSeparation) {
		this.distSeparation = distSeparation;
		return this;
	}

	/**
	 * @return the distNeighborDistance
	 */
	public Flock2d setDistAlign(float d) {
		distAlign = d;
		return this;
	}

	/**
	 * @return the distNeighborDistance
	 */
	public Flock2d setDistCohesion(float d) {
		distCohesion = d;
		return this;
	}

	/**
	 *
	 */
	public Flock2d setBounds(float minx, float miny, float maxx, float maxy) {
		minX = minx;
		minY = miny;
		maxX = maxx;
		maxY = maxy;
		boundsWidth = maxX - minX;
		boundsHeight = maxY - minY;

		return this;
	}

	/**
	 * @return the boundmode
	 */
	public int getBoundmode() {
		return boundmode;
	}

	/**
	 * @param boundmode
	 *            the boundmode to set
	 */
	public Flock2d setBoundmode(int boundmode) {
		this.boundmode = boundmode;
		return this;
	}

	/*
	 * update func
	 */
	public void update(float amount) {
		int boidsSize = boids.size();
		for (int i = 0; i < boidsSize; i++) {
			Boid2d b = boids.get(i);
			b.update(amount);
		}
	}

	// public void interfere(Flock2d otherFlock, float amount) {
	//
	// int boidsSize = boids.size();
	// for (int i = 0; i < boidsSize; i++) {
	// Boid2d b = boids.get(i);
	// b.update(amount);
	// }
	// }

	// public void set() {
	// int boidsSize = boids.size();
	// for (int i = 0; i < boidsSize; i++) {
	// Boid2d b = boids.get(i);
	// b.setFlock(this);
	// }
	// }

	public int size() {
		return boids.size();
	}

	public Boid2d get(int idx) {
		return boids.get(idx);
	}

	// / attraction points

	public Flock2d addAttractionPoint(float x, float y, float force,
			float sensorDist) {
		AttractionPoint2d ap = new AttractionPoint2d(x, y, force, sensorDist);
		attractionPoints.add(ap);
		return this;
	}

	public ArrayList<AttractionPoint2d> getAttractionPoints() {
		return attractionPoints;
	}

	public boolean hasAttractionPoints() {
		return attractionPoints.size() > 0;
	}

	public void changeAttractionPoint(int id, float x, float y, float force,
			float sensorDist) {
		try {
			AttractionPoint2d ap = attractionPoints.get(id);
			ap.x = x;
			ap.y = y;
			ap.force = force;
			ap.sensorDist = sensorDist;
		} catch (Exception e) {
			System.out.print("error in changeAttractionPoint \n");
		}
	}
}