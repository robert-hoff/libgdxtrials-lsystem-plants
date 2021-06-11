package hoffinc.gdxdesktop;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import hoffinc.gdxtrials1.Trial101_Tutorial;
import hoffinc.gdxtrials1.Trial102_CrapDemoGame;
import hoffinc.gdxtrials1.Trial103_Rendering3DObject;
import hoffinc.gdxtrials1.Trial104_AnalysingCubeMesh;
import hoffinc.gdxtrials1.Trial105_TransparentCubeAndAxes;
import hoffinc.gdxtrials1.Trial106_MeshRectangle;
import hoffinc.gdxtrials1.Trial107_ShapeRenderRectangle;
import hoffinc.gdxtrials1.Trial108_ImportTestShape;
import hoffinc.gdxtrials1.Trial109_MatrixTransforms;
import hoffinc.gdxtrials1.Trial110_TestShapeTransforms;
import hoffinc.gdxtrials1.Trial111_BuildingBasicShapes;
import hoffinc.gdxtrials2.Trial201_HilbertCurve;
import hoffinc.gdxtrials2.Trial202_BranchingSystems;
import hoffinc.gdxtrials2.Trial203_LeafShape;
import hoffinc.gdxtrials2.Trial204_BranchScaling;
import hoffinc.gdxtrials2.Trial205_3DPlant;
import hoffinc.gdxtrials2.Trial206_3DPlant_RandomVariation;
import hoffinc.gdxtrials2.Trial207_FlowerShapes;
import hoffinc.gdxtrials2.Trial208_Flower;
import hoffinc.gdxtrials2.Trial209_Parameterized_LSystem;
import hoffinc.gdxtrials2.Trial210_CrocusFlower;
import hoffinc.gdxtrials2.Trial211_CrocusContinuousGrowth;
import hoffinc.gdxtrials2.Trial212_PalmTreeAttempt;
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
    config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 3);


    int RUN_TRIAL = 212;
    switch (RUN_TRIAL) {
      case 101: new Lwjgl3Application(new Trial101_Tutorial(), config); break;
      case 102: new Lwjgl3Application(new Trial102_CrapDemoGame(), config); break;
      case 103: new Lwjgl3Application(new Trial103_Rendering3DObject(), config); break;
      case 104: new Lwjgl3Application(new Trial104_AnalysingCubeMesh(), config); break;
      case 105: new Lwjgl3Application(new Trial105_TransparentCubeAndAxes(), config); break;
      case 106: new Lwjgl3Application(new Trial106_MeshRectangle(), config); break;
      case 107: new Lwjgl3Application(new Trial107_ShapeRenderRectangle(), config); break;
      case 108: new Lwjgl3Application(new Trial108_ImportTestShape(), config); break;             // Shape for testing (imported from Blender)
      case 109: new Lwjgl3Application(new Trial109_MatrixTransforms(), config); break;
      case 110: new Lwjgl3Application(new Trial110_TestShapeTransforms(), config); break;
      case 111: new Lwjgl3Application(new Trial111_BuildingBasicShapes(), config); break;
      case 201: new Lwjgl3Application(new Trial201_HilbertCurve(), config); break;                // 3D Hilbert curve
      case 202: new Lwjgl3Application(new Trial202_BranchingSystems(), config); break;            // 3D branching structures
      case 203: new Lwjgl3Application(new Trial203_LeafShape(), config); break;
      case 204: new Lwjgl3Application(new Trial204_BranchScaling(), config); break;               // Ability to scale sub-components
      case 205: new Lwjgl3Application(new Trial205_3DPlant(), config); break;                     // 3D L-Systems plant
      case 206: new Lwjgl3Application(new Trial206_3DPlant_RandomVariation(), config); break;     // Plant with some random variations
      case 207: new Lwjgl3Application(new Trial207_FlowerShapes(), config); break;
      case 208: new Lwjgl3Application(new Trial208_Flower(), config); break;                      // 3D L-Systems flower
      case 209: new Lwjgl3Application(new Trial209_Parameterized_LSystem(), config); break;
      case 210: new Lwjgl3Application(new Trial210_CrocusFlower(), config); break;
      case 211: new Lwjgl3Application(new Trial211_CrocusContinuousGrowth(), config); break;      // Crocus flower continuous growth
      case 212: new Lwjgl3Application(new Trial212_PalmTreeAttempt(), config); break;
    }

  }


  private static Logger log = LoggerFactory.getLogger(DesktopLauncher.class);

}









