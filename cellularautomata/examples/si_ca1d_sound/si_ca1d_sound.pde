

import s373.cellularautomata.*;
import processing.opengl.*;
import ddf.minim.*;
import ddf.minim.signals.*;

Minim minim;
AudioOutput out;
SineWave sines[];

CA1d automata;

int nx=50,ny=200,cy=0;
float sx,sy;


void setup() {
  size(700,700,OPENGL);  
  
  minim = new Minim(this);  
  out = minim.getLineOut(Minim.STEREO);

  sines = new SineWave[nx];
  for(int i=0; i<nx; i++) {
    sines[i] = new SineWave( mtof(27+i) , 0.25, out.sampleRate());
    sines[i].portamento(1);
    out.addSignal(sines[i]);
  }
  // create a sine wave Oscillator, set to 440 Hz, at 0.5 amplitude, sample rate from line out
//  sine = new SineWave(440, 0.5, out.sampleRate());

  automata = new CA1d(nx);
  sx = (float)width/(float)nx;
  sy = (float)height/(float)ny;

  automata.setRules(5);
}



void draw() {

  if(frameCount%10==0){
      // update automata each frame?
      automata.update();
      cy = (cy + 1) % ny;
  
    
      // get data
      int data[] = automata.getData();
      
      // parse data
      for(int i=0; i<nx; i++) {   
        if(data[i] > 0)
          fill(255);
        else
          fill(0);
          
        rect(i*sx, cy*sy, sx, sy);
        
        //snd
        sines[i].setAmp(data[i]*5/(float)nx);
      }
    


  }

}


void keyPressed() {
  if(key==' ') {
    automata.setRules();
  }
}


float mtof(float midi){  
  return  (440.0f * pow(2, ((midi-69.0) / 12.0)) );  
}
