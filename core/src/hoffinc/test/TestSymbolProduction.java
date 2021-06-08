package hoffinc.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestSymbolProduction {




  public static void main(String[] args) {

    // R: The old sytem and new system gives the same result
    strCode3_OldSystem();
    // strCode2();
    strCode1();
  }


  static void strCode3_OldSystem() {
    Map<Character, String> p = new HashMap<>();
    String s = "P";
    p.put('P', "I+[P+R]−−//[−−E]I[++E]−[PR]++PR");
    p.put('I', "FS[//&&E][//^^E]FS");
    p.put('S', "SFS");
    p.put('E', "L");
    p.put('R', "[&&&C/D////D////D////D////D]");
    p.put('C', "FF");
    p.put('D', "[^F][&&&&W]");
    List<Character> symbols = lSystemProduction(3, s, p);
    // System.err.println(symbols);

    List<Character> symbols_red = new ArrayList<>();
    for(char c : symbols){
      if (c!='P' && c!='I' && c!='S' && c!='E' && c!='R' && c!='C' && c!='D') {
        symbols_red.add(c);
      }
    }

    System.err.println(LSystemProduction.getSymbolAsString(symbols_red));
  }


  static void strCode2() {
    LSystemProduction prod = new LSystemProduction();
    prod.addRule("rule1", "- rule2 - rule2 -");
    prod.addRule("rule2", "[^ rule3 ^]");
    prod.addRule("rule3", "rule1");
    prod.buildSymbols("rule1", 4);
    System.err.println(prod.getSymbolString());
  }


  static void strCode1() {

    // reserve lowercase letters for production names, separate names and actions by spaces
    String plant = "internode +[ plant + flower ]--//[-- leaf | internode [++ leaf ]-[ plant flower ]++ plant flower";
    String internode = "F seg [//&& leaf ][//^^ leaf ]F seg";
    String seg = "seg F seg";
    String leaf = "L";
    String flower = "[&&& pedicel / wedge //// wedge //// wedge //// wedge //// wedge ]";
    String pedicel = "FF";
    String wedge = "[^F][&&&&W]";

    LSystemProduction prod = new LSystemProduction();
    prod.addRule("plant", plant);
    prod.addRule("internode", internode);
    prod.addRule("seg", seg);
    prod.addRule("leaf", leaf);
    prod.addRule("flower", flower);
    prod.addRule("pedicel", pedicel);
    prod.addRule("wedge", wedge);

    prod.buildSymbols("plant", 3);
    // prod.showSymbols();
    // prod.showSymbolString();

    System.err.println(prod.getSymbolString());


  }




  static void listInsert() {

    List<Integer> list = new ArrayList<>();

    list.add(1);
    list.add(2);
    list.add(3);
    list.add(4);


    List<Integer> list2= new ArrayList<>();
    list2.add(5);
    list2.add(6);
    list2.add(7);

    System.err.println(list);

    // replaces entities at index 1
    list.remove(1);
    list.addAll(1, list2);
    System.err.println(list);
    // list.addAll(4, list2);
    // list2.remove(2);
  }





  /*
   * E.g.
   *
   *    Map<Character, String> p = new HashMap<>();
   *    String s = "X";
   *    p.put('X', "F[+X]F[-X]+X");
   *    p.put('F', "FF");
   *    List<Character> symbols = LSystemBasicVersion.lSystemProduction(2, s, p);
   *
   * Produces
   *
   *    FF[+F[+X]F[-X]+X]FF[-F[+X]F[-X]+X]+F[+X]F[-X]+X
   *
   *
   *
   */
  private static List<Character> lSystemProduction(int n, String s, Map<Character,String> p) {
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



  private static class LSystemProduction {

    private final int MAX_SIZE = 30000;
    private List<String> symbols = null;
    private Map<String, List<String>> productionRule = new HashMap<>();

    public LSystemProduction() {}


    // String plant = "internode +[ plant + flower ]--//[-- leaf | internode [++leaf ]-[ plant flower ]++ plant flower";
    public void addRule(String name, String rule) {
      List<String> strings = new ArrayList<>();
      String[] strs = rule.trim().split("\\s+");
      for(String s : strs){
        strings.add(s);
      }
      productionRule.put(name, strings);
    }


    public void buildSymbols(String seed, int n) {
      symbols = new ArrayList<>();
      if (n<1) {
        throw new RuntimeException("error! n less than 1");
      }
      List<String> start = productionRule.get(seed);
      symbols.addAll(start);
      if (start == null) {
        throw new RuntimeException("no production rule for this name: "+seed);
      }
      for (int i = 2; i <= n; i++) {
        for (int j = symbols.size()-1; j >=0; j--) {
          String token = symbols.get(j);
          if (token.matches("[a-z0-9]+")) {
            List<String> prod = productionRule.get(token);
            if (prod == null) {
              throw new RuntimeException("no production rule for this name: "+token);
            }
            symbols.remove(j);
            symbols.addAll(j, prod);
          }
          if (symbols.size()>MAX_SIZE) {
            throw new RuntimeException("production is too large!");
          }
        }
      }
    }


    public String getSymbolString() {
      StringBuilder symbol_str = new StringBuilder();
      for(String s : symbols){
        // System.err.println(s);
        if (productionRule.get(s) == null) {
          symbol_str.append(s);
        }
      }
      return symbol_str.toString();
    }



    public static String getSymbolAsString(List<Character> symbols) {
      String symbols_str = "";
      for (Character c : symbols) {
        symbols_str += c;
      }
      return symbols_str;
    }


  }





}









