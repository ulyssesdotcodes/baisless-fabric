(ns game.road
  (use
   arcadia.core
   arcadia.linear
   tween.core
   hard.core
   game.spawner)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            PrimitiveType Collider Light Renderer
            Color]
           [UnityEngine.Experimental.Rendering.HDPipeline HDAdditionalLightData]
           RoadSpawner
           SlowPlayerOnCollision
           RepeatingSpawnTrigger
           OneshotSpawnTrigger
           ExplodeOnCollision
           InvertOnCollision
           SpotToggleOnCollision
           ArcadiaState
           GroundMotion
           SpawnInfo
           FloatVariable
           GlitchOnCollision
           CameraShakeOnCollision
           StopCameraShakeOnCollision
           CameraShake
           CameraShakeManager
           CameraShake+ShakeType
           CameraShake+NoiseType
           DestroyAfterPosition
           VFXOnCollision
           [game.spawner SpawnComp]))

(use 'game.spawner :reload)

(destroy (object-named "Cube"))

(clear-pool)

(clear-spawner)

(defn speed [s]
  (let [bpmspeed (Resources/Load "ScriptableObjects/Variables/BPMSpeed")]
    (set! (.SliderValue bpmspeed) (float s))
    (.OnValidate bpmspeed))
  (def gamespeed s))

(speed 0.3)

(defn create-glow-light [parent col lumens]
  (let [gob (create-from-components
             parent (GameObject/Instantiate (Resources/Load "Prefabs/GlowLight"))
             [(SpawnComp. DestroyAfterPosition identity)
              default-ground-motion])
        light (cmpt (first (children gob)) Light)
        hdlight (cmpt (first (children gob)) HDAdditionalLightData)
        mat (.material (cmpt gob Renderer))]
    (set! (.color light) col)
    (set! (.intensity hdlight) lumens)
    (.SetColor mat "_Color" col)
    (.SetFloat mat "_Lumens" lumens)
    gob))

(clear-pool)

(defn repeating [go w pos]
  (timeline* 
   :loop
   (wait w)
   #(do (instantiate go pos) nil)))

(clear-cloned!)

(repeating)

(destroy! (mono-obj))
(timeline* :loop #(log "hi") (wait 0.2))

(repeating (create-glow-light @POOL (Color/cyan) (float 60)) 1 (v3 -0.5 1 4))

(-> (create-empty :))

(defn glitch-oncol 
  ([sl] (glitch-oncol sl 0 0 0))
  ([sl vj] (glitch-oncol sl vj 0 0))
  ([sl vj hs] (glitch-oncol sl vj hs 0))
  ([sl vj hs cd] 
   (SpawnComp. 
    GlitchOnCollision 
    #(do
       (set! (.ScanLine %1) (float-var sl)) 
       (set! (.VerticalJump %1) (float-var vj)) 
       (set! (.HorizontalShake %1) (float-var hs)) 
       (set! (.ColorDrift %1) (float-var cd)) 
       %1))))

(def ambient-shake (new-ambient-shake))

(defn new-ambient-shake [] (let [cs (ScriptableObject/CreateInstance "CameraShake")]
    (set! (.Shake cs) (enum-val CameraShake+ShakeType "Constant"))
    (set! (.Noise cs) (enum-val CameraShake+NoiseType "Perlin"))
    (set! (.RotateExtents cs) (v3 0.1 0.1 0.1))
    (set! (.MoveExtents cs) (v3 0.5 0.5 0.5))
    (set! (.Speed cs) (float (+ 0.25 (* 0.5 gamespeed))))
    (set! (.Duration cs) (float -1))
    cs))

(defn camerashake-oncol []
  (let [cs (new-ambient-shake)]
  [(SpawnComp. CameraShakeOnCollision #(do (set! (.CameraShake %1) cs))) 
   (SpawnComp. StopCameraShakeOnCollision #(do (set! (.CameraShake %1) cs)))]))

(def firevfx (Resources/Load "VFX/Fire"))
(def rainvfx (Resources/Load "VFX/Rain"))
(def clearvfx nil)

(defn vfx-oncol [vfx]
  (SpawnComp. VFXOnCollision #(do (set! (.Asset %1) vfx))))

(defn clearvfx-oncol [] (SpawnComp. VFXOnCollision #(do %1)))

(rainvfx-oncol)

(.Play (cmpt (object-named "Camera") CameraShakeManager) ambient-shake)
(.Stop (cmpt (object-named "Camera") CameraShakeManager) ambient-shake false)
(.StopAll (cmpt (object-named "Camera") CameraShakeManager) false)

(oneshot-comp (create-primitive :cube) [(vfx-oncol firevfx)] (v3 0.5 0.5 4))
(oneshot-comp (create-primitive :sphere) [(vfx-oncol rainvfx)] (v3 0.5 0.5 4))

(clear-spawner)

(instantiate (create-ground-col-with @POOL (create-primitive :cube) ) )
(instantiate (create-ground-col-with @POOL (create-primitive :cube) [(vfx-oncol clearvfx)]) (v3 0.5 0.5 4))

(destroy (object-named "Sphere"))

(do
  (clear-pool)
  (clear-spawner)
  (repeating-comp (create-primitive :cube) 4 1 (glitch-oncol 0.2 0.2 0 0.2))
  (repeating-comp (create-primitive :cube) 4 1 (glitch-oncol 0.2 0.2 0 0.2))
  (repeating-comp (create-primitive :cube) 4 0 (glitch-oncol 0 0.05 0 0))
  (let [cs (camerashake-oncol)]
    (repeating-comp (create-primitive :sphere) 16 8 (first cs))
    (repeating-comp (create-primitive :sphere) 16 0 (last cs))))



(let 
  [ sphere (create-ground-explode-sphere) 
    spawnInfo (create-spawn-info sphere)
    spawnTrigger (create-repeating spawnInfo 16)
  ]
  (.. @SPAWNER SpawnTriggers
    (Add spawnTrigger))
  )

(clear-spawner)
(clear-pool)


(let [sphere (create-primitive :sphere)]
  ; (.name sphere "exploding")
  (parent! sphere @POOL))

(destroy (object-named "Sphere(Clone)"))
(destroy (object-named "Sphere"))


; (defonce CLONED (atom []))

; (defn resource [s] (UnityEngine.Resources/Load s))

; (defn clear-cloned! []
;   (dorun (map retire @CLONED))
;   (reset! CLONED []))

; (defmacro clone! [kw]
;   (if (keyword? kw)
;     (let [source (clojure.string/replace (subs (str kw) 1) #"[:]" "/")]
;       `(let [^UnityEngine.GameObject source# (~'UnityEngine.Resources/Load ~source)
;              ^UnityEngine.GameObject gob# (~'UnityEngine.GameObject/Instantiate source#)]
;          (~'set! (.name gob#) (.name source#))
;          (swap! ~'CLONED #(cons gob# %))
;          gob#))
;     `(-clone! ~kw)))

; (defn ^UnityEngine.GameObject -clone!
;   ([ref] (-clone! ref nil))
;   ([ref pos]
;      (when-let [^UnityEngine.GameObject source
;                 (cond  (string? ref)  (resource ref)
;                        (keyword? ref) (resource (clojure.string/replace (subs (str ref) 1) #"[:]" "/"))
;                        :else nil)]

;        (let [pos   (or pos (.position (.transform source)))
;              quat  (.rotation (.transform source))
;              ^UnityEngine.GameObject gob   (arcadia.core/instantiate source pos quat)]
;          (set! (.name gob) (.name source))
;          (swap! CLONED #(cons gob %)) gob))))





;; (defn rm-obj []
;;   (.. (cmpt (object-named "Spawner") Spawner) SpawnTriggers
;;       (Remove (Resources/Load "ScriptableObjects/SpawnTriggers/Cube")))

; (defn start-game [])

; (start-game)


; (defn create-ground-explode-sphere []
;   (let [sphere (create-primitive :sphere)]
;     (set! (.name sphere) "exploding")
;     (parent! sphere @POOL)
;     (add-components sphere [ExplodeOnCollision GroundMotion])
;     (set-defaults sphere)
;     (let [col (.GetComponent sphere "Collider")]
;       (set! (.isTrigger col) true))
;     sphere))