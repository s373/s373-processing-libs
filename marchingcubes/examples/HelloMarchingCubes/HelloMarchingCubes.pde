import s373.marchingcubes.*;

MarchingCubes mc;
int mcres = 32;
float isoval = 0.01;
boolean drawwireframe = false;
boolean drawnormals = true;

void setup() {
  size(800, 600, P3D); 
  mc = new MarchingCubes(this, 800, 600, -1024, mcres, mcres, mcres);    
  mc.isolevel = 0.0161;
  mc.zeroData();
  mc.polygoniseData();
}

void draw() {
  background(0);
  lights();
  XYZ loc = new XYZ(mouseX, mouseY, map(sin(frameCount*0.021), -1., 1., -1., mc.worlddim.z ));
  XYZ dim = new XYZ(10, 10, 10);
  if (mousePressed) {
    mc.addCube(isoval, loc, dim); 
    // mc.addIsoPoint(isoval,loc);
    mc.polygoniseData();
  }

  if (drawwireframe) {
    stroke(255, 200);
    noFill();
  } 
  else {
    fill(255, 255);
    noStroke();
  }

  stroke(255, 0, 0, 150);
  mc.draw();
  if (drawnormals)
    mc.drawnormals(0.01);


  fill(255, 128, 0, 150);
  camera();
  text("click to draw, space clear, s save stl\n"+mc.getinfo(), 5, height-40);
}

void keyPressed() {

  if (key==' ') {
    mc.zeroData();
    mc.polygoniseData();
  }

  if (key=='n') {
    mc.invertnormals^=true;
  }

  if (key=='s') {
    String filename = "file-"+frameCount+".stl";
    mc.writeStl(filename);
  }
}

