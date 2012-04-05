/// test dna lib

import processing.opengl.*;
import s373.dna.*;


String targetStr = "i code therefore i am "+(int)random(100);
DNA targetDna;
DNA testDna, curDna, machDna, mach2Dna;
int numGenerations, numMach, numMach2;
boolean numMachSet = false, numMach2Set = false;
float fit;



void setup() {
  size(500,500,OPENGL);

  ///analyse and build target dna
  float data[] = new float [ targetStr.length() ];
  for(int i=0;i<data.length;i++) {
    data[i] = ( targetStr.charAt(i) / 255.0f );
  }
  targetDna = new DNA(data);
  /// random dnas to start
  testDna = new DNA(data.length);
  curDna = new DNA(testDna);
  machDna = new DNA(curDna);
  mach2Dna = new DNA(machDna);

  numGenerations = 0;

  targetDna.print();
  fit = curDna.fitness(targetDna);
  println("curDna fitness: "+fit);
  textFont(createFont("arial",15));
  // textMode(CENTER);
  fill(0);
  noStroke();
  println(targetStr);
}






void draw() {

  background(247);
  numGenerations++;
  
  /// monkey approach
  fill(0);
  text( writeFromDna(targetDna), 5, 20);
  text( writeFromDna(curDna), 5, 50);
  float bestFit = curDna.fitness(targetDna);
  float testFit=0;
  int numLoops = 0;
  //  while ( testFit <= bestFit && numLoops < 1000) {
  if ( testFit <= bestFit ) {
    testDna.setDna(curDna);
    testDna.mutate(0.05);
    testFit = testDna.fitness(targetDna);
    numLoops++;
  }
  curDna.setDna(testDna);

  text(""+numGenerations+" "+numLoops, 5, 100);

  int w = 250;
  drawSliders(targetDna, width - w - 25, 20, w, 100, color(0,100,200), 25);
  drawSliders(curDna, width - w - 25, 20, w, 100, color(0,200,100), 150);


  /// machDna approach
  boolean doneMach = true;
  for(int i=0; i<machDna.num;i++) {
    float difGene = machDna.differenceGene( i, targetDna );
    if( difGene > 1e-8) {  ///0.00000001
      machDna.mutateGene(i, difGene*1.2);
      doneMach = false;
      break;
    }
  }
  if(doneMach && !numMachSet) {
    numMachSet = true;
    numMach = numGenerations;
  }
  fill(0);
  text( writeFromDna(targetDna), 5, 220);
  text( writeFromDna(machDna)+" : "+numMach+" : "+doneMach, 5, 250);
  drawSliders(targetDna, width - w - 25, 220, w, 100, color(0,100,200), 25);
  drawSliders(machDna, width - w - 25, 220, w, 100, color(0,200,100), 150);


  /// mach2Dna
  boolean doneTest = true;
  float testDif[] = mach2Dna.differenceDNA(targetDna);
  for(int i=0; i<testDif.length;i++) {
    if( testDif[i] > 1e-8) {
      mach2Dna.mutateGene(i, testDif[i]*0.7);
      doneTest = false;
    }
  }
  if(doneTest && !numMach2Set) {
    numMach2Set = true;
    numMach2 = numGenerations;
  }
  fill(0);
  text( writeFromDna(targetDna), 5, 420);
  text( writeFromDna(mach2Dna)+" : "+numMach2+" : "+doneTest, 5, 450);
  drawSliders(targetDna, width - w -25, 420, w, 100, color(0,100,200), 25);
  drawSliders(mach2Dna, width - w -25, 420, w, 100, color(0,200,100), 150);
}



String writeFromDna(DNA d) {
  String s="";
  for(int i=0;i<d.num;i++) {
    s+= (char)( d.getGene(i)*255.0f);
  }
  return s;
}


void drawSliders(DNA dna, int x, int y, int w, int h, color c, int al) {
  float rw = (float) w / dna.size(); 
  fill(c, al);
  for(int i=0;i<dna.size();i++) {
    int locx = x + (int)(i*rw);
    rect(locx, y, rw, dna.getGene(i)*h);
  }
}


void keyPressed(){
   if(key=='s')
    saveFrame("dnaReach-#####.png"); 
}
