package s373.cellularautomata;
/**
 * CA2d 2d cellular automaton class.
 * 
 */
public class CA2d extends CAdata { 
//implements CAinterface {

	/**
	 * CA2d 2d cellular automaton class constructor with an int specifies number of int x int cells.
	 * @param dimx
	 */
	public CA2d(int dimx) {
		this(dimx, dimx);
	}

	/**
	 * CA2d 2d cellular automaton class constructor with x,y ints specifies grid of this 2d cellular automaton.
	 * @param rx
	 * @param ry
	 */
	public CA2d(int rx, int ry) {
		super();
		dimx = rx;
		dimy = ry;

		numpixels = dimx * dimy;
		currentGrid = new int[numpixels];
		nextGrid = new int[numpixels];

		setNumBits(512);// 2^(3*3) 
						//max rules 262144
		
		currentGrid[(int) (dimx * 0.5 + dimy * 0.5 * dimx)] = 1;

		System.out.print(this + "CA2d init: " + dimx + " " + dimy + " " + numpixels
				+ "\n");
	}

	/**
	 * set cell val at index.
	 * 
	 * @param locx
	 * @param locy
	 * @param val
	 */

	public void setCell2D(int locx, int locy, byte val) {
		int idx = locx + locy * dimx;
		try {
			currentGrid[idx] = val;
		} catch (Exception e) {
			System.out.print("CA2d error setCell2D " + locx + " " + locy + "\n"
					+ e + "\n");
		}
	}
	/**
	 * get cell val at index.
	 * 
	 * @param locx
	 * @param locy
	 * @return
	 */

	public int getCell2D(int locx, int locy) {
		int idx = locx + locy * dimx;
		int dst = 0;
		try {
			dst = currentGrid[idx];
		} catch (Exception e) {
			System.out.print("CA2d error getCell2D " + locx + " " + locy + "\n"
					+ e + "\n");
		}
		return dst;
	}

	/**
	 * update the automaton.
	 */

	public void update() {

		for (int x = 1; x < dimx - 1; x++) {
			for (int y = 1; y < dimy - 1; y++) {

				int count = countCells(x, y);
				setNextGrid(x, y, rules[count]);

			}
		}

		int[] temp = currentGrid;
		currentGrid = nextGrid;
		nextGrid = temp;
	}

	private void setNextGrid(int x, int y, int val) {
		int idx = x + y * dimx;
		try {
			nextGrid[idx] = val;
		} catch (Exception e) {
			System.out.print("CA2d error setNextGrid " + x + " " + y + "\n" + e
					+ "\n");
		}

	}

	/**
	 * get the exact case for this position.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int countCells(int x, int y) {
		
		int count = 0;
		for(int i=0; i<9; i++){
			int lx = i%3;
			int ly = i/3;
			if(getCell2D(x+lx-1,y+ly-1)==1){
				count |= (1<<i);
			}
			
		}
		return count;
		
	}

	public int[] getData() {
		return currentGrid;
	}

	public void setData() {

	}



	public void setCenter1() {
		currentGrid[(int) (dimx * 0.5 + dimy * 0.5 * dimx)] = 1;
	}

	

	public void print() {
		System.out.print("cell2D contents:\n ");
		for (int y = 0; y < dimy; y++) {
			String txt = "";
			for (int x = 0; x < dimx; x++) {
				txt += (getCell2D(x, y) + " ");
			}
			System.out.print(txt + "\n ");
		}
		System.out.print("\n");
	}

}
