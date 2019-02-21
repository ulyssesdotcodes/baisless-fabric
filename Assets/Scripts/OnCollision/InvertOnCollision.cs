using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Rendering.PostProcessing;

[RequireComponent(typeof(Collider))]
public class InvertOnCollision : MonoBehaviour
{
    void Start() {
    }

    void OnTriggerEnter(Collider other){
        PostProcessVolume layer = PostProcessManager.instance.GetHighestPriorityVolume(Camera.main.GetComponent<PostProcessLayer>());
        Invert settings = layer.profile.GetSetting<Invert>();
        settings.invert.value = Mathf.Abs(settings.invert.value - 1);
        Destroy(gameObject);
    }
}
