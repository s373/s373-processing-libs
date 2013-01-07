/**
  1d cellular automaton example
  
  keys space & 1-0 set rules
       c clear, x setcenter1
*/

import s373.cellularautomata.*;

CA1d automata; // 1 1d cellular automata object

int nx=500, // number of cells in 1d automaton
    ny=500, // number of lines
    cy=0; // current line
float sx,sy; // lines to screen ratios


void setup() {
  size(800,700);  

  automata = new CA1d(nx);
  sx = (float)width/(float)nx;
  sy = (float)height/(float)ny;

  automata.setRule(30);
}



void draw() {

  // update automata
  automata.update();
    
  // parse data
  for(int i=0; i<nx; i++) {   
    if(automata.getCell1D(i) > 0)
      fill(255);
    else
      fill(0);
      
    rect(i*sx, cy*sy, sx, sy);
  }

  // update current y
  cy = (cy + 1) % ny;
}


void keyPressed() {
  if(key==' ') {
    automata.setRules();
  }
  if(key=='1') {
    automata.setRules(110);
  }
  if(key=='2') {
    automata.setRules(30);
  }
  if(key=='3') {
    automata.setRules(28);
  }
  if(key=='4') {
    automata.setRules(50);
  }
  if(key=='5') {
    automata.setRules(60);
  }
  if(key=='6') {
    automata.setRules(90);
  }
  if(key=='7') {
    automata.setRules(94);
  }
  if(key=='8') {
    automata.setRules(102);
  }
  if(key=='9') {
    automata.setRules(220);
  }
  if(key=='0') {
    automata.setRules(73);
  }
  if(key=='c'){
     automata.clear(); 
     return;
  }
  if(key=='x'){
     automata.setCenter1();
     return; 
  }
  println("current rule: "+automata.getRule());
}

