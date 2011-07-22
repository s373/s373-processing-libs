package s373.boids;

import java.util.ArrayList;

public class Boid2d {

	public float x, y, vx, vy, ax, ay;

	public Flock2d flock;

	// public Team team;
	// public int om;
	// public float forceSeparate, forceAlign, forceCohesion;
	// public float maxTurn, maxSpeed, maxForce;

	public Boid2d() {
	}

	public Boid2d(Flock2d flock) {
		this.flock = flock;
	}

	public Boid2d setFlock(Flock2d flock) {
		this.flock = flock;
		return this;
	}

	public Boid2d setLoc(float lx, float ly) {
		x = lx;
		y = ly;
		return this;
	}

	public Boid2d setVel(float velx, float vely) {
		vx = velx;
		vy = vely;
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
		// change this to allow flock flock interaction
		// accX = vec[0];
		// accY = vec[1];

		// limit force
		float distMaxForce = Math.abs(ax) + Math.abs(ay);
		if (distMaxForce > flock.maxForce) {
			distMaxForce = flock.maxForce / distMaxForce;
			ax *= distMaxForce;
			ay *= distMaxForce;
		}

		vx += ax;
		vy += ay;
		// limit speed
		float distMaxSpeed = Math.abs(vx) + Math.abs(vy);
		if (distMaxSpeed > flock.maxSpeed) {
			distMaxSpeed = flock.maxSpeed / distMaxSpeed;
			vx *= distMaxSpeed;
			vy *= distMaxSpeed;
		}

		x += vx;
		y += vy;

		bounds();

		// reset acc on end
		ax = 0;
		ay = 0;
	}

	// protected void updateInterfere(Flock2d otherFlock, final float amount) {
	//
	// float vec[] = flockfullInterfere(otherFlock, amount);
	//
	// ax += vec[0];
	// ay += vec[1];
	// // change this to allow flock flock interaction
	// // accX = vec[0];
	// // accY = vec[1];
	//
	// // limit force
	// float distMaxForce = Math.abs(ax) + Math.abs(ay);
	// if (distMaxForce > flock.maxForce) {
	// distMaxForce = flock.maxForce / distMaxForce;
	// ax *= distMaxForce;
	// ay *= distMaxForce;
	// }
	//
	// // System.out.print("boid update " + accX + " " + accY + "\n");
	//
	// vx += ax;
	// vy += ay;
	// // limit speed
	// float distMaxSpeed = Math.abs(vx) + Math.abs(vy);
	// if (distMaxSpeed > flock.maxSpeed) {
	// distMaxSpeed = flock.maxSpeed / distMaxSpeed;
	// vx *= distMaxSpeed;
	// vy *= distMaxSpeed;
	// }
	//
	// x += vx;
	// y += vy;
	//
	// bounds();
	//
	// // reset acc on end
	// ax = 0;
	// ay = 0;
	// }

	// protected void update(final float amount) {
	// float vec[] = flock(amount);
	// accX = vec[0];
	// accY = vec[1];
	// // limit force
	// float distMaxForce = Math.abs(accX) + Math.abs(accY);
	// if (distMaxForce > flock.maxForce) {
	// distMaxForce = flock.maxForce / distMaxForce;
	// accX *= distMaxForce;
	// accY *= distMaxForce;
	// }
	//
	// // System.out.print("boid update " + accX + " " + accY + "\n");
	//
	// velX += accX;
	// velY += accY;
	// // limit speed
	// float distMaxSpeed = Math.abs(velX) + Math.abs(velY);
	// if (distMaxSpeed > flock.maxSpeed) {
	// distMaxSpeed = flock.maxSpeed / distMaxSpeed;
	// velX *= distMaxSpeed;
	// velY *= distMaxSpeed;
	// }
	//
	// locX += velX;
	// locY += velY;
	//
	// bounds();
	// }

	private float[] flock(final float amount) {
		float vec[] = new float[2];

		float sep[] = separate(flock.boids);
		float ali[] = align(flock.boids);
		float coh[] = cohesion(flock.boids);

		// System.out.print("boid flock sep " + sep[0] + " " + sep[1] + " "
		// + flock.forceSeparate + "\n");

		sep[0] *= flock.separate;
		sep[1] *= flock.separate;

		ali[0] *= flock.align;
		ali[1] *= flock.align;

		coh[0] *= flock.cohesion;
		coh[1] *= flock.cohesion;

		vec[0] = sep[0] + ali[0] + coh[0];
		vec[1] = sep[1] + ali[1] + coh[1];
		final float d = Math.abs(vec[0]) + Math.abs(vec[1]);
		float invDist = 1f;
		if (d > 0)
			invDist = amount / d;// 1f / d;
		vec[0] *= invDist;
		vec[1] *= invDist;

		return vec;
	}

	private float[] steer(final float[] target, final float amount) {

		float steer[] = new float[2];
		float dir[] = new float[2];
		dir[0] = target[0] - x;
		dir[1] = target[1] - y;
		float d = Math.abs(dir[0]) + Math.abs(dir[1]);

		if (d > 2) {
			final float invDist = 1f / d;
			dir[0] *= invDist;
			dir[1] *= invDist;
			// steer, desired - vel
			steer[0] = dir[0] - vx;
			steer[1] = dir[1] - vy;
			float steerLen = Math.abs(steer[0]) + Math.abs(steer[1]);
			if (steerLen > 0) {
				float invSteerLen = amount / steerLen;// 1f / steerLen;
				steer[0] *= invSteerLen;
				steer[1] *= invSteerLen;
			}
		}

		return steer;

	}

