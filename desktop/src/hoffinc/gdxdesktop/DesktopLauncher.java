package hoffinc.gdxdesktop;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import hoffinc.gdxtrials.Trial1_Tutorial;
import hoffinc.gdxtrials.Trial2_CrapDemoGame;
import hoffinc.gdxtrials.Trial3_Going3D;
import hoffinc.gdxtrials.Trial4_AnalysingCubeMesh;
import hoffinc.gdxtrials.Trial5_TransparentCubeAndAxes;
import hoffinc.gdxtrials.Trial7_ShapeRenderRectangle;
import hoffinc.gdxtrials.Trial8_ImportConeArrow;
import hoffinc.utils.ApplicationProp;

/*
 *
 * libGDX API
 * https://libgdx.badlogicgames.com/ci/nightlies/docs/api/
 *
 * switched to LWJGL3 graphics, see
 * https://gist.github.com/crykn/eb37cb4f7a03d006b3a0ecad27292a2d
 *
 *
 *
 *
 *
 *
 */
public class DesktopLauncher {

  public static void main (String[] arg) {
    log.trace("Test Game, desktop launch");

    String FILENAME = "app.auto.properties";

    // create prop-file if it doesn't exist
    File propFile = new File(FILENAME);
    try {
      propFile.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException("can't create properties file!");
    }


    ApplicationProp prop = new ApplicationProp(FILENAME);
    int win_width = prop.readInt("WIN_WIDTH", 700);
    int win_height = prop.readInt("WIN_HEIGHT", 500);
    int win_x = prop.readInt("WIN_X", 200);
    int win_y = prop.readInt("WIN_Y", 200);


    Lwjgl3ApplicationConfiguration  config = new Lwjgl3ApplicationConfiguration ();
    config.setWindowedMode(win_width, win_height);
    config.setTitle("My 3D Game");
    config.setWindowPosition(win_x, win_y);
    new Lwjgl3Application(new Trial8_ImportConeArrow(), config);

  }



  private static Logger log = LoggerFactory.getLogger(DesktopLauncher.class);




}











