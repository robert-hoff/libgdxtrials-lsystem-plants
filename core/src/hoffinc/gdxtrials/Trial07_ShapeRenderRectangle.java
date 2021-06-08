package hoffinc.gdxtrials;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;
import hoffinc.utils.ApplicationProp;

/*
 * Render a rectangle ouline with ShapeRenderer. Note the Shaperenderer will not usually work well
 * together with modelBatch renderer
 *
 */
public class Trial07_ShapeRenderRectangle extends ApplicationAdapter {


  private Environment environment;
  private PerspectiveCamera cam;
  private CameraInputController camController;
  private ShapeRenderer shapeRenderer;


  @Override
  public void create () {
    setTitle("Rectangle outline with ShapeRenderer");

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
  }


  @Override
  public void render () {
    Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    ScreenUtils.clear(1, 1, 1, 1);
    shapeRenderer.setColor(Color.BLACK);
    shapeRenderer.setProjectionMatrix(cam.combined);
    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.rect(0,0,20,20);
    shapeRenderer.end();

    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }
  }


  @Override
  public void dispose () {

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


  static private void setTitle(String title) {
    try {
      ((Lwjgl3Graphics) Gdx.graphics).getWindow().setTitle(title);
    } catch (Exception e) {}
  }

}











