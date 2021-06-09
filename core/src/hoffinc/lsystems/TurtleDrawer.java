package hoffinc.lsystems;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import hoffinc.utils.InspectData;

public class TurtleDrawer {

  private Array<ModelInstance> modelComposition = new Array<ModelInstance>();
  public List<TurtleModel> modelNodes = new ArrayList<>();
  private Vector3 FWD = new Vector3(0,0,1);
  private Vector3 UP = new Vector3(0,-1,0);
  private Vector3 RIGHT = new Vector3(1,0,0);
  public Matrix4 transform = new Matrix4();
  private Stack<Matrix4> stack = new Stack<>();

  public boolean show_path = false;


  public TurtleDrawer() {}

  public void addModel(TurtleModel model) {
    modelNodes.add(model);
  }
  // for many models we do not need to update position
  public void addModel(Model model) {
    addModel(model, new Vector3(0,0,0));
  }
  public void addModel(Model model, Vector3 pos) {
    modelNodes.add(new TurtleModel(model, pos));
  }

  public void walk(float x, float y, float z) {
    transform.translate(x, y, z);
  }

  public void walkNode(int id) {
    Vector3 model_pos = modelNodes.get(id).pos;
    transform.translate(model_pos);
  }

  public void drawNode(int id) {
    Model model = modelNodes.get(id).model;
    Matrix4 model_transform = modelNodes.get(id).model_transform;
    ModelInstance model_instance = new ModelInstance(model);
    model_instance.transform = new Matrix4(transform);
    model_instance.transform.mul(model_transform);

    Vector3 model_pos = modelNodes.get(id).pos;
    transform.translate(model_pos);
    modelComposition.add(model_instance);

    if (show_path) {
      InspectData.showVector3(new Vector3().mul(transform));
    }
  }

  public void scaleModel(int id, float x, float y, float z) {
    modelNodes.get(id).scale(x, y, z);
  }

  public void push() {
    stack.push(new Matrix4(transform));
    for (TurtleModel turtleModel : modelNodes) {
      turtleModel.push();
    }
  }

  public void pop() {
    transform = stack.pop();
    for (TurtleModel turtleModel : modelNodes) {
      turtleModel.pop();
    }
  }

  public void turnLeft(float angle_deg) {
    Quaternion rot = new Quaternion(UP, angle_deg);
    transform.rotate(rot);
  }

  public void turnRight(float angle_deg) {
    Quaternion rot = new Quaternion(UP, -angle_deg);
    transform.rotate(rot);
  }

  public void turnAround() {
    Quaternion rot = new Quaternion(UP, 180);
    transform.rotate(rot);
  }

  public void pitchDown(float angle_deg) {
    Quaternion rot = new Quaternion(RIGHT, -angle_deg);
    transform.rotate(rot);
  }

  public void pitchUp(float angle_deg) {
    Quaternion rot = new Quaternion(RIGHT, angle_deg);
    transform.rotate(rot);
  }

  public void rollLeft(float angle_deg) {
    Quaternion rot = new Quaternion(FWD, -angle_deg);
    transform.rotate(rot);
  }

  public void rollRight(float angle_deg) {
    Quaternion rot = new Quaternion(FWD, angle_deg);
    transform.rotate(rot);
  }

  public Array<ModelInstance> getComposition() {
    return modelComposition;
  }

  // R: this isn't great because underlying models may also have transformation on them (that also need reset)
  // for now, when redrawing a new model just resupply the sub-models (in modelNodes)
  //  public void clearModels() {
  //    transform = new Matrix4();
  //    modelComposition.clear();
  //  }

}







