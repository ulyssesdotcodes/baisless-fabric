(ns fighter-tutorial.core
  (:use arcadia.core arcadia.linear)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            PrimitiveType Collider Light Renderer
            Color]
           [UnityEngine.Experimental.Rendering.HDPipeline HDAdditionalLightData]
           RoadSpawner
           SlowPlayerOnCollision
           RepeatingSpawnTrigger
           ExplodeOnCollision
           InvertOnCollision
           SpotToggleOnCollision
           ArcadiaState
           GroundMotion
           SpawnInfo
           DestroyAfterPosition))

(defrecord SpawnComp [component paramf])

(defonce POOL (atom (.Pool (cmpt (object-named "Spawner") RoadSpawner))))
(defonce SPAWNER (atom (cmpt (object-named "Spawner") RoadSpawner)))

(defn add-spawntrigger [st]
  (.. @SPAWNER SpawnTriggers (Add st)))

(defn clear-spawner []
  (.. @SPAWNER SpawnTriggers
    (Clear)))

(defn clear-pool []
  (doall (map destroy (children @POOL))))

(defn ^GameObject parent! [^GameObject a ^GameObject b]
  (set! (.parent (.transform a)) (.transform b)) a)

(defn create-spawn-info [^GameObject prefab]
  (let [si (new SpawnInfo)]
    (set! (.yOffset si) 0.5) 
    (set! (.Prefab si) prefab)
    si))

(defn create-repeating [spawninfo mod]
  (let [st (ScriptableObject/CreateInstance "RepeatingSpawnTrigger")]
    (set! (.BeatMod st) mod)
    (set! (.Position st) (Resources/Load (str "ScriptableObjects/Variables/Position")))
    (set! (.spawnInfo st) spawninfo) 
    st))

(defn repeat-trigger [prefab mod]
  (-> prefab
    (create-spawn-info)
    (create-repeating mod)))

(defn add-components [^GameObject prefab components]
  (doall (map (fn [{:keys [component paramf]}] 
    (paramf (ensure-cmpt prefab component))) components))
  prefab)

(defn create-from-components [parent gob components]
  (-> gob 
  (add-components components)
  (parent! parent)))

(def default-ground-motion
  (SpawnComp. 
   GroundMotion
   #(do
      (set! (.Position %1) (Resources/Load "ScriptableObjects/Variables/Position"))
      (set! (.GameSpeed %1) (Resources/Load "ScriptableObjects/Variables/Global/GameSpeed")))))

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

(do
  (clear-pool)
  (clear-spawner)
  (-> (create-glow-light @POOL (Color/cyan) (float 120))
    (repeat-trigger 1)
    (add-spawntrigger)))

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