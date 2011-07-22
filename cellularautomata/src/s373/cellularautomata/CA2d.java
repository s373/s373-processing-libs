package s373.cellularautomata;

public class CA2d extends CAdata implements CAinterface {

	public CA2d(int rx) {
		this(rx, rx);
	}

	public CA2d(int rx, int ry) {
		super();
		dimx = rx;
		dimy = ry;

		numpixels = dimx * dimy;
		currentGrid = new int[numpixels];
		nextGrid = new int[numpixels];
		data = new byte[numpixels];

//		numbit = 10 * 10;
//		rules = new int[numbit];
//		setCenter1();
//		setRules();
		
		setNumBits(10);
		
		currentGrid[(int) (dimx * 0.5 + dimy * 0.5 * dimx)] = 1;

		System.out.print("CA2d init: " + dimx + " " + dimy + " " + numpixels
				+ "\n");
	}

	public void setCell2D(int locx, int locy, byte val) {
		int idx = locx + locy * dimx;
		try {
			currentGrid[idx] = val;
		} catch (Exception e) {
			System.out.print("CA2d error setCell2D " + locx + " " + locy + "\n"
					+ e + "\n");
		}
	}

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

	public void update() {

		for (int x = 1; x < dimx - 1; x++) {
			for (int y = 1; y < dimy - 1; y++) {

				int count = countCells(x, y);
				// System.out.print("\ncount: " + count); //setRule(int) blows
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

	private int countCells(int x, int y) {
		int count = getCell2D(x, y - 1) + // north
				getCell2D(x + 1, y - 1) + // northeast
				getCell2D(x + 1, y) + // east
				getCell2D(x + 1, y + 1) + // southeast
				getCell2D(x, y + 1) + // south
				getCell2D(x - 1, y + 1) + // southwest
				getCell2D(x - 1, y) + // west
				getCell2D(x - 1, y - 1); // northwest
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
