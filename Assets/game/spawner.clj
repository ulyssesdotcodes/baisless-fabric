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

(defn repeat-trigger [prefab mod x y z off]
  (-> prefab
    (create-spawn-info x y z)
    (create-repeating mod off)))

(defn add-spawntrigger [st]
  (.. @SPAWNER SpawnTriggers (Add st)))

(def trigger-collider
  (SpawnComp. 
   Collider
   #(do
      (set! (.isTrigger %1) true))))

(defn create-from-components [parent gob components]
  (-> gob 
  (add-components components)
  (parent! parent)))

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