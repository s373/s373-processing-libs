package s373.boids;

public class Boid3d {

	public float x, y, z, vx, vy, vz, ax, ay, az;

	public Flock3d flock;

	// public Team team;
	// public int om;
	// public float forceSeparate, forceAlign, forceCohesion;
	// public float maxTurn, maxSpeed, maxForce;

	public Boid3d() {
	}

	public Boid3d(Flock3d flock) {
		this.flock = flock;
	}

	public Boid3d setFlock(Flock3d flock) {
		this.flock = flock;
		return this;
	}

	public Boid3d setLoc(float lx, float ly, float lz) {
		x = lx;
		y = ly;
		z = lz;
		return this;
	}

	public Boid3d setVel(float velx, float vely, float velz) {
		vx = velx;
		vy = vely;
		vz = velz;
		return this;
	}

	private void bounds() {
		switch (flock.boundmode) {
		case 0: // CLAMP
			if (x < flock.minX) {
				x = flock.minX;
				vx = -vx;
			}
			if (x > flock.maxX) {
				x = flock.maxX;
				vx = -vx;
			}
			if (y < flock.minY) {
				y = flock.minY;
				vy = -vy;
			}
			if (y > flock.maxY) {
				y = flock.maxY;
				vy = -vy;
			}
			if (z < flock.minZ) {
				z = flock.minZ;
				vz = -vz;
			}
			if (z > flock.maxZ) {
				z = flock.maxZ;
				vz = -vz;
			}
			break;
		case 1: // WRAP
			if (x < flock.minX) {
				x += flock.boundsWidth;
			}
			if (x > flock.maxX) {
				x -= flock.boundsWidth;
			}
			if (y < flock.minY) {
				y += flock.boundsHeight;
			}
			if (y > flock.maxY) {
				y -= flock.boundsHeight;
			}
			if (z < flock.minZ) {
				z += flock.boundsDepth;
			}
			if (z > flock.maxZ) {
				z -= flock.boundsDepth;
			}
			break;
		}

	}

	/*
	 * main funcs
	 */

	protected void update(final float amount) {

		// float vec[] = flock(amount);// flockfull(amount);
		float vec[] = flockfull(amount);

		ax += vec[0];
		ay += vec[1];
		az += vec[2];
		// change this to allow flock flock interaction
		// accX = vec[0];
		// accY = vec[1];

		// limit force
		float distMaxForce = Math.abs(ax) + Math.abs(ay) + Math.abs(az);
		if (distMaxForce > flock.maxForce) {
			distMaxForce = flock.maxForce / distMaxForce;
			ax *= distMaxForce;
			ay *= distMaxForce;
			az *= distMaxForce;
		}

		vx += ax;
		vy += ay;
		vz += az;
		// limit speed
		float distMaxSpeed = Math.abs(vx) + Math.abs(vy) + Math.abs(vz);
		if (distMaxSpeed > flock.maxSpeed) {
			distMaxSpeed = flock.maxSpeed / distMaxSpeed;
			vx *= distMaxSpeed;
			vy *= distMaxSpeed;
			vz *= distMaxSpeed;
		}

		x += vx;
		y += vy;
		z += vz;

		bounds();

		// reset acc on end
		ax = 0;
		ay = 0;
		az = 0;
	}

	private float[] steer(final float[] target, final float amount) {

		float steer[] = new float[3];
		float dir[] = new float[3];
		dir[0] = target[0] - x;
		dir[1] = target[1] - y;
		dir[2] = target[2] - z;
		float d = Math.abs(dir[0]) + Math.abs(dir[1]) + Math.abs(dir[2]);

		if (d > 2) {
			final float invDist = 1f / d;
			dir[0] *= invDist;
			dir[1] *= invDist;
			dir[2] *= invDist;
			// steer, desired - vel
			steer[0] = dir[0] - vx;
			steer[1] = dir[1] - vy;
			steer[2] = dir[2] - vz;
			float steerLen = Math.abs(steer[0]) + Math.abs(steer[1])
					+ Math.abs(steer[2]);
			if (steerLen > 0) {
				float invSteerLen = amount / steerLen;// 1f / steerLen;
				steer[0] *= invSteerLen;
				steer[1] *= invSteerLen;
				steer[2] *= invSteerLen;
			}
		}

		return steer;

	}

