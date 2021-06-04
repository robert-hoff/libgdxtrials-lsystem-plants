package hoffinc.lsystems;

import java.util.Stack;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
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

  Stack<Matrix4> transforms = new Stack<>();
  Stack<Vector3> positions = new Stack<>();

  TurtleModel(Model model, Vector3 pos) {
    this.model = model;
    this.pos = pos;
  }

  public void push() {
    transforms.push(new Matrix4(model_transform));
    positions.push(new Vector3(pos));
  }

  public void pop() {
    model_transform = transforms.pop();
    pos = positions.pop();
  }

  public void scale(float scaleX, float scaleY, float scaleZ) {
    model_transform.scale(scaleX, scaleY, scaleZ);
    pos.scl(new Vector3(scaleX, scaleY, scaleZ));
  }


}






