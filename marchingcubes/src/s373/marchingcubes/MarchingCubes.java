
/*
 marching cubes implementation by andre sier in processing, 2010
 	http://s373.net/code/marchingcubes
 adapted code from paul bourke's polygonizing a scale field (marching cubes), 
 	http://paulbourke.net/geometry/polygonise/
 stl export code adapted from marius watz, 
 	http://workshop.evolutionzone.com/unlekkerlib/
 */



package s373.marchingcubes;


import s373.marchingcubes.GRIDCELL;
import s373.marchingcubes.XYZ;
import s373.marchingcubes.TRIANGLE;
import s373.marchingcubes.RndUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import java.util.ArrayList;
import java.util.Date;

import processing.core.PApplet;
import processing.core.PConstants;

public class MarchingCubes {
	public PApplet applet;
	String VERSION = "s373.marchingcubes - 0.1.0 ";
	final String VERSIONURL = " - http://s373.net/code/marchingcubes \n";

	// obj data
	public GRIDCELL grid;
	public float isolevel;
	public TRIANGLE[] triangles;

	public int gx, gy, gz, numxyz, gxgy;
	public float data[];
	public float themin, themax;
	public ArrayList<TRIANGLE> trilist;
	public int ntri = 0;
	public boolean invertnormals = true;
	public boolean closesides = true;

	// draw data
	public XYZ worlddim, worldstride, worldcenter, datastride;
	public String info;

	// file data
	public String filename;
	public File file;
	public byte[] header, byte4;
	public ByteBuffer buf;


	public MarchingCubes(PApplet pa, float sx, float sy, float sz, int x,
			int y, int z) {
		applet = pa;
		initResolution(x, y, z);
		setWorldDim(sx, sy, sz);

		triangles = new TRIANGLE[5];
		for (int i = 0; i < triangles.length; i++) {
			triangles[i] = new TRIANGLE();
		}
		isolevel = 0.0025f;
		grid = new GRIDCELL();
		polygoniseData();
		version();
		System.out.print(getinfo());

	}

	public void setWorldDim(float x, float y, float z) {
		worlddim = new XYZ(x, y, z);
		worldstride = new XYZ(gx / x, gy / y, gz / z);
		datastride = new XYZ(x / gx, y / gy, z / gz);
		worldcenter = new XYZ(x/2.0f, y/2.0f, z/2.0f);
		System.out.print("\nworlddim: " + x + " " + y + " " + z + "\n");
		System.out.print("worldcenter: " + worldcenter.x + " " + worldcenter.y
				+ " " + worldcenter.z + "\n");
		System.out.print("worldstride: " + worldstride.x + " " + worldstride.y
				+ " " + worldstride.z + "\n");
		System.out.print("datastride: " + datastride.x + " " + datastride.y
				+ " " + datastride.z + "\n");
	}

	public void initResolution(int x, int y, int z) {
		gx = x;
		gy = y;
		gz = z;
		gxgy = x * y;
		numxyz = x * y * z;
		data = new float[numxyz];
		for (int i = 0; i < numxyz; i++) {
			data[i] = RndUtils.random(0.0f, 0.7f);
		}
		trilist = new ArrayList<TRIANGLE>();
	}

	public String getinfo() {
		info = "tris: " + ntri + " volume: " + gx + " " + gy
		+ " " + gz + " cells: " + numxyz + " iso: " + isolevel;
		return info;
	}

	public XYZ getCenter(){
		return worldcenter;
	}
	
	
	/*
	 * ADDING VALUES methods
	 */
	
	public final int getIndex(XYZ ptpos){
		int cx = (int) (ptpos.x * worldstride.x);
		int cy = (int) (ptpos.y * worldstride.y);
		int cz = (int) (ptpos.z * worldstride.z);
		if (closesides) {
			if (cx < 1) {
				cx = 1;
			}
			if (cy < 1) {
				cy = 1;
			}
			if (cz < 1) {
				cz = 1;
			}
			if (cx >= gx - 1) {
				cx = gx - 2;
			}
			if (cy >= gy - 1) {
				cy = gy - 2;
			}
			if (cz >= gz - 1) {
				cz = gz - 2;
			}
		} else {
			if (cx < 0) {
				cx = 0;
			}
			if (cy < 0) {
				cy = 0;
			}
			if (cz < 0) {
				cz = 0;
			}
			if (cx >= gx) {
				cx = gx - 1;
			}
			if (cy >= gy) {
				cy = gy - 1;
			}
			if (cz >= gz) {
				cz = gz - 1;
			}
		}
		return cx + cy * gx + cz * gx * gy;
	}
	
	
	public void addIsoPoint(float val, XYZ ptpos) {
		final int idx = getIndex(ptpos);
		data[idx] += val;
	}

