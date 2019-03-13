(ns game.utils
  [use arcadia.core arcadia.linear]
  [:import 
   [UnityEngine Resources]
   FloatVariable
   ])

(defn speed [s]
  (let [bpmspeed (Resources/Load "ScriptableObjects/Variables/BPMSpeed")]
    (set! (.SliderValue bpmspeed) (float s))
    (.OnValidate bpmspeed))
  (def gamespeed s))