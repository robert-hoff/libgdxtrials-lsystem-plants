package hoffinc.models;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;


/*
 *
 * all models lie along the Z-axis (forward is the Z-axis)
 *
 *
 */
public class PlantParts {


  public static Model leaf1() {
    return leaf1(0x009933);     // leafy green
  }

  public static Model leaf1(int rgb) {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();

    Vector3 v0 = new Vector3( 0.0000f,0.0000f,0.0000f);
    Vector3 v1 = new Vector3( 0.0383f,0.0000f,0.0924f);
    Vector3 v2 = new Vector3( 0.0383f,0.0000f,0.1924f);
    Vector3 v3 = new Vector3( 0.0000f,0.0000f,0.2848f);
    Vector3 v4 = new Vector3(-0.0383f,0.0000f,0.1924f);
    Vector3 v5 = new Vector3(-0.0383f,0.0000f,0.0924f);
    Vector3 normal_f = new Vector3(0,-1,0);
    Vector3 normal_b = new Vector3(0, 1,0);

    int attr = Usage.Position | Usage.Normal;
    Material mat = BasicShapes.getMaterial(rgb);
    MeshPartBuilder meshBuilder = modelBuilder.part("nameid", GL20.GL_TRIANGLES, attr, mat);

    // Vector3 pos, Vector3 nor, Color col, Vector2 uv
    meshBuilder.vertex(v0, normal_f, null, null);
    meshBuilder.vertex(v1, normal_f, null, null);
    meshBuilder.vertex(v2, normal_f, null, null);
    meshBuilder.vertex(v3, normal_f, null, null);
    meshBuilder.vertex(v4, normal_f, null, null);
    meshBuilder.vertex(v5, normal_f, null, null);
    meshBuilder.vertex(v0, normal_b, null, null);
    meshBuilder.vertex(v1, normal_b, null, null);
    meshBuilder.vertex(v2, normal_b, null, null);
    meshBuilder.vertex(v3, normal_b, null, null);
    meshBuilder.vertex(v4, normal_b, null, null);
    meshBuilder.vertex(v5, normal_b, null, null);

    // CCW winding
    meshBuilder.triangle((short) 0, (short) 1, (short) 5);
    meshBuilder.triangle((short) 1, (short) 2, (short) 5);
    meshBuilder.triangle((short) 2, (short) 4, (short) 5);
    meshBuilder.triangle((short) 2, (short) 3, (short) 4);

    // back (use CW winding)
    meshBuilder.triangle((short) 11, (short) 7, (short) 6);
    meshBuilder.triangle((short) 11, (short) 8, (short) 7);
    meshBuilder.triangle((short) 11, (short) 10, (short) 8);
    meshBuilder.triangle((short) 10, (short) 9, (short) 8);

    Model leafModel = modelBuilder.end();
    return leafModel;
  }


  public static Model wedge(int rgb) {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    Vector3 v0 = new Vector3(0.0000f,0.0000f,0.0000f);
    Vector3 v1 = new Vector3(0.0309f,0.0000f,0.0951f);
    Vector3 v2 = new Vector3(0.0309f,0.0000f,0.1951f);
    Vector3 v3 = new Vector3(0.0000f,0.0000f,0.1000f);
    Vector3 normal_f = new Vector3(0,-1,0);
    Vector3 normal_b = new Vector3(0, 1,0);
    int attr = Usage.Position | Usage.Normal;
    Material mat = BasicShapes.getMaterial(rgb);
    MeshPartBuilder meshBuilder = modelBuilder.part("nameid", GL20.GL_TRIANGLES, attr, mat);
    // Vector3 pos, Vector3 nor, Color col, Vector2 uv
    meshBuilder.vertex(v0, normal_f, null, null);
    meshBuilder.vertex(v1, normal_f, null, null);
    meshBuilder.vertex(v2, normal_f, null, null);
    meshBuilder.vertex(v3, normal_f, null, null);
    meshBuilder.vertex(v0, normal_b, null, null);
    meshBuilder.vertex(v1, normal_b, null, null);
    meshBuilder.vertex(v2, normal_b, null, null);
    meshBuilder.vertex(v3, normal_b, null, null);
    // CCW winding
    meshBuilder.triangle((short) 0, (short) 1, (short) 3);
    meshBuilder.triangle((short) 1, (short) 2, (short) 3);
    // back (use CW winding)
    meshBuilder.triangle((short) 7, (short) 5, (short) 4);
    meshBuilder.triangle((short) 7, (short) 6, (short) 5);
    Model wedgeModel = modelBuilder.end();
    return wedgeModel;
  }




  /*
   *    0x996633           // dark brown
   *    0xcc9966           // medium brown
   *
   */
  public static Model stemTrunk(float length, float diam, int mesh_res, int rgb) {
    int attr = Usage.Position | Usage.Normal;
    Material mat = BasicShapes.getMaterial(rgb);
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder turtlePathBuilder = modelBuilder.part("branch", GL20.GL_TRIANGLES, attr, mat);
    turtlePathBuilder.setVertexTransform(new Matrix4().translate(0,0,length/2).rotate(1,0,0,90));
    CylinderShapeBuilder.build(turtlePathBuilder, diam, length, diam, mesh_res);
    return modelBuilder.end();
  }




}






