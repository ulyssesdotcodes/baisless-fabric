using System.Collections;
using System.Collections.Generic;
using UnityEngine;



[CreateAssetMenu(menuName="SpawnTrigger/Repeating")]
public class GroupedSpawnTrigger : SpawnTrigger
{
    float lastPosition;
    [SerializeField] float staggerAmount;
    [SerializeField] float offset;

    public void OnAfterDeserialize()
    {
        lastPosition = 0f;
    }

    public override Optional<SpawnInfo> Spawn()
    {
        return Optional<SpawnInfo>.none();
    }
}

