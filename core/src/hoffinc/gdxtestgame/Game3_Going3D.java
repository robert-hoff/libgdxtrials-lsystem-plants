package hoffinc.gdxtestgame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.ScreenUtils;

import hoffinc.utils.ApplicationProp;



/*
 *
 * libGDX API
 * ----------
 * https://libgdx.badlogicgames.com/ci/nightlies/docs/api/
 *
 *
 * com.badlogic.gdx.Application
 * ----------------------------
 * This class seems to be kind of a big deal
 *
 *
 * com.badlogic.gdx.Gdx
 * --------------------
 * This class has available various state that can be accessed through its static members
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class Game3_Going3D extends ApplicationAdapter {


  public PerspectiveCamera cam;

  @Override
  public void create () {
    log.info("starting app");

    cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.position.set(10f, 10f, 10f);
    cam.lookAt(0,0,0);
    cam.near = 1f;
    cam.far = 300f;
    cam.update();
  }



  @Override
  public void render () {
    ScreenUtils.clear(1, 1, 1, 1);




    // application detects Escape is pressed, prints"close application" multiple times

    // NOTE - the use of a 'Gdx' object making static instances available for checking input
    //

    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }

  }


  @Override
  public void dispose () {
    Lwjgl3Graphics lwjgl3 = (Lwjgl3Graphics) Gdx.graphics;
    int win_width = lwjgl3.getWidth();
    int win_height = lwjgl3.getHeight();
    int win_x = lwjgl3.getWindow().getPositionX();
    int win_y = lwjgl3.getWindow().getPositionY();

    // save window x,y and window width,height
    // NOTE The initial size of the window is set from the Desktop-launcher
    String FILENAME = "app.auto.properties";
    ApplicationProp prop = new ApplicationProp(FILENAME);

    prop.addProperty("WIN_WIDTH", ""+win_width);
    prop.addProperty("WIN_HEIGHT", ""+win_height);
    prop.addProperty("WIN_X", ""+win_x);
    prop.addProperty("WIN_Y", ""+win_y);
    prop.saveToFile();
  }



  private static Logger log = LoggerFactory.getLogger(Game3_Going3D.class);

}











