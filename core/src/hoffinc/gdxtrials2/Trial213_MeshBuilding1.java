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
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
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
import hoffinc.utils.InspectData;
import static hoffinc.utils.FloatMaths.randomNum;
import static hoffinc.utils.FloatMaths.sin;

/*
 * Building a mesh from the palm tree wireframe
 *
 */
public class Trial213_MeshBuilding1 extends ApplicationAdapter {

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
    setTitle("Mesh Building - single polygon and normal");
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

    // CLOSEUP
    camera.position.set(0.729f,-1.116f,0.648f);
    //    camera.position.set(8.736f,-11.133f,8.430f);
    camera.up.set(0f, 0f, 1f);
    camera.lookAt(0f, 0f, 0f);
    //    camera.lookAt(0f, 0f, 4f);
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
    // ...

    MyGameState.show_axes = true;
    MyGameState.animate_speed = -0.7f;
  }


  private void loadModels() {
    Model axes = AxesModel.buildAxesLineVersion();
    my_models.put("axes", axes);

    Model triangleLeafModel = PlantParts.triangleLeaf(0x009900);
    triangleLeafModel.meshes.get(0).scale(0.13f, 1f, 1f);  // downscale on x to make leaf a sharp triangle
    triangleLeafModel.materials.get(0).set(new BlendingAttribute(true, 0.8f));
    // lightens up the model a little
    triangleLeafModel.materials.get(0).set(BasicShapes.getEmmisiveAttribute(0x222222, 0xff));
    my_models.put("leaf-model", triangleLeafModel);

    // Setting a fixed seed
    FloatMaths.rand = new Random(0);

    //    LSystem lSystem = createLSystem(200);
    LSystem lSystem = createLSystem(3);
    //    System.err.println(lSystem);



    turtle = new TurtleDrawer();
    Model circleOutline = BasicShapes.buildCircleOutline1(CIRCLE_VERTEX_COUNT, BASE_RADIUS, 0); // vertex_count, radius, color_rgb
    my_models.put("circle-outline", circleOutline);
    turtle.addModel(circleOutline);
    turtle.addModel(triangleLeafModel);
    turtle.scaleModel(1, 0.9f, 0.9f, 0.9f);
    parseSymbolsWithTurtle(turtle, lSystem.symbols);


    Model trunkModel = buildTrunkModel(0xcc9966);
    my_models.put("trunk", trunkModel);


  }

  /*
   *    0x996633           // dark brown
   *    0xcc9966           // medium brown
   *
   */
  private Model buildTrunkModel(int rgb) {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();


    Vector3 v0 = trunkCircles.get(0)[0];
    Vector3 v1 = trunkCircles.get(0)[1];
    Vector3 v2 = trunkCircles.get(0)[2];
    Vector3 v3 = trunkCircles.get(0)[3];
    Vector3 v4 = trunkCircles.get(0)[4];
    Vector3 v5 = trunkCircles.get(0)[5];
    Vector3 v6 = trunkCircles.get(0)[6];
    Vector3 v7 = trunkCircles.get(1)[0];
    Vector3 v8 = trunkCircles.get(1)[1];

    Vector3 t1 = new Vector3(v8).sub(v0);
    Vector3 t2 = new Vector3(v7).sub(v0);
    Vector3 n0 = new Vector3(t1).crs(t2);


    //    Vector3 v0 = new Vector3( 0.5f,0.0f,0.0f);
    //    Vector3 v1 = new Vector3( 0.0f,0.0f,1.0f);
    //    Vector3 v2 = new Vector3(-0.5f,0.0f,0.0f);
    //    Vector3 normal_f = new Vector3(0,-1,0);
    int attr = Usage.Position | Usage.Normal;
    Material mat = BasicShapes.getMaterial(rgb);
    MeshPartBuilder meshBuilder = modelBuilder.part("nameid", GL20.GL_TRIANGLES, attr, mat);
    //    meshBuilder.vertex(v0, normal_f, null, null); // Vector3 pos, Vector3 nor, Color col, Vector2 uv
    //    meshBuilder.vertex(v1, normal_f, null, null);
    //    meshBuilder.vertex(v2, normal_f, null, null);
    //    meshBuilder.triangle((short) 0, (short) 1, (short) 2); // CCW winding


    meshBuilder.vertex(v0, n0, null, null); // Vector3 pos, Vector3 nor, Color col, Vector2 uv
    meshBuilder.vertex(v1, n0, null, null);
    meshBuilder.vertex(v2, n0, null, null);
    meshBuilder.vertex(v3, n0, null, null);
    meshBuilder.vertex(v4, n0, null, null);
    meshBuilder.vertex(v5, n0, null, null);
    meshBuilder.vertex(v6, n0, null, null);
    meshBuilder.vertex(v7, n0, null, null);
    meshBuilder.vertex(v8, n0, null, null);
    meshBuilder.triangle((short) 0, (short) 8, (short) 7); // CCW winding

    Model wedgeModel = modelBuilder.end();
    return wedgeModel;
  }



  private int CIRCLE_VERTEX_COUNT = 7;
  private float BASE_RADIUS = 0.3f;
  private List<Vector3[]> trunkCircles = new ArrayList<>();

  private static Vector3[] circleVertices(int vertex_count, float radius, Matrix4 transform) {
    if (vertex_count < 3 || vertex_count > 100 || radius <= 0f) {
      throw new RuntimeException("error!");
    }
    Vector3[] vertices = new Vector3[vertex_count];
    double angle = 0.0;
    double dtheta = 2 * Math.PI / vertex_count;
    for (int i = 0; i < vertex_count; i++) {
      float x = radius * (float) Math.cos(angle);
      float y = radius * (float) Math.sin(angle);
      vertices[i] = new Vector3(x,y,0);
      vertices[i].mul(transform);
      angle += dtheta;
    }
    return vertices;
  }


  static void getCircleVerticesExample() {
    int CIRCLE_VERTEX_COUNT = 5;
    Matrix4 transform = new Matrix4().translate(1, 0, 0);
    Vector3[] circle1 = circleVertices(CIRCLE_VERTEX_COUNT, 1f, transform);
    for (Vector3 v : circle1) {
      InspectData.showVector3(v);
    }
    translateAVectorExample();
  }


  static void translateAVectorExample() {
    Vector3[] circle1 = new Vector3[5];
    circle1[0] = new Vector3( 1.000f, 0.000f,0f);
    circle1[1] = new Vector3( 0.309f, 0.951f,0f);
    circle1[2] = new Vector3(-0.809f, 0.587f,0f);
    circle1[3] = new Vector3(-0.809f,-0.587f,0f);
    circle1[4] = new Vector3( 0.309f,-0.951f,0f);
    Matrix4 transform = new Matrix4().translate(2, 0, 0);
    circle1[0].mul(transform);
    System.err.println(circle1[0]);
    InspectData.showMatrix4(transform);

  }

  /*
   * Symbols in the form
   *
   *    /{263.148}
   *    !{0.850}
   *    ^{0.363}
   *    F{0.150}
   *    !{0.736}
   *    ^{-0.819}
   *    F{0.150}
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
        float trunk_scaling = ls.param_values[0];
        turtle.modelNodes.get(0).setScale(trunk_scaling, trunk_scaling, 1);

        // R: moved the draw to here
        turtle.drawNode(0);

        Matrix4 transform = new Matrix4(turtle.transform);
        transform.mul(turtle.modelNodes.get(0).model_transform);
        Vector3[] trunkCircle = circleVertices(CIRCLE_VERTEX_COUNT, BASE_RADIUS, transform);
        trunkCircles.add(trunkCircle);

        //        for (Vector3 v : trunkCircle) {
        //          InspectData.showVector3(v);
        //        }
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


  LSystem createLSystem(int n) {
    LSystem lSystem = new LSystem();
    // lSystem.add(new LSymbol('/', randomNum()*360));
    lSystem.add(new LSymbol('Q', 0.0f));
    lSystem.addRule('Q', new QProd());
    lSystem.addRule('A', new AProd());
    lSystem.iterate(n);
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
        result.add(new LSymbol('!', 0.1f - ind*0.1f/ 15f));
        result.add(new LSymbol('F', 0.1f));
        float arg1 = 50f*(randomNum()*0.4f+0.8f);
        float arg2 = d_ang*(randomNum()* 0.4f+0.8f);
        result.add(new LSymbol('L', new float[] {arg1,arg2}));
        arg1 = -50f*(randomNum()*0.4f+0.8f);
        arg2 = d_ang*(randomNum()* 0.4f+0.8f);
        result.add(new LSymbol('L', new float[] {arg1,arg2}));
        result.add(new LSymbol('&', 1f));
      }
      return result;
    }
  }


  private static class QProd implements LSystemProduction {
    //    final int DT = 4;
    //    final float P_MAX = 0.93f;  // R: this seems to control the angle of the leaves
    //    final int T_MAX = 350;

    final int DT = 4;
    final int T_MAX = 350;
    final float P_MAX = 0.95f;
    @Override
    public List<LSymbol> expand(LSymbol symbol) {
      List<LSymbol> result = new ArrayList<>();
      float t = symbol.param_values[0];
      float prop_off = t / T_MAX;
      if (prop_off < 1) {
        // NOTE - t is given in radians!
        LSymbol symbolDiameter = new LSymbol('!', 0.85f + 0.15f*sin(t) ); // no random variation for diameter trunk
        result.add(symbolDiameter);
        float lean_scale = 2f; // increase for more bend
        result.add(new LSymbol('^', (randomNum()-0.65f) * lean_scale));

        if (prop_off > P_MAX) {
          float d_ang = 1 / (1-P_MAX) * (1-prop_off)*110+15;
          result.add(new LSymbol('F', 0.2f));
          // result.add(new LSymbol('!', "w", 0.04f));
          int fronds = 7;
          //           int fronds = (int)(randomNum()*2+5);
          for (int ind = 0; ind < fronds; ind++) {
            float r_ang = t*10 + ind*(randomNum()*50 + 40);
            float e_d_ang = d_ang*(randomNum()*0.4f + 0.8f);
            // result.add(new LSymbol('F', "l", 0.1f));  // R: lift the base of the leaf
            result.add(new LSymbol('/', r_ang));
            result.add(new LSymbol('&', e_d_ang));
            result.add(new LSymbol('['));
            result.add(new LSymbol('A'));
            result.add(new LSymbol(']'));
            result.add(new LSymbol('^', e_d_ang));
            result.add(new LSymbol('\\', r_ang));
          }
          result.add(new LSymbol('F', 0.05f));
        } else {
          result.add(new LSymbol('F', 0.15f));
          result.add(new LSymbol('Q', t+DT));
        }
      } else {
        result.add(new LSymbol('!', 0.0f));
        result.add(new LSymbol('F', 0.15f));
      }
      return result;
    }
  }

  /*
   * e.g. may hold 0,1,2 or more parameters
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

    public LSymbol(Character symbol, float[] param_values) {
      this.name = symbol;
      this.param_values = param_values;
    }
    public LSymbol(Character symbol) {
      this(symbol, new float[] {});
    }
    public LSymbol(Character symbol, float param_value) {
      this(symbol, new float[] {param_value});
    }
    @Override
    public String toString() {
      if (param_values.length == 0) {
        return ""+name;
      }
      String param_str = "";
      for (int i=0; i<param_values.length; i++) {
        if (i>0) param_str += ", ";
        param_str += String.format("%.3f", param_values[i]);
      }
      return String.format("%s{%s}", name, param_str);
    }
  }


  private void refreshModels() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(my_models.get("axes")));
    }

    // R: this can be useful but doesn't work here because the leaf and circle are registered
    //    for (Entry<String,Model> e : my_models.entrySet()) {
    //      if (!e.getKey().equals("axes")) {
    //        instances.add(new ModelInstance(e.getValue()));
    //      }
    //    }

    instances.add(new ModelInstance(my_models.get("trunk")));
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









