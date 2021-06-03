package hoffinc.gdxdesktop;

import java.awt.Canvas;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import hoffinc.gdxtrials.Trial8_ImportConeArrow;
import hoffinc.input.MyInputProcessor;
import hoffinc.utils.ApplicationProp;

public class DesktopLauncher2 {

  public static void main(String[] args) {
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



    Canvas canvas = new Canvas();
    canvas.setSize(win_width, win_height);
    MyInputProcessor.canvas = canvas;


    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.width = win_width;
    config.height = win_height;
    config.samples = 3;
    new LwjglApplication(new Trial8_ImportConeArrow(), config, canvas);



    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.add( canvas );
    frame.setTitle("3D Viewer");
    frame.setLocationRelativeTo( null );
    frame.setLocation(win_x, win_y);
    // frame.setSize( config.width, config.height );
    frame.pack();
    frame.setVisible(true);



  }





}







