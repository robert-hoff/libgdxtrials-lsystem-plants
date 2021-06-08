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
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import hoffinc.gdxrewrite.CameraInputControllerZUp;
import hoffinc.input.MyGameState;
import hoffinc.input.MyInputProcessor;
import hoffinc.lsystems.TurtleDrawer;
import hoffinc.models.AxesModel;
import hoffinc.models.PlantParts;
import hoffinc.utils.ApplicationProp;

/*
 * 3D Flower generated with L-Systems
 * Example is taken from Figure 1.26 from the book 'The Algorithmic Beauty of Plants'
 *
 * Toogle animation:
 *
 *      right-click > Toggle animate
 *
 *
 *
 *
 */
public class Trial17_Flower extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera camera;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private Model axes;
  private TurtleDrawer turtle;
  private List<Character> treeLSymbols = null;
  private boolean show_axes = true;
  private boolean animate = false;


  @Override
  public void create () {
    setTitle("Lindenmayer Flower");
    MyGameState.show_axes = true;
    MyGameState.miniPopup.addListener("Toogle animate", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        animate = !animate;
      }
    });
    MyGameState.miniPopup.addListener("Print camera transforms", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n", "camera up:", camera.up.x, camera.up.z, camera.up.z);
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n", "camera position:", camera.position.x, camera.position.y, camera.position.z);
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n", "camera dir:", camera.direction.x, camera.direction.y, camera.direction.z);
      }
    });


    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, -0.2f, 0.2f, -0.8f)); // RBG and direction (r,g,b,x,y,z)

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(0.747f,-1.427f,0.600f);
    camera.up.set(0,0,1);
    camera.lookAt(0,0,0.5f);
    camera.near = 0.1f;
    camera.far = 300f;
    camera.update();
    camController = new CameraInputControllerZUp(camera);

    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    Gdx.input.setInputProcessor(inputMultiplexer);
    MyInputProcessor myInputProcessor = new MyInputProcessor();
    inputMultiplexer.addProcessor(myInputProcessor);
    inputMultiplexer.addProcessor(camController);

    axes = AxesModel.buildAxesLineVersion();
    modelBatch = new ModelBatch();
  }


  private void loadModels() {
    int DEPTH = 4;
    String plant = "internode +[ plant + flower ]--//[-- leaf ] internode [++&& leaf ]-[ plant flower ]++ plant flower";
    String internode = "F seg [//&& leaf ][//^^ leaf ]F seg";
    String seg = "seg F seg";
    String leaf = "L";
    String flower = "[& pedicel / wedge //// wedge //// wedge //// wedge //// wedge ]";
    String pedicel = "FF";
    String wedge = "[^U][&&&&W]";
    LSystemProduction prod = new LSystemProduction();
    prod.addRule("plant", plant);
    prod.addRule("internode", internode);
    prod.addRule("seg", seg);
    prod.addRule("leaf", leaf);
    prod.addRule("flower", flower);
    prod.addRule("pedicel", pedicel);
    prod.addRule("wedge", wedge);
    prod.buildSymbols("plant", DEPTH);
    treeLSymbols = prod.getSymbolStringCharList();
  }


  private synchronized void buildFlower() {
    turtle = new TurtleDrawer();
    float STEM_LEN = 0.04f;
    float STEM_DIAM = 0.008f;
    int MESH_RES = 5;
    int dark_green = 0x006600;
    int leaf_green = 0x00cc00;
    turtle.addModel(PlantParts.stemTrunk(STEM_LEN, STEM_DIAM, MESH_RES, dark_green), new Vector3(0,0,STEM_LEN));
    turtle.addModel(PlantParts.leaf1(leaf_green), new Vector3(0,0,0));
    turtle.modelNodes.get(1).scale(0.5f,1,0.6f);
    turtle.addModel(PlantParts.wedge(0xff33cc), new Vector3(0,0,0));
    turtle.modelNodes.get(2).scale(2.0f, 1, 0.5f);
    turtle.addModel(PlantParts.stemTrunk(STEM_LEN, STEM_DIAM, MESH_RES, 0xffccff), new Vector3(0,0,0));
    parseSymbolsWithTurtle(turtle, treeLSymbols, 18);
  }



  private void parseSymbolsWithTurtle(TurtleDrawer turtle, List<Character> symbols, float angle_deg) {
    for (Character c : symbols) {
      boolean found = false;
      if (c=='[') {
        turtle.push();
        found = true;
      }
      if (c==']') {
        turtle.pop();
        found = true;
      }
      if (c=='F') {
        turtle.drawNode(0);
        found = true;
      }
      if (c=='f') {
        turtle.walkNode(0);
        found = true;
      }
      if (c=='L') {
        turtle.drawNode(1);
        found = true;
      }
      if (c=='W') {
        turtle.drawNode(2);
        found = true;
      }
      if (c=='U') {
        turtle.scaleModel(3, 0.3f, 0.3f, 0.9f);
        turtle.drawNode(3);
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
      Vector3 origin = new Vector3(0,0,0);
      camera.rotateAround(origin, Vector3.Z, -1.5f);
      camera.update();
    }

    if (MyGameState.app_starting) {
      loadModels();
      buildFlower();
      refreshModels();
      MyGameState.ready = true;
    }

    if (MyGameState.show_axes != this.show_axes) {
      this.show_axes = MyGameState.show_axes;
      refreshModels();
    }

    if (MyGameState.ready) {
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




  private static class LSystemProduction {

    private final int MAX_SIZE = 30000;
    private List<String> symbols = null;
    private Map<String, List<String>> productionRule = new HashMap<>();

    public LSystemProduction() {}


    // String plant = "internode +[ plant + flower ]--//[-- leaf | internode [++leaf ]-[ plant flower ]++ plant flower";
    public void addRule(String name, String rule) {
      List<String> strings = new ArrayList<>();
      String[] strs = rule.trim().split("\\s+");
      for(String s : strs){
        strings.add(s);
      }
      productionRule.put(name, strings);
    }


    public void buildSymbols(String seed, int n) {
      symbols = new ArrayList<>();
      if (n<1) {
        throw new RuntimeException("error! n less than 1");
      }
      List<String> start = productionRule.get(seed);
      symbols.addAll(start);
      if (start == null) {
        throw new RuntimeException("no production rule for this name: "+seed);
      }
      for (int i = 2; i <= n; i++) {
        for (int j = symbols.size()-1; j >=0; j--) {
          String token = symbols.get(j);
          if (token.matches("[a-z0-9]+")) {
            List<String> prod = productionRule.get(token);
            if (prod == null) {
              throw new RuntimeException("no production rule for this name: "+token);
            }
            symbols.remove(j);
            symbols.addAll(j, prod);
          }
          if (symbols.size()>MAX_SIZE) {
            throw new RuntimeException("production is too large!");
          }
        }
      }
    }


    public List<Character> getSymbolStringCharList() {
      List<Character> symbols_charlist = new ArrayList<>();
      for(String s : symbols){
        if (productionRule.get(s) == null) {
          for (Character c : s.toCharArray()) {
            symbols_charlist.add(c);
          }
        }
      }
      return symbols_charlist;
    }
  }





}









