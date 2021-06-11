package hoffinc.gdxtrials1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.EllipseShapeBuilder;
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
 * Some matrix transforms on the testshape
 *
 */
public class Trial111_BuildingBasicShapes extends ApplicationAdapter {

  private Environment environment;
  private PerspectiveCamera camera;
  private CameraInputControllerZUp camController;
  private ModelBatch modelBatch;
  private Array<ModelInstance> instances = new Array<ModelInstance>();
  private Map<String, Model> my_models = new HashMap<>();


  @Override
  public void create () {
    setTitle("Circles");
    MyGameState.miniPopup.addListener("Print camera transforms", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n", "camera up:", camera.up.x, camera.up.z, camera.up.z);
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n", "camera position:", camera.position.x, camera.position.y, camera.position.z);
        System.out.printf("%-20s %.3ff,%.3ff,%.3ff \n\n", "camera dir:", camera.direction.x, camera.direction.y, camera.direction.z);
      }
    });

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0.5f, -0.3f, 0.5f)); // RBG and direction (r,g,b,x,y,z)
    environment.add(new PointLight().set(1f, 1f, 1f, new Vector3(2,2,2), 2f)); // r,g,b,pos,intensity
    environment.add(new PointLight().set(1f, 1f, 1f, new Vector3(1.713f,-3.408f,2.257f), 5f));

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(1.105f,-1.593f,1.100f);
    camera.up.set(0, 0, 1);
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

    modelBatch = new ModelBatch();
  }


  private void loadModels() {
    Model axes = AxesModel.buildAxesLineVersion();
    my_models.put("axes", axes);

    buildCircleOutline1(8, 1.1f);
    //    buildLineSegment();
    //    buildFlatCircle2();
    //    buildFlatCircle1();
  }

  /*
   * build a circle with line segments
   *
   */
  void buildCircleOutline1(int vertices, float radius) {
    if (vertices < 3 || vertices > 100 || radius <= 0f) {
      throw new RuntimeException("error!");
    }
    Material mat = BasicShapes.getMaterial(0xff0000);
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    // R: ColorUnpacked?
    // MeshPartBuilder circlePartBuilder = modelBuilder.part("lines", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, mat);
    MeshPartBuilder circlePartBuilder = modelBuilder.part("lines", GL20.GL_LINES, Usage.Position, mat);
    float x1 = radius;
    float y1 = 0f;
    double angle = 0.0;
    double dtheta = 2 * Math.PI / vertices;
    while(vertices --> 1) {
      angle += dtheta;
      float x2 = radius * (float) Math.cos(angle);
      float y2 = radius * (float) Math.sin(angle);
      circlePartBuilder.line(x1,y1,0,x2,y2,0);
      x1 = x2;
      y1 = y2;
      if (vertices == 1) {
        circlePartBuilder.line(x2,y2,0,radius,0,0);
      }
    }
    Model circleModel = modelBuilder.end();
    my_models.put("circle-outline1", circleModel);
  }


  void buildLineSegment() {
    Material mat = BasicShapes.getMaterial(0xff0000);
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder circlePartBuilder = modelBuilder.part("lines", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, mat);
    circlePartBuilder.line(0,0,0,1,1,0);
    Model circleModel = modelBuilder.end();
    my_models.put("lineseg", circleModel);
  }

  /*
   *  this one has an inner width
   */
  void buildFlatCircle2() {
    Material mat = BasicShapes.getMaterial(0xff0000);
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder circleBuilder = modelBuilder.part("my-circle", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, mat);
    // builder, width, height, innerWidth, innerHeight, divisions, x, y, z, normalx, normaly, normalz
    // both the circles are ellipse-like objects
    EllipseShapeBuilder.build(circleBuilder,1,1,0.9f,0.9f,10,0,0,0,0,0,1);
    Model circleModel1 = modelBuilder.end();
    my_models.put("circle2", circleModel1);
  }

  /*
   * a flat disk
   */
  void buildFlatCircle1() {
    Material mat = BasicShapes.getMaterial(0xff0000);
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder circleBuilder = modelBuilder.part("my-circle", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, mat);
    // builder, width, height, divisions, x, y, z, normalx, normaly, normalz
    EllipseShapeBuilder.build(circleBuilder,1,1,10,0,0,0,0,0,1);
    Model circleModel1 = modelBuilder.end();
    my_models.put("circle1", circleModel1);
  }


  private void refreshModels() {
    instances.clear();
    if (MyGameState.show_axes) {
      instances.add(new ModelInstance(my_models.get("axes")));
    }

    for (Entry<String,Model> e : my_models.entrySet()) {
      if (!e.getKey().equals("axes")) {
        instances.add(new ModelInstance(e.getValue()));
      }
    }
  }


  @Override
  public void render () {
    if (MyGameState.app_starting) {
      loadModels();
      refreshModels();
      MyGameState.ready = true;
      MyGameState.app_starting = false;
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
    for (Model m : my_models.values()) {
      m.dispose();
    }

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









