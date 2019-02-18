using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[RequireComponent(typeof(Collider))]
public class Ground : MonoBehaviour
{
    [SerializeField] 
    public FloatVariable GameSpeed;
    [SerializeField] 
    public FloatVariable Position;
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        // Adjust for a measure
        Position.RuntimeValue += GameSpeed.RuntimeValue * Time.deltaTime;
        GetComponent<Renderer>().materials[0].mainTextureOffset = new Vector2(0, -Position.RuntimeValue % 1);
    }
}