	public void addCube(float val, XYZ ptpos, XYZ ptdim) {
		// replace this getindex
		
		int cx = (int) (ptpos.x * worldstride.x); // // width * gx);
		int cy = (int) (ptpos.y * worldstride.y);
		int cz = (int) (ptpos.z * worldstride.z);

		if (closesides) {
			if (cx < 1) {
				cx = 1;
			}
			if (cy < 1) {
				cy = 1;
			}
			if (cz < 1) {
				cz = 1;
			}
			if (cx >= gx - 1) {
				cx = gx - 2;
			}
			if (cy >= gy - 1) {
				cy = gy - 2;
			}
			if (cz >= gz - 1) {
				cz = gz - 2;
			}
		} else {
			if (cx < 0) {
				cx = 0;
			}
			if (cy < 0) {
				cy = 0;
			}
			if (cz < 0) {
				cz = 0;
			}
			if (cx >= gx) {
				cx = gx - 1;
			}
			if (cy >= gy) {
				cy = gy - 1;
			}
			if (cz >= gz) {
				cz = gz - 1;
			}
		}

		int dimx = (int) (ptdim.x * worldstride.x); // // width * gx);
		int dimy = (int) (ptdim.y * worldstride.y);
		int dimz = (int) (ptdim.z * worldstride.z);

		if (dimx < 2) {
			dimx = 2;
		}
		if (dimy < 2) {
			dimy = 2;
		}
		if (dimz < 2) {
			dimz = 2;
		}
		addCube(val, cx, cy, cz, dimx, dimy, dimz);
	}

	private void addCube(float val, int centerx, int centery, int centerz,
			int dimx, int dimy, int dimz) {

		final int hx = dimx / 2;
		final int hy = dimy / 2;
		final int hz = dimz / 2;

		int sx = centerx - hx;
		int sy = centery - hy;
		int sz = centerz - hz;

		if (sx < 1) {
			sx = 1;
		}
		if (sy < 1) {
			sy = 1;
		}
		if (sz < 1) {
			sz = 1;
		}

		final int tx = Math.min(centerx + hx, gx - 1);
		final int ty = Math.min(centery + hy, gy - 1);
		final int tz = Math.min(centerz + hz, gz - 1);

		// println("adding cube: "+val+" "+sz+" "+tz+" "+dimz);

		int idx = 0;
		for (int i = sx; i < tx; i++) {
			for (int j = sy; j < ty; j++) {
				for (int k = sz; k < tz; k++) {
					idx = i + j * gx + k * gx * gy;
					data[idx] += val;
				}
			}
		}
	}

	public void zeroData() {
		themin = 0;
		themax = 0;
		for (int i = 0; i < numxyz; i++) {
			data[i] = 0.0f;
		}
	}

	public void setRndData(float mn, float mx) {
		for (int i = 0; i < numxyz; i++) {
			data[i] = RndUtils.random(mn, mx);
		}
	}
	public void setRndData(float mx) {
		for (int i = 0; i < numxyz; i++) {
			data[i] = RndUtils.random(mx);
		}
	}

	public void setData(float d[]) {
		for (int i = 0; i < numxyz; i++) {
			data[i] = d[i];
		}
	}

	public void addData(float d[]) {
////phps add close sides here
		for (int i = 0; i < numxyz; i++) {
			data[i] += d[i];
		}
	}

	public float[] getData() {
		return data;
	}

	public boolean isEmpty(){
		boolean empty = true;
		for(int i=0; i< numxyz; i++){
			if(data[i]>0){
				empty = false;
				break;
			}
		}
		return empty;
	}

	public void multData(final float v){
		for (int i = 0; i < numxyz; i++) {
			data[i] *= v;
		}
	}
	
	public void normalizeDataTo(final float v){
		checkMinMax();
		float dist = themax - themin;
		final float dst = v / dist;// 1.0f / v;
		multData(dst);
	}
	
	public void checkMinMax(){
		themin = (float) 1e10;
		themax = (float) -1e10;
		for (int i = 0; i < numxyz; i++) {
			if (data[i] > themax) {
				themax = data[i];
			}
			if (data[i] < themin) {
				themin = data[i];
			}
		}	
	}
	
	public void datainvert() {

		float max = -1;
		for (int i = 0; i < numxyz; i++) {
			if(data[i]>max){
				max = data[i];
			}
		}
		
		for (int i = 0; i < numxyz; i++) {
			data[i] = max - data[i];
		}
		
	}
	
	public void datasubs(float v) {

		for (int i = 0; i < numxyz; i++) {
			data[i] = v - data[i];
		}
		
//		normalizeDataTo(1);
//		for (int i = 0; i < numxyz; i++) {
//			if(data[i] > 0)
//				data[i] = 0.0f;
//			else
//				data[i] = 1.0f;
//		}
	}

	public void post(){
		System.out.print("\n");
		for (int i = 0; i < numxyz; i++) {
			System.out.print(" "+data[i]);// = v - data[i];
		}
		System.out.print("\n");
	}
	
	
	
