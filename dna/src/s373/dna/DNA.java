/**
 * s373.dna
 * 
 * mini-biblioteca java desenvolvida por andré sier jul 2010
 * 
 *  referências:
 *  - karl sims, "artificial evolution for computer graphics", 1991
 * 
 * @author andre sier
 * @date julho 2010
 * 
 */

package s373.dna;

import java.util.Random;

public class DNA {

	public float dna[] = null;
	public int num;

	private int mateMode;
	private int boundsMode;
	private Random myrand;

	// //////// constructores

	public DNA() {
		this(1);
	}

	public DNA(final int num) {
		setNum(num);
	}

	public DNA(final float data[]) {
		setNum(data.length);
		for (int i = 0; i < data.length; i++) {
			dna[i] = data[i];
		}
	}

	public DNA(final DNA d) {
		setNum(d.num);
		for (int i = 0; i < d.dna.length; i++) {
			dna[i] = d.dna[i];
		}
	}

	public DNA(DNA d, float dev) {
		setNum(d.num);
		for (int i = 0; i < d.dna.length; i++) {
			dna[i] = d.dna[i] + random(-dev, dev);
		}
	}

	// //////// métodos

	/**
	 * set num genes
	 * 
	 * @param n
	 * @return
	 */
	public DNA setNum(final int n) {
		num = n;
		dna = new float[num];
		return setRandomDNA();
	}

	/**
	 * @return num
	 */
	public int getNum() {
		return num;
	}

	/**
	 * @param dna
	 * @return
	 */
	public DNA setDna(final DNA d) {
		return setDna(d.dna);
	}

	/**
	 * @param data
	 * @return
	 */
	public DNA setDna(final float data[]) {
		if (data.length != dna.length)
			setNum(data.length);

		for (int i = 0; i < num; i++) {
			dna[i] = data[i];
		}

		return this;
	}

	/**
	 * @return dna float[]
	 */
	public float[] getDna() {
		return dna;
	}

	public float getGene(final int n) {
		return dna[n];
	}

	public DNA setGene(final int n, final float val) {
		dna[n] = val;
		return this;
	}

	public DNA setRandomDNA() {
		for (int i = 0; i < dna.length; i++) {
			dna[i] = random(1);
		}
		return this;
	}

	public DNA mutate(final float a) {
		for (int i = 0; i < num; i++) {
			if (random(1f) < a)
				dna[i] = random(1.0000001f);
		}
		return this;
	}

	public DNA mutate(final float a, final float b) {
		// for (int i = 0; i < num; i++) {
		// float rnd = random(1f);
		// if (rnd > a && rnd < b)
		// dna[i] = random(1.0000001f);
		// }
		// return this;
		for (int i = 0; i < num; i++) {
			if (random(1f) < a)
				dna[i] += random(-b, b);
		}
		return this;
	}

	// mate
	public DNA mate(final DNA dnaparent) {
		return mate(dnaparent, 0.5f);
	}

	// mate
	public DNA mate(final DNA dnaparent, final float param) {
		switch (mateMode) {
		case 0:
			return crossover1(dnaparent);
		case 1:
			return crossover2(dnaparent, param);
		case 2:
			return crossover3(dnaparent, param);
		case 3:
			return crossover4(dnaparent);
		}
		return this;
	}

	private DNA crossover1(final DNA dnaparent) {
		int pt = (int) random(dna.length);
		for (int i = 0; i < num; i++) {
			if (i < pt)
				continue;
			else
				dna[i] = dnaparent.dna[i];
		}
		return this;
	}

	private DNA crossover2(final DNA dnaparent, final float prob) {
		for (int i = 0; i < num; i++) {
			boolean useOtherGene = random(1) > prob;
			if (useOtherGene)
				dna[i] = dnaparent.dna[i];
		}
		return this;
	}

	private DNA crossover3(final DNA dnaparent, final float percent) {
		final float per0 = percent;
		final float per1 = 1.0f - percent;
		for (int i = 0; i < num; i++) {
			dna[i] = per0 * dna[i] + per1 * dnaparent.dna[i];
		}
		return this;
	}

	private DNA crossover4(final DNA dnaparent) {
		for (int i = 0; i < num; i++) {
			dna[i] = random(dna[i], dnaparent.dna[i]);
		}
		return this;
	}

	private void bound(){
		
		if(boundsMode == 0) {
			return;
		}
		
		if (boundsMode == 1) {
			for(int i=0; i<dna.length;i++) {
				if (dna[i] > 1)
					dna[i] = 1;
				if (dna[i] < 0)
					dna[i] = 0;
			}
		}
		if (boundsMode == 2) {
			for(int i=0; i<dna.length;i++) {
				while (dna[i] > 1)
					dna[i] -= 1;
				while (dna[i] < 0)
					dna[i] += 1;
			}
		}
		
	}

	
	
	/**
	 * @param gene
	 * @param dev
	 * @return
	 */
	public DNA mutateGene(final int gene, final float dev) {
		dna[gene] += random(-dev, dev);
		if (boundsMode > 0) {
			if (boundsMode == 1) {
				if (dna[gene] > 1)
					dna[gene] = 1;
				if (dna[gene] < 0)
					dna[gene] = 0;
			}
		}

		return this;
	}

	/**
	 * @param dnatarget
	 * @return
	 */
	private float difference(final DNA dnatarget) {
		float val = 0.f;
		for (int i = 0; i < num; i++) {
			val += Math.abs(dnatarget.dna[i] - dna[i]);
		}
		return val;
	}

	/**
	 * @param dnatarget
	 * @return
	 */
	public float[] differenceDNA(final DNA dnatarget) {
		float dif[] = new float[dnatarget.num];
		for (int i = 0; i < num; i++) {
			dif[i] = Math.abs(dnatarget.dna[i] - dna[i]);
		}
		return dif;
	}

	/**
	 * @param gene
	 * @param dnatarget
	 * @return
	 */
	public float differenceGene(final int gene, final DNA dnatarget) {
		return Math.abs(dnatarget.dna[gene] - dna[gene]);
	}

	/**
	 * @param dnatarget
	 * @return
	 */
	public float fitness(final DNA dnatarget) {
		return 1.0f - (difference(dnatarget) / num);
	}

	public void print() {
		printDna();
	}

	public void printDna() {
		System.out.print("dna num: " + num + "\n");
		for (int i = 0; i < num; i++) {
			System.out.print(dna[i] + " ");
		}
		System.out.print("\n");
	}

	public String toString() {
		String data = "";
		for (int i = 0; i < num; i++) {
			data += dna[i] + " ";
		}
		return data;
	}

	public DNA setMateMode(int mateMode) {
		this.mateMode = mateMode;
		return this;
	}

	public int getMateMode() {
		return mateMode;
	}

	public DNA setBoundsMode(int boundsMode) {
		this.boundsMode = boundsMode;
		return this;
	}

	public int getBoundsMode() {
		return boundsMode;
	}

	// //// random

	public float random(float max) {
		if (myrand == null)
			myrand = new Random();
		return myrand.nextFloat() * max;
	}

	public float random(float min, float max) {
		float dis = max - min;
		return random(dis) + min;

	}

}
