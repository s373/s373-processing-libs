package s373.marchingcubes;

public class TRIANGLE {
	public XYZ p[] = new XYZ[3];
	public XYZ n = new XYZ(0, 1, 0);

	public TRIANGLE() {
		for (int i = 0; i < 3; i++) {
			p[i] = new XYZ();
		}
	}

	public TRIANGLE(TRIANGLE t) {
		n = new XYZ(t.n);
		for (int i = 0; i < 3; i++) {
			p[i] = new XYZ(t.p[i]);
		}
	}

	public void calcnormal(boolean invertnormals) {
		// PVector nv = new PVector();
		// PVector v2v1 = new PVector( (float)(p[1].x-p[0].x), (float)(p[1].y -
		// p[0].y), (float)(p[1].z - p[0].z ) );
		// PVector v3v1 = new PVector( (float)(p[2].x-p[0].x), (float)(p[2].y -
		// p[0].y), (float)(p[2].z - p[0].z) );

		XYZ nv = new XYZ();
		XYZ v1 = new XYZ((p[1].x - p[0].x), (p[1].y - p[0].y),
				(p[1].z - p[0].z));
		XYZ v2 = new XYZ((p[2].x - p[0].x), (p[2].y - p[0].y),
				(p[2].z - p[0].z));

		nv.x = (v1.y * v2.z) - (v1.z * v2.y);
		nv.y = (v1.z * v2.x) - (v1.x * v2.z);
		nv.z = (v1.x * v2.y) - (v1.y * v2.x);
		// recheck, this
		// nv.normalize();
		// float d = nv.x*nv.x+nv.y*nv.y+nv.z*nv.z;
		// if (d>0) {
		// d = (float) (1.0/Math.sqrt(d));
		// nv.x*=d;
		// nv.y*=d;
		// nv.z*=d;
		// }
		if (!invertnormals) {
			n.x = nv.x;
			n.y = nv.y;
			n.z = nv.z;
		} else {
			n.x = -nv.x;
			n.y = -nv.y;
			n.z = -nv.z;
		}
	}
}
