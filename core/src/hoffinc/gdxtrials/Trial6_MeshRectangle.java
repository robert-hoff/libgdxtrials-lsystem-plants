package hoffinc.gdxtrials;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import hoffinc.utils.ApplicationProp;


/*
 *
 *
 */
public class Trial6_MeshRectangle extends ApplicationAdapter {


  public Environment environment;
  public PerspectiveCamera cam;
  public CameraInputController camController;
  public ModelBatch modelBatch;
  public Model cubeModel1;
  public ModelInstance instance;
  public ShapeRenderer shapeRenderer;


  @Override
  public void create () {

    shapeRenderer = new ShapeRenderer();


    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.position.set(4f, 4f, 10f);
    cam.lookAt(0,0,0);
    cam.near = 1f;
    cam.far = 300f;
    cam.update();
    camController = new CameraInputController(cam);
    Gdx.input.setInputProcessor(camController);
    modelBatch = new ModelBatch();




    // float x00, y00, z00
    // float x10, y10, z10
    // float x11, y11, z11
    // float x01, y01, z01
    // float normalX, normalY, normalZ,
    // int primitiveType, Material material, long attributes
    ModelBuilder modelBuilder = new ModelBuilder();
    Model rectangle = modelBuilder.createRect(
        0,0,0,
        5,0,0,
        5,5,0,
        0,5,0,
        0,0,1,
        GL20.GL_LINES, getMaterial(), Usage.Position);
    instance = new ModelInstance(rectangle);    // ModelInstance





  }





  private Material getMaterial() {
    Color black = new Color(0, 0, 0, 255);
    Material mat = new Material(ColorAttribute.createDiffuse(black));
    return mat;
  }




  @Override
  public void render () {
    Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    ScreenUtils.clear(1, 1, 1, 1);
    modelBatch.begin(cam);
    modelBatch.render(instance, environment);
    modelBatch.end();

    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }
  }


  @Override
  public void dispose () {
    modelBatch.dispose();
    cubeModel1.dispose();


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








  public static void showFloatArray(float[] a) {
    if (a.length == 0) {
      System.err.println("[]");
    } else {
      System.err.printf("[");
      for (int i = 0; i < a.length; i++) {
        System.err.printf("%7.3f", a[i]);
      }
      System.err.println("]");
    }
  }





}


