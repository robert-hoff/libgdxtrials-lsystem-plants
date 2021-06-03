package hoffinc.gdxtrials;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import hoffinc.gdxrewrite.CameraInputControllerZUp;
import hoffinc.input.MyGameState;
import hoffinc.input.MyInputProcessor;
import hoffinc.models.AxesModel;
import hoffinc.utils.ApplicationProp;


/*
 *
 * Stackoverflow example: Libgdx meshbuilder manually create 3d object
 * https://stackoverflow.com/questions/34568487/libgdx-meshbuilder-manually-create-3d-object
 *
 *
 */
public class Trial8_ImportConeArrow extends ApplicationAdapter {


  public Environment environment;
  public PerspectiveCamera cam;
  public CameraInputControllerZUp camController;
  public ModelBatch modelBatch;
  public Array<ModelInstance> instances = new Array<ModelInstance>();
  public AssetManager assets;
  // public boolean loading = true;

  private String coneArrowFilename = "conearrow.obj";




  @Override
  public void create () {
    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0.5f, -0.3f, 0.5f));

    cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.position.set(3.5f, -10f, 3f);
    cam.up.set(0, 0, 1);
    cam.lookAt(0,0,0);
    cam.near = 0.1f;
    cam.far = 300f;
    cam.update();


    camController = new CameraInputControllerZUp(cam);
    // see https://stackoverflow.com/questions/23546544
    // if one InputProcessor returns true, it means that input is considered complete
    // if returns false the input will be passed on to the next InputProcessor
    InputProcessor myInputProcessor = new MyInputProcessor();
    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    inputMultiplexer.addProcessor(myInputProcessor);
    inputMultiplexer.addProcessor(camController);
    // R: this will disable the camera completely
    // camController.autoUpdate = false;
    Gdx.input.setInputProcessor(inputMultiplexer);

    modelBatch = new ModelBatch();
    assets = new AssetManager();
    assets.load(coneArrowFilename, Model.class);
    MyGameState.loading = true;
  }



  private void doneLoading() {
    instances.clear();

    if (MyGameState.show_axes) {
      Model axes = AxesModel.buildAxesLineVersion();
      instances.add(new ModelInstance(axes));
    }

    Model model = assets.get(coneArrowFilename, Model.class);
    instances.add(new ModelInstance(model));
    showVertexIndices(model);
    showVertexData(model);

    MyGameState.loading = false;
  }


  private void showVertexIndices(Model model) {
    Mesh modelMesh = model.meshes.get(0);
    // 18
    // modelMesh.getNumIndices()
    short[] indices = new short[18];
    modelMesh.getIndices(indices);
    for (int i = 0; i < 18; i++) {
      System.err.printf("%4d ", indices[i]);
      if (i%3==2) System.err.println();
    }
  }



  private void showVertexData(Model model) {
    Mesh modelMesh = model.meshes.get(0);

    // 24, there are 6 floats, 3 for position and 3 for UV
    // System.err.println(modelMesh.getVertexSize());

    // 18
    // System.err.println(modelMesh.getNumVertices());
    // There are 6 triangles and in this model no two triangles are sharing a vertex
    // (although they could have in some cases)

    float[] vertexData = new float[108];
    modelMesh.getVertices(vertexData);
    for (int i = 0; i < 108; i+=1) {
      if (i%6==0) System.err.printf("%3d: ", i/6);
      System.err.printf("%9.4f   ", vertexData[i]);
      if (i%6==5) System.err.println();
    }
  }



  @Override
  public void render () {
    if (MyGameState.loading && assets.update()) {
      doneLoading();
    }

    // R: the camera works without this, not clear to me why
    // camController.update();

    // R: this glViewport(..) method doesn't seem to do anything
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

}






