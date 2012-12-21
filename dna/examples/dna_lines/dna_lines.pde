
import s373.dna.*;

DNA dna;

void setup(){
   size(400,400);
   dna = new DNA((int)random(10,100)*2);
}

void draw(){
  background(255);
  
  if(mousePressed && mouseButton == LEFT){    
      dna.mutate(  map(mouseX,0,width,0,1) );
  } 
  
   
   for(int i=0; i<dna.getNum()-2;i+=2){
     float x = dna.getGene(i) * 300 + width/2 - 150; 
     float y = dna.getGene(i+1) * 300 + height/2 - 150; 
     
     line(width/2, height/2, x, y);
     rect(x,y, 5, 5); 
   }
   
      
}

void keyPressed(){
   
}
