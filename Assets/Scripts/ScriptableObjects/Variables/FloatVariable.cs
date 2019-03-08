using System;
using UnityEngine;

[CreateAssetMenu(menuName="Variables/Float")]
public class FloatVariable : ScriptableObject, ISerializationCallbackReceiver {
    private float initialValue;
    public virtual float InitialValue { get { return initialValue; } set { this.initialValue = value; this.runtimeValue = value; } }

    private float runtimeValue;
    public virtual float RuntimeValue { get { return runtimeValue; } set { this.runtimeValue = value; } }

    public FloatVariable() {
        InitialValue = 0f;
        RuntimeValue = 0f;
    }

    public FloatVariable(float v) {
        InitialValue = v;
        runtimeValue = v;
    }

    public void OnBeforeSerialize() { }

    public void OnAfterDeserialize()
    {
        runtimeValue = InitialValue;
    }

    public void OnValidate() {
        RuntimeValue = InitialValue;
    }
}