import s373.cellularautomata.*;


CA2d automata;

void setup() { 
  size(300,300);
  automata = new CA2d(width,height);
  automata.setRules();
}

void draw() {

  background(0);
  stroke(255);
  
  automata.update();
  
  for(int j = 0;j<height; j++) {
    for(int i=0; i<width;i++) {
      if(automata.getCell2D(i,j) > 0) {
        point(i,j);
      } 
    }
  }
}

void keyPressed(){
   if(key==' '){
      automata.setRules();
   } 
}

