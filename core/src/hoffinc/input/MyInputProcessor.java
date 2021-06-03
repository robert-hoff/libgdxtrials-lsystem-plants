package hoffinc.input;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;


public class MyInputProcessor implements InputProcessor {


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

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    // System.err.println("touchDragged");
    // returning true here will prevent camera panning

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



  private class MiniPopup extends JPopupMenu {
    public MiniPopup() {
      JMenuItem item;
      item = new JMenuItem("Toggle Axes");
      item.addActionListener(new ToggleAxesListener());
      this.addPopupMenuListener(new MyPopupMenuListener());
      add(item);
    }

    @Override
    public void show(Component invoker, int mouseX, int mouseY) {
      super.show(invoker, mouseX, mouseY);
      // assignScaleListener.setXY(mouseX, mouseY);
    }

    class ToggleAxesListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
        MyGameState.show_axes = !MyGameState.show_axes;
        MyGameState.loading = true;
      }
    }

    class MyPopupMenuListener implements PopupMenuListener {
      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        if (MyGameState.jwin != null) {
          MyGameState.jwin.dispose();
          MyGameState.jwin = null;
        }
      }
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {}
    }

  } // end of MiniPopup




  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {

    //    if (button == 1) {
    //      Lwjgl3Graphics lwjgl3 = (Lwjgl3Graphics) Gdx.graphics;
    //      Lwjgl3Window window = lwjgl3.getWindow();
    //      if (MyGameState.jwin == null) {
    //        MyGameState.jwin = new JWindow();
    //        MyGameState.jwin.setLocation(window.getPositionX()+screenX, window.getPositionY()+screenY);
    //        MyGameState.jwin.setVisible(true);
    //        new MiniPopup().show(MyGameState.jwin, 0, 0);
    //      }
    //      return true;
    //    }

    if (button == 0 && MyGameState.jwin != null) {
      MyGameState.jwin.dispose();
      MyGameState.jwin = null;
    }

    return false;
  }




  private static Logger log = LoggerFactory.getLogger(MyInputProcessor.class);

}












