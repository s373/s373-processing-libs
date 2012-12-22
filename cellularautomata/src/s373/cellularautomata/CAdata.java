package s373.cellularautomata;

import java.util.Random;

public class CAdata {

	public byte data[] = { 0, 0 };
	public int currentGrid[] = { 0, 0 };
	public int nextGrid[] = { 0, 0 };
	public byte rules[] = { 0, 1, 1, 1, 1, 0, 0, 0, 0, 0 };
	public int rulesInt = 106;
	public int dimx = 10;
	public int dimy = 10;
	public int dimz = 10;
	public int numpixels = dimx * dimy;
	public int numbit = 10;
	public int numactive = 0;
	private Random myrand;
	private boolean debug = false;

	public void setRules() {
		if(debug)System.out.print("rule bin: ");
		for (int i = 0; i < numbit; i++) {
			rules[i] = (byte) random(2);
			if(debug)System.out.print(rules[i] + " ");
		}
		if(debug)System.out.print("\n");
		setCenter1();
	}

	public void setRule(int rule) {
		setRules(rule);
	}

	public void setRules(int rule) {
		if(debug)System.out.print("rule set: " + rule + "\n");
		if(debug)System.out.print("rule bin: ");

		int num = rule;

		for (int i = 0; i < rules.length; i++) {
			int rem = num % 2;
			num = num / 2;
			rules[i] = (byte) rem;
			if(debug)System.out.print(rules[i] + " ");
		}
		if(debug)System.out.print("\n");
	}

	public int getRule() {
		int num = 0;
		for (int i = 0; i < numbit; i++) {
			if (rules[i] == 1)
				num |= (1 << i);
		}
		return num;
	}

	public void update() {

	}

	public int[] getData() {
		return null;
	}

	public void setCenter1() {
		if(debug)System.out.print("CAdata setCenter1 called " + "\n");
	}

	public void setNumBits(int nbits) {
		numbit = nbits;// 10 * 3;
		rules = new byte[numbit];
		setRules();
		setCenter1();
	}

	public float random(float max) {
		if (myrand == null)
			myrand = new Random();
		return myrand.nextFloat() * max;
	}

}