	private float[] cohesion(ArrayList<Boid2d> b) {

		float cohesiondist = flock.distCohesion;
		float vec[] = new float[2];
		int count = 0;

		for (int i = 0; i < b.size(); i++) {
			Boid2d other = b.get(i);
			float dx = other.x - x;
			float dy = other.y - y;
			float d = Math.abs(dx) + Math.abs(dy);
			if (d > 0 && d < cohesiondist) {
				count++;
				vec[0] += other.x;// dx;
				vec[1] += other.y;// dy;
			}
		}

		if (count > 0) {
			final float invCount = 1f / count;
			vec[0] *= invCount;
			vec[1] *= invCount;
			return steer(vec, 1);
		}

		return vec;
	}

	private float[] align(ArrayList<Boid2d> b) {

		float aligndist = flock.distAlign;
		float vec[] = new float[2];
		int count = 0;

		for (int i = 0; i < b.size(); i++) {
			Boid2d other = b.get(i);
			float dx = other.x - x;
			float dy = other.y - y;
			float d = Math.abs(dx) + Math.abs(dy);
			if (d > 0 && d < aligndist) {
				count++;
				vec[0] += other.vx;// dx;
				vec[1] += other.vy;// dy;
			}
		}

		if (count > 0) {
			final float invCount = 1f / count;
			vec[0] *= invCount;
			vec[1] *= invCount;
		}

		return vec;

	}

	private float[] separate(final ArrayList<Boid2d> b) {

		float separatedist = flock.distSeparation;
		float vec[] = new float[2];
		int count = 0;

		for (int i = 0; i < b.size(); i++) {
			Boid2d other = b.get(i);
			float dx = other.x - x;
			float dy = other.y - y;
			float d = Math.abs(dx) + Math.abs(dy);
			if (d > 0 && d < separatedist) {
				count++;
				// / mais longe influenciam mais?
				// vec[0] += -dx;
				// vec[1] += -dy;
				float invD = 1f / d;
				vec[0] += -dx * invD;
				vec[1] += -dy * invD;
			}
		}

		if (count > 0) {
			final float invCount = 1f / count;
			vec[0] *= invCount;
			vec[1] *= invCount;
		}

		return vec;
	}

	/*
	 * integration of all forces in single eq now with attraction points builtin
	 */

	private float[] flockfull(final float amount) {
		float vec[] = new float[2];

		float sep[] = new float[2];// separate(flock.boids);
		float ali[] = new float[2];// align(flock.boids);
		float coh[] = new float[2];// cohesion(flock.boids);
		float attrForce[] = new float[2];
		int countsep = 0, countali = 0, countcoh = 0;

		float separatedist = flock.distSeparation;
		float aligndist = flock.distAlign;
		float cohesiondist = flock.distCohesion;
		float invD = 0;

		// boolean hasAttractionPoints = flock.hasAttractionPoints();

		// main full loop track all forces boid other boids
		for (int i = 0; i < flock.boids.size(); i++) {
			Boid2d other = flock.boids.get(i);
			float dx = other.x - x;
			float dy = other.y - y;
			float d = Math.abs(dx) + Math.abs(dy);
			if (d <= 1e-7)
				continue;

			// sep
			if (d < separatedist) {
				countsep++;
				invD = 1f / d;
				sep[0] -= dx * invD;
				sep[1] -= dy * invD;
			}

			// coh
			if (d < cohesiondist) {
				countcoh++;
				coh[0] += other.x;
				coh[1] += other.y;
			}

			// ali
			if (d < aligndist) {
				countali++;
				ali[0] += other.vx;
				ali[1] += other.vy;
			}

		}

		if (countsep > 0) {
			final float invForSep = flock.separate / (float) countsep;
			sep[0] *= invForSep;
			sep[1] *= invForSep;
		}
		if (countali > 0) {
			// final float invForAli = 1f / (float) countali;
			final float invForAli = flock.align / (float) countali;
			ali[0] *= invForAli;
			ali[1] *= invForAli;
		}
		if (countcoh > 0) {
			final float invForCoh = flock.cohesion / (float) countcoh;
			coh[0] *= invForCoh;
			coh[1] *= invForCoh;
			coh = steer(coh, 1);
		}

		// if using extra forces, place here

		// sep[0] *= flock.separate;
		// sep[1] *= flock.separate;
		//
		// ali[0] *= flock.align;
		// ali[1] *= flock.align;
		//
		// coh[0] *= flock.cohesion;
		// coh[1] *= flock.cohesion;

		// other forces
		if (flock.hasAttractionPoints()) {
			for (int i = 0; i < flock.attractionPoints.size(); i++) {
				AttractionPoint2d point = flock.attractionPoints.get(i);
				float dx = point.x - x;
				float dy = point.y - y;
				float d = Math.abs(dx) + Math.abs(dy);
				if (d <= 1e-7)
					continue;
				if (d > point.sensorDist)
					continue;

				// inbounds, calc
				float invForce = point.force / d;
				dx *= invForce;
				dy *= invForce;

				attrForce[0] += dx;
				attrForce[1] += dy;
			}

		}

		vec[0] = sep[0] + ali[0] + coh[0] + attrForce[0];
		vec[1] = sep[1] + ali[1] + coh[1] + attrForce[1];
		final float d = Math.abs(vec[0]) + Math.abs(vec[1]);
		if (d > 0) {
			float invDist = amount / d;
			vec[0] *= invDist;
			vec[1] *= invDist;
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
	// Boid2d other = i < flockSize ? flock.boids.get(i)
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
