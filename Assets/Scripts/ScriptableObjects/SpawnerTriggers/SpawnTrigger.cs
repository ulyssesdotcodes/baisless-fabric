using System;
using UnityEngine;

public abstract class SpawnTrigger : ScriptableObject {
    public float yOffset;
    public GameObject Prefab;
    public bool oneshot;
    
    abstract public bool Spawn();
}