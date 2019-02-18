using UnityEngine;
using MidiJack;

public class RegisterBeat : MonoBehaviour {
    [SerializeField] FloatVariable GameSpeed;
    [SerializeField] FloatVariable Position;

    float targetSpeed;
    float lastBeat = -1;
    float realBps = 0;

    float lastBeadDebug = -1;

    void Start() {
    }

    void Update() {
        if(MidiMaster.GetKeyDown(MidiChannel.Ch1, 1)) {
            if(lastBeat > 0) {
                realBps = 1/(Time.time - lastBeat);
                GameSpeed.RuntimeValue = (realBps * 2f + GameSpeed.RuntimeValue) / 2;
            }

            lastBeat = Time.time;
        }
    }
}