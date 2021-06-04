package hoffinc.lsystems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LSystem {


  /*
   * E.g.
   *
   *    Map<Character, String> p = new HashMap<>();
   *    String s = "X";
   *    p.put('X', "F[+X]F[-X]+X");
   *    p.put('F', "FF");
   *    List<Character> symbols = LSystem.lSystemProduction(2, s, p);
   *
   * Produces
   *
   *    FF[+F[+X]F[-X]+X]FF[-F[+X]F[-X]+X]+F[+X]F[-X]+X
   *
   *
   *
   */
  public static List<Character> lSystemProduction(int n, String s, Map<Character,String> p) {
    List<Character> symbols = new ArrayList<>();
    for(char c : s.toCharArray()){
      symbols.add(c);
    }
    for (int i = 1; i <= n; i++) {
      List<Character> nextSymbols = new ArrayList<>();
      for(char c : symbols){
        String p_rule = p.get(c);
        if (p_rule != null) {
          for(char c_prod : p_rule.toCharArray()){
            nextSymbols.add(c_prod);
          }
        } else {
          nextSymbols.add(c);
        }
      }
      symbols = nextSymbols;
    }
    return symbols;
  }



  public static List<Character> lSystemConvertString(String s) {
    List<Character> symbols = new ArrayList<>();
    for(char c : s.toCharArray()){
      symbols.add(c);
    }
    return symbols;
  }




}





