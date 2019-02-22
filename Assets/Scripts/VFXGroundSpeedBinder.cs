using UnityEngine;
using UnityEngine.Experimental.VFX;
using UnityEngine.VFX.Utils;

[VFXBinder("Custom/Ground")]
class VFXGroundSpeedBinder : VFXBinderBase {
    public string Parameter { get { return (string)m_Parameter; } set { m_Parameter = value; } }

    [VFXParameterBinding("UnityEditor.VFX.GroundSpeed", "UnityEngine.Float"), SerializeField]
    protected ExposedParameter m_Parameter = "GroundSpeed";
    public Ground Target;

    public override bool IsValid(VisualEffect component)
    {
        return Target != null && component.HasFloat(m_Parameter);
    }

    public override void UpdateBinding(VisualEffect component)
    {
        component.SetFloat(m_Parameter, Target.GameSpeed.RuntimeValue);
    }

    public override string ToString()
    {
        return string.Format("GroundSpeed : '{0}' -> {1}", m_Parameter, Target == null ? "(null)" : Target.name);
    }
}