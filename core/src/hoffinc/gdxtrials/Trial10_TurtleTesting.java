package hoffinc.gdxtrials;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import hoffinc.utils.ApplicationProp;

/*
 * Drawing 3D shapes with L-systems. Using a 3D turtle to achieve this, and the lSystemProduction(..) method
 *
 */
public class Trial10_TurtleTesting extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera camera;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private AssetManager assets;

  private String coneArrowFileName = "conearrow.obj";
  private Model axes;


  @Override
  public void create () {
    setTitle("L-Systems 3D Hilbert Curve");
    MyGameState.loading = true;

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    // color rgb and direction
    // float r, float g, float b, float dirX, float dirY, float dirZ
    environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, -0.2f, 0.2f, -0.8f));

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(10.767f,-7.179f,6.031f);
    camera.up.set(0,0,1);
    camera.lookAt(0,0,0);
    camera.near = 0.1f;
    camera.far = 300f;
    camera.update();


    camController = new CameraInputControllerZUp(camera);
    // camController.autoUpdate = false;
    // camController.scrollTarget = true;       // looks like it changes the scrolltarget, but didn't seem to have much affect

    InputProcessor myInputProcessor = new MyInputProcessor();
    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    inputMultiplexer.addProcessor(myInputProcessor);
    inputMultiplexer.addProcessor(camController);
    Gdx.input.setInputProcessor(inputMultiplexer);

    axes = AxesModel.buildAxesLineVersion();


    modelBatch = new ModelBatch();
    assets = new AssetManager();
    assets.load(coneArrowFileName, Model.class);

  }



  private void loadModels() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(axes));
    }

    // -- small test
    // TurtleTrial9 my_turtle = TurtleTest1();
    // TurtleTrial9 my_turtle = TurtleTest2();
    // instances.addAll(my_turtle.getPaths());


    // -- Dragon curve (2D)
    //    Map<Character, String> p = new HashMap<>();
    //    String s = "Fl";
    //    p.put('l', "l+rF+");
    //    p.put('r', "-Fl-r");
    //    List<Character> symbols = lSystemProduction(10, s, p);
    //    instances.addAll(buildTurtle(symbols).getPaths());


    // 3D Hilbert curve
    Map<Character, String> p = new HashMap<>();
    String s = "A";
    p.put('A', "B-F+CFC+F-D&F^D-F+&&CFC+F+B//");
    p.put('B', "A&F^CFB^F^D^^-F-D^|F^B|FC^F^A//");
    p.put('C', "|D^|F^B-F+C^F^A&&FA&F^C+F+B^F^D//");
    p.put('D', "|CFB-F+B|FA&F^A&&FB-F+B|FC//");
    List<Character> symbols = lSystemProduction(3, s, p);
    instances.addAll(buildTurtle(symbols).getPaths());

    MyGameState.loading = false;
  }





  private TurtleTrial9 buildTurtle(List<Character> symbols) {
    TurtleTrial9 turtle = new TurtleTrial9();

    // -- swap in the cone model
    //    Model model = assets.get(coneArrowFileName, Model.class);
    //    Turtle turtle = new Turtle(model, 2.5f);

    for (Character c : symbols) {
      boolean found = false;

      if (c=='F') {
        turtle.walkDraw();
        found = true;
      }
      if (c=='f') {
        turtle.walkWithoutDraw();
      }
      if (c=='+') {
        turtle.turnLeft(90);
        found = true;
      }
      if (c=='-') {
        turtle.turnRight(90);
        found = true;
      }
      if (c=='&') {
        turtle.pitchDown(90);
        found = true;
      }
      if (c=='^') {
        turtle.pitchUp(90);             // turtle.pitchDown(90);        (apply this for an interesting Hilbert result)
        found = true;
      }
      if (c=='\\') {
        turtle.rollLeft(90);
        found = true;
      }
      if (c=='/') {
        turtle.rollRight(90);
        found = true;
      }
      if (c=='|') {
        turtle.turnAround();            // turtle.rollRight(90);        (apply this for an interesting Hilbert result)
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


  TurtleTrial9 TurtleTest2() {
    Model model = assets.get(coneArrowFileName, Model.class);
    TurtleTrial9 my_turtle = new TurtleTrial9(model, 3f);
    my_turtle.walkDraw();
    my_turtle.turnLeft(90);
    my_turtle.walkDraw();
    return my_turtle;
  }



  TurtleTrial9 TurtleTest1() {
    TurtleTrial9 my_turtle = new TurtleTrial9();
    my_turtle.walkDraw();
    my_turtle.rollLeft(90);
    my_turtle.turnLeft(90);
    my_turtle.walkDraw();
    my_turtle.pitchUp(90);
    my_turtle.walkDraw();
    my_turtle.turnLeft(90);
    my_turtle.walkDraw();
    my_turtle.turnAround();
    my_turtle.pitchDown(90);
    my_turtle.walkDraw();
    my_turtle.walkDraw();
    return my_turtle;
  }



  private static class TurtleTrial9 {
    private float PATH_LEN;
    private Model turtlePath;
    private Array<ModelInstance> paths = new Array<ModelInstance>();

    Vector3 FWD = new Vector3(0,0,1);
    Vector3 UP = new Vector3(0,-1,0);
    Vector3 RIGHT = new Vector3(1,0,0);
    Matrix4 transform = new Matrix4();


    // Not sure I need these!
    // Vector3 pos = new Vector3(0,0,0);
    // Vector3 dir = new Vector3(0,1,0);
    // Vector3 right = new Vector3(1,0,0);


    public TurtleTrial9(Model model, float path_len) {
      turtlePath = model;
      this.PATH_LEN = path_len;
    }
    public TurtleTrial9() {
      turtlePath = TurtlePathModel.buildTurtlePath();
      this.PATH_LEN = 1f;
    }



    public void walk(float distance) {


      //Vector3 delta = new Vector3();
      // delta.mulAdd(new Vector3(dir.x,dir.y,dir.z), distance);
      // pos.mulAdd(new Vector3(dir.x,dir.y,dir.z), distance);
      // pos.add(delta);

      //      System.err.println("pos: "+strVector3(pos));
      //      System.err.println("delta: "+strVector3(delta));
      //      System.err.println("walk: " + pos.toString());
      //      System.err.println();

      // transform.translate(FWD);


      transform.translate(new Vector3().mulAdd(FWD, PATH_LEN));

    }


    // angle in degrees
    public void turnLeft(float angle) {
      // Vector3 up = upVector();
      // Quaternion rot = new Quaternion(up, angle);
      Quaternion rot = new Quaternion(UP, angle);
      transform.rotate(rot);
      // dir.rotate(up, angle);
      // right.rotate(up, angle);
    }


    public void turnRight(float angle) {
      // Vector3 up = upVector();
      // Quaternion rot = new Quaternion(up, -angle);
      Quaternion rot = new Quaternion(UP, -angle);
      transform.rotate(rot);
      // dir.rotate(up, -angle);
      // right.rotate(up, -angle);
    }

    public void turnAround() {
      Quaternion rot = new Quaternion(UP, 180);
      transform.rotate(rot);
    }


    public void pitchDown(float angle) {
      // dir.rotate(right, -angle);
      // Quaternion rot = new Quaternion(right, -angle);
      Quaternion rot = new Quaternion(RIGHT, -angle);
      transform.rotate(rot);
    }

    public void pitchUp(float angle) {
      // dir.rotate(right, angle);
      // Quaternion rot = new Quaternion(right, angle);
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

      // R: can't get this thing to work at all!!!
      // the model doesn't line up with the supplied 'forward' and 'up' vector
      // not sure what this is about..
      //      Vector3 pos_Vec = new Vector3(0,0,0);
      //      Vector3 for_Vec = new Vector3(0.4f,-0.4f,0.4f);
      //      Vector3 up_Vec = new Vector3(0,1,0);
      //      next_path.transform.setToWorld(pos_Vec, for_Vec, up_Vec);
      paths.add(next_path);
      walk(PATH_LEN);
    }
    public Array<ModelInstance> getPaths() {
      return paths;
    }

    public void walkWithoutDraw() {
      walk(PATH_LEN);
    }

    // cross product right x dir
    // (right is copied)
    // private Vector3 upVector() {
    // return right.cpy().crs(dir);
    // }

  }


  private static List<Character> lSystemProduction(int n, String s, Map<Character,String> p) {
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



  // a cylinder, pointing upwards (z-axis)
  private static class TurtlePathModel {
    private static final float CYL_DIAM = 0.1f;
    private static final float CYL_LENGTH = 1.0f;
    private static final int MESH_RES = 10;
    public static Model buildTurtlePath() {
      ModelBuilder modelBuilder = new ModelBuilder();
      modelBuilder.begin();
      MeshPartBuilder turtlePathBuilder = modelBuilder.part("turtle_path", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,getTurtleMaterial());
      turtlePathBuilder.setVertexTransform(new Matrix4().translate(0,0,CYL_LENGTH/2).rotate(1,0,0,90));
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



  @Override
  public void render () {
    if (MyGameState.loading && assets.update()) {
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
    assets.dispose();
    instances.clear();

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









