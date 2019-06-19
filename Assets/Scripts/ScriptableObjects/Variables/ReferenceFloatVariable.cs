using UnityEditor;
using UnityEngine;

[CreateAssetMenu(menuName="Variables/ReferenceFloat")]
public class ReferenceFloatVariable : FloatVariable {
    [SerializeField] FloatVariable reference;

    public override float RuntimeValue{ get { return reference.RuntimeValue; } }

}