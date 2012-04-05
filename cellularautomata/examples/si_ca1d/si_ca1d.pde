

import s373.cellularautomata.*;
import processing.opengl.*;

CA1d automata;

int nx=500,ny=500,cy=0;
float sx,sy;


void setup() {
  size(700,700,OPENGL);  

  automata = new CA1d(nx);
  sx = (float)width/(float)nx;
  sy = (float)height/(float)ny;

  automata.setRules(5);
}



void draw() {

  // update automata
  automata.update();
  
  // get data
  int data[] = automata.getData();
  
  // parse data
  for(int i=0; i<nx; i++) {   
    if(data[i] > 0)
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
}

