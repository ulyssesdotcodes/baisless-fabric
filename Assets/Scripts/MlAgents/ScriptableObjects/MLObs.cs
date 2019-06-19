using UnityEngine;

public abstract class MLObs : ScriptableObject {
    public void Initialize() {

    }

    public abstract void AddObs(Agent agent);
}