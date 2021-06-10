package hoffinc.gdxtrials1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import hoffinc.gdxrewrite.CameraInputControllerZUp;
import hoffinc.input.MyGameState;
import hoffinc.input.MyInputProcessor;
import hoffinc.models.AxesModel;
import hoffinc.models.BasicShapes;
import hoffinc.utils.ApplicationProp;

/*
 * Some matrix transforms on the testshape
 *
 */
public class Trial110_TestShapeTransforms extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera camera;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private AssetManager assets;
  private String coneArrowFilename = "conearrow.obj";
  private Model axes;
  private boolean show_axes = true;
  private Model modelTestShape;


  @Override
  public void create () {
    setTitle("Test shape transforms");
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
    camera.position.set(3.5f, -10f, 3f);
    camera.up.set(0, 0, 1);
    camera.lookAt(0,0,0);
    camera.near = 0.1f;
    camera.far = 300f;
    camera.update();
    camController = new CameraInputControllerZUp(camera);

    InputProcessor myInputProcessor = new MyInputProcessor();
    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    inputMultiplexer.addProcessor(myInputProcessor);
    inputMultiplexer.addProcessor(camController);
    Gdx.input.setInputProcessor(inputMultiplexer);

    modelBatch = new ModelBatch();
    assets = new AssetManager();
    assets.load(coneArrowFilename, Model.class);
  }



  private void loadModels() {
    axes = AxesModel.buildAxesLineVersion();
    modelTestShape = assets.get(coneArrowFilename, Model.class);

    // R: this doesn't work here! (the localTransform in Node is final)
    //    Vector3 RIGHT = new Vector3(1,0,0);
    //    Quaternion rotx = new Quaternion(RIGHT, -15);
    //    Matrix4 local_transform = new Matrix4();
    //    local_transform.rotate(rotx);
    //    modelTestShape.nodes.get(0).localTransform = local_transform;


    // R: it seems to be ok to change material here
    // But not all the color attributes seem to do anything (maybe need custom shaders?)
    // alpha doesn't seem to work in any of the cases
    Material mat1 = modelTestShape.materials.get(0);
    int leaf_green = 0x00cc00;
    mat1.set(BasicShapes.getDiffuseAttribute(leaf_green));
    mat1.set(BasicShapes.getSpecularAttribute(0x005500, 0xff));
    // mat1.set(BasicShapes.getEmmisiveAttribute(0x0000ff, 0x00));
    // mat1.set(BasicShapes.getReflectionAttribute(0x0000ff));
    // mat1.set(BasicShapes.getFogAttribute(0xffffff));
    mat1.set(new BlendingAttribute(true, 0.8f));
  }



  private void refreshModels() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(axes));
    }

    ModelInstance modelInstance1 = new ModelInstance(modelTestShape);

    // Creates a couple of leaf-instances in different shades of green
    ModelInstance modelInstance2 = new ModelInstance(modelTestShape);
    Vector3 RIGHT = new Vector3(1,0,0);
    Vector3 UP = new Vector3(0,0,1);
    Quaternion rotx = new Quaternion(RIGHT, -45);
    Matrix4 transform = new Matrix4();
    transform.translate(0, 1, 0);
    transform.scale(0.8f, 1f, 1f);
    transform.rotate(rotx);
    modelInstance2.transform = transform;

    ModelInstance modelInstance3 = new ModelInstance(modelTestShape);
    modelInstance3.materials.get(0).set(BasicShapes.getDiffuseAttribute(0x2eb82e));
    Matrix4 transform2 = new Matrix4();
    Quaternion rotz = new Quaternion(UP, 90);
    transform2.translate(-1, 0, 0);
    transform2.rotate(rotz);
    transform2.rotate(rotx);
    transform2.scale(0.9f, 1f, 1f);
    modelInstance3.transform = transform2;

    instances.add(modelInstance1);
    instances.add(modelInstance2);
    instances.add(modelInstance3);
  }


  @Override
  public void render () {

    if (MyGameState.app_starting && assets.update()) {
      loadModels();
      refreshModels();
      MyGameState.ready = true;
      MyGameState.app_starting = false;
    }

    if (MyGameState.ready && MyGameState.show_axes != this.show_axes) {
      this.show_axes = MyGameState.show_axes;
      refreshModels();
    }

    if (MyGameState.ready) {
      // R: the camera works without this, not clear to me why
      // camController.update();
      // R: this glViewport(..) method doesn't seem to do anything
      // Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      ScreenUtils.clear(1, 1, 1, 1);
      modelBatch.begin(camera);
      modelBatch.render(instances, environment);
      modelBatch.end();
    }

    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }
  }


  @Override
  public void dispose () {
    modelBatch.dispose();
    assets.dispose();
    instances.clear();
    axes.dispose();

    // NOTE - if models are registererd in the asset manager it will dispose of them for us
    // modelTestShape.dispose();


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









