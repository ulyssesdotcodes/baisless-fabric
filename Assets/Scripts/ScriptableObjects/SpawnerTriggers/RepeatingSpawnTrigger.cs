﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(menuName="SpawnTrigger/Repeating")]
public class RepeatingSpawnTrigger : SpawnTrigger
{
    public float BeatMod = 0f;
    public float Offset = 0f;
    public FloatVariable Position;
    public SpawnInfo spawnInfo;
    float lastPosition;

    public void OnAfterDeserialize()
    {
        lastPosition = 0f;
    }

    public override Optional<SpawnInfo> Spawn()
    {
        bool shouldSpawn = (Position.RuntimeValue + Offset) % BeatMod < (lastPosition + Offset) % BeatMod;
        lastPosition = Position.RuntimeValue;
        return shouldSpawn ? Optional<SpawnInfo>.of(spawnInfo) : Optional<SpawnInfo>.none();
    }
}

