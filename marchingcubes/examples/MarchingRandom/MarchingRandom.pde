import s373.marchingcubes.*;
import processing.opengl.*;


MarchingCubes mc;
float isoval = 0.21;
PVector rot=new PVector();
boolean drawwireframe = false;
boolean drawnormals = true;


void setup() {
  size(1000, 600, OPENGL); 
  int mcres = 25;
  mc = new MarchingCubes(this, 1000, 600, -1000, mcres, mcres, mcres);  
  mc.isolevel = 0.02;
  mc.setRndData(0, 1);
  mc.polygoniseData();
}

void draw() {
  background(0);
  lights();

  XYZ lightloc = new XYZ (
  map(sin(frameCount*0.051), -1., 1., -500, 2*mc.worlddim.x ), 
  map(cos(frameCount*0.0321), -1., 1., -500, 2*mc.worlddim.y ), 
  map(sin(frameCount*0.031), -1., 1., 500, 2*mc.worlddim.z )
    );

  pointLight( 0, 255, 0, lightloc.x, lightloc.y, lightloc.z);
  pointLight( 0, 0, 255, lightloc.x*0.7, lightloc.y*0.7, lightloc.z*0.7);
  shininess(mouseX);
  pushMatrix();
  translate(mc.worlddim.x/2, mc.worlddim.y/2, mc.worlddim.z/2);
  rotateX(rot.y);
  rotateY(rot.x);
  translate(-mc.worlddim.x/2, -mc.worlddim.y/2, -mc.worlddim.z/2);

  translate(lightloc.x, lightloc.y, lightloc.z);
  box(20);
  popMatrix();

  XYZ loc = new XYZ(mouseX, mouseY, map(sin(frameCount*0.021), -1., 1., -1., mc.worlddim.z ));
  XYZ dim = new XYZ(10, 10, 10);
  if (mousePressed) {
    if (mouseButton==LEFT) {
      rot.x += -(mouseX-pmouseX)*0.05;
      rot.y += (mouseY-pmouseY)*0.05;
    } 
    else {
      mc.setRndData(0, 1);
    }
  }

  mc.isolevel = sin(frameCount*0.016)*0.5+0.5;
  mc.polygoniseData();

  if (drawwireframe) {
    stroke(255, 200);
    noFill();
  } 
  else {
    fill(255, 255);
    noStroke();
  }



  pushMatrix();
  translate(mc.worlddim.x/2, mc.worlddim.y/2, mc.worlddim.z/2);
  rotateX(rot.y);
  rotateY(rot.x);
  translate(-mc.worlddim.x/2, -mc.worlddim.y/2, -mc.worlddim.z/2);
  stroke(255, 0, 0, 250);  
  fill(255, 200);
  mc.draw();
  if (drawnormals)
    mc.drawnormals(10.21);
  popMatrix();

  camera();
  fill(255, 128, 0, 250);
  text("left click rotate, right click random marching cube, fps: "+frameRate+"\n"+mc.getinfo(), 5, height-50);
}

void keyPressed() {

  if (key=='n') {     
    mc.invertnormals^=true;
  }

  if (key=='s') {
    mc.writeStl(""+frameCount+".stl"); 
    return;
  }
}

