package hoffinc.gdxtrials1;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import hoffinc.gdxrewrite.CameraInputControllerZUp;
import hoffinc.input.MyGameState;
import hoffinc.input.MyInputProcessor;
import hoffinc.models.AxesModel;
import hoffinc.utils.ApplicationProp;

/*
 *
 * A small walk, simulating the mechanisms of a turtle
 * The turtle is always translated by (0,1,0) on each walk = the direction it
 * is is facing in it's local coordinate system
 *
 *
 *
 */
public class Trial109_MatrixTransforms extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera camera;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private Model axes;
  private Model turtlePath;


  @Override
  public void create () {
    MyGameState.loading = true;
    setTitle("Short turtle walk");

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    // color rgb and direction
    // float r, float g, float b, float dirX, float dirY, float dirZ
    environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, -0.2f, 0.2f, -0.8f));

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(3.5f, -10f, 3f);
    camera.up.set(0, 0, 1);
    camera.lookAt(0,0,0);
    camera.near = 0.1f;
    camera.far = 300f;
    camera.update();
    camController = new CameraInputControllerZUp(camera);
    // camController.autoUpdate = false;
    // camController.scrollTarget = true;       // looks like it changes the scrolltarget, but didn't seem to affect much

    InputProcessor myInputProcessor = new MyInputProcessor();
    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    inputMultiplexer.addProcessor(myInputProcessor);
    inputMultiplexer.addProcessor(camController);
    Gdx.input.setInputProcessor(inputMultiplexer);
    axes = AxesModel.buildAxesLineVersion();

    modelBatch = new ModelBatch();
    turtlePath = TurtlePathModel.buildTurtlePath();
  }


  // a cylinder, pointing along the y-axis
  private static class TurtlePathModel {
    private static final float CYL_DIAM = 0.1f;
    private static final float CYL_LENGTH = 1.0f;
    private static final int MESH_RES = 8;
    public static Model buildTurtlePath() {
      ModelBuilder modelBuilder = new ModelBuilder();
      modelBuilder.begin();
      MeshPartBuilder turtlePathBuilder = modelBuilder.part("turtle_path", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, getTurtleMaterial());
      turtlePathBuilder.setVertexTransform(new Matrix4().translate(0, CYL_LENGTH/2, 0));
      CylinderShapeBuilder.build(turtlePathBuilder, CYL_DIAM, CYL_LENGTH, CYL_DIAM, MESH_RES);
      return modelBuilder.end();
    }
    private static Material getTurtleMaterial() {
      int color = 0x3399ff;  // light-blue
      int color_rgba8888 = (color << 8) + 0xff;
      Material turtleMat = new Material(ColorAttribute.createDiffuse(new Color(color_rgba8888)));
      return turtleMat;
    }
  }


  private void loadModels() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(axes));
    }

    Vector3 FWD = new Vector3(0,1,0);
    Vector3 UP = new Vector3(0,0,1);


    Matrix4 transform = new Matrix4();
    Quaternion rot = new Quaternion(UP, 90);

    ModelInstance path1 = new ModelInstance(turtlePath);
    instances.add(path1);
    transform.translate(FWD);

    ModelInstance path2 = new ModelInstance(turtlePath);
    path2.transform = new Matrix4(transform);
    instances.add(path2);
    transform.translate(FWD);

    transform.rotate(rot);

    ModelInstance path3 = new ModelInstance(turtlePath);
    path3.transform = new Matrix4(transform);
    instances.add(path3);
    transform.translate(FWD);

    transform.rotate(rot);

    ModelInstance path4 = new ModelInstance(turtlePath);
    path4.transform = new Matrix4(transform);
    instances.add(path4);
    transform.translate(FWD);



    MyGameState.loading = false;
  }





  @Override
  public void render () {
    if (MyGameState.loading) {
      loadModels();
    }
    // R: the camera works without this, not clear to me why
    // and enabling this doesn't make the camera work if auto-update is set to false
    // camController.update();

    // R: this glViewport(..) method doesn't seem to do anything
    // Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    ScreenUtils.clear(1, 1, 1, 1);
    modelBatch.begin(camera);
    modelBatch.render(instances, environment);
    modelBatch.end();

    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }
  }


  @Override
  public void dispose () {
    modelBatch.dispose();
    instances.clear();
    axes.dispose();
    turtlePath.dispose();

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




