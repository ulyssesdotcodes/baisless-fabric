using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Rendering.PostProcessing;

[RequireComponent(typeof(Collider))]
public class GlitchOnCollision : MonoBehaviour
{
    public FloatVariable ScanLine;
    public FloatVariable VerticalJump;
    public FloatVariable HorizontalShake;
    public FloatVariable ColorDrift;

    void OnStart() {
        ScanLine = new FloatVariable();
        VerticalJump = new FloatVariable();
        HorizontalShake = new FloatVariable();
        ColorDrift = new FloatVariable();
    }

    void OnTriggerEnter(Collider other){
        if(other.gameObject.tag != "Player") {
            return;
        }

        PostProcessVolume layer = PostProcessManager.instance.GetHighestPriorityVolume(Camera.main.GetComponent<PostProcessLayer>());
        Glitch settings = layer.profile.GetSetting<Glitch>();
        settings.scanLine.value = ScanLine.RuntimeValue;
        settings.verticalJump.value = VerticalJump.RuntimeValue;
        settings.horizontalShake.value = HorizontalShake.RuntimeValue;
        settings.colorDrift.value = ColorDrift.RuntimeValue;
        Destroy(gameObject);
    }
}
