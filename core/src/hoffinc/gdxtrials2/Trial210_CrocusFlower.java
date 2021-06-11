package hoffinc.gdxtrials2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
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

/*
 * Crocus symbol production and render,
 * from Figure 3.2, The Algorithmic Beauty of Plants
 *
 */
public class Trial210_CrocusFlower extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera camera;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private Model axes;
  private AssetManager assets;
  private LSystem lSystem;
  private TurtleDrawer turtle;
  private Random rand = new Random();
  private int iterations = 9;
  private Model leafModel;
  private Model flowerModel;


  @Override
  public void create () {
    setTitle("Growing Crocus n="+iterations);
    MyGameState.helpful_tips = ""+
        "+                                GROW flower\n"+
        "-                                UNGROW flower\n"+MyGameState.helpful_tips;

    MyGameState.miniPopup.addListener("GROW!", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (iterations < 19) {
          iterations++;
          setTitle("Growing Crocus n="+iterations);
          MyGameState.request_scene_refresh = true;
        }
      }
    });
    MyGameState.miniPopup.addListener("UNGROW", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (iterations > 1) {
          iterations--;
          setTitle("Growing Crocus n="+iterations);
          MyGameState.request_scene_refresh = true;
        }
      }
    });
    MyGameState.miniPopup.addListener("Toogle animate", new ActionListener() {
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
    // environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1.0f, 1.0f, 1.0f, 1f));
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
    environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, -0.2f, 0.2f, -0.8f)); // RBG and direction (r,g,b,x,y,z)
    // These lights needed to be set extremely intense to see the blender exported materials
    // NOTE - this might indicate some problem with backface culling (the interpretation of the normals)
    environment.add(new PointLight().set(1f, 1f, 1f, new Vector3(2,2,2), 2f)); // r,g,b,pos,intensity
    environment.add(new PointLight().set(1f, 1f, 1f, new Vector3(2.603f,-5.128f,1.783f ), 1.5f)); // r,g,b,pos,intensity

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(1.956f,-3.334f,3.372f );
    camera.up.set(0f, 0f, 1f);
    camera.lookAt(0f, 0f, 0f);
    camera.near = 0.1f;
    camera.far = 300f;
    camera.update();
    camController = new CameraInputControllerZUp(camera);

    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    Gdx.input.setInputProcessor(inputMultiplexer);
    MyInputProcessor myInputProcessor = new MyInputProcessor();
    myInputProcessor.registerKeyDownEvent(Keys.NUMPAD_ADD, new MyEventListener() {
      @Override
      public void triggerEvent() {
        if (iterations < 18) {
          iterations++;
          MyGameState.request_scene_refresh = true;
          setTitle("Growing Crocus n="+iterations);
        }
      }
    });
    myInputProcessor.registerKeyDownEvent(Keys.PLUS, new MyEventListener() {
      @Override
      public void triggerEvent() {
        if (iterations < 18) {
          iterations++;
          MyGameState.request_scene_refresh = true;
          setTitle("Growing Crocus n="+iterations);
        }
      }
    });
    myInputProcessor.registerKeyDownEvent(Keys.NUMPAD_SUBTRACT, new MyEventListener() {
      @Override
      public void triggerEvent() {
        if (iterations > 1) {
          iterations--;
          MyGameState.request_scene_refresh = true;
          setTitle("Growing Crocus n="+iterations);
        }
      }
    });
    myInputProcessor.registerKeyDownEvent(Keys.MINUS, new MyEventListener() {
      @Override
      public void triggerEvent() {
        if (iterations > 1) {
          iterations--;
          MyGameState.request_scene_refresh = true;
          setTitle("Growing Crocus n="+iterations);
        }
      }
    });

    inputMultiplexer.addProcessor(myInputProcessor);
    inputMultiplexer.addProcessor(camController);

    axes = AxesModel.buildAxesLineVersion();
    modelBatch = new ModelBatch();
    assets = new AssetManager();
    assets.load("crocus/crocus-leaf-simple.obj", Model.class);
    assets.load("crocus/crocus-flower.obj", Model.class);
  }


  private void loadModels() {
    leafModel = assets.get("crocus/crocus-leaf-simple.obj", Model.class);
    flowerModel = assets.get("crocus/crocus-flower.obj", Model.class);

    Material mat1 = leafModel.materials.get(0);
    int leaf_green2 = 0x009933; // darker green
    mat1.set(BasicShapes.getDiffuseAttribute(leaf_green2));
    // NOTE - The fullfaces here is showing the same color on the back and front (using the same normal)
    mat1.set(new IntAttribute(IntAttribute.CullFace));

    Material mat2 = flowerModel.materials.get(0);
    int yellow = 0xffff00;
    mat2.set(BasicShapes.getDiffuseAttribute(yellow));
    mat2.set(new IntAttribute(IntAttribute.CullFace));

    // put the symbol-parser (model building) in refreshModels()

    // populateLSystem();
    // createLSystemIterate();
    // createLSystem(12);
    // createLSystem(5);
    // testQProd();
    // testLSymbols();

    // lSystem.skip_line_breaks = true;
    // System.err.println(lSystem);
  }

  /*
   * After 2 iterations `List<LSymbol> symbols` will be
   *
   * F{2.00} [ &{30.00} L{7.00} ] /{137.50} F{2.00} [ &{30.00} L{6.00} ] /{137.50} F{2.00} [ &{30.00}
   *        L{5.00} ] /{137.50} F{1.80} [ &{30.00} L{4.00} ] /{137.50} F{20.00} K{2.00}
   *
   */
  private void parseSymbolsWithTurtle(TurtleDrawer turtle, List<LSymbol> symbols) {
    for (LSymbol ls : symbols) {
      if (ls.name == '|') {
        float strength = 0f;
        float v = rand.nextFloat() * strength - strength/2;
        turtle.rollRight(v);
      }
      if (ls.name == '[') {
        turtle.push();
      }
      if (ls.name == ']') {
        turtle.pop();
      }
      if (ls.name == 'F') {
        float stem_length = ls.param_values[0];
        // System.err.println(stem_length);
        // turtle.push();
        turtle.scaleModel(0, 1, 1, stem_length/1.8f);
        turtle.drawNode(0);
        // turtle.pop();
      }
      if (ls.name == 'L') {
        float leaf_size = ls.param_values[0];
        // float strength = 0.1f;
        float strength = 0.0f;
        float v = rand.nextFloat() * strength - strength/2;
        float scale = leaf_size/5+v;
        turtle.scaleModel(1, scale, scale, scale);
        turtle.drawNode(1);
      }
      if (ls.name == 'K') {
        float flower_size = ls.param_values[0];
        float scale = flower_size/5f;
        // System.err.println(scale);
        turtle.scaleModel(2, scale, scale, scale);
        turtle.drawNode(2);
      }
      if (ls.name == '+') {
        turtle.turnLeft(ls.param_values[0]);
      }
      if (ls.name == '&') {
        turtle.pitchDown(ls.param_values[0]);
      }
      if (ls.name == '/') {
        // float strength = 15f;
        float strength = 0f;
        float v = rand.nextFloat() * strength - strength/2;
        turtle.rollRight(ls.param_values[0]+v);
        // turtle.rollRight(ls.param_values[0]);
      }
    }
  }


  void createLSystemIterate() {
    for (int i = 0; i < 20; i++) {
      LSystem lSystem = new LSystem();
      lSystem.add(new LSymbol('a', 1.0f));
      lSystem.addRule('a', new AParamProd());
      lSystem.addRule('A', new AProd());
      lSystem.addRule('L', new LProd());
      lSystem.addRule('K', new KProd());
      lSystem.addRule('F', new FProd());
      lSystem.iterate(i);
      lSystem.skip_line_breaks = true;
      System.err.printf("%3d: %s \n", i, lSystem);
    }
  }

  /*
   *    ω : a(1)
   *   p1 : a(t) : t<Ta    -->   F(1) [ &(30) ~L(0) ] /(137.5) a(t+1)
   *   p2 : a(t) : t=Ta    -->   F(20) A
   *   p3 : A    : *       -->   ~K(0)
   *   p4 : L(t) : t<TL    -->   L(t+1)
   *   p5 : K(t) : t<TK    -->   K(t+1)
   *   p6 : F(l) : l<2     -->   F(l+0.2)
   *
   *   The ~ from the book means a predefined surface (the symbols is omitted here in the symbol processing)
   *
   *
   */
  void createLSystem(int n) {
    lSystem = new LSystem();
    lSystem.add(new LSymbol('|'));
    lSystem.add(new LSymbol('a', 1.0f));
    lSystem.addRule('a', new AParamProd());
    lSystem.addRule('A', new AProd());
    lSystem.addRule('L', new LProd());
    lSystem.addRule('K', new KProd());
    lSystem.addRule('F', new FProd());
    lSystem.iterate(n);
    // lSystem.print_java_instantiations = true;
    // System.err.println(lSystem);
  }


  private static final int T_A = 7;     // developmental switch time
  private static final int T_L = 8;     // leaf growth limit
  private static final int T_K = 8;     // flower growth limit

  /*
   *   p6 : F(l) : l<2     -->   F(l+0.2)
   *
   */
  private static class FProd implements LSystemProduction {
    @Override
    public List<LSymbol> expand(LSymbol symbol) {
      List<LSymbol> result = new ArrayList<>();
      float l = symbol.param_values[0];
      if (l<=2.01f) {
        result.add(new LSymbol('F', l+0.2f));
      } else {
        result.add(new LSymbol('F', l));
      }
      return result;
    }
  }

  /*
   *   p5 : K(t) : t<TK    -->   K(t+1)
   *
   */
  private static class KProd implements LSystemProduction {
    @Override
    public List<LSymbol> expand(LSymbol symbol) {
      List<LSymbol> result = new ArrayList<>();
      int t = (int) symbol.param_values[0];
      if (t<T_K) {
        result.add(new LSymbol('K', t+1f));
      } else {
        result.add(new LSymbol('K', t));
      }
      return result;
    }
  }

  /*
   *   p4 : L(t) : t<TL    -->   L(t+1)
   *
   */
  private static class LProd implements LSystemProduction {
    @Override
    public List<LSymbol> expand(LSymbol symbol) {
      List<LSymbol> result = new ArrayList<>();
      float t = symbol.param_values[0];
      if (t<T_L) {
        if (t==0) t=1;
        // System.err.println(t);
        result.add(new LSymbol('L', t*1.2f));
        // result.add(new LSymbol('L', t+1));
      } else {
        result.add(new LSymbol('L', t));
      }
      return result;
    }
  }

  /*
   *   p3 : A    : *       -->   ~K(0)
   *
   */
  private static class AProd implements LSystemProduction {
    @Override
    public List<LSymbol> expand(LSymbol symbol) {
      List<LSymbol> result = new ArrayList<>();
      result.add(new LSymbol('K', 0));
      return result;
    }
  }

  /*
   *
   *   p1 : a(t) : t<Ta    -->   F(1) [ &(30) ~L(0) ] /(137.5) a(t+1)
   *   p2 : a(t) : t=Ta    -->   F(20) A
   *
   * parameters are in the form
   *
   * a(1)
   * a(2)
   * a(3)
   * ...
   *
   *
   *
   */
  private static class AParamProd implements LSystemProduction {
    @Override
    public List<LSymbol> expand(LSymbol symbol) {
      List<LSymbol> result = new ArrayList<>();
      int t = (int) symbol.param_values[0];
      if (t < T_A) {
        result.add(new LSymbol('F', 1));
        result.add(new LSymbol('['));
        result.add(new LSymbol('&', 30));
        result.add(new LSymbol('L', 0));
        result.add(new LSymbol(']'));
        result.add(new LSymbol('/', 137.5f));
        result.add(new LSymbol('a', t+1f));
      } else if (t == T_A) {
        result.add(new LSymbol('F', 20));
        result.add(new LSymbol('A'));
      } else {
        log.error("this shouldn't happen");
      }
      return result;
    }
  }


  private static class LSystem {
    private final int MAX_SYMBOL_COUNT = 10000;
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
    boolean print_java_instantiations = false;
    boolean skip_line_breaks = false;
    @Override
    public String toString() {
      StringBuilder string_rtn = new StringBuilder();
      for (LSymbol lSymbol : symbols) {
        if (!print_java_instantiations) {
          string_rtn.append(lSymbol.toString());
          if (!skip_line_breaks) {
            string_rtn.append("\n");
          }
        } else {
          string_rtn.append(lSymbol.asInstantiation()+"\n");
        }
      }
      return string_rtn.toString();
    }
  }

  private static interface LSystemProduction {
    public List<LSymbol> expand(LSymbol symbol);
  }


  void testSymbolProduction() {
    AParamProd prod = new AParamProd();
    LSymbol aSymbol = new LSymbol('A', 1f);
    List<LSymbol> symbols = prod.expand(aSymbol);
    for (LSymbol ls : symbols) {
      System.err.println(ls);
    }
  }

  /*
   *
   *  A
   *  Q{8.000}
   *  Q{8.000}
   *  L{51.840, 2.207}
   *
   *
   */
  void testLSymbols() {
    LSymbol my_lsymbol1 = new LSymbol('A');
    LSymbol my_lsymbol2 = new LSymbol('Q', 8f);
    LSymbol my_lsymbol3 = new LSymbol('Q', new float[]{8f});
    LSymbol my_lsymbol4 = new LSymbol('L', new float[]{51.84f,2.207f});
    System.err.println(my_lsymbol1);
    System.err.println(my_lsymbol2);
    System.err.println(my_lsymbol3);
    System.err.println(my_lsymbol4);
    // System.err.println(my_lsymbol.asInstantiation());
  }

  /*
   * LSymbol may hold 0,1,2 or more parameters
   *
   *    A
   *    Q{8.000}
   *    !{-0.093}
   *    &{1}
   *    L{51.840, 2.207}
   *
   *
   */
  private static class LSymbol {
    Character name; // name includes special symbols like !,^,/ and \
    float[] param_values;

    public LSymbol(Character symbol) {
      this(symbol, new float[] {});
    }
    public LSymbol(Character symbol, float param_value) {
      this(symbol, new float[] {param_value});
    }
    public LSymbol(Character symbol, float[] param_values) {
      this.name = symbol;
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
        param_str += String.format("%.2f", param_values[i]);
      }
      return String.format("%s{%s}", name, param_str);
    }

    public String asInstantiation() {
      if (param_values.length == 0) {
        return String.format("lSystem.add(new LSymbol('%s'));", name);
      }
      if (param_values.length == 1) {
        return String.format("lSystem.add(new LSymbol('%s', %.5ff));", name, param_values[0]);
      }
      String float_params = "";
      for (int i = 0; i < param_values.length; i++) {
        if (i>0) {
          float_params += ", ";
        }
        float_params += String.format("%.5ff", param_values[i]);
      }
      return String.format("lSystem.add(new LSymbol('%s', new float[]{%s}));", name, float_params);
    }
  }



  private void refreshModels() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(axes));
    }

    turtle = new TurtleDrawer();
    float STEM_LEN = 0.022f;
    float STEM_DIAM = 0.023f;
    int MESH_RES = 5;
    int dark_green = 0x006600;

    turtle.addModel(PlantParts.stemTrunk(STEM_LEN, STEM_DIAM, MESH_RES, dark_green), new Vector3(0,0,STEM_LEN));
    turtle.addModel(leafModel);
    turtle.modelNodes.get(1).scale(0.80f, 1.00f, 1.00f);    // scale a bit along x and flatten along y
    turtle.modelNodes.get(1).scale(0.15f, 0.15f, 0.15f);    // then scale all uniformly
    turtle.modelNodes.get(1).rotX(-12f);
    turtle.addModel(flowerModel);
    turtle.modelNodes.get(2).scale(0.15f, 0.15f, 0.18f);
    turtle.modelNodes.get(2).translate(0, 0, -0.25f);

    createLSystem(iterations);
    parseSymbolsWithTurtle(turtle, lSystem.symbols);

    instances.addAll(turtle.getComposition());
  }


  @Override
  public void render() {
    if (MyGameState.animate) {
      Vector3 origin = new Vector3(0,0,0);
      camera.rotateAround(origin, Vector3.Z, -0.8f);
      camera.update();
    }

    if (MyGameState.app_starting && assets.update()) {
      loadModels();
      refreshModels();
      MyGameState.app_starting = false;
      MyGameState.ready = true;
    }

    if (MyGameState.request_scene_refresh && MyGameState.ready) {
      MyGameState.request_scene_refresh = false;
      refreshModels();
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
    axes.dispose();

    // NOTE - if the models are registererd in the asset manager it will dispose of it for us
    // coneModel.dispose();
    // leafModel.dispose();

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


  private static Logger log = LoggerFactory.getLogger(Trial210_CrocusFlower.class);

}









