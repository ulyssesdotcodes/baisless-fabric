using UnityEngine;
using System.Collections;

[RequireComponent(typeof(EnergyAgent))]
[RequireComponent(typeof(LineRenderer))]
public class RaycastShooter : MonoBehaviour {
    public RaycastAbility Ability;
    [HideInInspector] public LineRenderer laserLine;                    // Reference to the LineRenderer component which will display our laserline.
    private EnergyAgent energyAgent;

    private Coroutine LaserActive;

    void Start() {
        laserLine = GetComponent<LineRenderer> ();
        energyAgent = GetComponent<EnergyAgent>();
    }

    public GameObject Fire(float mult)
    {
        if(!Ability.CanRun(mult, energyAgent.EnergyPool)) {
            return null;
        }

        bool hitSomething = false;

        energyAgent.UseAbility(mult, Ability);
        //Create a vector at the center of our camera's near clip plane.
        Vector3 rayOrigin = gameObject.transform.position;
        
        //Draw a debug line which will show where our ray will eventually be
        Debug.DrawRay(rayOrigin, transform.forward * Ability.weaponRange, Color.green);
        
        //Declare a raycast hit to store information about what our raycast has hit.
        RaycastHit hit;

        //Set the start position for our visual effect for our laser to the position of gunEnd
        laserLine.SetPosition(0, transform.position);
        
        //Check if our raycast has hit anything
        if (Physics.Raycast(rayOrigin, transform.forward, out hit, Ability.weaponRange))
        {
            hitSomething = true;
            //Set the end position for our laser line 
            laserLine.SetPosition(1, hit.point);
            
            //Get a reference to a health script attached to the collider we hit
            HealthAgent health = hit.collider.GetComponent<HealthAgent>();
            
            //If there was a health script attached
            if (health != null)
            {
                //Call the damage function of that script, passing in our gunDamage variable
                health.Damage(Ability.gunDamage * Time.deltaTime);
            }
            
            //Check if the object we hit has a rigidbody attached
            if (hit.rigidbody != null)
            {
                //Add force to the rigidbody we hit, in the direction it was hit from
                hit.rigidbody.AddForce (-hit.normal * Ability.hitForce);
            }
        }
        else
        {
            //if we did not hit anything, set the end of the line to a position directly away from
            laserLine.SetPosition(1, transform.forward * Ability.weaponRange);
        }

        if(LaserActive != null) {
            StopCoroutine(LaserActive);
        }

        LaserActive = StartCoroutine(FireEnable());
        return hitSomething ? hit.collider.gameObject : null;
    }

    private IEnumerator FireEnable() {
        laserLine.enabled = true;
        yield return new WaitForSeconds(0.01667f);
        laserLine.enabled = false;
    }
}