	// ///
	public void polygoniseData() {

		// Polygonise the grid
		// println("Polygonising data ...\n");
//		final int idx = 0;
		ntri = 0;
		trilist.clear();
		for (int i = 0; i < gx - 1; i++) {
			// if (i % (gx/10) == 0)
			// println("   Slice "+i+" of "+gx);
			for (int j = 0; j < gy - 1; j++) {
				for (int k = 0; k < gz - 1; k++) {
					grid.p[0].x = i * datastride.x;
					grid.p[0].y = j * datastride.y;
					grid.p[0].z = k * datastride.z;
					grid.val[0] = data[i + j * gx + k * gxgy];
					grid.p[1].x = (i + 1) * datastride.x;
					grid.p[1].y = j * datastride.y;
					grid.p[1].z = k * datastride.z;
					grid.val[1] = data[i + 1 + j * gx + k * gxgy];
					grid.p[2].x = (i + 1) * datastride.x;
					grid.p[2].y = (j + 1) * datastride.y;
					grid.p[2].z = k * datastride.z;
					grid.val[2] = data[i + 1 + (j + 1) * gx + k * gxgy];
					grid.p[3].x = i * datastride.x;
					grid.p[3].y = (j + 1) * datastride.y;
					grid.p[3].z = k * datastride.z;
					grid.val[3] = data[i + (j + 1) * gx + k * gxgy];
					grid.p[4].x = i * datastride.x;
					grid.p[4].y = j * datastride.y;
					grid.p[4].z = (k + 1) * datastride.z;
					grid.val[4] = data[i + j * gx + (k + 1) * gxgy];
					grid.p[5].x = (i + 1) * datastride.x;
					grid.p[5].y = j * datastride.y;
					grid.p[5].z = (k + 1) * datastride.z;
					grid.val[5] = data[i + 1 + j * gx + (k + 1) * gxgy];
					grid.p[6].x = (i + 1) * datastride.x;
					grid.p[6].y = (j + 1) * datastride.y;
					grid.p[6].z = (k + 1) * datastride.z;
					grid.val[6] = data[i + 1 + (j + 1) * gx + (k + 1) * gxgy];
					grid.p[7].x = i * datastride.x;
					grid.p[7].y = (j + 1) * datastride.y;
					grid.p[7].z = (k + 1) * datastride.z;
					grid.val[7] = data[i + (j + 1) * gx + (k + 1) * gxgy];
					final int n = Polygonise(grid, isolevel, triangles);

					// calc tri norms
					for (int a0 = 0; a0 < n; a0++) {
						triangles[a0].calcnormal(invertnormals);
					}


					for (int l = 0; l < n; l++) {
						final TRIANGLE t = new TRIANGLE(triangles[l]);
						trilist.add(t);
					}
					ntri += n;
				}
			}
		}
		// println("Total of triangles: "+ntri);

		// Now do something with the triangles ....
		// Here I just write them to a geom file
		// http://local.wasp.uwa.edu.au/~pbourke/dataformats/geom/
		// fprintf(stderr,"Writing triangles ...\n");
		// if ((fptr = fopen("output.geom","w")) == NULL) {
		// fprintf(stderr,"Failed to open output file\n");
		// exit(-1);
		// }
		// for (i=0;i<ntri;i++) {
		// fprintf(fptr,"f3 ");
		// for (k=0;k<3;k++) {
		// fprintf(fptr,"%g %g %g ",tri[i].p[k].x,tri[i].p[k].y,tri[i].p[k].z);
		// }
		// fprintf(fptr,"0.5 0.5 0.5\n"); // colour
		// }
		// fclose(fptr);
	}

	public void draw() {// PApplet applet) {

		for (int i = 0; i < trilist.size(); i++) {
			final TRIANGLE tri = trilist.get(i);
			applet.beginShape(PConstants.TRIANGLES);
			applet.normal((float)tri.n.x, (float)tri.n.y, (float)tri.n.z);
			applet.vertex(tri.p[0].x, tri.p[0].y, tri.p[0].z);
			applet.vertex(tri.p[1].x, tri.p[1].y, tri.p[1].z);
			applet.vertex(tri.p[2].x, tri.p[2].y, tri.p[2].z);
			applet.endShape();

			// triangle((float)tri[i].p[0].x, tri[i].p[0].y, tri[i].p[0].z,
			// tri[i].p[1].x, tri[i].p[1].y, tri[i].p[1].z,
			// tri[i].p[2].x, tri[i].p[2].y, tri[i].p[2].z);
		}
	}

	public void drawnormals(float s) {// PApplet applet, float s) {

		// println("draw ntri "+trilist.size());
		for (int i = 0; i < trilist.size(); i++) {
			final TRIANGLE tri = trilist.get(i);
			final float x = (tri.p[0].x + tri.p[1].x + tri.p[2].x) / 3.0f;
			final float y = (tri.p[0].y + tri.p[1].y + tri.p[2].y) / 3.0f;
			final float z = (tri.p[0].z + tri.p[1].z + tri.p[2].z) / 3.0f;

			applet.beginShape(PConstants.LINES);
			// normal((float)tri.n.x, (float)tri.n.y, (float)tri.n.z);
			applet.vertex(x, y, z);
			applet.vertex(x + tri.n.x * s, y + tri.n.y * s, z + tri.n.z * s);
			applet.endShape();

		}
	}

	
	public void drawsize(float s) {// PApplet applet) {

		
		float c = s / worldstride.x;///applet.map(s, istart, istop, ostart, ostop)
		
		// println("draw ntri "+trilist.size());
		for (int i = 0; i < trilist.size(); i++) {
			final TRIANGLE tri = trilist.get(i);
			applet.beginShape(PConstants.TRIANGLES);
			// normal((float)tri.n.x, (float)tri.n.y, (float)tri.n.z);
			applet.vertex(tri.p[0].x*c, tri.p[0].y*c, tri.p[0].z*c);
			applet.vertex(tri.p[1].x*c, tri.p[1].y*c, tri.p[1].z*c);
			applet.vertex(tri.p[2].x*c, tri.p[2].y*c, tri.p[2].z*c);
			applet.endShape();

		}
	}

