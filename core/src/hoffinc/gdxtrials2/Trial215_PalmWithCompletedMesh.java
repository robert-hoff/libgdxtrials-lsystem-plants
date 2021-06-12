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
public class Trial215_PalmWithCompletedMesh extends ApplicationAdapter {

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
    setTitle("Palm tree");
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
    float lightIntensity = 0.8f;
    Color lightColor = new Color(lightIntensity, lightIntensity, lightIntensity, 0xff);
    environment.add(new DirectionalLight().set(lightColor, new Vector3(-0.8f,0.3f,-0.5f).nor()));
    // environment.add(new DirectionalLight().set(lightColor, new Vector3(-0.093f,-0.053f,-0.994f).nor()));
    // environment.add(new DirectionalLight().set(lightColor, new Vector3(-0.915f,0.344f,-0.210f).nor()));
    // environment.add(new PointLight().set(1f, 1f, 1f, new Vector3(5f,-1.5f,22f), 4f)); // r,g,b,pos,intensity

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    // CLOSEUP
    //    camera.position.set(0.729f,-1.116f,0.648f);
    camera.position.set(7.303f,-0.697f,6.833f);
    camera.up.set(0f, 0f, 1f);
    camera.lookAt(0f, 0f, 4f);

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

  /*
   *
   *
   * I'm thinking we keep the turtle, but instead of drawing models we should be doing something different.
   * Let's just populate a datastruct with the circles
   *
   * Currently, the turtle maintains
   *
   *      List<TurtleModel> modelNodes = new ArrayList<>();
   *
   * A TurtleModel maintains a Model (gdx.graphics.g3d.Model) and its local transform. It maintains the information
   * required to draw the model.
   *
   * TurtleDrawer offers the method
   *
   *      public void drawNode(int id) {..}
   *
   * Which places a Model onto an array of ModelInstances that is meant to be retrieved when refreshing the scene
   *
   *
   * The interface is therefore a bit restrictive in its use. Also important: we are *missing* any model
   * or conceptualisation of the plant, except for what is given in the LSystem production rules.
   *
   *
   *
   * Let's here focus on the mesh-building problem. We need to collect for each trunk-diameter a set of
   * vertices that describe a circle around the trunk at some given iteration (or height).
   *
   *
   *
   *
   */
  private void loadModels() {
    Model axes = AxesModel.buildAxesLineVersion();
    my_models.put("axes", axes);

    Model triangleLeafModel = PlantParts.triangleLeaf(0x009900);
    triangleLeafModel.meshes.get(0).scale(0.13f, 1f, 1f);  // downscale on x to make leaf a sharp triangle
    triangleLeafModel.materials.get(0).set(new BlendingAttribute(true, 0.8f));
    // lightens up the model a little
    // triangleLeafModel.materials.get(0).set(BasicShapes.getEmmisiveAttribute(0x222222, 0xff));
    my_models.put("leaf-model", triangleLeafModel);

    // Setting a fixed seed
    // FloatMaths.rand = new Random(10);

    LSystem lSystem = createLSystem(200);
    // LSystem lSystem = createLSystem(50);
    // System.err.println(lSystem);


    turtle = new TurtleDrawer();
    Model circleOutline = BasicShapes.buildCircleOutline1(CIRCLE_VERTEX_COUNT, BASE_RADIUS, 0); // vertex_count, radius, color_rgb
    my_models.put("circle-outline", circleOutline);
    turtle.addModel(circleOutline);
    turtle.addModel(triangleLeafModel);
    turtle.scaleModel(1, 0.9f, 0.9f, 0.9f);
    parseSymbolsWithTurtle(turtle, lSystem.symbols);


    // long time = System.currentTimeMillis();
    //    Model trunkModel = buildTrunkModel(0x996633);
    //    my_models.put("trunk", trunkModel);
    // System.err.println(System.currentTimeMillis()-time);
    // System.err.println(normalCount);

    Model trunkModel = buildTrunkModelFlatShaded(0x996633);
    my_models.put("trunk", trunkModel);
  }


  // int normalCount = 0;


  private Vector3 calculateNormal(int circle_ind, int vertex_id) {
    // normalCount++;
    Vector3 normal_0 = calculateNormalDownRight(circle_ind, vertex_id);
    Vector3 normal_1 = calculateNormalDownLeft(circle_ind, vertex_id);
    Vector3 normal_2 = calculateNormalUpRight(circle_ind, vertex_id);
    Vector3 normal_3 = calculateNormalUpLeft(circle_ind, vertex_id);
    Vector3 normal = new Vector3();
    if (normal_0 != null) normal.add(normal_0);
    if (normal_1 != null) normal.add(normal_1);
    if (normal_2 != null) normal.add(normal_2);
    if (normal_3 != null) normal.add(normal_3);
    normal.nor();
    return normal;
  }

