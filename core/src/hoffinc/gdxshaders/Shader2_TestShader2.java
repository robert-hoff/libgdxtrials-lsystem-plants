package hoffinc.gdxshaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

/*
 * See
 * https://xoppa.github.io/blog/using-materials-with-libgdx/
 *
 *
 */
public class Shader2_TestShader2 implements Shader {

  private ShaderProgram program;
  private Camera camera;
  private RenderContext context;
  private int u_projTrans;
  private int u_worldTrans;
  private int u_colorU;
  private int u_colorV;
  private String PATH = "shaders/shaders2";

  private int u_color;


  /*
   * init is called once only at application start
   *
   */
  @Override
  public void init() {
    String vertex_shader_source;
    String fragment_shader_source;

    try {
      vertex_shader_source = Files.readString(Paths.get(PATH+"/test.vertex.glsl"));
      fragment_shader_source = Files.readString(Paths.get(PATH+"/test.fragment.glsl"));
    } catch (IOException e) {
      throw new RuntimeException("couldn't read file!");
    }

    program = new ShaderProgram(vertex_shader_source, fragment_shader_source);
    if (!program.isCompiled()) {
      throw new GdxRuntimeException(program.getLog());
    }
    u_projTrans = program.getUniformLocation("u_projTrans");
    u_worldTrans = program.getUniformLocation("u_worldTrans");
    // u_colorU = program.getUniformLocation("u_colorU");
    // u_colorV = program.getUniformLocation("u_colorV");

    u_color = program.getUniformLocation("u_color");
  }


  /*
   * begin is called once each render cycle (or frame)
   *
   */
  @Override
  public void begin(Camera camera, RenderContext context) {
    this.camera = camera;
    this.context = context;
    program.begin();
    program.setUniformMatrix(u_projTrans, camera.combined);
    context.setDepthTest(GL20.GL_LEQUAL);
    context.setCullFace(GL20.GL_BACK);
  }


  //  @Override
  //  public void render(Renderable renderable) {
  //    program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
  //    DoubleColorAttribute attribute = ((DoubleColorAttribute) renderable.material.get(DoubleColorAttribute.DiffuseUV));
  //    program.setUniformf(u_colorU, attribute.color1.r, attribute.color1.g, attribute.color1.b);
  //    program.setUniformf(u_colorV, attribute.color2.r, attribute.color2.g, attribute.color2.b);
  //    renderable.meshPart.render(program);
  //  }

  /*
   * render is called once per renderable object (i.e. each for each ModelInstance)
   *
   */
  @Override
  public void render(Renderable renderable) {
    program.setUniformMatrix(u_worldTrans, renderable.worldTransform);

    Color color = (Color) renderable.userData;
    program.setUniformf(u_color, color.r, color.g, color.b);
    renderable.meshPart.render(program);

    // renderable.mesh.render(program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);

  }


  @Override
  public void end() {
    program.end();
  }


  @Override
  public int compareTo(Shader other) {
    return 0;
  }

  /*
   * NOTE - if false is returned here the renderer will try to use a different shader
   * may fall back on the default shader
   *
   */
  @Override
  public boolean canRender(Renderable renderable) {
    // return renderable.material.has(DoubleColorAttribute.DiffuseUV);
    return true;
  }


  @Override
  public void dispose() {
    program.dispose();
  }


  public static class DoubleColorAttribute extends Attribute {
    public final static String DiffuseUVAlias = "diffuseUVColor";
    public final static long DiffuseUV = register(DiffuseUVAlias);

    public final Color color1 = new Color();
    public final Color color2 = new Color();

    public DoubleColorAttribute (long type, Color c1, Color c2) {
      super(type);
      color1.set(c1);
      color2.set(c2);
    }

    @Override
    public Attribute copy () {
      return new DoubleColorAttribute(type, color1, color2);
    }

    @Override
    protected boolean equals (Attribute other) {
      DoubleColorAttribute attr = (DoubleColorAttribute) other;
      return type == other.type && color1.equals(attr.color1)
          && color2.equals(attr.color2);
    }

    @Override
    public int compareTo (Attribute other) {
      if (type != other.type)
        return (int) (type - other.type);
      DoubleColorAttribute attr = (DoubleColorAttribute) other;
      return color1.equals(attr.color1)
          ? attr.color2.toIntBits() - color2.toIntBits()
              : attr.color1.toIntBits() - color1.toIntBits();
    }
  }


}









