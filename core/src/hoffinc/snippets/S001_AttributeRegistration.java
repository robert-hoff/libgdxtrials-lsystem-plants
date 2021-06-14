package hoffinc.snippets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;


/*
 *
 * The attribute ColorAttribute maintains several long id values for its set of attributes,
 * therefore if extending ColorAttribute its mask needs to be updated for it to recognise a new type
 *
 * If extending Attribute there is no mask but its id needs to be maintained, here as
 *
 *          public final static long DiffuseUV
 *
 *
 *
 *
 *
 */
public class S001_AttributeRegistration {



  public static void main(String[] args) {
    trial4();
    // trial3();
    // trial2();
    // trial1();
  }


  static void trial4() {
    Color colorU = new Color();
    Color colorV = new Color();

    // NOTE - if this is called then my_attr.type below will have a very different value!!!
    ColorAttribute.createDiffuse(new Color(0xff000000));

    DoubleColorAttribute my_attr = new DoubleColorAttribute(DoubleColorAttribute.DiffuseUV, colorU, colorV);
    System.err.println(my_attr.type);
  }


  static void trial3() {
    TestColorAttribute2 myattr = new TestColorAttribute2(TestColorAttribute2.Attrib,1,1,1,1);
    myattr.showMask();
  }

  static void trial2() {
    new TestClass();
    new TestClass();
    new TestClass();
  }

  static void trial1() {
    TestColorAttribute attr = new TestColorAttribute(TestColorAttribute.DiffuseU, 1, 1, 1, 1);
  }


  private static class TestClass {
    static {
      System.err.println("this is run once");
    }
  }


  private static class TestColorAttribute2 extends ColorAttribute {
    public final static String AttribAlias = "diffuseUColor";
    public final static long Attrib = register(AttribAlias);

    // NOTE - if the mask is not registered it will be flagged as 'Invalid type'
    // when calling the constructor
    //
    //          new TestColorAttribute(..)
    //
    // but this is not the case for DoubleColorAttribute which
    // extends type type `Attribute` and not `ColorAttribute`
    //
    //
    //
    //
    static {
      Mask = Mask | Attrib;
    }
    public TestColorAttribute2(long type, float r, float g, float b, float a) {
      super(type, r, g, b, a);
    }
    public void showMask() {
      System.err.println(Mask);
    }
  }


  private static class TestColorAttribute extends ColorAttribute {
    public final static String DiffuseUAlias = "diffuseUColor";
    public final static long DiffuseU = register(DiffuseUAlias);
    public final static String DiffuseVAlias = "diffuseVColor";
    public final static long DiffuseV = register(DiffuseVAlias);

    static {
      // updates ColorAttribute.Mask property
      Mask = Mask | DiffuseU | DiffuseV;
    }

    public TestColorAttribute (long type, float r, float g, float b, float a) {
      super(type, r, g, b, a);

      // these are powers of 2, e.g.
      // 128 and 256
      // System.err.println(DiffuseV);
      // System.err.println(DiffuseU);
    }
  }

  /*
   *
   * Q. why doesn't DoubleColorAttribute need to register its mask like TestColorAttribute ??
   *
   *
   */
  private static class DoubleColorAttribute extends Attribute {
    public final static String DiffuseUVAlias = "diffuseUVColor";
    public final static long DiffuseUV = register(DiffuseUVAlias);
    public final Color color1 = new Color();
    public final Color color2 = new Color();

    public DoubleColorAttribute (long type, Color c1, Color c2) {
      super(type);
      color1.set(c1);
      color2.set(c2);
    }

    @Override
    public Attribute copy () {
      return new DoubleColorAttribute(type, color1, color2);
    }

    @Override
    protected boolean equals (Attribute other) {
      DoubleColorAttribute attr = (DoubleColorAttribute)other;
      return type == other.type && color1.equals(attr.color1) && color2.equals(attr.color2);
    }

    @Override
    public int compareTo (Attribute other) {
      if (type != other.type) {
        return (int) (type - other.type);
      }
      DoubleColorAttribute attr = (DoubleColorAttribute) other;
      return color1.equals(attr.color1) ? attr.color2.toIntBits()-color2.toIntBits() : attr.color1.toIntBits()-color1.toIntBits();
    }
  }

}









