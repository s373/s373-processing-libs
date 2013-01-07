
/**

s373.dna
  Simple dna library with mutation and crossovers from Karl Sims' paper.
  http://www.karlsims.com/papers/siggraph91.html  
  Implementation by André Sier, july 2010
  Available at http://s373.net/code/dna  

What's in the Library?
  DNA class. Each DNA class holds an arbitrary number of genes. 
  Each gene is a normal float [0-1]. Genes usually are assigned and mapped 
  to interesting parameters.

Main uses
  -evolve/mutate this DNA using
  --DNA.mutate(float a)
    * mutate DNA with probability a. 
    * each gene is tested for probability, if true, its value is randomized.
  --DNA.mutate(float a, float b)
    * mutate DNA with probability a and deviation b. 
    * each gene is tested for probability, if true, its value is deviated b amount.
 
  -mate/crossover this DNA with another using its mateMode
  --DNA.mate(DNA another)
  --DNA.mate(DNA another, float param)
   * crossover1: 1 rnd point is defined along the gene sequence. 
       after that point, genes from dnaparent get overwritten into this genome.
   * crossover2: each gene is tested with a probability, if true, 
       uses other gene into sequence.
   * crossover3: each gene is result of percentage between 2 genomes.
   * crossover4: each gene is result of random between 2 genomes.
   
  -evaluate fitness / difference
  --DNA.fitness(DNA target)
  --DNA.difference(DNA target)
  --DNA.differenceDNA(DNA target)
  --DNA.differenceGene(int gene, DNA target)

*/
