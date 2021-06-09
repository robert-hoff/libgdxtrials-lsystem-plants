package hoffinc.gdxdesktop;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import hoffinc.gdxtrials.Trial01_Tutorial;
import hoffinc.gdxtrials.Trial02_CrapDemoGame;
import hoffinc.gdxtrials.Trial03_Rendering3DObject;
import hoffinc.gdxtrials.Trial04_AnalysingCubeMesh;
import hoffinc.gdxtrials.Trial05_TransparentCubeAndAxes;
import hoffinc.gdxtrials.Trial06_MeshRectangle;
import hoffinc.gdxtrials.Trial07_ShapeRenderRectangle;
import hoffinc.gdxtrials.Trial08_ImportConeArrow;
import hoffinc.gdxtrials.Trial09_MatrixTransforms;
import hoffinc.gdxtrials.Trial10_TurtleTesting;
import hoffinc.gdxtrials.Trial11_BranchingSystems;
import hoffinc.gdxtrials.Trial12_LeafShape;
import hoffinc.gdxtrials.Trial13_BranchDiameter;
import hoffinc.gdxtrials.Trial14_3DPlant;
import hoffinc.gdxtrials.Trial15_3DPlant_RandomVariation;
import hoffinc.gdxtrials.Trial16_FlowerShapes;
import hoffinc.gdxtrials.Trial17_Flower;
import hoffinc.gdxtrials.Trial18_Parameterized_LSystem;
import hoffinc.gdxtrials.Trial19_CrocusFlower;
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
 * using LWJGL3 graphics, see
 * https://gist.github.com/crykn/eb37cb4f7a03d006b3a0ecad27292a2d
 *
 *
 *
 *
 *
 */
public class DesktopLauncher {

  public static void main (String[] arg) {
    log.trace("L-System trials");

    String FILENAME = "app.auto.properties";
    File propFile = new File(FILENAME); // create prop-file if it doesn't exist
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
    // The last parameter is MSAA sampling (multi-sampling anti aliasing), set to 3 to solve aliasing effects
    config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);


    int RUN_TRIAL = 19;
    switch (RUN_TRIAL) {
      case  1: new Lwjgl3Application(new Trial01_Tutorial(), config); break;
      case  2: new Lwjgl3Application(new Trial02_CrapDemoGame(), config); break;
      case  3: new Lwjgl3Application(new Trial03_Rendering3DObject(), config); break;
      case  4: new Lwjgl3Application(new Trial04_AnalysingCubeMesh(), config); break;
      case  5: new Lwjgl3Application(new Trial05_TransparentCubeAndAxes(), config); break;
      case  6: new Lwjgl3Application(new Trial06_MeshRectangle(), config); break;
      case  7: new Lwjgl3Application(new Trial07_ShapeRenderRectangle(), config); break;
      case  8: new Lwjgl3Application(new Trial08_ImportConeArrow(), config); break;             // Shape for testing (imported from Blender)
      case  9: new Lwjgl3Application(new Trial09_MatrixTransforms(), config); break;
      case 10: new Lwjgl3Application(new Trial10_TurtleTesting(), config); break;               // 3D Hilbert curve
      case 11: new Lwjgl3Application(new Trial11_BranchingSystems(), config); break;            // 3D branching structures
      case 12: new Lwjgl3Application(new Trial12_LeafShape(), config); break;
      case 13: new Lwjgl3Application(new Trial13_BranchDiameter(), config); break;              // Ability to scale sub-components
      case 14: new Lwjgl3Application(new Trial14_3DPlant(), config); break;                     // 3D L-Systems plant
      case 15: new Lwjgl3Application(new Trial15_3DPlant_RandomVariation(), config); break;     // Plant with some random variations
      case 16: new Lwjgl3Application(new Trial16_FlowerShapes(), config); break;
      case 17: new Lwjgl3Application(new Trial17_Flower(), config); break;                      // 3D L-Systems flower
      case 18: new Lwjgl3Application(new Trial18_Parameterized_LSystem(), config); break;
      case 19: new Lwjgl3Application(new Trial19_CrocusFlower(), config); break;
    }


  }




  private static Logger log = LoggerFactory.getLogger(DesktopLauncher.class);


}









