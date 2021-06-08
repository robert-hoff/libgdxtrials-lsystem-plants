package hoffinc.gdxtrials;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
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
 * Using a turtle to get the vertex positions of a leaf outline
 *
 */
public class Trial12_LeafShape extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera camera;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private Model axes;


  @Override
  public void create () {
    setTitle("Leaf shape");
    MyGameState.loading = true;

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, -0.2f, 0.2f, -0.8f)); // RBG and direction (r,g,b,x,y,z)

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(0.212f,-0.605f,0.182f);
    camera.up.set(0,0,1);
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

    MyGameState.show_axes = true;
    axes = AxesModel.buildAxesLineVersion();
    modelBatch = new ModelBatch();
  }



  private void loadModels() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(axes));
    }

    // Figure 1.25 in the Algorithmic Beauty of Plants
    // -f+f+f-|-f+f+f
    // -F+F+F-|-F+F+F
    String symbols_str = "-F+F+F-|-F+F+F";
    List<Character> symbols = lSystemConvertString(symbols_str);
    TurtleDrawer turtle = buildTurtle(symbols, 22.5f);
    instances.addAll(turtle.getPaths());
    turtle.showPath();


    int attr = Usage.Position | Usage.Normal;
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    // Vector3 zero = new Vector3();
    Vector3 normal_f = new Vector3(0,-1,0);
    Vector3 normal_b = new Vector3(0,1,0);
    Vector3 v0 = new Vector3(0.0000f,0.0000f,0.0000f);
    Vector3 v1 = new Vector3(0.0383f,0.0000f,0.0924f);
    Vector3 v2 = new Vector3(0.0383f,0.0000f,0.1924f);
    Vector3 v3 = new Vector3(0.0000f,0.0000f,0.2848f);
    Vector3 v4 = new Vector3(-0.0383f,0.0000f,0.1924f);
    Vector3 v5 = new Vector3(-0.0383f,0.0000f,0.0924f);



    Material mat = BasicShapes.getMaterial(0x009933);
    MeshPartBuilder meshBuilder = modelBuilder.part("nameid", GL20.GL_TRIANGLES, attr, mat);

    // vertex(Vector3 pos, Vector3 nor, Color col, Vector2 uv)
    meshBuilder.vertex(v0, normal_f, null, null);
    meshBuilder.vertex(v1, normal_f, null, null);
    meshBuilder.vertex(v2, normal_f, null, null);
    meshBuilder.vertex(v3, normal_f, null, null);
    meshBuilder.vertex(v4, normal_f, null, null);
    meshBuilder.vertex(v5, normal_f, null, null);
    meshBuilder.vertex(v0, normal_b, null, null);
    meshBuilder.vertex(v1, normal_b, null, null);
    meshBuilder.vertex(v2, normal_b, null, null);
    meshBuilder.vertex(v3, normal_b, null, null);
    meshBuilder.vertex(v4, normal_b, null, null);
    meshBuilder.vertex(v5, normal_b, null, null);

    // CCW
    meshBuilder.triangle((short) 0, (short) 1, (short) 5);
    meshBuilder.triangle((short) 1, (short) 2, (short) 5);
    meshBuilder.triangle((short) 2, (short) 4, (short) 5);
    meshBuilder.triangle((short) 2, (short) 3, (short) 4);
    // CW
    //    meshBuilder.triangle((short) 6, (short) 7, (short) 11);
    //    meshBuilder.triangle((short) 7, (short) 8, (short) 11);
    //    meshBuilder.triangle((short) 8, (short) 10, (short) 11);
    //    meshBuilder.triangle((short) 8, (short) 9, (short) 10);
    // back
    meshBuilder.triangle((short) 11, (short) 7, (short) 6);
    meshBuilder.triangle((short) 11, (short) 8, (short) 7);
    meshBuilder.triangle((short) 11, (short) 10, (short) 8);
    meshBuilder.triangle((short) 10, (short) 9, (short) 8);


    Model leafModel = modelBuilder.end();
    ModelInstance leaf1 = new ModelInstance(leafModel);


    Vector3 RIGHT = new Vector3(1,0,0);
    Quaternion rot = new Quaternion(RIGHT, -45);
    Matrix4 transform = new Matrix4();
    transform.rotate(rot);
    leaf1.transform = transform;
    instances.add(leaf1);

    // each vertex size is 24
    // System.err.println(leafModel.meshes.get(0).getVertexSize());


    MyGameState.loading = false;
  }

  private static List<Character> lSystemConvertString(String s) {
    List<Character> symbols = new ArrayList<>();
    for(char c : s.toCharArray()){
      symbols.add(c);
    }
    return symbols;
  }


  private static TurtleDrawer buildTurtle(List<Character> symbols, float angle_deg) {
    Model line = BasicShapes.line(0, 0, 0, 0, 0, 0.1f, 0x009933);
    TurtleDrawer turtle = new TurtleDrawer(line, 0.1f);

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
    private Vector3 FWD = new Vector3(0,0,1);
    private Vector3 UP = new Vector3(0,-1,0);
    private Vector3 RIGHT = new Vector3(1,0,0);
    private Matrix4 transform = new Matrix4();
    private Stack<Matrix4> stack = new Stack<>();
    private List<Vector3> path = new ArrayList<>();

    public TurtleDrawer(Model model, float path_len) {
      turtlePath = model;
      this.PATH_LEN = path_len;
      path.add(new Vector3());
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
      path.add(new Vector3().mul(transform));
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

    public void showPath() {
      for (int i = 0; i < path.size(); i++) {
        System.err.printf("%2d: %s\n", i, strVector3(path.get(i)));
      }
    }

    public static String strVector3(Vector3 vec) {
      return String.format("%9.4f %9.4f %9.4f", vec.x, vec.y, vec.z);
    }

  }



  @Override
  public void render() {
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


  static void setTitle(String title) {
    try {
      ((Lwjgl3Graphics) Gdx.graphics).getWindow().setTitle(title);
    } catch (Exception e) {}
  }


}









