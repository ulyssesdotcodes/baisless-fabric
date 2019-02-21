using System;
using UnityEngine;

[CreateAssetMenu(menuName="Variables/Float")]
public class FloatVariable : ScriptableObject, ISerializationCallbackReceiver {
    public float InitialValue;

    private float runtimeValue;
    public virtual float RuntimeValue { get { return runtimeValue; } set { this.runtimeValue = value; } }

    public void OnBeforeSerialize() { }

    public void OnAfterDeserialize()
    {
        runtimeValue = InitialValue;
    }

    public void OnValidate() {
        // RuntimeValue = InialValue;
    }
}