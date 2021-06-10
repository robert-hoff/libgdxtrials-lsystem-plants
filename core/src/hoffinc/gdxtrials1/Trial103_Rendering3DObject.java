package hoffinc.gdxtrials1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.ScreenUtils;
import hoffinc.utils.ApplicationProp;

/*
 * Drawing a cube
 * from https://xoppa.github.io/blog/basic-3d-using-libgdx/
 *
 *
 */
public class Trial103_Rendering3DObject extends ApplicationAdapter {


  private Environment environment;
  private PerspectiveCamera cam;
  private CameraInputController camController;
  private ModelBatch modelBatch;
  private Model cubeModel;
  private ModelInstance cubeInstance;


  @Override
  public void create () {
    log.trace("starting app");

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.position.set(0f, 7f, 10f);
    cam.lookAt(0,0,0);
    cam.near = 1f;
    cam.far = 300f;
    cam.update();
    camController = new CameraInputController(cam);
    Gdx.input.setInputProcessor(camController);

    modelBatch = new ModelBatch();
    ModelBuilder modelBuilder = new ModelBuilder();
    // createBox(..) taking 5 arguments
    cubeModel = modelBuilder.createBox(
        5f,
        5f,
        5f,
        new Material( ColorAttribute.createDiffuse(Color.GREEN) ),
        Usage.Position | Usage.Normal);         // without the Usage.Normal here the box will appear all in one colour
    cubeInstance = new ModelInstance(cubeModel);


  }



  @Override
  public void render () {
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


    ScreenUtils.clear(1, 1, 1, 1);
    modelBatch.begin(cam);
    modelBatch.render(cubeInstance, environment);
    modelBatch.end();



    // Note - if a println() is added here it will print multiple times
    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }

  }




  // called on Window resize
  // the camera currently gets distorted
  @Override
  public void resize(int width, int height) {
    //    System.err.println(width);
    //    System.err.println(height);
  }




  @Override
  public void dispose () {
    modelBatch.dispose();
    cubeModel.dispose();


    // save window x,y and window width,height
    // NOTE - The initial size of the window is set from the Desktop-launcher
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



  private static Logger log = LoggerFactory.getLogger(Trial103_Rendering3DObject.class);

}









