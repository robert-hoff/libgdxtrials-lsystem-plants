package hoffinc.gdxtrials1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import hoffinc.gdxrewrite.CameraInputControllerZUp;
import hoffinc.input.MyGameState;
import hoffinc.input.MyInputProcessor;
import hoffinc.models.AxesModel;
import hoffinc.utils.ApplicationProp;

/*
 * Skybox from Xoppa's tutorial
 * inspecting the mesh it is modelled as a large sphere with radius 200m
 *
 *
 *
 */
public class Trial114_SkyboxAttempt extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera camera;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private AssetManager assets;
  private Map<String, Model> my_models = new HashMap<>();
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private ModelInstance space;


  @Override
  public void create () {
    setTitle("Skybox");
    MyGameState.miniPopup.addListener("Print camera transforms", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n", "camera up:", camera.up.x, camera.up.z, camera.up.z);
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n", "camera position:", camera.position.x, camera.position.y, camera.position.z);
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n\n", "camera dir:", camera.direction.x, camera.direction.y, camera.direction.z);
      }
    });

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0.5f, -0.3f, 0.5f)); // RBG and direction (r,g,b,x,y,z)
    environment.add(new PointLight().set(1f, 1f, 1f, new Vector3(2,2,2), 2f)); // r,g,b,pos,intensity
    environment.add(new PointLight().set(1f, 1f, 1f, new Vector3(1.713f,-3.408f,2.257f), 5f));

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(1.105f,-1.593f,1.100f);
    camera.up.set(0, 0, 1);
    camera.lookAt(0,0,0);
    camera.near = 0.1f;
    camera.far = 1000f;
    camera.update();
    camController = new CameraInputControllerZUp(camera);

    InputProcessor myInputProcessor = new MyInputProcessor();
    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    inputMultiplexer.addProcessor(myInputProcessor);
    inputMultiplexer.addProcessor(camController);
    Gdx.input.setInputProcessor(inputMultiplexer);

    modelBatch = new ModelBatch();
    assets = new AssetManager();
    assets.load("spaceskybox/spacesphere.obj", Model.class);
    //    assets.load("spaceskybox/smallsphere.obj", Model.class);

  }


  private void loadModels() {
    Model axes = AxesModel.buildAxesLineVersion();
    my_models.put("axes", axes);

  }


  private void refreshModels() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(my_models.get("axes")));
    }

    for (Entry<String,Model> e : my_models.entrySet()) {
      if (!e.getKey().equals("axes")) {
        instances.add(new ModelInstance(e.getValue()));
      }
    }


    space = new ModelInstance(assets.get("spaceskybox/spacesphere.obj", Model.class));


    // render a small-sphere instead (uses the same model but scaled down)
    //    space = new ModelInstance(assets.get("spaceskybox/smallsphere.obj", Model.class));
    //    space.materials.get(0).set(BasicShapes.getDiffuseAttribute(0xff0000));
    //    instances.add(space);
  }


  @Override
  public void render () {
    if (MyGameState.app_starting && assets.update()) {
      loadModels();
      refreshModels();
      MyGameState.ready = true;
      MyGameState.app_starting = false;
    }

    if (MyGameState.request_scene_refresh && MyGameState.ready) {
      refreshModels();
      MyGameState.request_scene_refresh = false;
    }

    if (MyGameState.ready) {
      // Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      ScreenUtils.clear(1, 1, 1, 1);
      modelBatch.begin(camera);
      modelBatch.render(instances, environment);
      modelBatch.render(space);
      // modelBatch.render(space, environment);
      modelBatch.end();
    }

    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }
  }


  @Override
  public void dispose () {
    modelBatch.dispose();
    instances.clear();
    assets.dispose();

    for (Model m : my_models.values()) {
      m.dispose();
    }

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


  static private void setTitle(String title) {
    try {
      ((Lwjgl3Graphics) Gdx.graphics).getWindow().setTitle(title);
    } catch (Exception e) {}
  }

}









