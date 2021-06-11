package hoffinc.gdxtrials2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
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
import hoffinc.models.BasicShapes;
import hoffinc.models.PlantParts;
import hoffinc.utils.ApplicationProp;
import hoffinc.utils.FloatMaths;
import static hoffinc.utils.FloatMaths.randomNum;
import static hoffinc.utils.FloatMaths.sin;

/*
 * Idea for palm-tree, but trunk drawn as concentric circles (need to implement some mesh building)
 *
 * This model has a few weaknesses and minor errors
 *  - diameter of trunk a bit large near the top
 *  - all fronds drop at the same rate and a bit too much, specially fronds near the top should point more up
 *  - always the same number of fronds
 *  - vertex count way too high on leaves, prob should find a way to use a texture for the leaves
 *  - the distribution of fronds is random so sometimes they are clumped up
 *
 * Also, for my purposes there needs to be some better distinction between the geometric model
 * and the procedure for drawing it.
 *
 *
 *
 */
public class Trial212_PalmTreeAttempt extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera camera;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private AssetManager assets;
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private Map<String, Model> my_models = new HashMap<>();
  private TurtleDrawer turtle;


  @Override
  public void create () {
    setTitle("Palm Tree (wireframe trunk)");
    MyGameState.miniPopup.addListener("Toogle animate (A)", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        MyGameState.animate = !MyGameState.animate;
      }
    });
    MyGameState.miniPopup.addListener("Print camera transforms", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n", "camera up:", camera.up.x, camera.up.z, camera.up.z);
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n", "camera position:", camera.position.x, camera.position.y, camera.position.z);
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n\n", "camera dir:", camera.direction.x, camera.direction.y, camera.direction.z);
      }
    });

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
    float lightIntensity = 0.6f;
    Color lightColor = new Color(lightIntensity, lightIntensity, lightIntensity, 0xff);
    environment.add(new DirectionalLight().set(lightColor, new Vector3(-0.8f,0.3f,-0.5f).nor()));
    // environment.add(new PointLight().set(1f, 1f, 1f, new Vector3(5f,-1.5f,22f), 4f)); // r,g,b,pos,intensity

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(3.825f,-15.398f,11.786f);
    camera.up.set(0f, 0f, 1f);
    camera.lookAt(0f, 0f, 0f);
    camera.near = 0.1f;
    camera.far = 300f;
    camera.update();
    camController = new CameraInputControllerZUp(camera);

    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    Gdx.input.setInputProcessor(inputMultiplexer);
    MyInputProcessor myInputProcessor = new MyInputProcessor();
    inputMultiplexer.addProcessor(myInputProcessor);
    inputMultiplexer.addProcessor(camController);

    modelBatch = new ModelBatch();
    assets = new AssetManager();
    // register models here from the assets folder
    // ...

    MyGameState.show_axes = true;
    MyGameState.animate_speed = -0.7f;
  }


  private void loadModels() {
    Model axes = AxesModel.buildAxesLineVersion();
    my_models.put("axes", axes);

    Model triangleLeafModel = PlantParts.triangleLeaf(0x009900);
    // NOTE the one sided model doesn't work well with CullFace, it gives
    // the same color on the front and back of the model (i.e. it uses the same normal)
    //    Model leafModel = PlantParts.triangleLeafOneSide(0x00b300);
    triangleLeafModel.meshes.get(0).scale(0.13f, 1f, 1f);  // downscale on x to make leaf a sharp triangle
    triangleLeafModel.materials.get(0).set(new BlendingAttribute(true, 0.8f));
    //    leafModel.materials.get(0).set(new IntAttribute(IntAttribute.CullFace));

    // lightens up the model a little
    triangleLeafModel.materials.get(0).set(BasicShapes.getEmmisiveAttribute(0x222222, 0xff));
    my_models.put("leaf-model", triangleLeafModel);

    // Fix the seed
    FloatMaths.rand = new Random(0);

    // LSystem lSystem = populateLSystem();
    LSystem lSystem = createLSystem();
    // System.err.println(lSystem);

    turtle = new TurtleDrawer();
    int CIRCLE_VERTEX_COUNT = 10;
    Model circleOutline = BasicShapes.buildCircleOutline1(CIRCLE_VERTEX_COUNT, 0.3f, 0); // vertex_count, radius, color_rgb
    my_models.put("circle-outline", circleOutline);
    turtle.addModel(circleOutline);
    turtle.addModel(triangleLeafModel);
    turtle.scaleModel(1, 0.9f, 0.9f, 0.9f);
    parseSymbolsWithTurtle(turtle, lSystem.symbols);

  }

  /*
   * Symbols in the form
   *
   *    !{w: 0.200}
   *    /{a: 262.955}
   *    !{w: 0.850}
   *    ^{a: -0.204}
   *    F{l: 0.150}
   *    !{w: 0.736}
   *    ^{a: -0.392}
   *    F{l: 0.150}
   *    !{w: 0.998}
   *    ^{a: -0.237}
   *    F{l: 0.150}
   *    !{w: 0.770}
   *    ^{a: -0.591}
   *    ...
   *
   */
  private void parseSymbolsWithTurtle(TurtleDrawer turtle, List<LSymbol> symbols) {
    for (LSymbol ls : symbols) {
      if (ls.name == '[') {
        turtle.push();
      }
      if (ls.name == ']') {
        turtle.pop();
      }
      if (ls.name == 'F') {
        float length = ls.param_values[0];
        turtle.walk(0, 0, length);
        // R: it's a bit unatural to put the draw here but I think it was the intention of this model
        // turtle.drawNode(0);
      }
      if (ls.name == '!') {
        float tree_diameter = ls.param_values[0];
        turtle.modelNodes.get(0).setScale(tree_diameter, tree_diameter, 1);
        // R: moved the draw to here
        turtle.drawNode(0);
      }
      if (ls.name == '^') {
        float angle_deg = ls.param_values[0];
        turtle.pitchUp(angle_deg);
      }
      if (ls.name == '&') {
        turtle.pitchDown(ls.param_values[0]);
      }
      if (ls.name == '/') {
        turtle.rollRight(ls.param_values[0]);
      }
      if (ls.name == '\\') {
        turtle.rollLeft(ls.param_values[0]);
      }
      if (ls.name == 'L') {
        float r_ang = ls.param_values[0];
        float d_ang = ls.param_values[1];
        turtle.push();
        turtle.turnRight(r_ang);
        turtle.pitchDown(d_ang);
        turtle.drawNode(1);
        turtle.pop();
      }
    }
  }


  LSystem populateLSystem() {
    LSystem lSystem = new LSystem();
    lSystem.add(new LSymbol('!', "w", 0.20000f));
    lSystem.add(new LSymbol('/', "a", 262.95490f));
    lSystem.add(new LSymbol('!', "w", 0.85000f));
    lSystem.add(new LSymbol('^', "a", -0.20437f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('!', "w", 0.73648f));
    lSystem.add(new LSymbol('^', "a", -0.39220f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('!', "w", 0.99840f));
    lSystem.add(new LSymbol('^', "a", -0.23709f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('!', "w", 0.76951f));
    lSystem.add(new LSymbol('^', "a", -0.59080f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('!', "w", 0.80681f));
    lSystem.add(new LSymbol('^', "a", 0.02216f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('!', "w", 0.98694f));
    lSystem.add(new LSymbol('^', "a", -0.40588f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('!', "w", 0.71416f));
    lSystem.add(new LSymbol('^', "a", -0.28183f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('!', "w", 0.89064f));
    lSystem.add(new LSymbol('^', "a", 0.16881f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('!', "w", 0.93271f));
    lSystem.add(new LSymbol('^', "a", -0.29324f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('!', "w", 0.70123f));
    lSystem.add(new LSymbol('^', "a", -0.27939f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('!', "w", 0.96177f));
    lSystem.add(new LSymbol('^', "a", 0.00254f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('!', "w", 0.85266f));
    lSystem.add(new LSymbol('^', "a", 0.20628f));
    lSystem.add(new LSymbol('F', "l", 0.15000f));
    lSystem.add(new LSymbol('L', new String[]{"r_ang", "d_ang"}, new float[]{51.84000f, 2.20700f}));
    return lSystem;
  }


  LSystem createLSystem() {
    LSystem lSystem = new LSystem();
    lSystem.add(new LSymbol('!', "w", 0.2f));
    lSystem.add(new LSymbol('/', "a", randomNum()*360));
    lSystem.add(new LSymbol('Q', "t", 0.0f));
    lSystem.addRule('Q', new QProd());
    lSystem.addRule('A', new AProd());
    lSystem.iterate(86);
    return lSystem;
  }


  private static class LSystem {
    final int MAX_SYMBOL_COUNT = 10000;
    List<LSymbol> symbols = new ArrayList<>();
    Map<Character, LSystemProduction> rules = new HashMap<>();

    public LSystem(){}
    public void add(LSymbol symbol) {
      symbols.add(symbol);
    }
    public void addRule(Character c, LSystemProduction rule) {
      rules.put(c, rule);
    }

    public void iterate(int n) {
      for (int i = 0; i < n; i++) {
        List<LSymbol> next_symbols = new ArrayList<>();
        for (LSymbol lSymbol : symbols) {
          LSystemProduction production = rules.get(lSymbol.name);
          if (production == null) {
            next_symbols.add(lSymbol);
          } else {
            next_symbols.addAll(production.expand(lSymbol));
          }
          if (next_symbols.size() > MAX_SYMBOL_COUNT) {
            throw new RuntimeException("Production is too large!");
          }
        }
        symbols = next_symbols;
      }
    }

    @Override
    public String toString() {
      StringBuilder string_rtn = new StringBuilder();
      for (LSymbol lSymbol : symbols) {
        string_rtn.append(lSymbol.toString()+"\n");
      }
      return string_rtn.toString();
    }
  }


  private static interface LSystemProduction {
    public List<LSymbol> expand(LSymbol symbol);
  }


  private static class AProd implements LSystemProduction {
    @Override
    public List<LSymbol> expand(LSymbol symbol) {
      List<LSymbol> result = new ArrayList<>();
      int num = (int) (randomNum() * 5f + 30f);
      for (int ind = 0; ind < num; ind++) {
        float d_ang = (num - 1.0f - ind) * 80f / num;
        result.add(new LSymbol('!', "w", 0.1f - ind*0.1f/ 15f));
        result.add(new LSymbol('F', "l", 0.1f));
        float arg1 = 50f*(randomNum()*0.4f+0.8f);
        float arg2 = d_ang*(randomNum()* 0.4f+0.8f);
        result.add(new LSymbol('L', new String[] {"r_ang","d_ang"}, new float[] {arg1,arg2}));
        arg1 = -50f*(randomNum()*0.4f+0.8f);
        arg2 = d_ang*(randomNum()* 0.4f+0.8f);
        result.add(new LSymbol('L', new String[] {"r_ang","d_ang"}, new float[] {arg1,arg2}));
        result.add(new LSymbol('&', "a", 1f));
      }
      return result;
    }
  }


  private static class QProd implements LSystemProduction {
    private final int DT = 4;
    private final int T_MAX = 350;
    private final float P_MAX = 0.93f;

    @Override
    public List<LSymbol> expand(LSymbol symbol) {
      List<LSymbol> result = new ArrayList<>();
      if (!symbol.param_names[0].equals("t")) {
        throw new RuntimeException("Error!");
      }
      float t = symbol.param_values[0];
      float prop_off = t / T_MAX;
      if (prop_off < 1) {
        // NOTE - t is given in radians here!!
        LSymbol symbolDiameter = new LSymbol('!', "w", 0.85f + 0.15f*sin(t) ); // w is not randomized
        result.add(symbolDiameter);
        float lean_scale = 2f; // increase for more bend
        result.add(new LSymbol('^', "a", (randomNum() - 0.65f) * lean_scale));

        if (prop_off > P_MAX) {
          float d_ang = 1 / (1-P_MAX) * (1-prop_off)*110+15;
          result.add(new LSymbol('F', "l", 0.2f));
          // result.add(new LSymbol('!', "w", 0.04f));
          // for (int ind = 0; ind < (int)(randomNum()*2+5); ind++) {
          int fronds = 7;
          for (int ind = 0; ind < fronds; ind++) {
            float r_ang = t*10 + ind*(randomNum()*50 + 40);
            float e_d_ang = d_ang*(randomNum()*0.4f + 0.8f);
            // result.add(new LSymbol('F', "l", 0.1f));  // R: lift each leaf
            result.add(new LSymbol('/', "a", r_ang));
            result.add(new LSymbol('&', "a", e_d_ang));
            result.add(new LSymbol('['));
            result.add(new LSymbol('A'));
            result.add(new LSymbol(']'));
            result.add(new LSymbol('^', "a", e_d_ang));
            result.add(new LSymbol('\\', "a", r_ang));
          }
          result.add(new LSymbol('F', "l", 0.05f));
        } else {
          result.add(new LSymbol('F', "l", 0.15f));
          result.add(new LSymbol('Q', "t", t+DT));
        }
      } else {
        result.add(new LSymbol('!', "w", 0.0f));
        result.add(new LSymbol('F', "l", 0.15f));
      }
      return result;
    }
  }

  /*
   * e.g. may hold 0,1,2 or more parameters
   *
   *    A
   *    Q{t: 8.000}
   *    !{w: -0.093}
   *    &{a: 1}
   *    L{r_ang: 51.840, d_ang: 2.207}
   *
   *
   */
  private static class LSymbol {
    Character name; // name includes special symbols like !,^,/ and \
    String[] param_names;
    float[] param_values;

    public LSymbol(Character symbol) {
      this(symbol, new String[] {}, new float[] {});
    }
    public LSymbol(Character symbol, String param_name, float param_value) {
      this(symbol, new String[] {param_name}, new float[] {param_value});
    }
    public LSymbol(Character symbol, String[] param_names, float[] param_values) {
      if (param_names.length != param_values.length) {
        throw new RuntimeException("ERROR!");
      }
      this.name = symbol;
      this.param_names = param_names;
      this.param_values = param_values;
    }

    @Override
    public String toString() {
      if (param_values.length == 0) {
        return ""+name;
      }
      String param_str = "";
      for (int i=0; i<param_values.length; i++) {
        if (i>0) param_str += ", ";
        param_str += String.format("%s: %.3f", param_names[i], param_values[i]);
      }
      return String.format("%s{%s}", name, param_str);
    }
  }


  private void refreshModels() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(my_models.get("axes")));
    }
    // instances.add(new ModelInstance(my_models.get("leaf-model")));
    instances.addAll(turtle.getComposition());
  }


  @Override
  public void render() {
    if (MyGameState.animate) {
      Vector3 origin = new Vector3(0,0,0);
      camera.rotateAround(origin, Vector3.Z, MyGameState.animate_speed);
      camera.update();
    }

    if (MyGameState.app_starting && assets.update()) {
      loadModels();
      refreshModels();
      MyGameState.app_starting = false;
      MyGameState.ready = true;
    }

    if (MyGameState.request_scene_refresh && MyGameState.ready) {
      refreshModels();
      MyGameState.request_scene_refresh = false;
    }

    if (MyGameState.ready) {
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
    assets.dispose();
    for (Model m : my_models.values()) {
      m.dispose();
    }

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









