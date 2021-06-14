package hoffinc.gdxtrials1;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import hoffinc.gdxshaders.Shader1_TestShader;
import hoffinc.gdxshaders.Shader2_TestShader2;
import hoffinc.gdxshaders.Shader2_TestShader2.DoubleColorAttribute;
import hoffinc.input.MyGameState;
import hoffinc.models.BasicShapes;
import hoffinc.utils.ApplicationProp;

/*
 * See
 * https://xoppa.github.io/blog/using-materials-with-libgdx/
 *
 *
 */
public class Trial113_ShaderTest2 implements ApplicationListener {

  public PerspectiveCamera cam;
  public CameraInputController camController;
  public Shader shader;
  public Model sphereModel;
  public Array<ModelInstance> instances = new Array<ModelInstance>();
  public ModelBatch modelBatch;


  @Override
  public void create () {
    cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.position.set(0f,8f,8f);
    cam.lookAt(0,0,0);
    cam.near = 1f;
    cam.far = 300f;
    cam.update();

    camController = new CameraInputController(cam);
    Gdx.input.setInputProcessor(camController);

    ModelBuilder modelBuilder = new ModelBuilder();

    Material mat = BasicShapes.getMaterial(0xff0000);
    long attrib = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
    sphereModel = modelBuilder.createSphere(2f, 2f, 2f, 20, 20, mat, attrib);
    Color colorU = new Color();
    Color colorV = new Color();

    for (float x=-5; x<5.01f; x+=2) {
      for (float z=-5; z<5.01f; z+=2) {
        ModelInstance instance = new ModelInstance(sphereModel, x, 0, z); // <-- a new ModelInstance at (x,y,z)
        //        colorU.set(   (x+5)/10, 1-(z+5)/10,          0,   1); // r,g,b,a
        //        colorV.set( 1-(x+5)/10,          0,   (z+5)/10,   1);
        //        DoubleColorAttribute my_attr = new DoubleColorAttribute(DoubleColorAttribute.DiffuseUV, colorU, colorV);
        //        instance.materials.get(0).set(my_attr);

        instance.userData = new Color((x+5f)/10f, (z+5f)/10f, 0, 1);
        instances.add(instance);
      }
    }

    //    shader = new Shader1_TestShader();
    shader = new Shader2_TestShader2();
    shader.init();
    modelBatch = new ModelBatch();
  }


  @Override
  public void render () {
    camController.update();

    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    // R: this looks more like the approach used in previous tutorials, but instead of passing
    // environment like so
    //
    //                modelBatch.render(instances, environment);
    //
    // we have
    //
    //                modelBatch.render(instance, shader);
    //
    // where shader is a custom class implementing gdx.graphics.g3d.Shader (Shader2_TestShader2.java).
    // my custom shader class sets up the ShaderProgram (gdx.graphics.glutils.ShaderProgram)
    //
    //
    //
    //
    //
    modelBatch.begin(cam);
    for (ModelInstance instance : instances) {
      modelBatch.render(instance, shader);
    }
    modelBatch.end();


    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }
  }


  @Override
  public void dispose () {
    shader.dispose();
    sphereModel.dispose();
    modelBatch.dispose();

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


  @Override
  public void resize(int width, int height) {}
  @Override
  public void pause() {}
  @Override
  public void resume() {}

}