	private float[] flockfull(final float amount) {
		float vec[] = new float[3];

		float sep[] = new float[3];// separate(flock.boids);
		float ali[] = new float[3];// align(flock.boids);
		float coh[] = new float[3];// cohesion(flock.boids);
		float attrForce[] = new float[3];
		int countsep = 0, countali = 0, countcoh = 0;

		float separatedist = flock.distSeparation;
		float aligndist = flock.distAlign;
		float cohesiondist = flock.distCohesion;
		float invD = 0;

		// main full loop track all forces boid other boids
		for (int i = 0; i < flock.boids.size(); i++) {
			Boid3d other = flock.boids.get(i);
			float dx = other.x - x;
			float dy = other.y - y;
			float dz = other.z - z;
			float d = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
			if (d <= 1e-7)
				continue;

			// sep
			if (d < separatedist) {
				countsep++;
				invD = 1f / d;
				sep[0] -= dx * invD;
				sep[1] -= dy * invD;
				sep[2] -= dz * invD;
			}

			// coh
			if (d < cohesiondist) {
				countcoh++;
				coh[0] += other.x;
				coh[1] += other.y;
				coh[2] += other.z;
			}

			// ali
			if (d < aligndist) {
				countali++;
				ali[0] += other.vx;
				ali[1] += other.vy;
				ali[2] += other.vz;
			}

		}

		if (countsep > 0) {
			final float invForSep = flock.separate / (float) countsep;
			sep[0] *= invForSep;
			sep[1] *= invForSep;
			sep[2] *= invForSep;
		}
		if (countali > 0) {
			final float invForAli = flock.align / (float) countali;
			ali[0] *= invForAli;
			ali[1] *= invForAli;
			ali[2] *= invForAli;
		}
		if (countcoh > 0) {
			final float invForCoh = flock.cohesion / (float) countcoh;
			coh[0] *= invForCoh;
			coh[1] *= invForCoh;
			coh[2] *= invForCoh;
			coh = steer(coh, 1);
		}

		// other forces
		if (flock.hasAttractionPoints()) {
			for (int i = 0; i < flock.attractionPoints.size(); i++) {
				AttractionPoint3d point = flock.attractionPoints.get(i);
				float dx = point.x - x;
				float dy = point.y - y;
				float dz = point.z - z;
				float d = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
				if (d <= 1e-7)
					continue;
				if (d > point.sensorDist)
					continue;

				// inbounds, calc
				float invForce = point.force / d;
				dx *= invForce;
				dy *= invForce;
				dz *= invForce;

				attrForce[0] += dx;
				attrForce[1] += dy;
				attrForce[2] += dz;
			}

		}

		vec[0] = sep[0] + ali[0] + coh[0] + attrForce[0];
		vec[1] = sep[1] + ali[1] + coh[1] + attrForce[1];
		vec[2] = sep[2] + ali[2] + coh[2] + attrForce[2];
		final float d = Math.abs(vec[0]) + Math.abs(vec[1]) + Math.abs(vec[2]);
		if (d > 0) {
			float invDist = amount / d;// 1f / d;
			vec[0] *= invDist;
			vec[1] *= invDist;
			vec[2] *= invDist;
		}
		return vec;
	}

	// private float[] flockfullInterfere(Flock2d otherFlock, final float
	// amount) {
	// float vec[] = new float[2];
	//
	// float sep[] = new float[2];// separate(flock.boids);
	// float ali[] = new float[2];// align(flock.boids);
	// float coh[] = new float[2];// cohesion(flock.boids);
	// int countsep = 0, countali = 0, countcoh = 0;
	//
	// float separatedist = flock.distSeparation;
	// float aligndist = flock.distAlign;
	// float cohesiondist = flock.distCohesion;
	// float invD = 0;
	//
	// int flockSize = flock.boids.size();
	// int NumAgents = flockSize + otherFlock.size();
	// // main full loop track all forces
	// for (int i = 0; i < NumAgents; i++) {
	// Boid3d other = i < flockSize ? flock.boids.get(i)
	// : otherFlock.boids.get(i - flockSize);
	// float dx = other.x - x;
	// float dy = other.y - y;
	// float d = Math.abs(dx) + Math.abs(dy);
	// if (d <= 0)
	// continue;
	//
	// // sep
	// if (d < separatedist) {
	// countsep++;
	// invD = 1f / d;
	// sep[0] -= dx * invD;
	// sep[1] -= dy * invD;
	// }
	//
	// // coh
	// if (d < cohesiondist) {
	// countcoh++;
	// coh[0] += other.x;
	// coh[1] += other.y;
	// }
	//
	// // ali
	// if (d < aligndist) {
	// countali++;
	// ali[0] += other.vx;
	// ali[1] += other.vy;
	// }
	//
	// }
	//
	// if (countsep > 0) {
	// final float invForSep = flock.separate / countsep;
	// sep[0] *= invForSep;
	// sep[1] *= invForSep;
	// }
	// if (countali > 0) {
	// final float invForAli = flock.align / countali;
	// ali[0] *= invForAli;
	// ali[1] *= invForAli;
	// }
	// if (countcoh > 0) {
	// final float invForCoh = flock.cohesion / countcoh;
	// coh[0] *= invForCoh;
	// coh[1] *= invForCoh;
	// }
	//
	// // if using extra forces, place here
	//
	// // sep[0] *= flock.forceSeparate;
	// // sep[1] *= flock.forceSeparate;
	// //
	// // ali[0] *= flock.forceAlign;
	// // ali[1] *= flock.forceAlign;
	// //
	// // coh[0] *= flock.forceCohesion;
	// // coh[1] *= flock.forceCohesion;
	//
	// vec[0] = sep[0] + ali[0] + coh[0];
	// vec[1] = sep[1] + ali[1] + coh[1];
	// final float d = Math.abs(vec[0]) + Math.abs(vec[1]);
	// float invDist = 1f;
	// if (d > 0)
	// invDist = amount / d;// 1f / d;
	// vec[0] *= invDist;
	// vec[1] *= invDist;
	//
	// return vec;
	// }

}