	public void drawnormalssize(float siz, float s) {// PApplet applet, float s) {

		for (int i = 0; i < trilist.size(); i++) {
			final TRIANGLE tri = trilist.get(i);
			final float x = (tri.p[0].x + tri.p[1].x + tri.p[2].x) / 3.0f;
			final float y = (tri.p[0].y + tri.p[1].y + tri.p[2].y) / 3.0f;
			final float z = (tri.p[0].z + tri.p[1].z + tri.p[2].z) / 3.0f;

			applet.beginShape(PConstants.LINES);
			// normal((float)tri.n.x, (float)tri.n.y, (float)tri.n.z);
			applet.vertex(x, y, z);
			applet.vertex(x + tri.n.x * s, y + tri.n.y * s, z + tri.n.z * s);
			applet.endShape();

		}
	}
	
	
	public void drawGL(){
		
//		FloatBuffer pointBuffer;
//	    int numberElements = numstars*3;
//	    pointBuffer = ByteBuffer.allocateDirect(4 * numberElements).order(ByteOrder.nativeOrder()).asFloatBuffer();
//	    pointBuffer.limit(numberElements);
//	    pointBuffer.rewind();
//	    
//	    
//	    
//	    
//	  //Tell it the max and min sizes we can use using our pre-filled array.
//	  gl.glPointParameterfARB( GL.GL_POINT_SIZE_MIN_ARB, sizes[0] );
//	  gl.glPointParameterfARB( GL.GL_POINT_SIZE_MAX_ARB, sizes[1] );
//	  	
//	  //Tell OGL to replace the coordinates upon drawing.
//	  //gl.glTexEnvi(GL.GL_POINT_SPRITE_ARB, GL.GL_COORD_REPLACE_ARB, GL.GL_TRUE);
//	  	
//	  //Set the size of the points.
//	  gl.glPointSize(32.0f);
//	  	
//	  //Turn off depth masking so particles in front will not occlude particles behind them.
//	  gl.glDepthMask(false);//GL.GL_FALSE);
//	  	
//	  //Save the current transform.
//	  gl.glPushMatrix();
//
//
//
//	   gl.glBindTexture(GL.GL_TEXTURE_2D, tex[0]);
//
//
//	   // gl.glDisable(GL.GL_TEXTURE_2D);
//
//	    
//
//	    gl.glPointSize(5.5f);
//	    gl.glColor4f(1.,1.,1.,0.30);//0.25);//0.7
//	    gl.glBindTexture(GL.GL_TEXTURE_2D, tex[1]);
//
//	    
//	    
//	    
//	    }
//
//	    gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
//	    gl.glVertexPointer(3, GL.GL_FLOAT, 0, pointBuffer);
//	    gl.glDrawArrays(GL.GL_POINTS, 0, stars.length);
//	 //  gl.glDrawArrays(GL.GL_LINE_STRIP, 0, stars.length);
//	 // gl.glDrawArrays(GL.GL_LINES, 0, stars.length);
//
//	    gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
//
//	    gl.glDisable(GL.GL_POINT_SPRITE_ARB);
//	    gl.glDisable(GL.GL_TEXTURE_2D);
//	    gl.glDisable(GL.GL_VERTEX_PROGRAM_POINT_SIZE_NV);

	
	}
	
	
	
	public void drawnormalsGL(float s){
		
	}
	

