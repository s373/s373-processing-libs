package s373.cellularautomata;
/**
 * CA3d 3d cellular automaton class.
 * 
 */
public class CA3d extends CAdata {

	/**
	 * CA3d 3d cellular automaton class constructor with an int specifies number of int x int x int cells.
	 * @param dimx
	 * 
	 */
	public CA3d(int dimx) {
		this(dimx, dimx, dimx);
	}
	
	/**
	 * CA3d 3d cellular automaton class constructor with x,y,z ints specifies grid of this 3d cellular automaton.
	 * @param rx
	 * @param ry
	 * @param rz
	 */

	public CA3d(int rx, int ry, int rz) {
		super();

		dimx = rx;
		dimy = ry;
		dimz = rz;
		numpixels = dimx * dimy * dimz;
		currentGrid = new int[numpixels];
		nextGrid = new int[numpixels];

		setNumBits(134217728); //2^(3*3*3)

		System.out.print(this + " CA3d init: " + dimx + " " + dimy + " " + dimz + " "
				+ numpixels + "\n");
	}

	/**
	 * set cell val at index.
	 * 
	 * @param locx
	 * @param locy
	 * @param locz
	 * @param val
	 */

	void setCell3D(int locx, int locy, int locz, byte val) {
		int idx = locx + locy * dimx + locz * dimx * dimy;
		try {
			currentGrid[idx] = val;
		} catch (Exception e) {
			System.out.print("CA3d error setCell2D " + locx + " " + locy + " "
					+ locz + "\n" + e + "\n");
		}
	}
	/**
	 * get cell val at index.
	 * 
	 * @param locx
	 * @param locy
	 * @param locz
	 * @return
	 */

	public int getCell3D(int locx, int locy, int locz) {
		int idx = locx + locy * dimx + locz * dimx * dimy;
		int dst = 0;
		try {
			dst = currentGrid[idx];
		} catch (Exception e) {
			// System.out.print("CA3d error getCell4D " + locx + " " + locy +
			// " "
			// + locz + "\n" + e + "\n");
		}
		return dst;
	}

	/**
	 * update the automaton.
	 */

	public void update() {

		for (int z = 1; z < dimz - 1; z++) {
			for (int y = 1; y < dimy - 1; y++) {
				for (int x = 1; x < dimx - 1; x++) {

					int count = countCells(x, y, z);
					setNextGrid(x, y, z, rules[count]);
				}
			}
		}

		int[] temp = currentGrid;
		currentGrid = nextGrid;
		nextGrid = temp;
	}

	private void setNextGrid(int x, int y, int z, int val) {
		int idx = x + y * dimx + z * dimx * dimy;
		try {
			nextGrid[idx] = val;
		} catch (Exception e) {
			System.out.print("CA3d error setNextGrid " + x + " " + y + " " + z
					+ "\n" + e + "\n");
		}

	}
	/**
	 * get the exact case for this position.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private int countCells(int x, int y, int z) {
		int count = 0;
		for(int i=0; i<27; i++){
			int lx = i%3;
			int ly = i/3;
			int lz = i/9;
			if(getCell3D(x+lx-1,y+ly-1, z+lz-1)==1){
				count |= (1<<i);
			}
		}
		
		return count;
		
//		int count = getCell3D(x, y - 1, z - 1) + // north
//				getCell3D(x + 1, y - 1, z - 1) + // northeast
//				getCell3D(x + 1, y, z - 1) + // east
//				getCell3D(x + 1, y + 1, z - 1) + // southeast
//				getCell3D(x, y + 1, z - 1) + // south
//				getCell3D(x - 1, y + 1, z - 1) + // southwest
//				getCell3D(x - 1, y, z - 1) + // west
//				getCell3D(x - 1, y - 1, z - 1) + // northwest
//
//				getCell3D(x, y - 1, z + 1) + // north
//				getCell3D(x + 1, y - 1, z + 1) + // northeast
//				getCell3D(x + 1, y, z + 1) + // east
//				getCell3D(x + 1, y + 1, z + 1) + // southeast
//				getCell3D(x, y + 1, z + 1) + // south
//				getCell3D(x - 1, y + 1, z + 1) + // southwest
//				getCell3D(x - 1, y, z + 1) + // west
//				getCell3D(x - 1, y - 1, z + 1) +
//
//				getCell3D(x, y - 1, z) + // north
//				getCell3D(x + 1, y - 1, z) + // northeast
//				getCell3D(x + 1, y, z) + // east
//				getCell3D(x + 1, y + 1, z) + // southeast
//				getCell3D(x, y + 1, z) + // south
//				getCell3D(x - 1, y + 1, z) + // southwest
//				getCell3D(x - 1, y, z) + // west
//				getCell3D(x - 1, y - 1, z);
//
//		return count;
	}

	
	public int[] getData() {
		return currentGrid;
	}

	
	public void setData() {

	}

	

	public void setCenter1() {
		currentGrid[(int) (dimx * 0.5 + dimy * 0.5 * dimx + dimz * 0.5 * dimx
				* dimy)] = 1;
	}

	

	public void print() {
		System.out.print("cell3D contents:\n ");
		for (int z = 0; z < dimz; z++) {
			for (int y = 0; y < dimy; y++) {
				String txt = "";
				for (int x = 0; x < dimx; x++) {
					txt += (getCell3D(x, y, z) + " ");
				}
				System.out.print(txt + "\n ");
			}
		}
	}

}
