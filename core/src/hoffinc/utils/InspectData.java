package hoffinc.utils;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;

public class InspectData {






  public static void printVertices(Mesh mesh) {

    int vertexSize = mesh.getVertexSize();
    int floatsPerVertex = vertexSize / 4;
    int nVertices = mesh.getNumVertices();
    int nFloats = nVertices*floatsPerVertex;

    float[] data = new float[nFloats];
    mesh.getVertices(data);

    for (int i = 0; i < nFloats; i++) {
      if (i%floatsPerVertex == 0) {
        System.err.printf("%3d: ",i/floatsPerVertex);
      }
      System.err.printf("%9.3f",data[i]);
      if (i%floatsPerVertex == floatsPerVertex-1) {
        System.err.println();
      }
    }

  }



  public static void showVector3(Vector3 vec) {
    System.err.printf("%9.4f %9.4f %9.4f \n", vec.x, vec.y, vec.z);
  }

  public static String strVector3(Vector3 vec) {
    return String.format("%9.4f %9.4f %9.4f", vec.x, vec.y, vec.z);
  }






}








