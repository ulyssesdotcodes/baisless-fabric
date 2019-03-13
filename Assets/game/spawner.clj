(ns game.spawner
  (use arcadia.core arcadia.linear hard.core)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            PrimitiveType Collider Light Renderer
            Color Application Debug]
           [UnityEngine.Experimental.VFX VisualEffect]
           SlowPlayerOnCollision
           ExplodeOnCollision
           SpotToggleOnCollision
           ArcadiaState
           GroundMotion
           SpawnInfo
           Spawner
           FloatVariable
           GlitchOnCollision
           SpawnInfo
           DestroyAfterPosition))

(defrecord GameState [gpos gspeed pool player cam])

(defrecord SpawnComp [component paramf])

(defonce game-state 
  (GameState.
   (Resources/Load "ScriptableObjects/Variables/Position")
   (Resources/Load "ScriptableObjects/Variables/Global/GameSpeed")
   (.Pool (cmpt (object-named "Spawner") Spawner))
   (object-tagged "Player")
   (object-named "CameraParent")))

(defn gpos-mod [gstate speed mult modv] (* mult (mod (* speed (.RuntimeValue (:gpos gstate))) modv)))
(defn gpos-cos [gstate speed mult] (* mult (Mathf/Cos (* speed (.RuntimeValue (:gpos gstate))))))
(defn gpos-sin [gstate speed mult] (* mult (Mathf/Sin (* speed (.RuntimeValue (:gpos gstate))))))

(defn float-var [f]
  (let [fv (ScriptableObject/CreateInstance "FloatVariable")]
    (set! (.InitialValue fv) (float f))
    fv))

(defn add-components [^GameObject prefab components]
  (doall (map (fn [{:keys [component paramf]}] 
    (paramf (ensure-cmpt prefab component))) components))
  prefab)

(def trigger-collider
  (SpawnComp. 
   Collider
   #(do
      (set! (.isTrigger %1) true))))

(defn oneshot [gob pos]
  (-> (parent! gob (:pool game-state))
    (instantiate pos)))

(defn when-player [^Collider collider f] (when (= (.. collider gameObject tag) "Player") (f)))

(defprotocol Trigger (run [this gstate]))

(defrecord RepeatTrigger [prefab prefab-fns gposmod gposoff]
  Trigger
  (run [this gstate] 
         (let [{:keys [gpos gspeed]} gstate
               gposv (.RuntimeValue gpos)
               gspeedv (.RuntimeValue gspeed)
               {:keys [prefab prefab-fn gposmod gposoff]} this
               offsetpos (+ gposv gposoff)
               lastpos (- offsetpos (* gspeedv Time/deltaTime))
               moddedpos (mod offsetpos gposmod)
               moddedlastpos (mod lastpos gposmod)]
           (when (< moddedpos moddedlastpos) (let [gob (instantiate prefab)] (doseq [prefab-fn prefab-fns] (prefab-fn gob gstate))))))) 

(defn repeat-trigger [prefab prefab-fn gposmod gposoff]
  (RepeatTrigger. (parent! prefab (:pool game-state)) prefab-fn gposmod gposoff))

(defn spawner-update [obj k]
  (let [spawnstate (state obj k)
        {:keys [triggers gstate]} spawnstate]
    (doseq [triggerf (vals triggers)] (run triggerf gstate))))

