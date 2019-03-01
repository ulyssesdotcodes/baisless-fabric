using System;
using UnityEngine;
using System.Collections.Generic;

public class SpawnInfo {
    public float yOffset;
    public GameObject Prefab; 
    public List<OnCollisionParams> OnCollisionParams;
}

public abstract class SpawnTrigger : ScriptableObject {
    public bool oneshot;
    
    abstract public Optional<SpawnInfo> Spawn();
}