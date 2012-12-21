
import s373.dna.*;

DNA A,B;
int y;

void setup(){
   size(1024,768);
   A = new DNA(width/2);
   B = new DNA(width/2);
}

void draw(){
  
  if(mousePressed && mouseButton == LEFT){
      DNA temp = new DNA(A);
      A.mate(B,  map(mouseX,0,width,0,1) );
      B.mate(temp,  map(mouseX,0,width,0,1) );   
  } 

  if(mousePressed && mouseButton == RIGHT){    
      A.mutate(  map(mouseX,0,width,0,1) );
      B.mutate(  map(mouseX,0,width,0,1) );   
  } 
  
   
   for(int i=0; i<width/2;i++){
      stroke(A.getGene(i)*255);
      point(i,y); 
   }
   for(int i=0; i<width/2;i++){
      stroke(B.getGene(i)*255);
      point(i+width/2,y); 
   }
   
   
   
   y = (y+1)%height;
      
}

void keyPressed(){
   if(key=='a'){
      int m = A.getMateMode();
      m = (m+1)%4;
      A.setMateMode(m);
      println("A matemode " + m);
   } 
   if(key=='b'){
      int m = B.getMateMode();
      m = (m+1)%4;
      B.setMateMode(m);
      println("B matemode " + m);
   } 
   
}
