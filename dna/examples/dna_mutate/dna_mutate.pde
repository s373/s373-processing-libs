import s373.dna.*;

DNA dna;
int y;

void setup(){
   size(1024,768);
   dna = new DNA(width); 
}

void draw(){
   dna.mutate ( map(mouseX,0,width,0,0.05) );
   
   for(int i=0; i<width;i++){
      stroke(dna.getGene(i)*255);
      point(i,y); 
   }
   y = (y+1)%height;
  
    
}
