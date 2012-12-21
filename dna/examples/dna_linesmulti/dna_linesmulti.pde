
import s373.dna.*;

LineAgent la[];

void setup() {
  size(800, 800);

  la = new LineAgent[100];
  for (int i=0; i<100;i++) {
    float rad = width/12;
    PVector loc = new PVector((i%10)*width/10+rad/2, (i/10)*height/10+rad/2);
    la[i] = new LineAgent(loc, rad);
  }
}

void draw() {
  background(255);

  if (mousePressed && mouseButton == LEFT) {    
    for (int i=0;i<100;i++) {
      la[i].dna.mutate(  map(mouseX, 0, width, 0, 0.1) );
    }
  } 

  if (mousePressed && mouseButton == RIGHT) {    
    for (int i=0;i<99;i++) {
      la[i].dna.mate( la[i+1].dna, map(mouseX, 0, width, 0, 0.1)  );
    }
  } 

  for (int i=0;i<100;i++) {
    la[i].display();
  }
  String txt = "notmutating"; 
  if (mousePressed && mouseButton==LEFT) txt = "mutating";
  if (mousePressed && mouseButton==RIGHT) txt = "mating";
  txt += "\nmutationindex: "+map(mouseX, 0, width, 0, 0.1);
  fill(0);
  text(txt, 5, 20);
}

