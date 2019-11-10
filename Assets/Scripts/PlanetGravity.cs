using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlanetGravity: MonoBehaviour
{
  private Rigidbody rb;

    // Start is called before the first frame update
    void Start()
    {
      rb = GetComponent<Rigidbody>();
    }

    // Update is called once per frame
    void Update()
    {
      rb.AddForce(-transform.position.normalized * -9.8f);
    }
}
