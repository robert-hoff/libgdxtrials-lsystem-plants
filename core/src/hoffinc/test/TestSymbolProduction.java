package hoffinc.lsystems.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hoffinc.lsystems.LSystemProduction;
import hoffinc.utils.LSystemBasicVersion;

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
    List<Character> symbols = LSystemBasicVersion.lSystemProduction(3, s, p);
    // System.err.println(symbols);

    List<Character> symbols_red = new ArrayList<>();
    for(char c : symbols){
      if (c!='P' && c!='I' && c!='S' && c!='E' && c!='R' && c!='C' && c!='D') {
        symbols_red.add(c);
      }
    }

    // System.err.println(symbols_red);
    LSystemBasicVersion.showSymbolsAsString(symbols_red);

  }


  static void strCode2() {
    LSystemProduction prod = new LSystemProduction();
    prod.addRule("rule1", "- rule2 - rule2 -");
    prod.addRule("rule2", "[^ rule3 ^]");
    prod.addRule("rule3", "rule1");
    prod.buildSymbols("rule1", 4);
    prod.showSymbols();
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

    prod.showSymbolString();


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





}









