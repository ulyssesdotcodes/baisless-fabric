using UnityEngine;
using MLAgents;

public abstract class MLReset : ScriptableObject {
    public virtual void Initialize(BaseAgent agent) {
      if(agent.area.academy == null) {
        agent.area.academy = Object.FindObjectOfType<Academy>();
      }   
    }

    public abstract void Reset(BaseAgent agent);
}
