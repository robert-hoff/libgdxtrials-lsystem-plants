package hoffinc.gdxtrials;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import hoffinc.gdxrewrite.CameraInputControllerZUp;
import hoffinc.input.MyEventListener;
import hoffinc.input.MyGameState;
import hoffinc.input.MyInputProcessor;
import hoffinc.lsystems.TurtleDrawer;
import hoffinc.models.AxesModel;
import hoffinc.models.BasicShapes;
import hoffinc.models.PlantParts;
import hoffinc.utils.ApplicationProp;
import hoffinc.utils.LSystemBasicVersion;

/*
 *
 * 3D Plant using an L-System production
 * Example is taken from Figure 1.25 from the book 'The Algorithmic Beauty of Plants'
 *
 * This plant has some random variation written in the parseSymbolsWithTurtle(..) method
 *
 * A new plant is generated on pressing 'R'
 * The plant may be animated by
 *
 *      right-click > Toggle animate
 *
 *
 *
 */
public class Trial15_3DPlant_RandomVariation extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera camera;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private Model axes;
  private TurtleDrawer turtle;
  private Random rand = new Random();
  private volatile boolean buildNewTree = false;
  private List<Character> treeLSymbols = null;
  private boolean show_axes = true;
  private boolean animate = false;


  @Override
  public void create () {
    setTitle("Plant with Random Variation");

    MyGameState.show_axes = true;
    MyGameState.miniPopup.addListener("Build a new tree", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        buildNewTree = true;
      }
    });
    MyGameState.miniPopup.addListener("Toogle animate", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        animate = !animate;
      }
    });

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, -0.2f, 0.2f, -0.8f)); // RBG and direction (r,g,b,x,y,z)

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(3.5f, -10f, 3f);
    camera.up.set(0,0,1);
    camera.lookAt(0,0,0);
    camera.near = 0.1f;
    camera.far = 300f;
    camera.update();
    camController = new CameraInputControllerZUp(camera);

    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    Gdx.input.setInputProcessor(inputMultiplexer);

    MyInputProcessor myInputProcessor = new MyInputProcessor();
    myInputProcessor.registerKeyDownEvent(Keys.R, new MyEventListener() {
      @Override
      public void triggerEvent() {
        buildNewTree = true;
      }
    });

    inputMultiplexer.addProcessor(myInputProcessor);
    inputMultiplexer.addProcessor(camController);

    axes = AxesModel.buildAxesLineVersion();
    modelBatch = new ModelBatch();
  }



  // the forward direction of the model is along the Z-axis
  private static Model branchModel(float length, float diam, int mesh_res) {
    int meshAttr = Usage.Position | Usage.Normal;
    // int colorRGB = 0x996633; // dark brown
    int colorRGB = 0xcc9966;    // lighter brown
    Material mat = BasicShapes.getMaterial(colorRGB);
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder turtlePathBuilder = modelBuilder.part("branch", GL20.GL_TRIANGLES, meshAttr, mat);
    turtlePathBuilder.setVertexTransform(new Matrix4().translate(0,0,length/2).rotate(1,0,0,90));
    CylinderShapeBuilder.build(turtlePathBuilder, diam, length, diam, mesh_res);
    return modelBuilder.end();
  }


  private void loadModels() {
    Map<Character, String> p = new HashMap<>();
    String s = "!fA";
    p.put('A', "[&FL!A]/////'[&FL!A]///////'[&FL!A]");
    p.put('F', "S/////F");
    p.put('S', "FL");
    p.put('L', "['''^^W]");
    p.put('f', "g");
    p.put('g', "F");
    treeLSymbols = LSystemBasicVersion.lSystemProduction(7, s, p);
  }


  private synchronized void buildTree() {
    float BRANCH_LEN = 0.08f;
    Model branch = branchModel(BRANCH_LEN, 0.05f, 5);
    Model leaf = PlantParts.leaf1();
    turtle = new TurtleDrawer();
    turtle.addModel(branch, new Vector3(0,0,BRANCH_LEN));
    turtle.addModel(leaf, new Vector3(0,0,0));

    float scale_leaf = 1.1f;
    turtle.modelNodes.get(1).scale(scale_leaf, scale_leaf, scale_leaf);
    parseSymbolsWithTurtle(turtle, treeLSymbols, 22.5f);
  }


  private void parseSymbolsWithTurtle(TurtleDrawer turtle, List<Character> symbols, float angle_deg) {
    int level = 0;

    for (Character c : symbols) {
      boolean found = false;

      if (c=='[') {
        turtle.push();
        level++;
        found = true;
      }
      if (c==']') {
        turtle.pop();
        level--;
        found = true;
      }
      if (c=='F') {
        if (level<5) {
          turtle.drawNode(0);
        } else {
          turtle.walkNode(0);
        }
        found = true;
      }
      if (c=='f') {
        turtle.walkNode(0);
        found = true;
      }
      if (c=='W') {
        if (level>2) turtle.drawNode(1);
        // found = true;
      }
      if (c=='!') {
        // float strength = 3.0f / level;
        // float strength = 1.5f / (level+3);
        float strength = 0.3f;
        // float strength = 0;
        float var1 = rand.nextFloat() * strength - strength/2;
        // System.err.println(var1);
        // float var2 = rand.nextFloat() * strength - strength/2;
        float var3 = rand.nextFloat() * strength - strength/2;
        turtle.scaleModel(0, 0.7f+var1, 0.7f+var1, 0.9f+var3);
        found = true;
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
        float strength = 20f;
        // float strength = 0;
        float v = rand.nextFloat() * strength - strength/2;
        turtle.pitchUp(angle_deg+v);
        found = true;
      }
      if (c=='\\') {
        turtle.rollLeft(angle_deg);
        found = true;
      }
      if (c=='/') {
        float strength = 15f;
        // float strength = 0;
        float v = rand.nextFloat() * strength - strength/2;
        turtle.rollRight(angle_deg+v);
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

  }


  private void refreshModels() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(axes));
    }
    instances.addAll(turtle.getComposition());
    MyGameState.app_starting = false;
  }


  @Override
  public void render() {

    if (animate) {
      // camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
      // camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits));
      Vector3 origin = new Vector3(0,0,0);
      Vector3 tmpV1 = new Vector3();
      camera.rotateAround(origin, tmpV1.nor(), 0.8f);
      camera.rotateAround(origin, Vector3.Z, -0.8f);
      camera.update();
    }

    if (MyGameState.app_starting) {
      loadModels();
      buildTree();
      refreshModels();
      MyGameState.ready = true;
    }

    if (MyGameState.show_axes != this.show_axes) {
      this.show_axes = MyGameState.show_axes;
      refreshModels();
    }

    if (buildNewTree == true) {
      buildNewTree = false;
      buildTree();
      refreshModels();
    }

    if (MyGameState.ready) {
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
    }

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


  private static void setTitle(String title) {
    try {
      ((Lwjgl3Graphics) Gdx.graphics).getWindow().setTitle(title);
    } catch (Exception e) {}
  }


}









