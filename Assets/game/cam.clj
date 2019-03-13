(ns game.cam
  (use arcadia.core arcadia.linear game.spawner)
  (:import 
   [UnityEngine ScriptableObject]
   CameraShake
   CameraShake+ShakeType
   CameraShake+NoiseType
   CameraShakeOnCollision
   StopCameraShakeOnCollision
   CameraShakeManager
   [game.spawner SpawnComp]))

(defn new-ambient-shake
  ([] (new-ambient-shake 0.25))
  ([speed] (let [cs (ScriptableObject/CreateInstance "CameraShake")]
             (set! (.Shake cs) (enum-val CameraShake+ShakeType "Constant"))
             (set! (.Noise cs) (enum-val CameraShake+NoiseType "Perlin"))
             (set! (.RotateExtents cs) (v3 0.1 0.1 0.1))
             (set! (.MoveExtents cs) (v3 0.5 0.5 0.5))
             (set! (.Speed cs) (float speed))
             (set! (.Duration cs) (float -1))
             cs)))

(defn new-impact-shake
  ([] (new-impact-shake 5))
  ([speed] (let [cs (ScriptableObject/CreateInstance "CameraShake")]
             (set! (.Shake cs) (enum-val CameraShake+ShakeType "EaseOut"))
             (set! (.Noise cs) (enum-val CameraShake+NoiseType "Perlin"))
             (set! (.RotateExtents cs) (v3 0.5 0.5 0.5))
             (set! (.MoveExtents cs) (v3 1 1 1))
             (set! (.Speed cs) (float speed))
             (set! (.Duration cs) (float 0.5))
             cs)))

(defn camerashake-oncol []
  (let [cs (new-ambient-shake)]
  [(SpawnComp. CameraShakeOnCollision #(do (set! (.CameraShake %1) cs))) 
   (SpawnComp. StopCameraShakeOnCollision #(do (set! (.CameraShake %1) cs)))]))

(defn play-shake [cs]
  (.Play (cmpt (object-named "Camera") CameraShakeManager) cs))

(defn stop-shake [cs]
  (.Stop (cmpt (object-named "Camera") CameraShakeManager) cs false))

(defn stop-all-shakes 
  ([] (stop-all-shakes false))
  ([imm] (.StopAll (cmpt (object-named "Camera") CameraShakeManager) imm)))