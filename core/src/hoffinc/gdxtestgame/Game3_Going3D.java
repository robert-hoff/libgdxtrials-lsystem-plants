package hoffinc.gdxtestgame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import hoffinc.utils.ApplicationProp;


/*
 *
 * libGDX API
 * ----------
 * https://libgdx.badlogicgames.com/ci/nightlies/docs/api/
 *
 *
 * com.badlogic.gdx.Application
 * ----------------------------
 * This class seems to be kind of a big deal
 *
 *
 * com.badlogic.gdx.Gdx
 * --------------------
 * This class has available various state that can be accessed through its static members
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class Game3_Going3D extends ApplicationAdapter {


  public Environment environment;
  public PerspectiveCamera cam;
  public CameraInputController camController;
  public ModelBatch modelBatch;
  public Array<ModelInstance> instances = new Array<ModelInstance>();
  public Array<ModelInstance> blocks = new Array<ModelInstance>();
  public Array<ModelInstance> invaders = new Array<ModelInstance>();
  public AssetManager assets;
  public boolean loading = true;

  public ModelInstance ship;
  public ModelInstance space;


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
    assets = new AssetManager();
    assets.load("data/ship.obj", Model.class);
    assets.load("data/block.obj", Model.class);
    assets.load("data/invader.obj", Model.class);
    assets.load("data/spacesphere.obj", Model.class);

    loading = true;


    // startTime = System.currentTimeMillis();

  }



  private void doneLoading() {
    ship = new ModelInstance(assets.get("data/ship.obj", Model.class));
    ship.transform.setToRotation(Vector3.Y, 180).trn(0, 0, 6f);
    instances.add(ship);

    Model blockModel = assets.get("data/block.obj", Model.class);
    for (float x = -5f; x <= 5f; x += 2f) {
      ModelInstance block = new ModelInstance(blockModel);
      block.transform.setToTranslation(x, 0, 3f);
      instances.add(block);
      blocks.add(block);
    }

    Model invaderModel = assets.get("data/invader.obj", Model.class);
    for (float x = -5f; x <= 5f; x += 2f) {
      for (float z = -8f; z <= 0f; z += 2f) {
        ModelInstance invader = new ModelInstance(invaderModel);
        invader.transform.setToTranslation(x, 0, z);
        instances.add(invader);
        invaders.add(invader);
      }
    }

    space = new ModelInstance(assets.get("data/spacesphere.obj", Model.class));

    loading = false;
  }


  @Override
  public void render () {
    if (loading && assets.update()) {
      doneLoading();
    }
    camController.update();


    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


    ScreenUtils.clear(1, 1, 1, 1);
    modelBatch.begin(cam);
    modelBatch.render(instances, environment);
    if (space != null) {
      modelBatch.render(space);
    }
    modelBatch.end();




    // Note - if a println() is added here it will print multiple times
    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }

  }







  @Override
  public void dispose () {
    modelBatch.dispose();
    instances.clear();
    blocks.clear();
    invaders.clear();
    assets.dispose();


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






  private static Logger log = LoggerFactory.getLogger(Game3_Going3D.class);

}











