using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(menuName="OnCollision/Component")]
public abstract class OnCollisionParams : ScriptableObject
{
    public abstract void AddComponent(GameObject gameObject);
}