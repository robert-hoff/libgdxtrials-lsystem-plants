package hoffinc.gdxtrials;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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
import hoffinc.lsystems.LSystemProduction;
import hoffinc.lsystems.TurtleDrawer;
import hoffinc.models.AxesModel;
import hoffinc.models.PlantParts;
import hoffinc.utils.ApplicationProp;


public class Trial17_Flower extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera cam;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private Model axes;
  private TurtleDrawer turtle;
  private String treeLSymbols = null;
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
    treeLSymbols = prod.getSymbolString();
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


  private void parseSymbolsWithTurtle(TurtleDrawer turtle, String symbols_str, float angle_deg) {
    List<Character> symbols = new ArrayList<>();
    for(char c : symbols_str.toCharArray()){
      symbols.add(c);
    }
    parseSymbolsWithTurtle(turtle, symbols, angle_deg);
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
      cam.rotateAround(origin, Vector3.Z, -1.5f);
      cam.update();
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
      modelBatch.begin(cam);
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








