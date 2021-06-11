package hoffinc.utils;

import java.util.Random;


public class FloatMaths {

  public static float sin(double angle_radians) {
    return sin((float) angle_radians);
  }

  public static float sin(float angle_radians) {
    return (float) Math.sin(angle_radians);
  }


  public static Random rand = new Random();
  public static float randomNum() {
    return rand.nextFloat();
  }


}









