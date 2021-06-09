package hoffinc.lsystems;

import java.util.Stack;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/*
 *
 * After drawing a model we also want to reposition the turtle
 *
 *
 *
 */
public class TurtleModel {

  public Model model;
  public Matrix4 model_transform = new Matrix4();
  public Vector3 pos;
  private Stack<Matrix4> transforms = new Stack<>();
  private Stack<Vector3> positions = new Stack<>();


  TurtleModel(Model model, Vector3 pos) {
    this.model = model;
    this.pos = pos;
    push();
  }

  public void push() {
    transforms.push(new Matrix4(model_transform));
    positions.push(new Vector3(pos));
  }

  public void pop() {
    model_transform = transforms.pop();
    pos = positions.pop();
    if (transforms.size() == 0 || positions.size() == 0) {
      throw new RuntimeException("this should not happen!");
    }
  }

  public void scale(float scaleX, float scaleY, float scaleZ) {
    model_transform.scale(scaleX, scaleY, scaleZ);
    pos.scl(new Vector3(scaleX, scaleY, scaleZ));
  }


  // FIXME - these things probably need to also update the pos Vector
  // if the turtle should traverse the model correctly after draw
  public void rotX(float angle_deg) {
    Vector3 RIGHT = new Vector3(1,0,0);
    Quaternion rotX = new Quaternion(RIGHT, angle_deg);
    model_transform.rotate(rotX);
  }

  public void translate(float x, float y, float z) {
    model_transform.translate(x, y, z);
  }


}






