package hoffinc.input;

import javax.swing.JWindow;


public class MyGameState {

  public static volatile BuildMiniPopup miniPopup = new BuildMiniPopup();
  public static volatile JWindow jwin = null;
  public static volatile boolean show_axes = true;
  public static volatile boolean app_starting = true;
  public static volatile boolean ready = false;
  public static volatile boolean reload = false;

  // used previously
  public static volatile boolean loading = true;


  public static volatile String helpful_tips = ""+
      "Leftmouse                        orbit viewport\n"+
      "Shift+Leftmouse                  pan viewport\n"+
      "Scrollwheel                      zoom in/out\\n"+
      "Esc                              quit\n";



}





