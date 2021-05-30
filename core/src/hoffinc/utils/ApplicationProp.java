package hoffinc.utils;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 *
 * ApplicationProp
 * will import as
 *
 *      import hoffexec.propfile.ApplicationProp
 *
 *
 *
 * in maven
 *
 *    <dependency>
 *        <groupId>hoffexec</groupId>
 *        <artifactId>prop-file</artifactId>
 *        <version>1.0.3</version>
 *    </dependency>
 *
 *
 */
public class ApplicationProp {

  private final String DIRECTORY = "./";
  private final String FILENAME;

  private Properties properties = new Properties();





  /**
   * ApplicationProp()
   * open file and load key,value pairs stored in file
   *
   */
  public ApplicationProp(String filename) {
    this.FILENAME = filename;
    log.trace("new ApplicationProp()");

    // open file
    InputStream input = null;
    try {
      input = new FileInputStream(qualifiedFilename());
    } catch (IOException e) {
      // e.printStackTrace();
      log.warn("couldn't find properties file");    // no properties to load (not an error)
      log.warn("expected file {}", qualifiedFilename());
      return;
    }

    // load all properties
    try {
      properties.load(input);
    } catch (IOException e1) {
      e1.printStackTrace();
      log.error("couldn't read properties from file");
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e2) {
          e2.printStackTrace();
          log.error("couldn't close file after read");
        }
      }
    }
  }

  public ApplicationProp() {
    this("app.properties");
  }


  private String qualifiedFilename() {
    return DIRECTORY+FILENAME;
  }


  /**
   * @return
   * property as a String or null if property does not exist
   */
  public String read(String key) {
    return properties.getProperty(key);
  }

  /**
   * @return
   * property as a String or @param default_str if property does not exist
   *
   * NOTE - this method also records the default_str
   * should a value not already exist
   *
   */
  public String read(String key, String default_str) {
    String value = properties.getProperty(key);
    if (value == null) {
      addProperty(key, default_str);
      value = default_str;
    }
    return value;
  }



  /**
   * @return
   * property as an Integer or null if property does not exist
   */
  public Integer readInt(String key) {
    String val_str = read(key);
    return val_str == null ? null : parseInt(val_str);
  }

  /**
   * @return
   * property as an Integer or @param default_str as an Integer
   */
  public Integer readInt(String key, String default_str) {
    String val_str = read(key, default_str);
    return parseInt(val_str);
  }

  public Integer readInt(String key, Integer default_int) {
    return readInt(key, ""+default_int);
  }



  private Integer parseInt(String val_str) {
    Integer integer_value = null;
    try {
      integer_value = Integer.parseInt(val_str);
    } catch (Exception e) {
      log.error("");
      e.printStackTrace();
      throw new RuntimeException("couldn't parse val_str: " + val_str);
    }
    return integer_value;
  }



  public Float readFloat(String key) {
    String val_str = read(key);
    Float val = null;
    try {
      val = Float.parseFloat(val_str);
    } catch (Exception e) {
      return null;
    }
    return val;
  }
  public Float readFloat(String key, float val_default) {
    Float val = readFloat(key);
    return val != null ? val : val_default;
  }



  public Double readDouble(String key) {
    String val_str = read(key);
    Double val = null;
    try {
      val = Double.parseDouble(val_str);
    } catch (Exception e) {
      return null;
    }
    return val;
  }



  public Boolean readBoolean(String name) {
    String val_str = read(name);
    if (val_str == null) {return null;}
    Boolean val = null;
    try {
      val = Boolean.parseBoolean(val_str);
    } catch (Exception e) {
      return null;
    }
    return val;
  }
  public Boolean readBoolean(String name, boolean val_default) {
    Boolean val = readBoolean(name);
    return val != null ? val : val_default;
  }

  /**
   * add a key,value pair
   */
  public void addProperty(String key, String value) {
    properties.setProperty(key, value);
  }

  /**
   * remove a key,value pair
   */
  public void removeProperty(String key) {
    String removed_value = (String) properties.remove(key);
    if (removed_value == null) {
      log.warn("removeProperty(..) key: {} did not exist", key);
    }
  }

  /**
   * save all properties to file
   */
  public void saveToFile() {
    OutputStream output = null;
    File file = new File(DIRECTORY);        // works for single-level dir
    file.mkdir();

    try {
      output = new FileOutputStream(qualifiedFilename());
      properties.store(output, null); // -- saves all properties
    } catch (IOException e1) {
      e1.printStackTrace();
      log.error("couldn't create output stream");
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e2) {
          e2.printStackTrace();
          log.error("couldn't close file after save");
        }
      }
    }
  }


  public void showProperties() {
    System.out.println("PropertiesFile.showProperties()");
    for (Entry<Object, Object> entry: properties.entrySet()) {
      String key = (String) entry.getKey();
      String val = (String) entry.getValue();
      System.out.printf("%10s %10s \n", key, val);
    }
  }


  private static Logger log = LoggerFactory.getLogger(ApplicationProp.class);





  public static void main(String[] args) {
    log.info("running PropertiesFile tests");
    testBooleanDefault();
    testDefaultProperty();
  }



  /**
   * if the param doesn't exist prints
   *
   *  null
   *  false
   *  true
   *
   */
  public static void testBooleanDefault() {
    ApplicationProp properties_file = new ApplicationProp();
    Boolean boolean_property1 = properties_file.readBoolean("hello");
    Boolean boolean_property2 = properties_file.readBoolean("hello", false);
    Boolean boolean_property3 = properties_file.readBoolean("hello", true);
    System.out.println(boolean_property1);
    System.out.println(boolean_property2);
    System.out.println(boolean_property3);

    properties_file.addProperty("hello", "true");
    properties_file.saveToFile();
  }

  /**
   * prints
   *    22
   *    44
   */
  public static void testDefaultProperty() {
    ApplicationProp properties_file = new ApplicationProp();
    Integer property1 = properties_file.readInt("hi","11");
    properties_file.addProperty("hi", "22");
    Integer property2 = properties_file.readInt("hi","11");
    System.out.printf("property1 = %d \n", property1 * 2);    // numeric type
    System.out.printf("property2 = %d \n", property2 * 2);
  }





}
































