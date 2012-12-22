

class Part {
  XYZ loc, vel, dim;

  Part() {
    reset();
  }

  void reset() {
    loc = new XYZ(random(width), 0, random(-1000));
    vel = new XYZ(random(-2, 2), 0, random(-2, 2));
    float s = random(2, 20);
    dim = new XYZ(s, s, s);
  }

  void update() {
    vel.y += 0.09;
    loc.x += vel.x;
    loc.y += vel.y;
    loc.z += vel.z;
    bound();
    mc.addCube(isoval, loc, dim);
  }

  void display() {
    pushMatrix();
    translate(loc.x, loc.y, loc.z);
    box(dim.x, dim.x, dim.x);
    popMatrix();
  }

  void bound() {
    if (loc.x < 0) {
      loc.x = mc.worlddim.x;
    }
    if (loc.x > mc.worlddim.x) {
      loc.x = 0;
    }
    if (loc.z > 0) {
      loc.z = mc.worlddim.z;
    }
    if (loc.z < mc.worlddim.z) {
      loc.z = 0;
    }
    if (loc.y > mc.worlddim.y) {
      loc.y = mc.worlddim.y;
      vel.y = -vel.y;
    }
  }
}

