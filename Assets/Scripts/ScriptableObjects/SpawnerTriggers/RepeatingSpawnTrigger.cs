using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(menuName="SpawnTrigger/Repeating")]
public class RepeatingSpawnTrigger : SpawnTrigger
{
    [SerializeField] float BeatMod = 0f;
    [SerializeField] float Offset = 0f;
    [SerializeField] FloatVariable Position;
    float lastPosition;

    public void OnAfterDeserialize()
    {
        lastPosition = 0f;
    }

    public override bool ShouldSpawn()
    {
        bool shouldSpawn = (Position.RuntimeValue + Offset) % BeatMod < (lastPosition + Offset) % BeatMod;
        lastPosition = Position.RuntimeValue;
        return shouldSpawn;
    }
}

