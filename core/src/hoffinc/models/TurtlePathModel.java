package hoffinc.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.Matrix4;

public class TurtlePathModel {



  private static final float CYL_DIAM = 0.1f;
  private static final float CYL_LENGTH = 1.0f;
  private static final int MESH_RES = 10;


  // float width, float height, float depth, int divisions, Material material, long attributes
  // turtlePath = modelBuilder.createCylinder(0.1f, 1f, 0.1f, 10, getTurtleMaterial(), Usage.Position | Usage.Normal);




  public static Model buildTurtlePath() {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();

    MeshPartBuilder turtlePathBuilder = modelBuilder.part("turtle_path", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,getTurtleMaterial());
    // turtlePathBuilder.setVertexTransform(new Matrix4().translate(0,0,CYL_LENGTH/2).rotate(1,0,0,90));
    turtlePathBuilder.setVertexTransform(new Matrix4().translate(0,CYL_LENGTH/2,0));
    CylinderShapeBuilder.build(turtlePathBuilder, CYL_DIAM, CYL_LENGTH, CYL_DIAM, MESH_RES);

    return modelBuilder.end();
  }


  private static Material getTurtleMaterial() {
    Material turtleMat = new Material(ColorAttribute.createDiffuse(new Color(0x3399ffff)));
    return turtleMat;
  }


}

