using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Rendering.PostProcessing;

[RequireComponent(typeof(PostProcessVolume))]
public class PostProcessingControl : MonoBehaviour
{
    [SerializeField] Camera mainCam;
    [SerializeField] GameObject player;
    PostProcessProfile profile;
    DepthOfField dof;
    
    // Start is called before the first frame update
    void Start()
    {
        profile = GetComponent<PostProcessVolume>().profile;
        dof = profile.GetSetting<DepthOfField>();
    }

    // Update is called once per frame
    void Update()
    {
        dof.focusDistance.value = Vector3.Distance(player.transform.position, mainCam.transform.position);
    }
}
