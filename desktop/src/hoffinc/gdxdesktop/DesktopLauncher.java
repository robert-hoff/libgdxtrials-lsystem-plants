package hoffinc.gdxdesktop;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import hoffinc.gdxtrials.Trial01_Tutorial;
import hoffinc.gdxtrials.Trial02_CrapDemoGame;
import hoffinc.gdxtrials.Trial03_Going3D;
import hoffinc.gdxtrials.Trial04_AnalysingCubeMesh;
import hoffinc.gdxtrials.Trial05_TransparentCubeAndAxes;
import hoffinc.gdxtrials.Trial07_ShapeRenderRectangle;
import hoffinc.gdxtrials.Trial08_ImportConeArrow;
import hoffinc.gdxtrials.Trial09_TurtleTesting;
import hoffinc.gdxtrials.Trial10_MatrixTransforms;
import hoffinc.gdxtrials.Trial11_BranchingSystems;
import hoffinc.gdxtrials.Trial12_LeafShape;
import hoffinc.gdxtrials.Trial13_BranchDiameter;
import hoffinc.gdxtrials.Trial14_3DPlant;
import hoffinc.utils.ApplicationProp;


/*
 *
 *
 * libGDX API
 * https://libgdx.badlogicgames.com/ci/nightlies/docs/api/
 *
 * lwjgl3 package API
 * http://javadox.com/com.badlogicgames.gdx/gdx-backend-lwjgl3/1.9.8/com/badlogic/gdx/backends/lwjgl3/package-summary.html
 *
 * switched to LWJGL3 graphics, see
 * https://gist.github.com/crykn/eb37cb4f7a03d006b3a0ecad27292a2d
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
    config.setTitle("3D Viewport");
    config.setWindowPosition(win_x, win_y);
    // R: set MSAA sampling (multi-sampling anti aliasing) to 2 or 3 to solve aliasing effects
    config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 3);
    new Lwjgl3Application(new Trial14_3DPlant(), config);

  }



  private static Logger log = LoggerFactory.getLogger(DesktopLauncher.class);




}


