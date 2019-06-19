using UnityEngine;

public abstract class MLActionMove : ScriptableObject {
    public void Initialize() {

    }

    abstract void RunAction(float[] vectorActions, GameObject go);
}