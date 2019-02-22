using UnityEngine;
using MidiJack;

[CreateAssetMenu(menuName="SpawnTrigger/Midi")]
class MidiSpawnTrigger : SpawnTrigger {
    [SerializeField] int note;

    override public bool Spawn(){
        return MidiMaster.GetKeyDown(MidiChannel.Ch1, note);
    }
}