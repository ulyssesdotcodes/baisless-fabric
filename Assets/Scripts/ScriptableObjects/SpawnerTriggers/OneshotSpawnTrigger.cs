﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(menuName="SpawnTrigger/Oneshot")]
public class OneshotSpawnTrigger : SpawnTrigger
{
    // Start is called before the first frame update
    public OneshotSpawnTrigger() {
        oneshot = true;
    }

    public override bool Spawn()
    {
        return true;
    }
}