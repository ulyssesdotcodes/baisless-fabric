using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GraffitiArea : Area
{
    public string TargetSpawnNumberKeyVal = "8";
    public string TargetSpawnDistanceKeyVal = "16";
    public string ActorSpawnNumberKeyVal = "8";
    public GameObject Target;
    public GameObject BlueActor;
    public GameObject RedActor;
    private List<GameObject> Spawned = new List<GameObject>();

    private int SpawnNumber;
    private float SpawnDistance;
    private int ActorSpawnNumber;

    // Start is called before the first frame update
    protected override void Start()
    {
        base.Start();

        SpawnNumber = (int)AcademyParameters.FetchOrParse(academy, TargetSpawnNumberKeyVal);
        SpawnDistance = AcademyParameters.FetchOrParse(academy, TargetSpawnDistanceKeyVal);
        ActorSpawnNumber = (int)AcademyParameters.FetchOrParse(academy, ActorSpawnNumberKeyVal);
    }

    public override void ResetArea() {
        SpawnNumber = (int) AcademyParameters.Update(academy, TargetSpawnNumberKeyVal, (int)SpawnNumber);
        SpawnDistance = (int) AcademyParameters.Update(academy, TargetSpawnDistanceKeyVal, (int)SpawnDistance);
        ActorSpawnNumber = (int) AcademyParameters.Update(academy, ActorSpawnNumberKeyVal, (int)ActorSpawnNumber);

        foreach(GameObject go in Spawned) {
            Destroy(go);
        }

        Spawned.Clear();

        for(int i = 0; i < SpawnNumber; i++) {
            Vector2 position = new Vector2(Random.Range(-SpawnDistance, SpawnDistance), Random.Range(-SpawnDistance, SpawnDistance));
            GameObject wall = GameObject.Instantiate(Target, new Vector3(position.x, base.StartY + 1f, position.y), Quaternion.identity);
            if(Random.Range(0f, 1f) > 0.5f) {
                wall.tag = "bluetarget";
            } else {
                wall.tag = "redtarget";
            }
            wall.transform.SetParent(gameObject.transform);
            Spawned.Add(wall);
        }

        for(int i = 0; i < ActorSpawnNumber; i++) {
            GameObject actorPrefab = i % 2 == 0 ? BlueActor : RedActor;
            Vector2 position = new Vector2(Random.Range(-SpawnDistance, SpawnDistance), Random.Range(-SpawnDistance, SpawnDistance));
            GameObject actor = GameObject.Instantiate(actorPrefab, new Vector3(position.x, base.StartY + 1f, position.y), Quaternion.identity, gameObject.transform);
            Spawned.Add(actor);
        }
    }
}
