package hoffinc.gdxtrials1;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.utils.ScreenUtils;
import hoffinc.gdxshaders.Shader1_TestShader;
import hoffinc.input.MyGameState;
import hoffinc.utils.ApplicationProp;

/*
 * From https://xoppa.github.io/blog/creating-a-shader-with-libgdx/
 *
 */
public class Trial112_ShaderTest implements ApplicationListener {

  public PerspectiveCamera cam;
  public CameraInputController camController;
  public Shader shader;
  public RenderContext renderContext;
  public Model sphereModel;
  public Renderable renderable;


  @Override
  public void create () {
    setTitle("Shader super simple test");

    cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.position.set(2f, 2f, 2f);
    cam.lookAt(0,0,0);
    cam.near = 1f;
    cam.far = 300f;
    cam.update();

    camController = new CameraInputController(cam);
    Gdx.input.setInputProcessor(camController);

    ModelBuilder modelBuilder = new ModelBuilder();
    long attr = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
    Material mat = new Material();
    //    sphereModel = modelBuilder.createSphere(2f, 2f, 2f, 20, 20, mat, attr);
    sphereModel = modelBuilder.createBox(2,2,2,mat,attr);

    NodePart blockPart = sphereModel.nodes.get(0).parts.get(0);

    renderable = new Renderable();
    blockPart.setRenderable(renderable);
    // renderable.meshPart.primitiveType = GL20.GL_POINTS;
    renderable.environment = null;
    renderable.worldTransform.idt();

    // renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
    // R: The WEIGHTED parameter doesn't exist anymore so changed to LRU (possibly - least recently used?)
    // Seems to work the same as in the example
    renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.LRU, 1));
    shader = new Shader1_TestShader();
    shader.init();


    // Printing the vertices reveals 6 flots per vertex, the last two are UV coordinates
    // ranging from 0 to 1
    // InspectData.printVertices(sphereModel.meshes.get(0));
  }


  @Override
  public void render () {
    camController.update();

    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    ScreenUtils.clear(1, 1, 1, 1);

    renderContext.begin();
    shader.begin(cam, renderContext);
    shader.render(renderable);
    shader.end();
    renderContext.end();

    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }
  }


  @Override
  public void dispose () {
    shader.dispose();
    sphereModel.dispose();

    if (MyGameState.jwin != null) {
      MyGameState.jwin.dispose();
    }

    // save window x,y and window width,height
    // Note - The initial size of the window is set from the Desktop-launcher
    Lwjgl3Graphics lwjgl3 = (Lwjgl3Graphics) Gdx.graphics;
    int win_width = lwjgl3.getWidth();
    int win_height = lwjgl3.getHeight();
    int win_x = lwjgl3.getWindow().getPositionX();
    int win_y = lwjgl3.getWindow().getPositionY();

    String FILENAME = "app.auto.properties";
    ApplicationProp prop = new ApplicationProp(FILENAME);
    prop.addProperty("WIN_WIDTH", ""+win_width);
    prop.addProperty("WIN_HEIGHT", ""+win_height);
    prop.addProperty("WIN_X", ""+win_x);
    prop.addProperty("WIN_Y", ""+win_y);
    prop.saveToFile();
  }


  @Override
  public void resize(int width, int height) {}
  @Override
  public void pause() { }
  @Override
  public void resume() {}


  static private void setTitle(String title) {
    try {
      ((Lwjgl3Graphics) Gdx.graphics).getWindow().setTitle(title);
    } catch (Exception e) {}
  }

}









