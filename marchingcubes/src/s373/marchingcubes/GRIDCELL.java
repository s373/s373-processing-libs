package s373.marchingcubes;


public class GRIDCELL {
  public XYZ p[]=new XYZ[8];
  public float val[] = new float[8];
  public GRIDCELL() {
    for (int i=0; i<8;i++) {
      p[i] = new XYZ(); 
      val[i] = 0.0f;
    }
  }
}