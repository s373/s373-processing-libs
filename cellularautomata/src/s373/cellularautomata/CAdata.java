package s373.cellularautomata;

import java.util.Random;
/**
 * CAdata base cellular automaton class.
 * 
 * holds rules and data mechanics up to 3d.
 * 
 * @author Andre Sier, july 2010
 *
 */
public class CAdata {

	public byte data[] = { 0, 0 };
	public int currentGrid[] = { 0, 0 };
	public int nextGrid[] = { 0, 0 };
	public byte rules[] = { 0, 1, 1, 1, 1, 0, 0, 0, 0, 0 };
	public int rulesInt = 110;
	public int dimx = 10;
	public int dimy = 10;
	public int dimz = 10;
	public int numpixels = dimx * dimy;
	public int numbit = 10;
	public int numactive = 0;
	private Random myrand;
	public boolean debug = false;

	/**
	 * set random rules for automaton
	 */
	public void setRules() {
		if(debug)System.out.print("rule bin: ");
		for (int i = 0; i < numbit; i++) {
			rules[i] = (byte) random(2);
			if(debug)System.out.print(rules[i] + " ");
		}
		long num = 0;
		for (int i = 0; i < numbit; i++) {
			if (rules[i] == 1)
				num |= (1 << i);
		}
		rulesInt = (int)num;

		
		if(debug)System.out.print("\n");
		setCenter1();
	}

	/**
	 * set rule for automaton in int notation.
	 * @param rule
	 */
	public void setRule(int rule) {
		setRules(rule);
	}

	/**
	 * set rule for automaton in int notation.
	 * @param rule
	 */
	public void setRules(int rule) {
		if(debug)System.out.print("rule set: " + rule + "\n");
		if(debug)System.out.print("rule bin: ");

		int num = rule;
		rulesInt = rule;

		for (int i = 0; i < rules.length; i++) {
			int rem = num % 2;
			num = num / 2;
			rules[i] = (byte) rem;
			if(debug)System.out.print(rules[i] + " ");
		}
		if(debug)System.out.print("\n");
	}

	/**
	 * get rule in the automaton in int notation.
	 * @return rule
	 */
	public int getRule() {
		return rulesInt;
		
//		int num = 0;
//		for (int i = 0; i < numbit; i++) {
//			if (rules[i] == 1)
//				num |= (1 << i);
//		}
//		return num;
	}

	
	/**
	 * update the automaton.
	 */

	public void update() {

	}

	/**
	 * get data array
	 * @return
	 */
	public int[] getData() {
		return null;
	}

	/**
	 * set center cell 1.
	 * 
	 */
	public void setCenter1() {
		if(debug)System.out.print("CAdata setCenter1 called " + "\n");
	}

	/**
	 * set number of bits in this ruleset.
	 * @param nbits
	 */
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
