package s373.cellularautomata;

/**
 * CA1d 1d cellular automaton class.
 * 
 */

public class CA1d extends CAdata { 

	/**
	 * CA1d 1d cellular automaton class constructor with an int specifies number of cells.
	 * @param dimx
	 */
	public CA1d(int dimx) {
		super();
		this.dimx = dimx;

		numpixels = dimx;
		currentGrid = new int[numpixels];
		nextGrid = new int[numpixels];
		data = new byte[numpixels];

		setNumBits(8);  // 2^3
						// max rules 256 
		
		System.out.print(this+" CA1d init: " + dimx + " " + numpixels + "\n");
	}

	/**
	 * set cell val at index.
	 * 
	 * @param locx
	 * @param val
	 */
	public void setCell1D(int locx, byte val) {

		try {
			currentGrid[locx] = val;
		} catch (Exception e) {
			System.out.print("CA1d error setCell2D " + locx + "\n" + e + "\n");
		}
	}

	/**
	 * get cell val at index.
	 * 
	 * @param locx
	 * @return
	 */
	public int getCell1D(int locx) {
		int dst = 0;
		try {
			dst = currentGrid[locx];
		} catch (Exception e) {
			System.out.print("CA1d error getCell2D " + locx + "\n" + e + "\n");
		}
		return dst;
	}

	/**
	 * set center cell 1.
	 * 
	 */

	public void setCenter1() {
		currentGrid[(int) (dimx * 0.5)] = 1;
	}

	/**
	 * update the automaton.
	 */
	public void update() {

		for (int x = 1; x < dimx - 1; x++) {

			int count = countCells(x);
			setNextGrid(x, rules[count]);

		}

		int[] temp = currentGrid;
		currentGrid = nextGrid;
		nextGrid = temp;
	}

	private void setNextGrid(int x, int val) {

		try {
			nextGrid[x] = val;
		} catch (Exception e) {
			System.out.print("CA1d error setNextGrid " + x + "\n" + e + "\n");
		}

	}

	/**
	 * get the exact case for this position.
	 * 
	 * @param x
	 * @return
	 */
	private int countCells(int x) {
		
		//current pattern	111	110	101	100	011	010	001	000
		int num = 0;
		for (int i = 0; i < 3; i++) {
			if (getCell1D(x - 1 + i) == 1)
				num |= (1 << i);
		}
		return num;
		
//		int count = getCell1D(x - 1) + getCell1D(x) +
//				getCell1D(x + 1);
//		return count;
	}

	public int[] getData() {
		return currentGrid;
	}

	public void setData() {

	}


	public void print() {
		System.out.print("cell1D contents:\n ");
		
			String txt = "";
			for (int x = 0; x < dimx; x++) {
				txt += (getCell1D(x) + " ");
			}
			System.out.print(txt + "\n ");
	
		System.out.print("\n");
	}

}
