package s373.boids;

import java.util.ArrayList;

public class Flock3d {

	public ArrayList<Boid3d> boids;
	public ArrayList<AttractionPoint3d> attractionPoints;

	// public ArrayList<Team> teams; // superclass flockTeam, etc

	// forces
	protected float separate, align, cohesion;
	protected float distSeparation, distAlign, distCohesion;
	protected float maxTurn, maxSpeed, maxForce;
	// bounds
	protected float minX, minY, maxX, maxY, minZ, maxZ, boundsWidth,
			boundsHeight, boundsDepth;
	protected int boundmode;

	// / construcores
	public Flock3d(int num, float lx, float ly, float lz) {
		this(num, lx, ly, lz, 1);
	}

	public Flock3d(int num, float lx, float ly, float lz, float dev) {
		boids = new ArrayList<Boid3d>();
		attractionPoints = new ArrayList<AttractionPoint3d>();

		for (int i = 0; i < num; i++) {
			Boid3d b = new Boid3d(this);
			// need to be scattered or else no work
			b.setLoc(lx + RndUtils.random(-dev, dev),
					ly + RndUtils.random(-dev, dev),
					lz + RndUtils.random(-dev, dev));
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
		setBounds(0, 0, -700, 700, 700, 0);
	}

	// metodos

	public Flock3d add(float lx, float ly, float lz) {
		Boid3d b = new Boid3d(this);
		b.setLoc(lx, ly, lz);
		boids.add(b);
		return this;
	}

	public Flock3d setMaxTurn(float mt) {
		maxTurn = mt;
		return this;
	}

	public Flock3d setMaxSpeed(float ms) {
		maxSpeed = ms;
		return this;
	}

	public Flock3d setMaxForce(float mf) {
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

	public Flock3d setSeparate(float forceSeparate) {
		this.separate = forceSeparate;
		return this;
	}

	public Flock3d setAlign(float forceAlign) {
		this.align = forceAlign;
		return this;
	}

	public Flock3d setCohesion(float forceCohesion) {
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
	public Flock3d setDistSeparation(float distSeparation) {
		this.distSeparation = distSeparation;
		return this;
	}

	/**
	 * @return the distNeighborDistance
	 */
	public Flock3d setDistAlign(float d) {
		distAlign = d;
		return this;
	}

	/**
	 * @return the distNeighborDistance
	 */
	public Flock3d setDistCohesion(float d) {
		distCohesion = d;
		return this;
	}

	/**
		 *
		 */
	public Flock3d setBounds(float minx, float miny, float minz, float maxx,
			float maxy, float maxz) {
		minX = minx;
		minY = miny;
		maxX = maxx;
		maxY = maxy;
		minZ = minz;
		maxZ = maxz;
		boundsWidth = maxX - minX;
		boundsHeight = maxY - minY;
		boundsDepth = maxZ - minZ;

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
	public Flock3d setBoundmode(int boundmode) {
		this.boundmode = boundmode;
		return this;
	}

	/*
	 * update func
	 */
	public void update(float amount) {
		int boidsSize = boids.size();
		for (int i = 0; i < boidsSize; i++) {
			Boid3d b = boids.get(i);
			b.update(amount);
		}
	}

	public int size() {
		return boids.size();
	}

	public Boid3d get(int idx) {
		return boids.get(idx);
	}

	// / attraction points

	public Flock3d addAttractionPoint(float x, float y, float z, float force,
			float sensorDist) {
		AttractionPoint3d ap = new AttractionPoint3d(x, y, z, force, sensorDist);
		attractionPoints.add(ap);
		return this;
	}

	public ArrayList<AttractionPoint3d> getAttractionPoints() {
		return attractionPoints;
	}

	public boolean hasAttractionPoints() {
		return attractionPoints.size() > 0;
	}

	public void changeAttractionPoint(int id, float x, float y, float z,
			float force, float sensorDist) {
		try {
			AttractionPoint3d ap = attractionPoints.get(id);
			ap.x = x;
			ap.y = y;
			ap.z = z;
			ap.force = force;
			ap.sensorDist = sensorDist;
		} catch (Exception e) {
			System.out.print("error in changeAttractionPoint \n");
		}
	}
}
