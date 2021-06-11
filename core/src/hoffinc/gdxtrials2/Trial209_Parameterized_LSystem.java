package hoffinc.gdxtrials2;

import java.util.ArrayList;
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
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import hoffinc.gdxrewrite.CameraInputControllerZUp;
import hoffinc.input.MyGameState;
import hoffinc.input.MyInputProcessor;
import hoffinc.models.AxesModel;
import hoffinc.models.PlantParts;
import hoffinc.utils.ApplicationProp;
import hoffinc.utils.FloatMaths;
import static hoffinc.utils.FloatMaths.randomNum;
import static hoffinc.utils.FloatMaths.sin;

/*
 * Parameterized L-System
 *
 *
 */
public class Trial209_Parameterized_LSystem extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera camera;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private Map<String, Model> my_models = new HashMap<>();
  private boolean show_axes = true;


  @Override
  public void create () {
    setTitle("Parameterized L-System");
    MyGameState.show_axes = true;

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, -0.2f, 0.2f, -0.8f)); // RBG and direction (r,g,b,x,y,z)

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(0.747f,-1.427f,0.600f);
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

    my_models.put("axes", AxesModel.buildAxesLineVersion());
    modelBatch = new ModelBatch();
  }


  private void loadModels() {
    Model leafModel = PlantParts.triangleLeaf(0x33ff33);
    leafModel.meshes.get(0).scale(0.1f, 1f, 1f);
    leafModel.meshes.get(0).transform(new Matrix4().rotate(new Quaternion(new Vector3(1,0,0), -45)));
    // leafModel.materials.get(0).set(new IntAttribute(IntAttribute.CullFace));
    my_models.put("palm-leaf", leafModel);


    //    testQProd();
    //    testLSymbols();

    // Using a fixed seed!
    FloatMaths.rand = new Random(10);
    // LSystem lSystem = createLSystem(83);
    LSystem lSystem = createLSystem(20);
    //    System.err.println(lSystem);
  }


  /*
   * Note, this system stops developing after 83 iterations
   *
   */
  LSystem createLSystem(int n) {
    LSystem lSystem = new LSystem();
    lSystem.add(new LSymbol('!', "w", 0.2f));
    lSystem.add(new LSymbol('/', "a", randomNum()*360));
    lSystem.add(new LSymbol('Q', "t", 0.0f));
    lSystem.addRule('Q', new QProd());
    lSystem.iterate(n);
    lSystem.print_java_instantiations = true;
    return lSystem;
  }


  /*
   *    !{w: 0.998}
   *    ^{a: -0.593}
   *    F{l: 0.150}
   *    Q{t: 12.000}
   *
   */
  void testQProd() {
    QProd qProd = new QProd();
    LSymbol qSymbol = new LSymbol('Q', "t", 8f);
    List<LSymbol> symbols = qProd.expand(qSymbol);
    for (LSymbol ls : symbols) {
      System.err.println(ls);
    }
  }


  /*
   *    A
   *    Q{t: 8.000}
   *    Q{t: 8.000}
   *    L{r_ang: 51.840, d_ang: 2.207}
   *
   */
  void testLSymbols() {
    LSymbol my_lsymbol1 = new LSymbol('A');
    LSymbol my_lsymbol2 = new LSymbol('Q', "t", 8f);
    LSymbol my_lsymbol3 = new LSymbol('Q', new String[]{"t"}, new float[]{8f});
    LSymbol my_lsymbol4 = new LSymbol('L', new String[]{"r_ang","d_ang"}, new float[]{51.84f,2.207f});
    System.err.println(my_lsymbol1);
    System.err.println(my_lsymbol2);
    System.err.println(my_lsymbol3);
    System.err.println(my_lsymbol4);
  }


  private static class LSystem {
    final int MAX_SYMBOL_COUNT = 10000;
    List<LSymbol> symbols = new ArrayList<>();
    Map<Character, LSystemProduction> rules = new HashMap<>();
    boolean print_java_instantiations = false;

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
        if (!print_java_instantiations) {
          string_rtn.append(lSymbol.toString()+"\n");
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
        result.add(new LSymbol('!', "w", 0.85f + 0.15f * sin(t) ));
        result.add(new LSymbol('^', "a", randomNum() - 0.65f ));
        if (prop_off > P_MAX) {
          float d_ang = 1 / (1-P_MAX) * (1-prop_off)*110+15;
          result.add(new LSymbol('!', "w", 0.1f));

          for (int ind = 0; ind < (int)(randomNum()*2+5); ind++) {
            float r_ang = t*10 + ind*(randomNum()*50 + 40);
            float e_d_ang = d_ang*(randomNum()*0.4f + 0.8f);
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

    // R: could use a map
    // Map<String, Float> param = new HashMap<>();
    //    LSymbol p(String name, Float val) {
    //      param.put(name, val);
    //      return this;
    //    }

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

    public String asInstantiation() {
      if (param_values.length == 0) {
        return String.format("lSystem.add(new LSymbol('%s'));", name);
      }
      if (param_values.length == 1) {
        return String.format("lSystem.add(new LSymbol('%s', \"%s\", %.5ff));", name, param_names[0], param_values[0]);
      }
      String string_params = "";
      String float_params = "";
      for (int i = 0; i < param_values.length; i++) {
        if (i>0) {
          string_params += ", ";
          float_params += ", ";
        }
        string_params += String.format("\"%s\"", param_names[i]);
        float_params += String.format("%.5ff", param_values[i]);
      }
      return String.format("lSystem.add(new LSymbol('%s', new String[]{%s}, new float[]{%s}));", name, string_params, float_params);
    }
  }


  private void refreshModels() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(my_models.get("axes")));
    }
    ModelInstance leafInstance = new ModelInstance(my_models.get("palm-leaf"));
    instances.add(leafInstance);
  }


  @Override
  public void render() {

    if (MyGameState.app_starting) {
      loadModels();
      refreshModels();
      MyGameState.app_starting = false;
      MyGameState.ready = true;
    }

    if (MyGameState.show_axes != this.show_axes) {
      refreshModels();
      this.show_axes = MyGameState.show_axes;
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









