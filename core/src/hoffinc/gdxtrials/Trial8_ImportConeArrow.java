package hoffinc.gdxtrials;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import hoffinc.gdxrewrite.CameraInputControllerZUp;
import hoffinc.utils.ApplicationProp;


/*
 *
 * API
 * https://libgdx.badlogicgames.com/ci/nightlies/docs/api/
 *
 *
 * Stackoverflow example: Libgdx meshbuilder manually create 3d object
 * https://stackoverflow.com/questions/34568487/libgdx-meshbuilder-manually-create-3d-object
 *
 *
 *
 *
 *
 *
 *
 */
public class Trial8_ImportConeArrow extends ApplicationAdapter {


  //  class MyCameraInputController extends CameraInputController {
  //    public MyCameraInputController(Camera camera) {
  //      super(camera);
  //    }
  //    @Override
  //    public void update () {
  //    }
  //    @Override // this one activates
  //    public boolean touchDragged (int screenX, int screenY, int pointer) {
  //      System.err.println("touch");
  //      return true;
  //    }
  //  }



  public Environment environment;
  public PerspectiveCamera cam;
  public CameraInputControllerZUp camController;
  public ModelBatch modelBatch;
  public Array<ModelInstance> instances = new Array<ModelInstance>();
  public AssetManager assets;
  public boolean loading = true;

  String targetObject = "conearrow.obj";
  //  String targetObject = "data/ship.obj";


  @Override
  public void create () {
    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    // environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0.5f, -0.3f, 0.5f));

    cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    // cam.position.set(4f, 4f, 10f);

    cam.position.set(3.5f, -10f, 3f);
    cam.up.set(0, 0, 1);
    cam.lookAt(0,0,0);
    cam.near = 0.1f;
    cam.far = 300f;
    cam.update();


    camController = new CameraInputControllerZUp(cam);
    // camController.autoUpdate = false;


    // doesn't seem to do anything ... (it makes the orbit bigger)
    // camController.target.set(0, 0, 10);


    Gdx.input.setInputProcessor(camController);



    modelBatch = new ModelBatch();
    assets = new AssetManager();
    assets.load(targetObject, Model.class);
    loading = true;
  }



  private void doneLoading() {

    Model xAxis = getXAxis();
    Model yAxis = getYAxis();
    Model zAxis = getZAxis();

    instances.add(new ModelInstance(xAxis));
    instances.add(new ModelInstance(yAxis));
    instances.add(new ModelInstance(zAxis));



    Model model = assets.get(targetObject, Model.class);
    instances.add(new ModelInstance(model));

    //    ModelBuilder modelBuilder = new ModelBuilder();
    //    Model x_axis = modelBuilder.createXYZCoordinates(4, getMaterial(), Usage.Position);
    //    instances.add(new ModelInstance(x_axis));


    loading = false;
  }




  private float AXIS_LINE = 3f;
  private float AXIS_DIAM = 0.05f;
  private float ARROW_HEIGHT = 0.15f;
  private float ARROW_DIAM = 0.1f;

  private Model getXAxis() {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    Material redMat = new Material(ColorAttribute.createDiffuse(Color.RED));

    MeshPartBuilder axisLine = modelBuilder.part("cylinder", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,redMat);
    axisLine.setVertexTransform(new Matrix4().translate(AXIS_LINE/2,0,0).rotate(0,0,1,90));
    // R: the angleFrom, angleTo is if a sector is desirable
    // float width, float height, float depth, int divisions, float angleFrom, float angleTo
    // axisLine.cylinder(0.2f, 10f, 0.2f, 8, 0, 360);
    axisLine.cylinder(AXIS_DIAM, AXIS_LINE, AXIS_DIAM, 8);

    MeshPartBuilder axisArrow = modelBuilder.part("arrow", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,redMat);
    axisArrow.setVertexTransform(new Matrix4().translate(ARROW_HEIGHT/2+AXIS_LINE,0,0).rotate(0,0,1,-90));
    // float width, float height, float depth, int divisions
    // width and depth must be the same for the cone to be circular
    axisArrow.cone(ARROW_DIAM, ARROW_HEIGHT, ARROW_DIAM, 10);
    return modelBuilder.end();
  }

  private Model getYAxis() {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    Material greenMat = new Material(ColorAttribute.createDiffuse(Color.GREEN));

    MeshPartBuilder axisLine = modelBuilder.part("cylinder", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,greenMat);
    axisLine.setVertexTransform(new Matrix4().translate(0,AXIS_LINE/2,0));
    axisLine.cylinder(AXIS_DIAM, AXIS_LINE, AXIS_DIAM, 8);

    MeshPartBuilder axisArrow = modelBuilder.part("arrow", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,greenMat);
    axisArrow.setVertexTransform(new Matrix4().translate(0,ARROW_HEIGHT/2+AXIS_LINE,0));
    axisArrow.cone(ARROW_DIAM, ARROW_HEIGHT, ARROW_DIAM, 10);
    return modelBuilder.end();
  }


  private Model getZAxis() {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    Material blueMat = new Material(ColorAttribute.createDiffuse(Color.BLUE));

    MeshPartBuilder axisLine = modelBuilder.part("cylinder", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,blueMat);
    axisLine.setVertexTransform(new Matrix4().translate(0,0,AXIS_LINE/2).rotate(1,0,0,90));
    axisLine.cylinder(AXIS_DIAM, AXIS_LINE, AXIS_DIAM, 8);

    MeshPartBuilder axisArrow = modelBuilder.part("arrow", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,blueMat);
    axisArrow.setVertexTransform(new Matrix4().translate(0,0,ARROW_HEIGHT/2+AXIS_LINE).rotate(1,0,0,90));
    axisArrow.cone(ARROW_DIAM, ARROW_HEIGHT, ARROW_DIAM, 10);
    return modelBuilder.end();
  }



  private Material getMaterial() {
    Color black = new Color(255, 0, 0, 255);
    Material mat = new Material(ColorAttribute.createDiffuse(black));
    return mat;
  }

  private void showVertexData(Model model) {
    Mesh modelMesh = model.meshes.get(0);

    // 24, there are 6 floats, 3 for position and 3 for UV
    // System.err.println(modelMesh.getVertexSize());

    // 18
    // System.err.println(modelMesh.getNumVertices());

    float[] vertexData = new float[108];
    modelMesh.getVertices(vertexData);
    for (int i = 0; i < 108; i+=6) {
      for (int j = 0; j <= 5; j++) {
        System.err.printf("%9.4f   ", vertexData[i+j]);
      }
      System.err.println();
    }
  }



  @Override
  public void render () {
    if (loading && assets.update()) {
      doneLoading();
    }

    // R: turns out this isn't needed, unclear why that is
    // camController.update();

    // R: this glViewport(..) method doesn't really seem to do anything
    // Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


    ScreenUtils.clear(1, 1, 1, 1);

    modelBatch.begin(cam);
    modelBatch.render(instances, environment);
    modelBatch.end();

    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }
  }


  @Override
  public void dispose () {
    modelBatch.dispose();
    assets.dispose();
    instances.clear();



    // save window x,y and window width,height
    // NOTE - The initial size of the window is set from the Desktop-launcher
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



}



