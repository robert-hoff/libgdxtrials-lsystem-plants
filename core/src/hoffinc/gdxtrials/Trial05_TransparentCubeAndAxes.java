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
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;

import hoffinc.utils.ApplicationProp;


/*
 *
 *
 */
public class Trial05_TransparentCubeAndAxes extends ApplicationAdapter {


  public Environment environment;
  public PerspectiveCamera cam;
  public CameraInputController camController;
  public ModelBatch modelBatch;
  public Model cubeModel;
  public Model arrowX;
  public Model arrowY;
  public Model arrowZ;
  public ModelInstance instance;
  public ModelInstance instance2;
  public ModelInstance instance3;
  public ModelInstance instance4;
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
    ModelBuilder modelBuilder = new ModelBuilder();


    Material mat = getMat4();
    cubeModel = modelBuilder.createBox(5f, 5f, 5f, mat, Usage.Position | Usage.Normal);
    //    cubeModel1 = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position);
    instance = new ModelInstance(cubeModel);



    Material redMat = new Material(ColorAttribute.createDiffuse(Color.RED));
    Material greenMat = new Material(ColorAttribute.createDiffuse(Color.GREEN));
    Material blueMat = new Material(ColorAttribute.createDiffuse(Color.BLUE));
    // float x1, float y1, float z1, float x2, float y2, float z2
    // float capLength, float stemThickness, int divisions, int primitiveType, Material material, long attributes
    arrowX = modelBuilder.createArrow(0, 0, 0, 8, 0, 0, 0.08f, 0.25f, 8, GL20.GL_TRIANGLES, redMat, Usage.Position | Usage.Normal);
    arrowY = modelBuilder.createArrow(0, 0, 0, 0, 8, 0, 0.08f, 0.25f, 8, GL20.GL_TRIANGLES, greenMat, Usage.Position | Usage.Normal);
    arrowZ = modelBuilder.createArrow(0, 0, 0, 0, 0, 8, 0.08f, 0.25f, 8, GL20.GL_TRIANGLES, blueMat, Usage.Position | Usage.Normal);

    instance2 = new ModelInstance(arrowX);
    instance3 = new ModelInstance(arrowY);
    instance4 = new ModelInstance(arrowZ);


  }




  private Material getMat4() {
    Color cyan = new Color(0, 255, 255, 100);
    // Material mat = new Material(ColorAttribute.createDiffuse(greeny), new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
    Material mat = new Material(ColorAttribute.createDiffuse(cyan), new BlendingAttribute(true, 0.35f));
    return mat;
  }

  // R: I think these effects are typically combined with the diffuse color (they don't seem to work well by themselves)
  // One material can have any number of attributes, the texture attributes encapsulate
  private Material getMat3() {
    Color greeny = new Color(0, 1f, 0, 0.1f);
    Material mat = new Material(ColorAttribute.createAmbient(greeny));
    return mat;
  }

  private Material getMat2() {
    Color greeny = new Color(0, 1f, 0, 0.1f);
    Material mat = new Material(ColorAttribute.createSpecular(greeny));
    return mat;
  }

  private Material getMat1() {
    Color greeny = new Color(0, 1f, 0, 0.1f);
    Material mat = new Material(ColorAttribute.createEmissive(greeny));
    return mat;
  }




  @Override
  public void render () {
    Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    ScreenUtils.clear(1, 1, 1, 1);


    // some forums suggesting I needed these to achieve transparency (but obviously not)
    //    Gdx.gl20.glEnable(GL20.GL_BLEND);
    //    Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    //    Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);
    //    Gdx.gl20.glBlendEquation(GL20.GL_BLEND);


    modelBatch.begin(cam);
    modelBatch.render(instance, environment);
    modelBatch.render(instance2, environment);
    modelBatch.render(instance3, environment);
    modelBatch.render(instance4, environment);
    modelBatch.end();


    //    Gdx.gl20.glDisable(GL11.GL_BLEND);
    //    Gdx.gl20.glDisable(GL20.GL_TEXTURE_2D);


    //    shapeRenderer.setProjectionMatrix(cam.combined);
    //    shapeRenderer.begin(ShapeType.Line);
    //    shapeRenderer.setColor(0, 0, 0, 1);
    //    shapeRenderer.line(0, 0, 0, 5, 0, 0);
    //    shapeRenderer.end();


    // NOTE - combining shapeRenderer and modelBatch renderer creates some very strange effects
    // my guess is that one is not suppose to combine these
    //    Gdx.gl.glEnable(GL11.GL_BLEND);
    //    Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    //    shapeRenderer.setProjectionMatrix(cam.combined);
    //    shapeRenderer.begin(ShapeType.Filled);
    //    shapeRenderer.setColor(new Color(0, 1, 0, 0.5f));
    //    shapeRenderer.circle(0, 0, 10);
    //    shapeRenderer.end();
    //    Gdx.gl.glDisable(GL11.GL_BLEND);





    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }
  }


  @Override
  public void dispose () {
    modelBatch.dispose();
    cubeModel.dispose();
    arrowX.dispose();
    arrowY.dispose();
    arrowZ.dispose();


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



