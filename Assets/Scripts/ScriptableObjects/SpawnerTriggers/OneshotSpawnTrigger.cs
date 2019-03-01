using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(menuName="SpawnTrigger/Oneshot")]
public class OneshotSpawnTrigger : SpawnTrigger
{
    public SpawnInfo spawnInfo;
    // Start is called before the first frame update
    public OneshotSpawnTrigger() {
        oneshot = true;
    }

    public override Optional<SpawnInfo> Spawn()
    {
        return Optional<SpawnInfo>.of(spawnInfo);
    }
}
