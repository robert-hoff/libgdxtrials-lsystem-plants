package hoffinc.lsystems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LSystemProduction {


  private final int MAX_SIZE = 30000;
  List<String> symbols = null;
  public LSystemProduction() {}
  Map<String, List<String>> productionRule = new HashMap<>();

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


  public void showSymbols() {
    System.err.println(symbols.size());
    for(String s : symbols){
      System.err.println(s);
    }
  }

  public void showSymbolString() {
    System.err.println(getSymbolString());
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


}