  private Vector3 calculateNormalDownRight(int circle_ind, int vertex_id) {
    if (circle_ind == 0) {
      return null; // just return a null - it won't be used
    }
    Vector3[] circle_md = trunkCircles.get(circle_ind);
    Vector3[] circle_lo = circle_ind>0 ? trunkCircles.get(circle_ind-1) : null;
    Vector3 p0 = new Vector3(circle_lo[vertex_id]);
    Vector3 p1 = new Vector3(circle_md[vertex_id]);
    Vector3 p2 = new Vector3(circle_md[(vertex_id+1)%CIRCLE_VERTEX_COUNT]);
    Vector3 normal_0 = p2.sub(p0).crs(p1.sub(p0)).nor();
    return normal_0;
  }

  private Vector3 calculateNormalDownLeft(int circle_ind, int vertex_id) {
    if (circle_ind == 0) {
      return null;
    }
    Vector3[] circle_md = trunkCircles.get(circle_ind);
    Vector3[] circle_lo = circle_ind>0 ? trunkCircles.get(circle_ind-1) : null;
    Vector3 p0 = new Vector3(circle_lo[(vertex_id-1+CIRCLE_VERTEX_COUNT)%CIRCLE_VERTEX_COUNT]);
    Vector3 p1 = new Vector3(circle_md[(vertex_id-1+CIRCLE_VERTEX_COUNT)%CIRCLE_VERTEX_COUNT]);
    Vector3 p2 = new Vector3(circle_md[vertex_id]);
    Vector3 normal_1 = p2.sub(p0).crs(p1.sub(p0)).nor();
    return normal_1;
  }

  private Vector3 calculateNormalUpRight(int circle_ind, int vertex_id) {
    if (circle_ind == trunkCircles.size()-1) {
      return null;
    }
    Vector3[] circle_hi = trunkCircles.get(circle_ind+1);
    Vector3[] circle_md = trunkCircles.get(circle_ind);
    Vector3 p0 = new Vector3(circle_md[vertex_id]);
    Vector3 p1 = new Vector3(circle_hi[vertex_id]);
    Vector3 p2 = new Vector3(circle_hi[(vertex_id+1)%CIRCLE_VERTEX_COUNT]);
    Vector3 normal_2 = p2.sub(p0).crs(p1.sub(p0)).nor();
    return normal_2;
  }

  private Vector3 calculateNormalUpLeft(int circle_ind, int vertex_id) {
    if (circle_ind == trunkCircles.size()-1) {
      return null;
    }
    Vector3[] circle_hi = trunkCircles.get(circle_ind+1);
    Vector3[] circle_md = trunkCircles.get(circle_ind);
    Vector3 p0 = new Vector3(circle_md[(vertex_id-1+CIRCLE_VERTEX_COUNT)%CIRCLE_VERTEX_COUNT]);
    Vector3 p1 = new Vector3(circle_hi[(vertex_id-1+CIRCLE_VERTEX_COUNT)%CIRCLE_VERTEX_COUNT]);
    Vector3 p2 = new Vector3(circle_hi[vertex_id]);
    Vector3 normal_3 = p2.sub(p0).crs(p1.sub(p0)).nor();
    return normal_3;
  }




  /*
   *    0x996633           // dark brown
   *    0xcc9966           // medium brown
   *
   */
  private Model buildTrunkModelFlatShaded(int rgb) {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    int attr = Usage.Position | Usage.Normal;
    Material mat = BasicShapes.getMaterial(rgb);
    MeshPartBuilder meshBuilder = modelBuilder.part("nameid", GL20.GL_TRIANGLES, attr, mat);

    for (int i = 0; i < trunkCircles.size(); i++) {
      Vector3[] circle_vertices = trunkCircles.get(i);
      for (int j = 0; j < circle_vertices.length; j++) {
        meshBuilder.vertex(circle_vertices[j], calculateNormalDownRight(i,j), null, null);
        meshBuilder.vertex(circle_vertices[j], calculateNormalDownLeft(i,j), null, null);
        meshBuilder.vertex(circle_vertices[j], calculateNormalUpRight(i,j), null, null);
        meshBuilder.vertex(circle_vertices[j], calculateNormalUpLeft(i,j), null, null);
      }
    }

    for (int i = 0; i < trunkCircles.size()-1; i++) {
      Vector3[] circle_vertices = trunkCircles.get(i);
      for (int j = 0; j < circle_vertices.length; j++) {
        short index1 = (short) (i*4*CIRCLE_VERTEX_COUNT+j*4 + 2);
        short index2 = (short) ((i+1)*4*CIRCLE_VERTEX_COUNT+(j+1)*4%(CIRCLE_VERTEX_COUNT*4) + 1);
        short index3 = (short) ((i+1)*4*CIRCLE_VERTEX_COUNT+j*4 + 0);
        meshBuilder.triangle(index1, index2, index3);

        index1 = (short) (i*4*CIRCLE_VERTEX_COUNT+j*4 + 2);
        index2 = (short) (i*4*CIRCLE_VERTEX_COUNT+(j+1)*4%(CIRCLE_VERTEX_COUNT*4) + 3);
        index3 = (short) ((i+1)*4*CIRCLE_VERTEX_COUNT+(j+1)*4%(CIRCLE_VERTEX_COUNT*4) + 1);
        meshBuilder.triangle(index1, index2, index3);
      }
    }
    Model trunkModel = modelBuilder.end();
    return trunkModel;
  }


