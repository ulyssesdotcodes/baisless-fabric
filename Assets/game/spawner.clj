(ns game.spawner
  (use arcadia.core arcadia.linear hard.core)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            PrimitiveType Collider Light Renderer
            Color]
          Spawner
          SlowPlayerOnCollision
          RepeatingSpawnTrigger
          ExplodeOnCollision
          InvertOnCollision
          SpotToggleOnCollision
          ArcadiaState
          GroundMotion
          SpawnInfo
          FloatVariable
          GlitchOnCollision
          SpawnInfo))

(defrecord SpawnComp [component paramf])

(defonce POOL (atom (.Pool (cmpt (object-named "Spawner") Spawner))))
(defonce SPAWNER (atom (cmpt (object-named "Spawner") Spawner)))

(defn clear-spawner []
  (.. @SPAWNER SpawnTriggers
    (Clear)))

(defn clear-pool []
  (doall (map destroy (children @POOL))))

(clear-pool)

(defn parent-pool [gob]
  (parent! gob @POOL))

(defn add-components [^GameObject prefab components]
  (doall (map (fn [{:keys [component paramf]}] 
    (paramf (ensure-cmpt prefab component))) components))
  prefab)

(defn create-spawn-info [^GameObject prefab x y z]
  (let [si (new SpawnInfo)]
    (set! (.xOffset si) x) 
    (set! (.yOffset si) y) 
    (set! (.zOffset si) z) 
    (set! (.Prefab si) prefab)
    si))

(defn create-repeating
  ([spawninfo mod] (create-repeating spawninfo mod 0))
  ([spawninfo mod off]
   (let [st (ScriptableObject/CreateInstance "RepeatingSpawnTrigger")]
     (set! (.BeatMod st) mod)
     (set! (.Offset st) off)
     (set! (.Position st) (Resources/Load (str "ScriptableObjects/Variables/Position")))
     (set! (.spawnInfo st) spawninfo)
     st)))

(defn create-oneshot [spawninfo]
  (let [st (ScriptableObject/CreateInstance "OneshotSpawnTrigger")]
    (set! (.spawnInfo st) spawninfo)
    st))

(defn spawn-trigger [prefab x y z cfn]
  (-> (create-spawn-info prefab x y z)
      (cfn)))

(defn repeat-trigger [prefab mod x y z off]
  (spawn-trigger prefab x y z #(create-repeating %1 mod off)))

(defn oneshot-trigger [prefab x y z]
  (spawn-trigger prefab x y z #(create-oneshot %1)))

(defn add-spawntrigger [st]
  (.. @SPAWNER SpawnTriggers (Add st)))

(def trigger-collider
  (SpawnComp. 
   Collider
   #(do
      (set! (.isTrigger %1) true))))

(defn create-from-components [parent gob components]
  (if parent (parent! gob parent) ())
  (add-components gob components)
  gob)

(defn float-var [f]
  (let [fv (ScriptableObject/CreateInstance "FloatVariable")]
    (set! (.InitialValue fv) (float f))
    fv))

(def default-ground-motion
  (SpawnComp. 
   GroundMotion
   #(do
      (set! (.Position %1) (Resources/Load "ScriptableObjects/Variables/Position"))
      (set! (.GameSpeed %1) (Resources/Load "ScriptableObjects/Variables/Global/GameSpeed")))))

(defn create-ground-col-with [parent gob comps]
  (create-from-components parent gob (into [] (concat comps [default-ground-motion trigger-collider]))))

(defn repeating-comp [gob comps pos mod off]
  (-> (create-ground-col-with @POOL gob comps)
      (repeat-trigger mod (.x pos) (.y pos) (.z pos) off)
      (add-spawntrigger)))

(defn oneshot-comp [gob comps pos]
  (let [pooled (create-ground-col-with @POOL gob comps)]
    (instantiate pooled pos)
    (destroy pooled)))
