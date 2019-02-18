using System;
using UnityEngine;

[CreateAssetMenu(menuName="Variables/Float")]
public class FloatVariable : ScriptableObject, ISerializationCallbackReceiver {
    public float InitialValue;

    public float RuntimeValue;

    public void OnBeforeSerialize() { }

    public void OnAfterDeserialize()
    {
        RuntimeValue = InitialValue;
    }

    public void OnValidate() {
        RuntimeValue = InitialValue;
    }
}