package hoffinc.input;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;


public class MyInputProcessor implements InputProcessor {

  private Map<Integer, MyEventListener> customKeyDownEvents = new HashMap<>();

  public MyInputProcessor() {
    log.trace("MyInputProcessor()");
  }


  public void registerKeyDownEvent(int keycode, MyEventListener eventListener) {
    customKeyDownEvents.put(keycode, eventListener);
  }

  @Override
  public boolean keyDown(int keycode) {
    MyEventListener eventListener = customKeyDownEvents.get(keycode);
    if (eventListener != null) {
      eventListener.triggerEvent();
      return true;
    }
    return false;
  }


  @Override
  public boolean keyUp(int keycode) {
    // System.err.println("up: "+keycode);
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



  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {

    if (button == 1) {
      Lwjgl3Graphics lwjgl3 = (Lwjgl3Graphics) Gdx.graphics;
      Lwjgl3Window window = lwjgl3.getWindow();
      if (MyGameState.jwin == null) {
        MyGameState.jwin = new JWindow();
        MyGameState.jwin.setLocation(window.getPositionX()+screenX, window.getPositionY()+screenY);
        MyGameState.jwin.setVisible(true);
        MyGameState.miniPopup.getPopup().show(MyGameState.jwin, 0, 0);
      } else {
        MyGameState.jwin.dispose();
        MyGameState.jwin = null;
      }
      return true;
    }

    if (button == 0 && MyGameState.jwin != null) {
      MyGameState.jwin.dispose();
      MyGameState.jwin = null;
    }

    return false;
  }



  private static Logger log = LoggerFactory.getLogger(MyInputProcessor.class);

}









