import processing.opengl.*;
import s373.marchingcubes.*;

MarchingCubes mc;
float isoval = 0.01;
PVector rot=new PVector();
boolean drawwireframe = false;

void setup() {
  size(1000, 600, OPENGL); 
  mc = new MarchingCubes(this, 1000.0f, 600.0f, -600.0f, 32, 32, 16);  
  mc.zeroData();
  mc.polygoniseData();
  mc.isolevel = 0.01;
}


void draw() {
  
  background(0);
  lights();
//  if (frameCount%60==0) {
//    mc.setRndData(0., 0.02);      
//  }
  
  XYZ loc = new XYZ(mouseX, mouseY, width/2);
  XYZ dim = new XYZ(1,1,10);
  if(mousePressed){
    if(mouseButton==LEFT){
      rot.x += -(mouseX-pmouseX)*0.005;
      rot.y += (mouseY-pmouseY)*0.005;

    } else {

       mc.addCube(isoval, loc,dim); 
       mc.polygoniseData();
    }

  }
  
  if(drawwireframe){
    stroke(255,200);
    noFill();
  } else {
    
  fill(255,255);
//  stroke(255,100);
  noStroke();
  }

 
  pushMatrix();
  translate(width/2, height/2, -width/2);
  rotateX(rot.y);
  rotateY(rot.x);
  translate(-width/2, -height/2, width/2);
  mc.draw();
  stroke(255,0,0,100);
  mc.drawnormals(0.05);//50.0);
  popMatrix();
  camera();
  text(mc.getinfo(),5,height-20);
}

void keyPressed(){
  
  if(key=='A'){
     mc.post();
//     mc.datasubs(1);//isoval);
     mc.datainvert();//isoval);
     mc. polygoniseData();
     mc.post();
  }
  
  if(key=='w'){
     drawwireframe^=true;
     return; 
  }
  
  if(key=='s'){
     mc.writeStl(dataPath(""+frameCount+".stl"));//"/testStl/"+frameCount+".stl"); 
     return;
  }
  
  if(key=='n'){
//     invertnormals^=true; 
  }
  
  if(key=='i'){
     mc.isolevel-=0.005; 
  }
  
  if(key=='I'){
     mc.isolevel+=0.005; 
    
  }
  
  if(key=='a')
    isoval*=-1;
  if(key=='g')
    isoval*=0.9;
  if(key=='G')
    isoval*=1.1;
  if(key=='r'){
    mc.setRndData(0.001,0.5);
  }
 
   mc.polygoniseData();
    
}

