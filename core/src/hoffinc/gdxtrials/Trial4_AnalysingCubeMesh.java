package hoffinc.gdxtrials;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import hoffinc.utils.ApplicationProp;


/*
 *
 *
 *
 *
 *
 */
public class Trial4_AnalysingCubeMesh extends ApplicationAdapter {


  public Environment environment;
  public PerspectiveCamera cam;
  public CameraInputController camController;
  public ModelBatch modelBatch;
  public Model cubeModel1;
  public Model arrowX;
  public ModelInstance instance;
  public ModelInstance instance2;


  @Override
  public void create () {
    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.position.set(0f, 7f, 10f);
    cam.lookAt(0,0,0);
    cam.near = 1f;
    cam.far = 300f;
    cam.update();
    camController = new CameraInputController(cam);
    Gdx.input.setInputProcessor(camController);





    modelBatch = new ModelBatch();
    ModelBuilder modelBuilder = new ModelBuilder();

    Color greeny = new Color(0, 1f, 0, 0.1f);
    // cubeModel1 = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(greeny)), Usage.Position | Usage.Normal);

    cubeModel1 = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createAmbient(greeny)), Usage.Position | Usage.Normal);
    // cubeModel1 = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position);
    instance = new ModelInstance(cubeModel1);



    Material redMat = new Material(ColorAttribute.createDiffuse(Color.RED));

    // float x1, float y1, float z1, float x2, float y2, float z2
    // float capLength, float stemThickness, int divisions, int primitiveType, Material material, long attributes
    arrowX = modelBuilder.createArrow(0, 0, 0, 5, 0, 0, 0.9f, 0.3f, 6, GL20.GL_TRIANGLES, redMat, Usage.Position | Usage.Normal);
    instance2 = new ModelInstance(arrowX);


    // trial5();

    // trial4();

    // trial3();

    // this is a much simpler cube
    // trial2();

    // with both the Usage.Position and Usage.Normal attributes we get 24 vertices, 3 for each corver of the cube
    // trial1();


  }



  static void trial5() {
    ModelBuilder modelBuilder = new ModelBuilder();
    Model cubeModel = modelBuilder.createBox(
        5f,
        5f,
        5f,
        new Material( ColorAttribute.createDiffuse(Color.GREEN) ),
        Usage.Position | Usage.Normal);



    // System.err.println(cubeModel.materials.size);          // 1
    // System.err.println(cubeModel.meshes.size);             // 1
    // System.err.println(cubeModel.meshParts.size);          // 1
    // System.err.println(cubeModel.nodes.size);              // 1
    // System.err.println(cubeModel.animations.size);         // 0


    MeshPart meshPart = cubeModel.meshParts.get(0);
    // System.err.println(meshPart.id);                             // box                  String
    // System.err.println(meshPart.primitiveType);                  // same as GL20.GL_TRIANGLES
    // System.err.println(meshPart.radius);                         // 4.3301272, radius of the bounding sphere = sqrt(2.5^2+2.5^2+2.5^2)
    // System.err.println(meshPart.center);                         // (0.0,0.0,0.0)        Vector3
    // System.err.println(meshPart.mesh);                           // this gets the 24 vertex mesh (positions and normals)
  }




  // The simpler cube also has 36 indices, even though it only has 8 vertices
  static void trial4() {
    ModelBuilder modelBuilder = new ModelBuilder();
    Model cubeModel = modelBuilder.createBox(
        5f,
        5f,
        5f,
        new Material( ColorAttribute.createDiffuse(Color.GREEN) ),
        Usage.Position);
    Mesh cubeMesh = cubeModel.meshes.get(0);

    // There are 36 vertex indices
    // I take it this is because there are 2 triangle per face
    // 3 x 2 x 6 = 36
    System.err.println(cubeMesh.getNumIndices());

    short[] cubeIndices = new short[36];
    cubeMesh.getIndices(cubeIndices);
    for (int i = 0; i < 36; i+=3) {
      for (int j = 0; j <= 2; j++) {
        System.err.printf("%3d    ", cubeIndices[i+j]);
      }
      System.err.println();
    }

  }



  static void trial3() {
    ModelBuilder modelBuilder = new ModelBuilder();
    Model cubeModel = modelBuilder.createBox(
        5f,
        5f,
        5f,
        new Material( ColorAttribute.createDiffuse(Color.GREEN) ),
        Usage.Position | Usage.Normal);
    Mesh cubeMesh = cubeModel.meshes.get(0);


    // There are 36 vertex indices
    // I take it this is because there are 2 triangle per face
    // 3 x 2 x 6 = 36
    System.err.println(cubeMesh.getNumIndices());

    short[] cubeIndices = new short[36];
    cubeMesh.getIndices(cubeIndices);
    for (int i = 0; i < 36; i+=3) {
      for (int j = 0; j <= 2; j++) {
        System.err.printf("%3d    ", cubeIndices[i+j]);
      }
      System.err.println();
    }
  }



  static void trial2() {
    ModelBuilder modelBuilder = new ModelBuilder();
    Model cubeModel = modelBuilder.createBox(
        5f,
        5f,
        5f,
        new Material( ColorAttribute.createDiffuse(Color.GREEN) ),
        Usage.Position);
    Mesh cubeMesh = cubeModel.meshes.get(0);


    // returns 12
    // only 3 floats are used (4 bytes each)
    // System.err.println(cubeModel.meshes.get(0).getVertexSize());

    // returns 8
    // one vertex per corner of the cube
    // System.err.println(cubeModel.meshes.get(0).getNumVertices());

    float[] vertices = new float[24];
    cubeMesh.getVertices(vertices);
    for (int i = 0; i < 24; i+=3) {
      for (int j = 0; j <= 2; j++) {
        System.err.printf("%7.3f   ", vertices[i+j]);
      }
      System.err.println();
    }

  }




  static void trial1() {
    ModelBuilder modelBuilder = new ModelBuilder();
    Model cubeModel = modelBuilder.createBox(
        5f,
        5f,
        5f,
        new Material( ColorAttribute.createDiffuse(Color.GREEN) ),
        Usage.Position | Usage.Normal);         // without the Usage.Normal here the box will appear all in one colour
    Mesh cubeMesh = cubeModel.meshes.get(0);

    // returns 24
    // a Java float is 4 bytes => this suggests 6 floats per vertex (3 for position and 3 for normal)
    // System.err.println(cubeModel.meshes.get(0).getVertexSize());

    // returns 24
    // the number of vertices is also given as 24, but this is just coincidence
    // there appears to be 3 vertices per corner of the cube. So 8x3
    // System.err.println(cubeModel.meshes.get(0).getNumVertices());


    float[] vertices = new float[144];
    cubeMesh.getVertices(vertices);
    for (int i = 0; i < 144; i+=6) {
      for (int j = 0; j <= 5; j++) {
        System.err.printf("%7.3f   ", vertices[i+j]);
      }
      System.err.println();
    }
  }





  @Override
  public void render () {
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    ScreenUtils.clear(1, 1, 1, 1);
    modelBatch.begin(cam);
    modelBatch.render(instance, environment);
    modelBatch.render(instance2, environment);
    modelBatch.end();


    if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }
  }


  @Override
  public void dispose () {
    modelBatch.dispose();
    cubeModel1.dispose();
    arrowX.dispose();


    // save window x,y and window width,height
    // NOTE - The initial size of the window is set from the Desktop-launcher
    Lwjgl3Graphics lwjgl3 = (Lwjgl3Graphics) Gdx.graphics;
    int win_width = lwjgl3.getWidth();
    int win_height = lwjgl3.getHeight();
    int win_x = lwjgl3.getWindow().getPositionX();
    int win_y = lwjgl3.getWindow().getPositionY();

    String FILENAME = "app.auto.properties";
    ApplicationProp prop = new ApplicationProp(FILENAME);
    prop.addProperty("WIN_WIDTH", ""+win_width);
    prop.addProperty("WIN_HEIGHT", ""+win_height);
    prop.addProperty("WIN_X", ""+win_x);
    prop.addProperty("WIN_Y", ""+win_y);
    prop.saveToFile();
  }








  public static void showFloatArray(float[] a) {
    if (a.length == 0) {
      System.err.println("[]");
    } else {
      System.err.printf("[");
      for (int i = 0; i < a.length; i++) {
        System.err.printf("%7.3f", a[i]);
      }
      System.err.println("]");
    }
  }




}



