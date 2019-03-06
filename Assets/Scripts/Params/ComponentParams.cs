using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public abstract class ComponentParams : ScriptableObject
{
    public abstract void AddComponent(GameObject gameObject);
}