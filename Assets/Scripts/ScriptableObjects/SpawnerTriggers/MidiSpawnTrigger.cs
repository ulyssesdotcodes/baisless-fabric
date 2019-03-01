using UnityEngine;
using MidiJack;

[CreateAssetMenu(menuName="SpawnTrigger/Midi")]
class MidiSpawnTrigger : SpawnTrigger {
    SpawnInfo spawnInfo;
    [SerializeField] int note;
    override public Optional<SpawnInfo> Spawn(){
        return MidiMaster.GetKeyDown(MidiChannel.Ch1, note) ? 
            Optional<SpawnInfo>.of(spawnInfo) : Optional<SpawnInfo>.none();
    }
}