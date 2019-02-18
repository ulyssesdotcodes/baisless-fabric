using System;
using UnityEngine;

public abstract class SpawnTrigger : ScriptableObject {
    public float yOffset;
    public GameObject Prefab;
    
    abstract public bool ShouldSpawn();
}