  private Model buildTrunkModel(int rgb) {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    int attr = Usage.Position | Usage.Normal;
    Material mat = BasicShapes.getMaterial(rgb);
    MeshPartBuilder meshBuilder = modelBuilder.part("nameid", GL20.GL_TRIANGLES, attr, mat);

    for (int i = 0; i < trunkCircles.size(); i++) {
      Vector3[] circle_vertices = trunkCircles.get(i);
      for (int j = 0; j < circle_vertices.length; j++) {
        Vector3 vertex_normal = calculateNormal(i,j);
        // (Vector3 pos, Vector3 nor, Color col, Vector2 uv)
        meshBuilder.vertex(circle_vertices[j], vertex_normal, null, null);
      }
    }

    for (int i = 0; i < trunkCircles.size()-1; i++) {
      Vector3[] circle_vertices = trunkCircles.get(i);
      for (int j = 0; j < circle_vertices.length; j++) {
        short index1 = (short) (i*CIRCLE_VERTEX_COUNT+j);
        short index2 = (short) ((i+1)*CIRCLE_VERTEX_COUNT+(j+1)%CIRCLE_VERTEX_COUNT);
        short index3 = (short) ((i+1)*CIRCLE_VERTEX_COUNT+j);
        meshBuilder.triangle(index1, index2, index3);
        index1 = (short) (i*CIRCLE_VERTEX_COUNT+j);
        index2 = (short) (i*CIRCLE_VERTEX_COUNT+(j+1)%CIRCLE_VERTEX_COUNT);
        index3 = (short) ((i+1)*CIRCLE_VERTEX_COUNT+(j+1)%CIRCLE_VERTEX_COUNT);
        meshBuilder.triangle(index1, index2, index3);
      }
    }
    Model trunkModel = modelBuilder.end();
    return trunkModel;
  }


  private int CIRCLE_VERTEX_COUNT = 7;
  private float BASE_RADIUS = 0.22f;
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
      }
      if (ls.name == '!') {
        float trunk_scaling = ls.param_values[0];
        turtle.modelNodes.get(0).setScale(trunk_scaling, trunk_scaling, 1);
        // -- draw wireframe here
        //        turtle.drawNode(0);
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
        // result.add(new LSymbol('!', 0.1f - ind*0.1f/ 15f));
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
    final int T_MAX = 250;
    final float P_MAX = 0.95f;
    @Override
    public List<LSymbol> expand(LSymbol symbol) {
      List<LSymbol> result = new ArrayList<>();
      float t = symbol.param_values[0];
      float prop_off = t / T_MAX;
      if (prop_off < 1) {
        // NOTE - t is given in radians!
        // float strength = 0.f;
        // float v = strength * randomNum() - strength/2;
        float w = 0.85f + 0.15f*sin(t);
        //        if (w>1) {
        //          w=1;
        //        }
        LSymbol symbolDiameter = new LSymbol('!',  w); // no random variation for diameter trunk
        result.add(symbolDiameter);
        float lean_scale = 2f; // increase for more bend
        result.add(new LSymbol('^', (randomNum()-0.65f) * lean_scale));

        if (prop_off > P_MAX) {
          float d_ang = 1 / (1-P_MAX) * (1-prop_off)*110+15;
          result.add(new LSymbol('F', 0.2f));
          // result.add(new LSymbol('!', "w", 0.04f));
          int fronds = 8;
          //           int fronds = (int)(randomNum()*2+5);
          for (int ind = 0; ind < fronds; ind++) {
            float r_ang = t*10 + ind*(randomNum()*50 + 40);
            float e_d_ang = d_ang*(randomNum()*0.4f + 0.8f);
            result.add(new LSymbol('F', 0.02f));  // R: lift the base of the leaf
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

    Model trunk = my_models.get("trunk");
    if (trunk != null) {
      instances.add(new ModelInstance(trunk));
    }
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









