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

	
	/**
	 * DNA() empty constructor returns one DNA object with one Gene.
	 */
	public DNA() {
		this(1);
	}

	/**
	 * DNA(int num) returns one DNA object with num Genes.
	 * @param num
	 */
	public DNA(final int num) {
		setNum(num);
	}

	/**
	 * DNA(float data[]) constructs one DNA object with contents of data.
	 * @param num
	 */

	public DNA(final float data[]) {
		setNum(data.length);
		for (int i = 0; i < data.length; i++) {
			dna[i] = data[i];
		}
	}

	/**
	 * DNA(DNA d) constructs one DNA object with contents of DNA d.
	 * @param d
	 */

	public DNA(final DNA d) {
		setNum(d.num);
		for (int i = 0; i < d.dna.length; i++) {
			dna[i] = d.dna[i];
		}
	}

	/**
	 * DNA(DNA d) constructs one DNA object with contents of DNA d +- random(dev).
	 * @param d, dev
	 */
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
	 * return number of genes in DNA
	 * @return num
	 */
	public int getNum() {
		return num;
	}	
	/**
	 * return number of genes in DNA
	 * @return num
	 */
	public int size() {
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
	 * Get dna float array
	 * @return dna float[]
	 */
	public float[] getDna() {
		return dna;
	}

	/**
	 * get Gene value index n
	 * @param n
	 * @return
	 */
	public float getGene(final int n) {
		return dna[n];
	}
	
	/**
	 * set Gene index n with value val
	 * @param n
	 * @param val
	 * @return
	 */
	public DNA setGene(final int n, final float val) {
		dna[n] = val;
		return this;
	}
	
	/**
	 * set Random vals on all genes
	 * @return
	 */
	public DNA setRandomDNA() {
		for (int i = 0; i < dna.length; i++) {
			dna[i] = random(1);
		}
		return this;
	}

	/**
	 * mutate DNA with probability a. 
	 * each gene is tested for probability, if true, its value is randomized.
	 * 
	 * @param a
	 * @return
	 */
	public DNA mutate(final float a) {
		for (int i = 0; i < num; i++) {
			if (random(1f) < a)
				dna[i] = random(1.0000001f);
		}
		bound();
		return this;
	}

	/**
	 * mutate DNA with probability a and deviation b. 
	 * each gene is tested for probability, if true, its value is deviated b amount.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
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
		bound();
		return this;
	}

	/**
	 * mate this DNA with other DNA according to its mateMode.
	 * 
	 * @param another
	 * @return
	 */
	public DNA mate(final DNA another) {
		return mate(another, 0.5f);
	}

	/**
	 *  mate this DNA with other DNA according to its mateMode with param.
	 *  
	 * @param another
	 * @param param
	 * @return
	 */
	public DNA mate(final DNA another, final float param) {
		switch (mateMode) {
		case 0:
			return crossover1(another);
		case 1:
			return crossover2(another, param);
		case 2:
			return crossover3(another, param);
		case 3:
			return crossover4(another);
		}
		bound();
		return this;
	}

	/**
	 * crossover1: 1 rnd point is defined along the gene sequence. after that point, genes from another get overwritten into this genome.
	 * @param another
	 * @return
	 */
	private DNA crossover1(final DNA another) {
		int pt = (int) random(dna.length);
		for (int i = 0; i < num; i++) {
			if (i < pt)
				continue;
			else
				dna[i] = another.dna[i];
		}
		return this;
	}

	/**
	 * crossover2: each gene is tested with a probability, if true, uses other gene into sequence.
	 * @param another
	 * @param prob
	 * @return
	 */
	private DNA crossover2(final DNA another, final float prob) {
		for (int i = 0; i < num; i++) {
			boolean useOtherGene = random(1) > prob;
			if (useOtherGene)
				dna[i] = another.dna[i];
		}
		return this;
	}

	/**
	 * crossover3: each gene is result of percentage between 2 genomes.
	 * @param another
	 * @param percent
	 * @return
	 */
	private DNA crossover3(final DNA another, final float percent) {
		final float per0 = percent;
		final float per1 = 1.0f - percent;
		for (int i = 0; i < num; i++) {
			dna[i] = per0 * dna[i] + per1 * another.dna[i];
		}
		return this;
	}

	/**
	 * crossover4: each gene is result of random between 2 genomes.
	 * @param another
	 * @return
	 */
	private DNA crossover4(final DNA another) {
		for (int i = 0; i < num; i++) {
			dna[i] = random(dna[i], another.dna[i]);
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
	 * mutate gene
	 * 
	 * @param gene
	 * @param dev
	 * @return this
	 */
	public DNA mutateGene(final int gene, final float dev) {
		dna[gene] += random(-dev, dev);
		bound();

		return this;
	}

	/**
	 * return the absolute difference value to another DNA
	 * @param dnatarget
	 * @return
	 */
	public float difference(final DNA dnatarget) {
		float val = 0.f;
		for (int i = 0; i < num; i++) {
			val += Math.abs(dnatarget.dna[i] - dna[i]);
		}
		return val;
	}

	/**
	 * return the absolute difference gene array to another DNA
	 * @param dnatarget
	 * @return float[]
	 */
	public float[] differenceDNA(final DNA dnatarget) {
		float dif[] = new float[dnatarget.num];
		for (int i = 0; i < num; i++) {
			dif[i] = Math.abs(dnatarget.dna[i] - dna[i]);
		}
		return dif;
	}

	/**
	 * return the absolute difference gene to another DNA's gene.
	 * @param gene
	 * @param dnatarget
	 * @return
	 */
	public float differenceGene(final int gene, final DNA dnatarget) {
		return Math.abs(dnatarget.dna[gene] - dna[gene]);
	}

	/**
	 * evaluate the fitness of this DNA regarding a dnatarget.
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

	/**
	 * set DNA mateMode
	 * @param mateMode
	 * @return
	 */
	public DNA setMateMode(int mateMode) {
		this.mateMode = mateMode;
		return this;
	}

	/**
	 * get DNA mateMode
	 * @return
	 */
	public int getMateMode() {
		return mateMode;
	}

	/**
	 * set DNA boundsMode
	 * @param boundsMode
	 * @return
	 */
	public DNA setBoundsMode(int boundsMode) {
		this.boundsMode = boundsMode;
		return this;
	}

	/**
	 * get DNA boundsMode
	 * @return
	 */
	public int getBoundsMode() {
		return boundsMode;
	}

	// //// random

	/**
	 * return a random number up to max
	 * @param max
	 * @return
	 */
	public float random(float max) {
		if (myrand == null)
			myrand = new Random();
		return myrand.nextFloat() * max;
	}

	/**
	 * return a random number between min and max.
	 * @param min
	 * @param max
	 * @return
	 */
	public float random(float min, float max) {
		float dis = max - min;
		return random(dis) + min;

	}

}
