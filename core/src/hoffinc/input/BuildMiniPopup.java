package hoffinc.input;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import com.badlogic.gdx.graphics.PerspectiveCamera;


public class BuildMiniPopup {

  private List<String> names = new ArrayList<>();
  private List<ActionListener> listeners = new ArrayList<>();
  private PerspectiveCamera camera = null;

  public BuildMiniPopup() {}


  // R: performing camera operations from here seems like generally a bad idea
  //  public BuildMiniPopup(PerspectiveCamera camera) {
  //    this.camera = camera;
  //  }


  public void addListener(String name, ActionListener listener) {
    names.add(name);
    listeners.add(listener);
  }

  public MiniPopup getPopup() {
    MiniPopup popup = new MiniPopup();
    for (int i = 0; i < names.size(); i++) {
      String name = names.get(i);
      ActionListener listener = listeners.get(i);
      JMenuItem item;
      item = new JMenuItem(name);
      item.addActionListener(listener);
      popup.add(item);
    }
    return popup;
  }


  public class MiniPopup extends JPopupMenu {

    public MiniPopup() {
      JMenuItem showTips = new JMenuItem("Show viewport controls");
      showTips.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          System.out.println(MyGameState.helpful_tips);
        }
      });
      this.addPopupMenuListener(new MyPopupMenuListener());
      add(showTips);

      JMenuItem item;
      item = new JMenuItem("Toggle axes");
      item.addActionListener(new ToggleAxesListener());
      this.addPopupMenuListener(new MyPopupMenuListener());
      add(item);




      if (camera != null) {
        //        JMenuItem cameraItem1 = new JMenuItem("Show camera transforms");
        //        cameraItem1.addActionListener(new ActionListener() {
        //          @Override
        //          public void actionPerformed(ActionEvent e) {
        //            System.out.printf("%-20s %s \n", "camera up:", InspectData.strVector3(camera.up));
        //            System.out.printf("%-20s %s \n", "camera position:", InspectData.strVector3(camera.position));
        //            System.out.printf("%-20s %s \n", "camera dir:",  InspectData.strVector3(camera.direction));
        //          }
        //        });
        //        this.addPopupMenuListener(new MyPopupMenuListener());
        //        add(cameraItem1);

        // this doesn't work properly (camera transforms can not be set independently of the camera controller)
        //        JMenuItem cameraItem2 = new JMenuItem("Set camera target to origin");
        //        cameraItem2.addActionListener(new ActionListener() {
        //          @Override
        //          public void actionPerformed(ActionEvent e) {
        //            camera.up.set(0, 0, 1);
        //            camera.lookAt(new Vector3(0,0,0));
        //            camera.update();
        //          }
        //        });
        //        this.addPopupMenuListener(new MyPopupMenuListener());
        //        add(cameraItem2);
      }
    }


    @Override
    public void show(Component invoker, int mouseX, int mouseY) {
      super.show(invoker, mouseX, mouseY);
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

  }


}






