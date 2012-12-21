import s373.dna.*;

DNA dna;
int y;

void setup(){
   size(1024,768);
   dna = new DNA(width); 
}

void draw(){
   dna.mutateGene ( (int) map(mouseX,0,width,0, dna.getNum()) , 
                    map(mouseY, 0, height, 0, 1) );
   
   for(int i=0; i<width;i++){
      stroke(dna.getGene(i)*255);
      point(i,y); 
   }
   y = (y+1)%height;
  
    
}
