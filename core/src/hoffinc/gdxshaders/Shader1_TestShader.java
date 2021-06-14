package hoffinc.gdxshaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/*
 * See
 * https://xoppa.github.io/blog/creating-a-shader-with-libgdx/
 *
 *
 */
public class Shader1_TestShader implements Shader {
  private ShaderProgram program;
  private Camera camera;
  private RenderContext context;
  private int u_projTrans;
  private int u_worldTrans;

  private String PATH = "shaders/shaders1";


  @Override
  public void init() {
    // String vert = Gdx.files.internal(data+"/test.vertex.glsl").readString();
    // String frag = Gdx.files.internal(data+"/test.fragment.glsl").readString();

    // R: put shader files in `desktop/shaders/`
    // (otherwise forced to refresh Gradle project on each change)

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
    u_projTrans = program.getUniformLocation("u_projViewTrans");
    u_worldTrans = program.getUniformLocation("u_worldTrans");
  }


  @Override
  public void dispose() {
    program.dispose();
  }


  @Override
  public void begin(Camera camera, RenderContext context) {
    this.camera = camera;
    this.context = context;
    program.begin();
    program.setUniformMatrix(u_projTrans, camera.combined);
    context.setDepthTest(GL20.GL_LEQUAL);
    context.setCullFace(GL20.GL_BACK);
  }


  @Override
  public void render(Renderable renderable) {
    program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
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


  @Override
  public boolean canRender(Renderable instance) {
    return true;
  }

}









