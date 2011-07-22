package s373.cellularautomata;

public class CA1d extends CAdata implements CAinterface {

	public CA1d(int rx) {
		super();
		dimx = rx;

		numpixels = dimx;
		currentGrid = new int[numpixels];
		nextGrid = new int[numpixels];
		data = new byte[numpixels];

		setNumBits(3);
		
		System.out.print("CA1d init: " + dimx + " " + numpixels + "\n");
	}

	public void setCell1D(int locx, byte val) {

		try {
			currentGrid[locx] = val;
		} catch (Exception e) {
			System.out.print("CA1d error setCell2D " + locx + "\n" + e + "\n");
		}
	}

	public int getCell1D(int locx) {
		int dst = 0;
		try {
			dst = currentGrid[locx];
		} catch (Exception e) {
			System.out.print("CA1d error getCell2D " + locx + "\n" + e + "\n");
		}
		return dst;
	}

	public void setCenter1() {
		currentGrid[(int) (dimx * 0.5)] = 1;
	}

	public void update() {

		for (int x = 1; x < dimx - 1; x++) {

			int count = countCells(x);
			// System.out.print("\ncount: " + count); //setRule(int) blows
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

	private int countCells(int x) {
		int count = getCell1D(x - 1) + // north
				getCell1D(x + 1);
		return count;
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
