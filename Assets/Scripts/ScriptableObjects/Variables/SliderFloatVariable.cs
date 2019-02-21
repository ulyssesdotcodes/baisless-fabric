using UnityEditor;
using UnityEngine;

[CreateAssetMenu(menuName="Variables/Slider")]
class SliderFloatVariable : FloatVariable {
    [SerializeField] float min = 0f;
    [SerializeField] float max = 1f;
    [SerializeField] float mult;

    [Range(0, 1)]
    [SerializeField] 
    float SliderValue;

    new public void OnValidate(){
        InitialValue = Mathf.Lerp(min, max, SliderValue) * mult;
    }
}