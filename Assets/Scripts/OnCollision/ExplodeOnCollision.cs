using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[RequireComponent(typeof(GroundMotion))]
public class ExplodeOnCollision : MonoBehaviour
{
    public float CubeSize = 0.1f;
    public float ExplosionForce = 2f;
    public float UpForce = 10f;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
   }

    void OnTriggerEnter(Collider other) {
        if(other.gameObject.tag != "player") {
            return;
        }

        GameObject.Destroy(gameObject);

        GroundMotion gm = GetComponent<GroundMotion>();
        Renderer renderer = GetComponent<Renderer>();
        float offsetx = renderer.bounds.min.x;
        float offsety = renderer.bounds.min.y;
        while(offsetx < renderer.bounds.max.x) {
            while(offsety < renderer.bounds.max.y) {
                GameObject cube = GameObject.CreatePrimitive(PrimitiveType.Cube);
                cube.transform.position = new Vector3(offsetx, offsety, transform.position.z);
                cube.transform.localScale = Vector3.one * CubeSize;
                cube.GetComponent<Renderer>().materials = renderer.materials;
                
                GroundMotion gmcube = cube.AddComponent(typeof(GroundMotion)) as GroundMotion;
                gmcube.Position = gm.Position;
                gmcube.GameSpeed = gm.GameSpeed;

                Rigidbody rb = cube.AddComponent(typeof(Rigidbody)) as Rigidbody;
                Vector3 explodepos = new Vector3(transform.position.x, offsety, transform.position.z);
                rb.mass = CubeSize;
                rb.velocity = new Vector3(0, 0, gm.GameSpeed.RuntimeValue);
                rb.AddExplosionForce(ExplosionForce * gm.GameSpeed.RuntimeValue, explodepos, 0.25f, UpForce, ForceMode.Impulse);

                GameObject.Destroy(cube, 1f);

                offsety += CubeSize;
            }
            offsetx += CubeSize;
            offsety = renderer.bounds.min.y;
        }
    }
}