	/*
	 * Given a grid cell and an isolevel, calculate the triangular facets
	 * required to represent the isosurface through the cell. Return the number
	 * of triangular facets, the array "triangles" will be loaded up with the
	 * vertices at most 5 triangular facets. 0 will be returned if the grid cell
	 * is either totally above of totally below the isolevel.
	 */
	private int Polygonise(GRIDCELL grid, float isolevel, TRIANGLE triangles[]) {
		int i, ntriang;
		int cubeindex;
		final XYZ vertlist[] = new XYZ[12];
		/*
		 * Determine the index into the edge table which tells us which vertices
		 * are inside of the surface
		 */
		cubeindex = 0;
		if (grid.val[0] < isolevel) {
			cubeindex |= 1;
		}
		if (grid.val[1] < isolevel) {
			cubeindex |= 2;
		}
		if (grid.val[2] < isolevel) {
			cubeindex |= 4;
		}
		if (grid.val[3] < isolevel) {
			cubeindex |= 8;
		}
		if (grid.val[4] < isolevel) {
			cubeindex |= 16;
		}
		if (grid.val[5] < isolevel) {
			cubeindex |= 32;
		}
		if (grid.val[6] < isolevel) {
			cubeindex |= 64;
		}
		if (grid.val[7] < isolevel) {
			cubeindex |= 128;
		}

		/* Cube is entirely in/out of the surface */
		if (edgeTable[cubeindex] == 0) {
			return (0);
		}

		/* Find the vertices where the surface intersects the cube */
		// int temp = edgeTable[cubeindex] & 1;
		if ((edgeTable[cubeindex] & 1) != 0) {
			vertlist[0] = VertexInterp(isolevel, grid.p[0], grid.p[1],
					grid.val[0], grid.val[1]);
		}
		if ((edgeTable[cubeindex] & 2) != 0) {
			vertlist[1] = VertexInterp(isolevel, grid.p[1], grid.p[2],
					grid.val[1], grid.val[2]);
		}
		if ((edgeTable[cubeindex] & 4) != 0) {
			vertlist[2] = VertexInterp(isolevel, grid.p[2], grid.p[3],
					grid.val[2], grid.val[3]);
		}
		if ((edgeTable[cubeindex] & 8) != 0) {
			vertlist[3] = VertexInterp(isolevel, grid.p[3], grid.p[0],
					grid.val[3], grid.val[0]);
		}
		if ((edgeTable[cubeindex] & 16) != 0) {
			vertlist[4] = VertexInterp(isolevel, grid.p[4], grid.p[5],
					grid.val[4], grid.val[5]);
		}
		if ((edgeTable[cubeindex] & 32) != 0) {
			vertlist[5] = VertexInterp(isolevel, grid.p[5], grid.p[6],
					grid.val[5], grid.val[6]);
		}
		if ((edgeTable[cubeindex] & 64) != 0) {
			vertlist[6] = VertexInterp(isolevel, grid.p[6], grid.p[7],
					grid.val[6], grid.val[7]);
		}
		if ((edgeTable[cubeindex] & 128) != 0) {
			vertlist[7] = VertexInterp(isolevel, grid.p[7], grid.p[4],
					grid.val[7], grid.val[4]);
		}
		if ((edgeTable[cubeindex] & 256) != 0) {
			vertlist[8] = VertexInterp(isolevel, grid.p[0], grid.p[4],
					grid.val[0], grid.val[4]);
		}
		if ((edgeTable[cubeindex] & 512) != 0) {
			vertlist[9] = VertexInterp(isolevel, grid.p[1], grid.p[5],
					grid.val[1], grid.val[5]);
		}
		if ((edgeTable[cubeindex] & 1024) != 0) {
			vertlist[10] = VertexInterp(isolevel, grid.p[2], grid.p[6],
					grid.val[2], grid.val[6]);
		}
		if ((edgeTable[cubeindex] & 2048) != 0) {
			vertlist[11] = VertexInterp(isolevel, grid.p[3], grid.p[7],
					grid.val[3], grid.val[7]);
		}

		/* Create the triangle */
		ntriang = 0;
		for (i = 0; triTable[cubeindex][i] != -1; i += 3) {
			triangles[ntriang].p[0] = vertlist[triTable[cubeindex][i]];
			triangles[ntriang].p[1] = vertlist[triTable[cubeindex][i + 1]];
			triangles[ntriang].p[2] = vertlist[triTable[cubeindex][i + 2]];
			ntriang++;
		}

		return (ntriang);
	}

	/*
	 * Linearly interpolate the position where an isosurface cuts an edge
	 * between two vertices, each with their own scalar value
	 */
	private XYZ VertexInterp(float isolevel, XYZ p1, XYZ p2, float valp1,
			float valp2) {
		float mu;
		final XYZ p = new XYZ();
		p.x = p.y = p.z = 0.0f;

		if (Math.abs(isolevel - valp1) < 0.00001) {
			return (p1);
		}
		if (Math.abs(isolevel - valp2) < 0.00001) {
			return (p2);
		}
		if (Math.abs(valp1 - valp2) < 0.00001) {
			return (p1);
		}
		mu = ((isolevel - valp1) / (valp2 - valp1));
		p.x = p1.x + mu * (p2.x - p1.x);
		p.y = p1.y + mu * (p2.y - p1.y);
		p.z = p1.z + mu * (p2.z - p1.z);

		return (p);
	}

