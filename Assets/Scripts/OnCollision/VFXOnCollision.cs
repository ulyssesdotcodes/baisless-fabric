using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.VFX;
using UnityEngine.Experimental.VFX;

public class VFXOnCollision : MonoBehaviour
{
    
    // Start is called before the first frame update
    [SerializeField]
    public VisualEffectAsset Asset;

    [SerializeField] FloatVariable speed;

    void OnTriggerEnter(Collider other){
        VisualEffect vfx = GameObject.FindGameObjectWithTag("VFX").GetComponent<VisualEffect>();
        vfx.visualEffectAsset = Asset;
        Destroy(gameObject);
    }
}
