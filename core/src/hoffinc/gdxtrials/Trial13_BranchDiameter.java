package hoffinc.gdxtrials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
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
import hoffinc.lsystems.LSystem;
import hoffinc.lsystems.TurtleDrawer;
import hoffinc.models.AxesModel;
import hoffinc.models.BasicShapes;
import hoffinc.models.LeafModels;
import hoffinc.utils.ApplicationProp;
import hoffinc.utils.InspectData;


/*
 *
 *
 *
 */
public class Trial13_BranchDiameter extends ApplicationAdapter {


  public Environment environment;
  public PerspectiveCamera cam;
  public CameraInputControllerZUp camController;
  public ModelBatch modelBatch;
  public Array<ModelInstance> instances = new Array<ModelInstance>();
  public AssetManager assets;

  private String coneArrowFileName = "conearrow.obj";
  private Model axes;


  @Override
  public void create () {
    MyGameState.loading = true;
    setTitle("Adjusting branch sizes");



    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    // color rgb and direction (float r, float g, float b, float dirX, float dirY, float dirZ)
    environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, -0.2f, 0.2f, -0.8f));

    cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.position.set(3.5f, -10f, 3f);
    cam.up.set(0,0,1);
    cam.lookAt(0,0,0);
    cam.near = 0.1f;
    cam.far = 300f;
    cam.update();
    camController = new CameraInputControllerZUp(cam);


    InputProcessor myInputProcessor = new MyInputProcessor();
    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    inputMultiplexer.addProcessor(myInputProcessor);
    inputMultiplexer.addProcessor(camController);
    Gdx.input.setInputProcessor(inputMultiplexer);

    MyGameState.show_axes = true;
    axes = AxesModel.buildAxesLineVersion();


    modelBatch = new ModelBatch();
    assets = new AssetManager();
    assets.load(coneArrowFileName, Model.class);

  }



  private void doneLoading() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(axes));
    }

    float BRANCH_LEN = 0.4f;
    Model branch = branchModel(BRANCH_LEN, 0.05f, 5);
    Model leaf = LeafModels.leaf1();


    TurtleDrawer turtle = new TurtleDrawer();
    turtle.addModel(branch, new Vector3(0,0,BRANCH_LEN));
    turtle.addModel(leaf, new Vector3(0,0,0));


    turtle.drawNode(0);
    turtle.drawNode(0);
    turtle.push();
    turtle.pitchDown(35);
    turtle.scaleModel(0, 0.5f, 0.5f, 0.8f);
    turtle.drawNode(0);
    turtle.pitchDown(15);
    turtle.drawNode(0);
    turtle.pitchDown(35);
    turtle.turnLeft(60);
    turtle.drawNode(1);
    turtle.turnRight(60);
    turtle.drawNode(1);
    turtle.turnRight(60);
    turtle.drawNode(1);
    turtle.pop();
    // turtle.rollLeft(90);
    turtle.drawNode(0);
    turtle.drawNode(0);
    turtle.scaleModel(0, 0.5f, 0.5f, 0.5f);
    turtle.drawNode(0);
    turtle.drawNode(0);

    instances.addAll(turtle.getComposition());
    MyGameState.loading = false;
  }



  // private static final float CYL_DIAM = 0.05f;
  // private static final float CYL_LENGTH = 0.4f;
  // private static final int MESH_RES = 5;

  // points along the z-axis
  private static Model branchModel(float length, float diam, int mesh_res) {
    int attr = Usage.Position | Usage.Normal;
    // Material mat = BasicShapes.getMaterial(0x996633); // dark brown
    Material mat = BasicShapes.getMaterial(0xcc9966); // lighter brown

    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder turtlePathBuilder = modelBuilder.part("branch", GL20.GL_TRIANGLES, attr, mat);
    turtlePathBuilder.setVertexTransform(new Matrix4().translate(0,0,length/2).rotate(1,0,0,90));
    CylinderShapeBuilder.build(turtlePathBuilder, diam, length, diam, mesh_res);
    return modelBuilder.end();
  }



  // scale a leaf a lot
  void transformExample() {
    Model leafModel = LeafModels.leaf1();
    ModelInstance leaf1 = new ModelInstance(leafModel);
    Vector3 RIGHT = new Vector3(1,0,0);
    Quaternion rot = new Quaternion(RIGHT, -45);
    leaf1.transform.translate(new Vector3(0.1f,0,0));
    leaf1.transform.rotate(rot);
    leaf1.transform.scale(10.5f, 10.5f, 10.5f);
    instances.add(leaf1);
  }



  private TurtleDrawer buildTurtle(TurtleDrawer turtle, List<Character> symbols, float angle_deg) {

    for (Character c : symbols) {
      boolean found = false;

      if (c=='[') {
        turtle.push();
      }
      if (c==']') {
        turtle.pop();
      }
      if (c=='F') {
        turtle.drawNode(0);
      }
      if (c=='f') {
        turtle.walkNode(0);
      }
      if (c=='+') {
        turtle.turnLeft(angle_deg);
        found = true;
      }
      if (c=='-') {
        turtle.turnRight(angle_deg);
        found = true;
      }
      if (c=='&') {
        turtle.pitchDown(angle_deg);
        found = true;
      }
      if (c=='^') {
        turtle.pitchUp(angle_deg);
        found = true;
      }
      if (c=='\\') {
        turtle.rollLeft(angle_deg);
        found = true;
      }
      if (c=='/') {
        turtle.rollRight(angle_deg);
        found = true;
      }
      if (c=='|') {
        turtle.turnAround();
        found = true;
      }

      if (!found) {
        // System.err.println(c);
      }
    }
    return turtle;
  }






  static void setTitle(String title) {
    try {
      ((Lwjgl3Graphics) Gdx.graphics).getWindow().setTitle(title);
    } catch (Exception e) {}
  }



  @Override
  public void render() {
    if (MyGameState.loading && assets.update()) {
      doneLoading();
    }
    // R: the camera works without this, not clear to me why
    // and enabling this doesn't make the camera work if auto-update is set to false
    // camController.update();

    // R: this glViewport(..) method doesn't seem to do anything
    // Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    ScreenUtils.clear(1, 1, 1, 1);
    modelBatch.begin(cam);
    modelBatch.render(instances, environment);
    modelBatch.end();

    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }
  }


  @Override
  public void dispose () {
    modelBatch.dispose();
    assets.dispose();
    instances.clear();

    if (MyGameState.jwin != null) {
      MyGameState.jwin.dispose();
    }


    // save window x,y and window width,height
    // (the initial size of the window is set from the Desktop-launcher)
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



}

















