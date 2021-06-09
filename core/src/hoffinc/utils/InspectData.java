package hoffinc.utils;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;


import static com.badlogic.gdx.math.Matrix4.*;


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


  public static void showMatrix4(Matrix4 mat) {
    // System.err.printf("%9.4f %9.4f %9.4f \n", vec.x, vec.y, vec.z);
    float[] v = mat.val;
    //    System.err.printf("%9.4f %9.4f %9.4f %9.4f \n", v[0], v[4], v[ 8], v[12]);
    //    System.err.printf("%9.4f %9.4f %9.4f %9.4f \n", v[1], v[5], v[ 9], v[13]);
    //    System.err.printf("%9.4f %9.4f %9.4f %9.4f \n", v[2], v[6], v[10], v[14]);
    //    System.err.printf("%9.4f %9.4f %9.4f %9.4f \n", v[3], v[7], v[11], v[15]);

    // for clarity, use the static ints
    System.err.printf("%8.3f %8.3f %8.3f %8.3f \n", v[M00], v[M01], v[M02], v[M03]);
    System.err.printf("%8.3f %8.3f %8.3f %8.3f \n", v[M10], v[M11], v[M12], v[M13]);
    System.err.printf("%8.3f %8.3f %8.3f %8.3f \n", v[M20], v[M21], v[M22], v[M23]);
    System.err.printf("%8.3f %8.3f %8.3f %8.3f \n", v[M30], v[M31], v[M32], v[M33]);

  }



  public static void showFloatArray(float[] a) {
    if (a.length == 0) {
      System.err.println("[]");
    } else {
      System.err.printf("[");
      for (int i = 0; i < a.length; i++) {
        System.err.printf("%7.3f", a[i]);
      }
      System.err.println("]");
    }
  }




}