	// /TABLES
	int edgeTable[] = { // 256
	0x0, 0x109, 0x203, 0x30a, 0x406, 0x50f, 0x605, 0x70c, 0x80c, 0x905, 0xa0f,
			0xb06, 0xc0a, 0xd03, 0xe09, 0xf00, 0x190, 0x99, 0x393, 0x29a,
			0x596, 0x49f, 0x795, 0x69c, 0x99c, 0x895, 0xb9f, 0xa96, 0xd9a,
			0xc93, 0xf99, 0xe90, 0x230, 0x339, 0x33, 0x13a, 0x636, 0x73f,
			0x435, 0x53c, 0xa3c, 0xb35, 0x83f, 0x936, 0xe3a, 0xf33, 0xc39,
			0xd30, 0x3a0, 0x2a9, 0x1a3, 0xaa, 0x7a6, 0x6af, 0x5a5, 0x4ac,
			0xbac, 0xaa5, 0x9af, 0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0, 0x460,
			0x569, 0x663, 0x76a, 0x66, 0x16f, 0x265, 0x36c, 0xc6c, 0xd65,
			0xe6f, 0xf66, 0x86a, 0x963, 0xa69, 0xb60, 0x5f0, 0x4f9, 0x7f3,
			0x6fa, 0x1f6, 0xff, 0x3f5, 0x2fc, 0xdfc, 0xcf5, 0xfff, 0xef6,
			0x9fa, 0x8f3, 0xbf9, 0xaf0, 0x650, 0x759, 0x453, 0x55a, 0x256,
			0x35f, 0x55, 0x15c, 0xe5c, 0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53,
			0x859, 0x950, 0x7c0, 0x6c9, 0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5,
			0xcc, 0xfcc, 0xec5, 0xdcf, 0xcc6, 0xbca, 0xac3, 0x9c9, 0x8c0,
			0x8c0, 0x9c9, 0xac3, 0xbca, 0xcc6, 0xdcf, 0xec5, 0xfcc, 0xcc,
			0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0, 0x950, 0x859,
			0xb53, 0xa5a, 0xd56, 0xc5f, 0xf55, 0xe5c, 0x15c, 0x55, 0x35f,
			0x256, 0x55a, 0x453, 0x759, 0x650, 0xaf0, 0xbf9, 0x8f3, 0x9fa,
			0xef6, 0xfff, 0xcf5, 0xdfc, 0x2fc, 0x3f5, 0xff, 0x1f6, 0x6fa,
			0x7f3, 0x4f9, 0x5f0, 0xb60, 0xa69, 0x963, 0x86a, 0xf66, 0xe6f,
			0xd65, 0xc6c, 0x36c, 0x265, 0x16f, 0x66, 0x76a, 0x663, 0x569,
			0x460, 0xca0, 0xda9, 0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac,
			0x4ac, 0x5a5, 0x6af, 0x7a6, 0xaa, 0x1a3, 0x2a9, 0x3a0, 0xd30,
			0xc39, 0xf33, 0xe3a, 0x936, 0x83f, 0xb35, 0xa3c, 0x53c, 0x435,
			0x73f, 0x636, 0x13a, 0x33, 0x339, 0x230, 0xe90, 0xf99, 0xc93,
			0xd9a, 0xa96, 0xb9f, 0x895, 0x99c, 0x69c, 0x795, 0x49f, 0x596,
			0x29a, 0x393, 0x99, 0x190, 0xf00, 0xe09, 0xd03, 0xc0a, 0xb06,
			0xa0f, 0x905, 0x80c, 0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203,
			0x109, 0x0 };
	int triTable[][] = // 256x16
	{ { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1 },
			{ 8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1 },
			{ 3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1 },
			{ 4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1 },
			{ 4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1 },
			{ 9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1 },
			{ 10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1 },
			{ 5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1 },
			{ 5, 4, 8, 5, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 7, 8, 5, 7, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 3, 0, 9, 5, 3, 5, 7, 3, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 7, 8, 0, 1, 7, 1, 5, 7, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 7, 8, 9, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3, -1, -1, -1, -1 },
			{ 8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2, -1, -1, -1, -1 },
			{ 2, 10, 5, 2, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1 },
			{ 7, 9, 5, 7, 8, 9, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11, -1, -1, -1, -1 },
			{ 2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7, -1, -1, -1, -1 },
			{ 11, 2, 1, 11, 1, 7, 7, 1, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11, -1, -1, -1, -1 },
			{ 5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0, -1 },
			{ 11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0, -1 },
			{ 11, 10, 5, 7, 11, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 3, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 0, 1, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 8, 3, 1, 9, 8, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 6, 5, 2, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 6, 5, 1, 2, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 6, 5, 9, 0, 6, 0, 2, 6, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8, -1, -1, -1, -1 },
			{ 2, 3, 11, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 11, 0, 8, 11, 2, 0, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 1, 9, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11, -1, -1, -1, -1 },
			{ 6, 3, 11, 6, 5, 3, 5, 1, 3, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6, -1, -1, -1, -1 },
			{ 3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9, -1, -1, -1, -1 },
			{ 6, 5, 9, 6, 9, 11, 11, 9, 8, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 10, 6, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 3, 0, 4, 7, 3, 6, 5, 10, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 9, 0, 5, 10, 6, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4, -1, -1, -1, -1 },
			{ 6, 1, 2, 6, 5, 1, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7, -1, -1, -1, -1 },
			{ 8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6, -1, -1, -1, -1 },
			{ 7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9, -1 },
			{ 3, 11, 2, 7, 8, 4, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11, -1, -1, -1, -1 },
			{ 0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1 },
			{ 9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6, -1 },
			{ 8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6, -1, -1, -1, -1 },
			{ 5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11, -1 },
			{ 0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7, -1 },
			{ 6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9, -1, -1, -1, -1 },
			{ 10, 4, 9, 6, 4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 10, 6, 4, 9, 10, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 0, 1, 10, 6, 0, 6, 4, 0, -1, -1, -1, -1, -1, -1, -1 },
			{ 8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10, -1, -1, -1, -1 },
			{ 1, 4, 9, 1, 2, 4, 2, 6, 4, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4, -1, -1, -1, -1 },
			{ 0, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 8, 3, 2, 8, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 4, 9, 10, 6, 4, 11, 2, 3, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6, -1, -1, -1, -1 },
			{ 3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10, -1, -1, -1, -1 },
			{ 6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1, -1 },
			{ 9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3, -1, -1, -1, -1 },
			{ 8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1, -1 },
			{ 3, 11, 6, 3, 6, 0, 0, 6, 4, -1, -1, -1, -1, -1, -1, -1 },
			{ 6, 4, 8, 11, 6, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 7, 10, 6, 7, 8, 10, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10, -1, -1, -1, -1 },
			{ 10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0, -1, -1, -1, -1 },
			{ 10, 6, 7, 10, 7, 1, 1, 7, 3, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7, -1, -1, -1, -1 },
			{ 2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9, -1 },
			{ 7, 8, 0, 7, 0, 6, 6, 0, 2, -1, -1, -1, -1, -1, -1, -1 },
			{ 7, 3, 2, 6, 7, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7, -1, -1, -1, -1 },
			{ 2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7, -1 },
			{ 1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11, -1 },
			{ 11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1, -1, -1, -1, -1 },
			{ 8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6, -1 },
			{ 0, 9, 1, 11, 6, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0, -1, -1, -1, -1 },
			{ 7, 11, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 0, 8, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 1, 9, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 8, 1, 9, 8, 3, 1, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 1, 2, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 10, 3, 0, 8, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 9, 0, 2, 10, 9, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1 },
			{ 6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8, -1, -1, -1, -1 },
			{ 7, 2, 3, 6, 2, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 7, 0, 8, 7, 6, 0, 6, 2, 0, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 7, 6, 2, 3, 7, 0, 1, 9, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6, -1, -1, -1, -1 },
			{ 10, 7, 6, 10, 1, 7, 1, 3, 7, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8, -1, -1, -1, -1 },
			{ 0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7, -1, -1, -1, -1 },
			{ 7, 6, 10, 7, 10, 8, 8, 10, 9, -1, -1, -1, -1, -1, -1, -1 },
			{ 6, 8, 4, 11, 8, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 6, 11, 3, 0, 6, 0, 4, 6, -1, -1, -1, -1, -1, -1, -1 },
			{ 8, 6, 11, 8, 4, 6, 9, 0, 1, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6, -1, -1, -1, -1 },
			{ 6, 8, 4, 6, 11, 8, 2, 10, 1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6, -1, -1, -1, -1 },
			{ 4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9, -1, -1, -1, -1 },
			{ 10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3, -1 },
			{ 8, 2, 3, 8, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8, -1, -1, -1, -1 },
			{ 1, 9, 4, 1, 4, 2, 2, 4, 6, -1, -1, -1, -1, -1, -1, -1 },
			{ 8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1, -1, -1, -1, -1 },
			{ 10, 1, 0, 10, 0, 6, 6, 0, 4, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3, -1 },
			{ 10, 9, 4, 6, 10, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 9, 5, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 3, 4, 9, 5, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 0, 1, 5, 4, 0, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5, -1, -1, -1, -1 },
			{ 9, 5, 4, 10, 1, 2, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5, -1, -1, -1, -1 },
			{ 7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2, -1, -1, -1, -1 },
			{ 3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6, -1 },
			{ 7, 2, 3, 7, 6, 2, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7, -1, -1, -1, -1 },
			{ 3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0, -1, -1, -1, -1 },
			{ 6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8, -1 },
			{ 9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7, -1, -1, -1, -1 },
			{ 1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4, -1 },
			{ 4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10, -1 },
			{ 7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10, -1, -1, -1, -1 },
			{ 6, 9, 5, 6, 11, 9, 11, 8, 9, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5, -1, -1, -1, -1 },
			{ 0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11, -1, -1, -1, -1 },
			{ 6, 11, 3, 6, 3, 5, 5, 3, 1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6, -1, -1, -1, -1 },
			{ 0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10, -1 },
			{ 11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5, -1 },
			{ 6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3, -1, -1, -1, -1 },
			{ 5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2, -1, -1, -1, -1 },
			{ 9, 5, 6, 9, 6, 0, 0, 6, 2, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8, -1 },
			{ 1, 5, 6, 2, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6, -1 },
			{ 10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0, -1, -1, -1, -1 },
			{ 0, 3, 8, 5, 6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 11, 5, 10, 7, 5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 11, 5, 10, 11, 7, 5, 8, 3, 0, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 11, 7, 5, 10, 11, 1, 9, 0, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1, -1, -1, -1, -1 },
			{ 11, 1, 2, 11, 7, 1, 7, 5, 1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11, -1, -1, -1, -1 },
			{ 9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7, -1, -1, -1, -1 },
			{ 7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2, -1 },
			{ 2, 5, 10, 2, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5, -1, -1, -1, -1 },
			{ 9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2, -1, -1, -1, -1 },
			{ 9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2, -1 },
			{ 1, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 7, 0, 7, 1, 1, 7, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 0, 3, 9, 3, 5, 5, 3, 7, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 8, 7, 5, 9, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 8, 4, 5, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0, -1, -1, -1, -1 },
			{ 0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5, -1, -1, -1, -1 },
			{ 10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4, -1 },
			{ 2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8, -1, -1, -1, -1 },
			{ 0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11, -1 },
			{ 0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5, -1 },
			{ 9, 4, 5, 2, 11, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4, -1, -1, -1, -1 },
			{ 5, 10, 2, 5, 2, 4, 4, 2, 0, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9, -1 },
			{ 5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2, -1, -1, -1, -1 },
			{ 8, 4, 5, 8, 5, 3, 3, 5, 1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 4, 5, 1, 0, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5, -1, -1, -1, -1 },
			{ 9, 4, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 11, 7, 4, 9, 11, 9, 10, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11, -1, -1, -1, -1 },
			{ 1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11, -1, -1, -1, -1 },
			{ 3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4, -1 },
			{ 4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2, -1, -1, -1, -1 },
			{ 9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3, -1 },
			{ 11, 7, 4, 11, 4, 2, 2, 4, 0, -1, -1, -1, -1, -1, -1, -1 },
			{ 11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4, -1, -1, -1, -1 },
			{ 2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9, -1, -1, -1, -1 },
			{ 9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7, -1 },
			{ 3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10, -1 },
			{ 1, 10, 2, 8, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 9, 1, 4, 1, 7, 7, 1, 3, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1, -1, -1, -1, -1 },
			{ 4, 0, 3, 7, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 0, 9, 3, 9, 11, 11, 9, 10, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 1, 10, 0, 10, 8, 8, 10, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 1, 10, 11, 3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 11, 1, 11, 9, 9, 11, 8, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9, -1, -1, -1, -1 },
			{ 0, 2, 11, 8, 0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 3, 8, 2, 8, 10, 10, 8, 9, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 10, 2, 0, 9, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8, -1, -1, -1, -1 },
			{ 1, 10, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 3, 8, 9, 1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 9, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 3, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 } };

