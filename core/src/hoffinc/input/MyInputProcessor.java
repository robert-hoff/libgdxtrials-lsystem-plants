package hoffinc.input;

import java.awt.Canvas;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.InputProcessor;



public class MyInputProcessor implements InputProcessor {


  public static Canvas canvas;


  public static int[] getWindowXY() {
    int[] xy = {100,100};
    try {
      java.awt.Point p = canvas.getParent().getParent().getParent().getParent().getLocation();
      xy[0] = p.x;
      xy[1] = p.y;
    } catch (Exception e) {
      log.warn("couldn't get window xy");
    }
    return xy;
  }



  @Override
  public boolean keyDown(int keycode) {
    // System.err.println(keycode);

    return false;
  }




  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }



  private class MiniPopup extends JPopupMenu {
    public MiniPopup() {
      JMenuItem item;
      item = new JMenuItem("Toggle Axes");
      // item.addActionListener(new ToggleAxesListener());
      add(item);
    }

    @Override
    public void show(Component invoker, int mouseX, int mouseY) {
      super.show(invoker, mouseX, mouseY);
      // assignScaleListener.setXY(mouseX, mouseY);
    }

  } // end of MiniPopup




  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {


    if (button == 1) {
      // System.err.println(screenX);
      // System.err.println(screenY);


      // Lwjgl3Graphics lwjgl3 = (Lwjgl3Graphics) Gdx.graphics;
      // Lwjgl3Window window = lwjgl3.getWindow();

      // new MiniPopup().show(canvas, screenX, screenY);

      JFrame frame = new JFrame();
      // frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      // frame.add( canvas );
      frame.setTitle("My Dialogue");
      frame.setLocationRelativeTo( null );
      frame.setLocation(100,100);
      frame.setSize(100,100);
      frame.pack();
      frame.setVisible(true);



      return true;
    }

    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {

    //    System.err.println("------------------");
    //    System.err.println(amountX);
    //    System.err.println(amountY);

    return false;
  }






  private static Logger log = LoggerFactory.getLogger(MyInputProcessor.class);

}












