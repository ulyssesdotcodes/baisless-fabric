(ns fighter-tutorial.core
  (:use arcadia.core arcadia.linear)
  (:import [UnityEngine Physics
            GameObject Input 
            Vector2 Mathf Resources Transform
            PrimitiveType
            Collider]
          Spawner
          SlowPlayerOnCollision
          RepeatingSpawnTrigger
          ExplodeOnCollision
          InvertOnCollision
          SpotToggleOnCollision
          ArcadiaState
          GroundMotion
          SpawnInfo)
  )

(defonce POOL (atom (.Pool (cmpt (object-named "Spawner") Spawner))))
(defonce SPAWNER (atom (cmpt (object-named "Spawner") Spawner)))

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

(defn add-components [^GameObject prefab components]
  (doall (map #(ensure-cmpt prefab %) components))
  prefab)

(defn set-defaults [^GameObject prefab]
  (let [gm (ensure-cmpt prefab GroundMotion)]
    (set! (.Position gm) (Resources/Load "ScriptableObjects/Variables/Position"))
    (set! (.GameSpeed gm) (Resources/Load "ScriptableObjects/Variables/Global/GameSpeed")))
  (-> (ensure-cmpt prefab Collider) .isTrigger (set! true))
  prefab)

(defn repeat-trigger [prim mod components]
  (-> (create-primitive prim)
    (parent! @POOL)
    (add-components components)
    set-defaults
    (create-spawn-info)
    (create-repeating mod)))
    ))

(do
  (clear-pool)
  (clear-spawner)
  (add-spawntrigger (repeat-trigger :cube 16 [ExplodeOnCollision SlowPlayerOnCollision GroundMotion InvertOnCollision]))
  (add-spawntrigger (repeat-trigger :sphere 2 [SpotToggleOnCollision]))
  )

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