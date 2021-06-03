package hoffinc.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.Matrix4;

public class AxesModel {


  private static float AXIS_LINE = 3f;
  private static float AXIS_DIAM = 0.05f;
  private static float ARROW_HEIGHT = 0.15f;
  private static float ARROW_DIAM = 0.1f;



  public static Model buildAxes() {
    Material redMat = new Material(ColorAttribute.createDiffuse(Color.RED));
    Material greenMat = new Material(ColorAttribute.createDiffuse(Color.GREEN));
    Material blueMat = new Material(ColorAttribute.createDiffuse(Color.BLUE));

    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();

    // x-axis
    MeshPartBuilder xAxisLine = modelBuilder.part("x_axis_line", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,redMat);
    xAxisLine.setVertexTransform(new Matrix4().translate(AXIS_LINE/2,0,0).rotate(0,0,1,90));
    CylinderShapeBuilder.build(xAxisLine, AXIS_DIAM, AXIS_LINE, AXIS_DIAM, 8);
    MeshPartBuilder xAxisArrow = modelBuilder.part("x_axis_arrow", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,redMat);
    xAxisLine.setVertexTransform(new Matrix4().translate(ARROW_HEIGHT/2+AXIS_LINE,0,0).rotate(0,0,1,-90));
    ConeShapeBuilder.build(xAxisArrow, ARROW_DIAM, ARROW_HEIGHT, ARROW_DIAM, 10);

    // y-axis
    MeshPartBuilder yAxisLine = modelBuilder.part("y_axis_line", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,greenMat);
    yAxisLine.setVertexTransform(new Matrix4().translate(0,AXIS_LINE/2,0));
    CylinderShapeBuilder.build(yAxisLine, AXIS_DIAM, AXIS_LINE, AXIS_DIAM, 8);
    MeshPartBuilder yAxisArrow = modelBuilder.part("y_axis_line", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,greenMat);
    yAxisArrow.setVertexTransform(new Matrix4().translate(0,ARROW_HEIGHT/2+AXIS_LINE,0));
    ConeShapeBuilder.build(yAxisArrow, ARROW_DIAM, ARROW_HEIGHT, ARROW_DIAM, 10);

    // z-axis
    MeshPartBuilder zAxisLine = modelBuilder.part("z_axis_line", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,blueMat);
    zAxisLine.setVertexTransform(new Matrix4().translate(0,0,AXIS_LINE/2).rotate(1,0,0,90));
    CylinderShapeBuilder.build(zAxisLine, AXIS_DIAM, AXIS_LINE, AXIS_DIAM, 8);
    MeshPartBuilder zAxisArrow = modelBuilder.part("z_axis_line", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,blueMat);
    zAxisArrow.setVertexTransform(new Matrix4().translate(0,0,ARROW_HEIGHT/2+AXIS_LINE).rotate(1,0,0,90));
    ConeShapeBuilder.build(zAxisArrow, ARROW_DIAM, ARROW_HEIGHT, ARROW_DIAM, 10);
    return modelBuilder.end();
  }


  public static Model buildAxesLineVersion() {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder line3D = modelBuilder.part("lines", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
    line3D.setColor(Color.GRAY);
    line3D.line(-10,0,0,10,0,0);
    line3D.line(0,-10,0,0,10,0);
    // line3D.line(0,0,-10,0,0,0);
    line3D.setColor(Color.RED);
    line3D.line(0,0,0,10,0,0);
    line3D.setColor(Color.GREEN);
    line3D.line(0,0,0,0,10,0);
    line3D.setColor(Color.BLUE);
    line3D.line(0,0,0,0,0,4);
    return modelBuilder.end();
  }



  public static Model buildXAxis() {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    Material redMat = new Material(ColorAttribute.createDiffuse(Color.RED));

    MeshPartBuilder axisLine = modelBuilder.part("cylinder", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,redMat);
    axisLine.setVertexTransform(new Matrix4().translate(AXIS_LINE/2,0,0).rotate(0,0,1,90));
    CylinderShapeBuilder.build(axisLine, AXIS_DIAM, AXIS_LINE, AXIS_DIAM, 8);

    MeshPartBuilder axisArrow = modelBuilder.part("arrow", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,redMat);
    axisLine.setVertexTransform(new Matrix4().translate(ARROW_HEIGHT/2+AXIS_LINE,0,0).rotate(0,0,1,-90));
    ConeShapeBuilder.build(axisArrow, ARROW_DIAM, ARROW_HEIGHT, ARROW_DIAM, 10);
    return modelBuilder.end();
  }



}









