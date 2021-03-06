package hoffinc.gdxtrials1;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import hoffinc.gdxshaders.Shader2_TestShader2;
import hoffinc.gdxshaders.Shader2_TestShader2.DoubleColorAttribute;
import hoffinc.input.MyGameState;
import hoffinc.models.BasicShapes;
import hoffinc.utils.ApplicationProp;
import hoffinc.utils.InspectData;

/*
 * See
 * https://xoppa.github.io/blog/using-materials-with-libgdx/
 *
 *
 *
 * Some useful shader resources
 *
 *      https://gumroad.com/hiddenpeopleclub
 *      https://thebookofshaders.com/
 *      https://www.shadertoy.com/view/Md23DV
 *
 *
 *
 *
 *
 */
public class Trial113_ShaderTest2 implements ApplicationListener {

  public PerspectiveCamera cam;
  public CameraInputController camController;
  public Shader shader;
  private AssetManager assets;
  private Map<String, Model> my_models = new HashMap<>();
  public Array<ModelInstance> instances = new Array<ModelInstance>();
  public ModelBatch modelBatch;
  public Model sphereModel;
  public Model spaceSphereModel;


  @Override
  public void create () {
    setTitle("Shader tutorial part 2");

    cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.position.set(0f,8f,8f);
    cam.lookAt(0,0,0);
    cam.near = 1f;
    cam.far = 300f;
    cam.update();

    camController = new CameraInputController(cam);
    Gdx.input.setInputProcessor(camController);



    shader = new Shader2_TestShader2();
    shader.init();
    modelBatch = new ModelBatch();

    assets = new AssetManager();
    // assets.load("spaceskybox/spacesphere.obj", Model.class);
    assets.load("spaceskybox/smallsphere.obj", Model.class);
  }


  private void loadModels() {
    ModelBuilder modelBuilder = new ModelBuilder();
    Material mat = new Material();
    long attrib = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
    sphereModel = modelBuilder.createSphere(2f, 2f, 2f, 20, 20, mat, attrib);
    spaceSphereModel = assets.get("spaceskybox/smallsphere.obj", Model.class);

    //    InspectData.printVertices(spaceSphereModel.meshes.get(0));

  }


  private void refreshModels() {
    instances.clear();
    for (Entry<String,Model> e : my_models.entrySet()) {
      if (!e.getKey().equals("axes")) {
        instances.add(new ModelInstance(e.getValue()));
      }
    }

    // space = new ModelInstance(assets.get("spaceskybox/spacesphere.obj", Model.class));
    // space = new ModelInstance(assets.get("spaceskybox/smallsphere.obj", Model.class));
    // space.materials.get(0).set(BasicShapes.getDiffuseAttribute(0xff0000));
    // instances.add(space);


    for (float x=-5; x<5.01f; x+=2) {
      for (float z=-5; z<5.01f; z+=2) {
        ModelInstance instance = new ModelInstance(sphereModel, x, 0, z); // <-- a new ModelInstance at (x,y,z)
        //        ModelInstance instance = new ModelInstance(spaceSphereModel, x, 0, z);
        Color colorU = new Color(   (x+5)/10, 1-(z+5)/10,          0,   1); // r,g,b,a
        Color colorV = new Color( 1-(x+5)/10,          0,   (z+5)/10,   1);
        DoubleColorAttribute my_attr = new DoubleColorAttribute(DoubleColorAttribute.DiffuseUV, colorU, colorV);
        instance.materials.get(0).set(my_attr);
        instances.add(instance);
      }
    }

  }




  @Override
  public void render () {
    camController.update();

    if (MyGameState.app_starting && assets.update()) {
      loadModels();
      refreshModels();
      MyGameState.ready = true;
      MyGameState.app_starting = false;
    }

    // R: this looks more like the approach used in previous tutorials, but instead of passing
    // environment like so
    //
    //                modelBatch.render(instances, environment);
    //
    // we have
    //
    //                modelBatch.render(instance, shader);
    //
    // where shader is a custom class implementing gdx.graphics.g3d.Shader (Shader2_TestShader2.java).
    // my custom shader class sets up the ShaderProgram (gdx.graphics.glutils.ShaderProgram)
    //
    //
    //
    //
    //
    if (MyGameState.ready) {
      // Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      ScreenUtils.clear(1, 1, 1, 1);
      modelBatch.begin(cam);
      for (ModelInstance instance : instances) {
        modelBatch.render(instance, shader);
      }
      // modelBatch.render(new ModelInstance(spaceSphereModel));
      modelBatch.end();
    }

    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }

  }




  @Override
  public void dispose () {
    shader.dispose();
    modelBatch.dispose();
    assets.dispose();

    for (Model m : my_models.values()) {
      m.dispose();
    }
    if (sphereModel != null) {
      sphereModel.dispose();
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

  @Override
  public void resize(int width, int height) {}
  @Override
  public void pause() {}
  @Override
  public void resume() {}


  static private void setTitle(String title) {
    try {
      ((Lwjgl3Graphics) Gdx.graphics).getWindow().setTitle(title);
    } catch (Exception e) {}
  }

}









