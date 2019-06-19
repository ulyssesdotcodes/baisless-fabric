using System;
using UnityEngine;
using System.Collections.Generic;

[Serializable]
public class SpawnInfo {
    public float yOffset;
    public float xOffset;
    public float zOffset;
    public GameObject Prefab; 
    public List<ComponentParams> ComponentParams;

    public SpawnInfo() {
        ComponentParams = new List<ComponentParams>();
    }
}

public abstract class SpawnTrigger : ScriptableObject {
    public bool oneshot;
    
    abstract public Optional<SpawnInfo> Spawn();
}