package hoffinc.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;


/*
 *
 * For different types of attributes
 * https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/g3d/Attribute.html
 *
 *
 */
public class BasicShapes {

  public static Material getMaterial(int rgb) {
    int color_rgba8888 = (rgb << 8) + 0xff;
    Material mat = new Material(ColorAttribute.createDiffuse(new Color(color_rgba8888)));
    return mat;
  }

  public static ColorAttribute getDiffuseAttribute(int rgb) {
    return getDiffuseAttribute(rgb,0xff);
  }

  public static ColorAttribute getDiffuseAttribute(int rgb, int alpha) {
    int color_rgba8888 = (rgb << 8) + alpha;
    // System.err.printf("%08X \n", color_rgba8888);
    return ColorAttribute.createDiffuse(new Color(color_rgba8888));
  }

  public static ColorAttribute getSpecularAttribute(int rgb) {
    return getSpecularAttribute(rgb, 0xff);
  }

  public static ColorAttribute getSpecularAttribute(int rgb, int alpha) {
    int color_rgba8888 = (rgb << 8) + alpha;
    return ColorAttribute.createSpecular(new Color(color_rgba8888));
  }

  public static ColorAttribute getEmmisiveAttribute(int rgb, int alpha) {
    int color_rgba8888 = (rgb << 8) + alpha;
    // System.err.printf("%08X \n", color_rgba8888);
    return ColorAttribute.createEmissive(new Color(color_rgba8888));
  }

  public static ColorAttribute getReflectionAttribute(int rgb) {
    int color_rgba8888 = (rgb << 8) + 0xff;
    return ColorAttribute.createReflection(new Color(color_rgba8888));
  }

  public static ColorAttribute getFogAttribute(int rgb) {
    int color_rgba8888 = (rgb << 8) + 0xff;
    return ColorAttribute.createFog(new Color(color_rgba8888));
  }

  public static Model line(float x0, float y0, float z0, float x1, float y1, float z1, int rgb) {
    int color = (rgb << 8) + 0xff;
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder line3D = modelBuilder.part("lines", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
    line3D.setColor(new Color(color));
    line3D.line(x0,y0,z0,x1,y1,z1);
    return modelBuilder.end();
  }

  /*
   * Build a circle with line segments
   * Centered at (0,0,0) in the x-y plane
   *
   */
  public static Model buildCircleOutline1(int vertex_count, float radius, int color_rgb) {
    if (vertex_count < 3 || vertex_count > 100 || radius <= 0f) {
      throw new RuntimeException("error!");
    }
    Material mat = BasicShapes.getMaterial(color_rgb);
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    // R: ColorUnpacked?
    // MeshPartBuilder circlePartBuilder = modelBuilder.part("lines", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, mat);
    MeshPartBuilder circlePartBuilder = modelBuilder.part("lines", GL20.GL_LINES, Usage.Position, mat);
    float x1 = radius;
    float y1 = 0f;
    double angle = 0.0;
    double dtheta = 2 * Math.PI / vertex_count;
    while(vertex_count --> 1) {
      angle += dtheta;
      float x2 = radius * (float) Math.cos(angle);
      float y2 = radius * (float) Math.sin(angle);
      circlePartBuilder.line(x1,y1,0,x2,y2,0);
      x1 = x2;
      y1 = y2;
      if (vertex_count == 1) {
        circlePartBuilder.line(x2,y2,0,radius,0,0);
      }
    }
    return modelBuilder.end();
  }


}