(defrole spawner-role :state { :triggers {}, :gstate game-state } :update #'spawner-update)

(def -cljspawner (volatile! nil))
(defn cljspawner []
    (if (. Application isPlaying)
      (or (if (not (UnityEngine.Object/op_Equality @-cljspawner nil)) 
              @-cljspawner)
        (vreset! -cljspawner 
          (or (GameObject/Find "game.road/-spawner")
                      (let [go (GameObject. "game.road/-spawner")] (role+ go :spawner spawner-role) go))))))

(defn destroy-on-trigger-player-enter [obj k col]
  (when-player col #(destroy obj)))
(defrole destroy-on-trigger-player-role :on-trigger-enter #'destroy-on-trigger-player-enter)

(defn destroy-after-time-update [obj k] 
  (when (> Time/time (-> obj (state k) (:destroy-time))) (destroy obj)))

(defn destroy-after-time-role [plustime]
  {:state {:destroy-time (+ Time/time plustime)} :update #'destroy-after-time-update})

(defn clear-triggers [] 
  (update-state (cljspawner) :spawner #(assoc % :triggers {}))
  (doall (map destroy (children (:pool game-state)))))

(defn groundmotion-update [obj k]
  (let [rolestate (state obj k)
        gamespeed (.RuntimeValue (rolestate :gamespeed))
        ]
    (set! (.. obj transform position) (v3+ (.. obj transform position) (v3 0 0 (* (- gamespeed) (Time/deltaTime)))))))

(defrole groundmotion-role
  :state {:pos (Resources/Load "ScriptableObjects/Variables/Position")
          :gamespeed (Resources/Load "ScriptableObjects/Variables/Global/GameSpeed")}
  :update #'groundmotion-update)

(defn spawnable 
  ([base roles comps] (spawnable base roles comps -32))
  ([base roles comps groundMotionLimit]
   (when groundMotionLimit (role+ base :groundmotion groundmotion-role))
   (roles+ base roles)
   (add-components base comps)
   (when groundMotionLimit
     (set! (.isTrigger (ensure-cmpt base Collider)) true)
     (set! (.zPos (ensure-cmpt base DestroyAfterPosition)) groundMotionLimit))
   base))

(defn cube
  ([s] (cube s s s))
  ([x y z]
  (let [c (create-primitive :cube)]
    (set! (.localScale (.transform c)) (v3 (float x) (float y) (float z)))
    c)))

(defn create-ground-col-with [gob comps]
  (add-components gob (into [] (concat comps [trigger-collider])))
  (role+ gob :groundmotion groundmotion-role))

(defn oneshot-comp [gob comps pos]
  (let [pooled (create-ground-col-with gob comps)]
    (oneshot pooled pos)))

(defn trigger-setpos [pos]
  (fn [gob gstate] (set! (.. gob transform position) pos)))

(defn trigger-setpos-fn [f]
  (fn [gob gstate] (set! (.. gob transform position) (f gstate))))

(defn add-trigger [name trigger]
  (update-state (cljspawner) :spawner (fn [spawnstate] (assoc spawnstate :triggers (assoc (spawnstate :triggers) name trigger)))))

(defn remove-trigger [name]
  (update-state (cljspawner) :spawner (fn [spawnstate] (assoc spawnstate :triggers (dissoc (spawnstate :triggers) name)))))

(defn toggle-trigger [name trigger]
  (if (contains? ((state (cljspawner) :spawner) :triggers) name) (remove-trigger name) (add-trigger name trigger)))


; (defn repeating [gob pos mod off]
;   (-> (parent! gob @POOL)
;       (repeat-trigger mod (.x pos) (.y pos) (.z pos) off)
;       (add-spawntrigger)))

; (defn repeating-comp [gob comps pos mod off]
;   (-> (create-ground-col-with gob comps)
;       (repeating pos mod off)))

; (defn create-spawn-info [^GameObject prefab x y z]
;   (let [si (new SpawnInfo)]
;     (set! (.xOffset si) x) 
;     (set! (.yOffset si) y) 
;     (set! (.zOffset si) z) 
;     (set! (.Prefab si) prefab)
;     si))

; (defn create-repeating
;   ([spawninfo mod] (create-repeating spawninfo mod 0))
;   ([spawninfo mod off]
;    (let [st (ScriptableObject/CreateInstance "RepeatingSpawnTrigger")]
;      (set! (.BeatMod st) mod)
;      (set! (.Offset st) off)
;      (set! (.Position st) (Resources/Load (str "ScriptableObjects/Variables/Position")))
;      (set! (.spawnInfo st) spawninfo)
;      st)))

; (defn repeat-trigger [prefab mod x y z off]
;   (spawn-trigger prefab x y z #(create-repeating %1 mod off)))

; (defn create-oneshot [spawninfo]
;   (let [st (ScriptableObject/CreateInstance "OneshotSpawnTrigger")]
;     (set! (.spawnInfo st) spawninfo)
;     st))

; (defn spawn-trigger [prefab x y z cfn]
;   (-> (create-spawn-info prefab x y z)
;       (cfn)))

; (defn oneshot-trigger [prefab x y z]
;   (spawn-trigger prefab x y z #(create-oneshot %1)))

; (defn add-spawntrigger [st]
;   (.. @SPAWNER SpawnTriggers (Add st)))
; (defrecord SpawnComp [component paramf])

; (defn clear-spawner []
;   (.. @SPAWNER SpawnTriggers
;     (Clear)))
