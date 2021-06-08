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


public class BuildMiniPopup {

  List<String> names = new ArrayList<>();
  List<ActionListener> listeners = new ArrayList<>();

  public BuildMiniPopup() {}

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


  public static class MiniPopup extends JPopupMenu {

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






