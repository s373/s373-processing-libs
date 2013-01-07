import s373.cellularautomata.*;


CA3d automata;
int s = 64;

void setup() { 
  size(500,500);
  automata = new CA3d(s,s,s);
  automata.setRules();
}

void draw() {

  background(0);
  stroke(255);
  
  automata.update();
  
  float sx = width/s;
  float sy = height/s;
  int layer = (int)map(mouseX,0,width,0,s);
  for(int j = 0;j<s; j++) {
    for(int i=0; i<s;i++) {
      if(automata.getCell3D(i,j,layer) > 0) {
        point(i*sx,j*sy);
      } 
    }
  }
  text("layer "+layer, 5, height-20);
}

void keyPressed(){
   if(key==' '){
      automata.setRules();
   } 

}

