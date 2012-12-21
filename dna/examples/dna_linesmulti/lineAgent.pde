class LineAgent {

  DNA dna;
  PVector origin;
  float rad, rad2;

  LineAgent(PVector loc, float radius) {
    origin = new PVector(loc.x, loc.y);
    dna = new DNA(10 * 2);
    rad = radius; 
    rad2 = rad/2;
  }

  void display() {
    for (int i=0; i<dna.getNum()-1;i+=2) {
      float x = dna.getGene(i) * rad + origin.x - rad2; 
      float y = dna.getGene(i+1) * rad + origin.y - rad2; 
      line(origin.x, origin.y, x, y);
      rect(x, y, 3, 3);
    }
  }
}

