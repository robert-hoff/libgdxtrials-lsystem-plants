package hoffinc.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class BasicShapes {


  public static Model line(float x0, float y0, float z0, float x1, float y1, float z1, int rgb) {
    int color = (rgb << 8) + 0xff;
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder line3D = modelBuilder.part("lines", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
    line3D.setColor(new Color(color));
    line3D.line(x0,y0,z0,x1,y1,z1);
    return modelBuilder.end();
  }



  public static Material getMaterial(int rgb) {
    int color_rgba8888 = (rgb << 8) + 0xff;
    Material mat = new Material(ColorAttribute.createDiffuse(new Color(color_rgba8888)));
    return mat;
  }



}









