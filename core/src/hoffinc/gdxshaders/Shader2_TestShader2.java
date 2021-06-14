package hoffinc.gdxshaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/*
 * See
 * https://xoppa.github.io/blog/using-materials-with-libgdx/
 *
 *
 */
public class Shader2_TestShader2 implements Shader {

  private ShaderProgram program;

  // private Camera camera;
  // private RenderContext context;


  /*
   * u_projTrans, u_worldTrans and u_colorV are int IDs returned by OpenGL
   * E.g. setUniformf(id, f,f,f) called with 3 float values will call the appropriate OpenGL function
   * to set a vec3 float uniform, i.e.
   *
   *        GL20.glUniform3f(location, v1, v2, v3)
   *
   *
   *
   *
   */
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


    // R: it's possible these names should be unique even between different shaders (needs test)
    u_color = program.getUniformLocation("u_color");
    u_projTrans = program.getUniformLocation("u_projTrans");
    u_worldTrans = program.getUniformLocation("u_worldTrans");
    // u_colorU = program.getUniformLocation("u_colorU");
    // u_colorV = program.getUniformLocation("u_colorV");


  }




  /*
   * begin is called once each render-cycle (= frame)
   *
   * RenderContext accesses the OpenGL interface, used to managed a few configurations
   * culling, depth-test, blending and texture-binding
   *
   *
   */
  @Override
  public void begin(Camera camera, RenderContext context) {
    // this.camera = camera;
    // this.context = context;
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
   * we should keep in mind what things need to be set for each frame and others that should be set for each model
   * e.g. a shadow-map may be set once per frame (and can be set in the shader's being method)
   *
   */
  @Override
  public void render(Renderable renderable) {
    // Here in this case the CPU passes the uniforms to the GPU once for each model
    program.setUniformMatrix(u_worldTrans, renderable.worldTransform);

    Color color = ((ColorAttribute) renderable.material.get(ColorAttribute.Diffuse)).color;
    program.setUniformf(u_color, color.r, color.g, color.b);

    // R: calls
    // Gdx.gl20.glUniform3f(u_color, modelColor.r, modelColor.g, modelColor.b);
    renderable.meshPart.render(program);


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

    return renderable.material.has(ColorAttribute.Diffuse);
    // return true;
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









