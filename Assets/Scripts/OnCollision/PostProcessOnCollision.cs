using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Rendering.PostProcessing;

[RequireComponent(typeof(Collider))]
public class PostProcessOnCollision : MonoBehaviour
{
    public PostProcessEffectSettings settings;

    void OnTriggerEnter(Collider other){
        PostProcessVolume layer = PostProcessManager.instance.GetHighestPriorityVolume(Camera.main.GetComponent<PostProcessLayer>());
        if(layer.profile.HasSettings(settings.GetType())) {
            layer.profile.RemoveSettings(settings.GetType());
        }
        layer.profile.AddSettings(settings);
        Destroy(gameObject);
    }
}