	/*
	 * 
	 * 
	 * file io
	 */

	public void readStl(String filestr) {
		header = new byte[80];
		byte4 = new byte[4];

		try {
			if (filestr != null) {
				filename = filestr;
				file = new File(filestr);
				// if (!file.isAbsolute()) file=new File(p.savePath(path));
				// if (!file.isAbsolute())
				// throw new
				// RuntimeException("RawSTLBinary requires an absolute path " +
				// "for the location of the input file.");
			}

			final FileInputStream in = new FileInputStream(file);
			System.out.println("\n\nReading " + file.getName());

			in.read(header);
			in.read(byte4);
			buf = ByteBuffer.wrap(byte4);
			buf.order(ByteOrder.nativeOrder());
			final int num = buf.getInt();

			System.out.println("Polygons to read: " + num);

			header = new byte[50];

			// poly=new FaceList(num);

			for (int i = 0; i < num; i++) {
				in.read(header);
				buf = ByteBuffer.wrap(header);
				buf.order(ByteOrder.nativeOrder());
				buf.rewind();

				// poly.addFace(Face.parseFace(buf));
				if (i % 1000 == 0) {
					System.out.println(i + " triangles read.");// f[i]);
				}
			}
			System.out.println("Facets: " + num);

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void writeStl(String filestr) {
		String path = applet.sketchPath + "/stlexport";
		writeStl(path, filestr);
	}
			
	public void writeStl(String path, String filestr) {
		try {

			(new File(path)).mkdirs();
			
			filename = path + "/"+filestr;

			final FileOutputStream out = new FileOutputStream(filename);

			header = new byte[80];

			buf = ByteBuffer.allocate(200);

			header = new byte[80];
			buf.get(header, 0, 80);
			out.write(header);
			buf.rewind();

			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(ntri); // /poly.num);
			buf.rewind();
			buf.get(header, 0, 4);
			out.write(header, 0, 4);
			buf.rewind();

			// System.out.println("\n\nWriting "+file.getName());

			buf.clear();
			header = new byte[50];

			for (int i = 0; i < ntri; i++) {
				// System.out.println(i+": "+f[i]);

				final TRIANGLE t = trilist.get(i);

				buf.rewind();
				// poly.f[i].write(buf);
				// ordem normal e 9 verts
				// for(int i=0; i<12; i++) buf.putFloat(v[i]);

				// buf.putDouble(t.n.x);
				// buf.putDouble(t.n.y);
				// buf.putDouble(t.n.z);
				// for (int ii=0; ii<3; ii++) {
				// buf.putDouble(t.p[ii].x);
				// buf.putDouble(t.p[ii].y);
				// buf.putDouble(t.p[ii].z);
				// }

				buf.putFloat(t.n.x);
				buf.putFloat(t.n.y);
				buf.putFloat(t.n.z);
				for (int ii = 2; ii > -1; ii--) {
					buf.putFloat(t.p[ii].x);
					buf.putFloat(t.p[ii].y);
					buf.putFloat(t.p[ii].z);
				}

				buf.rewind();
				buf.get(header);
				out.write(header);
			}

			out.flush();
			out.close();
			// isDisposed=true;
			System.out.print("s373.marchingcubes: Saved '" + filename
					+ " with " + ntri + " tris.\n");
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public String version() {

		try {
			// http://www.neowin.net/forum/index.php?showtopic=746508
			File jarFile = new File(this.getClass().getProtectionDomain()
					.getCodeSource().getLocation().toURI());
//			VERSION = "\n" + VERSION + new Date(jarFile.lastModified())
//			+ " - http://s373.net/code/marchingcubes \n";
			VERSION = VERSION + new Date(jarFile.lastModified())
			+ VERSIONURL;

		} catch (URISyntaxException e1) {
			System.out.print("marchingcubes couldnt access file version. " + e1);
		}

		System.out.print(VERSION);
		return VERSION;
	}

	
}
