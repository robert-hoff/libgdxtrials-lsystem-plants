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
import hoffinc.models.BasicShapes;
import hoffinc.utils.ApplicationProp;


/*
 *
 *
 *
 */
public class Trial12_LeafShape extends ApplicationAdapter {


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
    setTitle("Leaf shape");


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


    // -f+f+f-|-f+f+f
    // -F+F+F-|-F+F+F


    String symbols_str = "-F+F+F-|-F+F+F";
    List<Character> symbols = lSystemConvertString(symbols_str);
    TurtleDrawer turtle = buildTurtle(symbols, 22.5f);
    instances.addAll(turtle.getPaths());






    MyGameState.loading = false;
  }







  private TurtleDrawer buildTurtle(List<Character> symbols, float angle_deg) {
    // TurtleDrawer turtle = new TurtleDrawer();

    Model line = BasicShapes.line(0, 0, 0, 0, 0, 1, 0x009933);
    TurtleDrawer turtle = new TurtleDrawer(line, 1);

    for (Character c : symbols) {
      boolean found = false;

      if (c=='[') {
        turtle.push();
      }
      if (c==']') {
        turtle.pop();
      }
      if (c=='F') {
        turtle.walkDraw();
        found = true;
      }
      if (c=='f') {
        turtle.walk();
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
        turtle.pitchUp(angle_deg);            // turtle.pitchDown(90);
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
        turtle.turnAround();                  // turtle.rollRight(90);
        found = true;
      }

      if (!found) {
        // System.err.println(c);
      }
      if (found) {
        // System.err.println(c);
      }
    }

    return turtle;
  }




  private static class TurtleDrawer {
    private float PATH_LEN;
    private Model turtlePath;
    private Array<ModelInstance> paths = new Array<ModelInstance>();

    Vector3 FWD = new Vector3(0,0,1);
    Vector3 UP = new Vector3(0,-1,0);
    Vector3 RIGHT = new Vector3(1,0,0);
    Matrix4 transform = new Matrix4();
    Stack<Matrix4> stack = new Stack<>();


    public TurtleDrawer(Model model, float path_len) {
      turtlePath = model;
      this.PATH_LEN = path_len;
    }
    public TurtleDrawer() {
      turtlePath = TurtlePathModel.buildTurtlePath();
      this.PATH_LEN = 0.1f;

    }

    public void push() {
      stack.push(new Matrix4(transform));
    }

    public void pop() {
      transform = stack.pop();
    }


    // (doesn't draw anything)
    public void walk() {
      walk(PATH_LEN);
    }
    public void walk(float distance) {
      transform.translate(new Vector3().mulAdd(FWD, distance));
    }

    // angle in degrees
    public void turnLeft(float angle) {
      Quaternion rot = new Quaternion(UP, angle);
      transform.rotate(rot);
    }

    public void turnRight(float angle) {
      Quaternion rot = new Quaternion(UP, -angle);
      transform.rotate(rot);
    }

    public void turnAround() {
      Quaternion rot = new Quaternion(UP, 180);
      transform.rotate(rot);
    }

    public void pitchDown(float angle) {
      Quaternion rot = new Quaternion(RIGHT, -angle);
      transform.rotate(rot);
    }

    public void pitchUp(float angle) {
      Quaternion rot = new Quaternion(RIGHT, angle);
      transform.rotate(rot);
    }

    public void rollLeft(float angle) {
      Quaternion rot = new Quaternion(FWD, -angle);
      transform.rotate(rot);
    }

    public void rollRight(float angle) {
      Quaternion rot = new Quaternion(FWD, angle);
      transform.rotate(rot);
    }





    public void walkDraw() {
      ModelInstance next_path = new ModelInstance(turtlePath);
      next_path.transform = new Matrix4(transform);
      paths.add(next_path);
      walk(PATH_LEN);
    }






    public Array<ModelInstance> getPaths() {
      return paths;
    }




    public static void showVector3(Vector3 vec) {
      System.err.printf("%9.4f %9.4f %9.4f \n", vec.x, vec.y, vec.z);
    }

    public static String strVector3(Vector3 vec) {
      return String.format("%9.4f %9.4f %9.4f", vec.x, vec.y, vec.z);
    }

  }





  // Path adjusted slightly to fit this example
  // a cylinder, pointing upwards (z-axis)
  private static class TurtlePathModel {
    private static final float CYL_DIAM = 0.01f;
    private static final float CYL_LENGTH = 0.1f;
    private static final int MESH_RES = 5;
    public static Model buildTurtlePath() {
      ModelBuilder modelBuilder = new ModelBuilder();
      modelBuilder.begin();
      MeshPartBuilder turtlePathBuilder = modelBuilder.part("turtle_path", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,getTurtleMaterial());
      turtlePathBuilder.setVertexTransform(new Matrix4().translate(0,0,CYL_LENGTH/2).rotate(1,0,0,90));
      CylinderShapeBuilder.build(turtlePathBuilder, CYL_DIAM, CYL_LENGTH, CYL_DIAM, MESH_RES);
      return modelBuilder.end();
    }
    private static Material getTurtleMaterial() {
      int color = 0x009933;  // green
      int color_rgba8888 = (color << 8) + 0xff;
      Material turtleMat = new Material(ColorAttribute.createDiffuse(new Color(color_rgba8888)));
      return turtleMat;
    }
  }




  static List<Character> lSystemProduction(int n, String s, Map<Character,String> p) {
    List<Character> symbols = new ArrayList<>();
    for(char c : s.toCharArray()){
      symbols.add(c);
    }
    for (int i = 1; i <= n; i++) {
      List<Character> nextSymbols = new ArrayList<>();
      for(char c : symbols){
        String p_rule = p.get(c);
        if (p_rule != null) {
          for(char c_prod : p_rule.toCharArray()){
            nextSymbols.add(c_prod);
          }
        } else {
          nextSymbols.add(c);
        }
      }
      symbols = nextSymbols;
    }
    return symbols;
  }


  static List<Character> lSystemConvertString(String s) {
    List<Character> symbols = new ArrayList<>();
    for(char c : s.toCharArray()){
      symbols.add(c);
    }
    return symbols;
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

















