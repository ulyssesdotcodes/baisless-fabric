(ns game.road
  (use
   arcadia.core
   arcadia.linear
   tween.core
   hard.core
   game.spawner
   game.utils
   game.cam
   game.actions
   game.palette)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            PrimitiveType Collider Light Renderer
            Color Mathf Application Debug]
           [UnityEngine.Experimental.VFX VisualEffect]
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
           CameraShakeOnCollision
           StopCameraShakeOnCollision
           CameraShake
           CameraShakeManager
           CameraShake+ShakeType
           CameraShake+NoiseType
           DestroyAfterPosition
           VFXOnCollision
           Invert
           [game.spawner SpawnComp]))

(use 'game.actions :reload)
(use 'game.cam :reload)

(use 'game.spawner :reload)
(use 'game.palette :reload)

(remove-ns 'game.road)

; setup
(do

  (deftween [:lookatrotation] [this]
    {:get (.rotationOffset this)
     :tag UnityEngine.Vector3})

  (def cam1 {:rotation (v3 -13 16 -1) :position (v3 0.6 0.47 3.52)})
  (def cam2 {:rotation (v3 -15 -28 13.1) :position (v3 -0.11 2.49 -4.03)})
  (def firevfx (Resources/Load "VFX/Fire"))
  (def rainvfx (Resources/Load "VFX/Rain"))
  (def clearvfx nil)
  (defn vfx-oncol [vfx]
    (SpawnComp. VFXOnCollision #(do (set! (.Asset %1) vfx))))
  (defn color-to-v4 [col]
    (v4 (.r col) (.g col) (.b col) (.a col)))
  (defn v3-to-v4 [vec z]
    (v4 (.x vec) (.y vec) (.z vec) (.a vec)))
  )

(speed 1)
(clear-triggers)

(def globalpal buddhist)

(speed 0.8)

(oneshot 
 (spawnable 
  (cube 0.2) 
  {:destroy destroy-on-trigger-player-role}
  [(glitch-oncol 0 0)])
 (v3 1.5 0.5 16))

(oneshot 
 (spawnable 
  (create-glow-light 0) 
  {:togglelights (toggle-lights-role 1 4 40 globalpal 0.123)} 
  [trigger-collider]) 
 (v3 1.5 0.5 2))

(toggle-trigger :invert
  (repeat-trigger 
    (spawnable 
      (cube 4 0.2 0.2) 
      {:invert-toggle invert-toggle-role 
       :slowplayer (slowplayer-role 0.33) 
       :destroy destroy-on-trigger-player-role} 
      []) 
    [(trigger-setpos (v3 0 0.5 16))]
    16 0))


(toggle-trigger 
 :dust
  (repeat-trigger
   (spawnable 
    (cube 0.2) 
    {:spawnvfxtoggle spawn-vfx-toggle-role} 
    [(spawned-vfx-comp (color-to-v4 Color/red))])
   [(trigger-setpos (v3 1.5 0.5 1))
    (fn [gob gstate]
      (let [vfxcmpt (cmpt gob VisualEffect)]
        (set! (.visualEffectAsset vfxcmpt) (Resources/Load "VFX/SpawnImpulse"))
        (.SetVector4 vfxcmpt "Color" (palette-lerp globalpal (gpos-mod gstate 0.123 1 1)))))]
   16 12))

(toggle-trigger 
 :cam2
 (repeat-trigger 
  (spawnable (cube 0.8) 
             {:movecam (movecam-role cam2 1) 
              :destroy destroy-on-trigger-player-role} [])
  [(trigger-setpos (v3 1.5 0.5 4))]
  64 32))

(toggle-trigger 
 :cam1
 (repeat-trigger 
  (spawnable (cube 0.8) 
             {:movecam (movecam-role cam1 1) 
              :destroy destroy-on-trigger-player-role} [])
  [(trigger-setpos (v3 1.5 0.5 4))]
  64 0))

(palette-lerp globalpal 0.4)

(speed 0.2)

(movecam cam1 1)

(* 4 (Mathf/Cos (* (.RuntimeValue (:gpos %)) 0.2))) (* 4 (Mathf/Sin ((.RuntimeValue (:gpos %)) 0.2))) 

(Mathf/Cos 0.2)

(destroy (object-named "GlowLight(Clone)"))


; useful stuff
(clear-pool)

(speed 1)
(movecam cam2 2)

(def ambient-shake (new-ambient-shake 0.33))
(play-shake ambient-shake)

(def impact-shake (new-impact-shake))
(play-shake impact-shake)

(stop-shake ambient-shake)
(stop-all-shakes)

(destroy (object-named "Sphere(Clone)"))
(destroy (object-named "Sphere"))
(destroy (object-named "GlowLight(Clone)"))
(destroy (object-named "Cube"))
(destroy (object-named "Cube(Clone)"))

(defn clearvfx-oncol [] (SpawnComp. VFXOnCollision #(do %1)))

(rainvfx-oncol)

(set! (.visualEffectAsset (cmpt (object-named "PlayerVFX") VisualEffect)) (Resources/Load "VFX/PlayerParticles"))

(oneshot-comp (create-primitive :cube) [(vfx-oncol firevfx)] (v3 0.5 0.5 4))
(oneshot-comp (create-primitive :sphere) [(vfx-oncol rainvfx)] (v3 0.5 0.5 4))

(oneshot-trigger-enter )

(clear-spawner)

(instantiate (create-ground-col-with @POOL (create-primitive :cube) ) )
(instantiate (create-ground-col-with @POOL (create-primitive :cube) [(vfx-oncol clearvfx)]) (v3 0.5 0.5 4))

(destroy (object-named "GlowLight(Clone)"))



(stop-all-shakes)

; next I'll make this into a roles sytem instead of the hodgepodge now
; it'll let us add roles but also ensure components like colliders

(defn oneshot-trigger-enter [obj f pos]
  (oneshot-comp (do (hook+ obj :on-trigger-enter :k f) obj) 
                [(SpawnComp. DestroyAfterPosition #(set! (.zPos %) -7))
                (SpawnComp. Collider #(set! (.isTrigger %) true))] pos))

; e.g. role we want. state is on the spawned object
; actually let's do something with state
; Ugh, docs are wrong here
{:state {:cam cam1 :duration 2} :on-trigger-enter #'movecam-ontrigger}

; Gotta run get dinner, thanks for watching!

(use 'game.actions :reload)

(defn movecam-mod [{:keys [cam duration]}]
    (timeline* (tween {:lookatrotation (cam :rotation)} (cmpt @CAMPAR LookAtConstraint) duration {:in pow3}))
    (timeline* (tween {:local {:position (cam :position)}} @CAMPAR duration {:in pow3})))

(defn movecam-ontrigger [obj k col]
  (when-player col #(movecam (state obj k))))

(defn movecam-ontriggerrole [cam duration]
  {:state {:cam cam :duration duration} :on-trigger-enter #'movecam-ontrigger})

; not sure if this will work with def instead of defn


(destroy "Cube (Clone)")

(oneshot (spawnable (cube 0.2) {:move-cam (movecam-ontriggerrole cam2 1)} []) (v3 1.5 0.5 4))


(destroy (object-named "Cube(Clone)"))

(oneshot-trigger-enter (cube 0.2) #'player-vfx-toggle-oncol (v3 1.5 0.5 4))
(oneshot-trigger-enter (cube 0.2) #'invert-on-collision (v3 1.5 0.5 4))
(oneshot-trigger-enter (let [c (cube 0.2)] (state+ c :cam-move cam1) (state+ c :cam-dur 0.3) c) #'movecam-oncol (v3 1.5 0.5 4))


; 1. How it is now - spawner has a list that it checks in on in update
; 2. spawner has a list with each spawntrigger activated in update Each spawntrigger does its own spawning - more feasible with functional programming
;   Problem: defrecord doesn't have methods
;   Solution: defprotocol? just pass functions?
; 3. gameobject with each spawntrigger having an update state - difficult because method has to be its own defn


(speed 0.2)

(update-state (cljspawner) :spawner #(assoc % :triggers (conj (% :triggers) (repeat-trigger-rec (spawnable (cube 0.2) {:invert-toggle inverttoggle-role} []) (constantly (v3 1 1 4)) 4 0))))

(update-state (cljspawner) :spawner #(assoc % :triggers []))

(def rpt (RepeatTrigger. (spawnable (cube 0.2) {:invert-toggle inverttoggle-role} []) (constantly (v3 1 1 4)) 4 0))

(spawn rpt 4 2)



(spawn (SpawnTriggerImpl. 0))



(filter identity (map #(spawn % pos speed) triggers))



(destroy (cljspawner))




        List<SpawnTrigger> removes = new List<SpawnTrigger>();
        foreach(SpawnTrigger st in SpawnTriggers) {
            Optional<SpawnInfo> spawn = st.Spawn();
            if(!spawn.isNothing) {
                SpawnInfo si = spawn.value;
                GameObject instantiated = 
                    Instantiate(
                        si.Prefab, 
                        new Vector3(si.xOffset, si.yOffset, 4f), 
                        Quaternion.identity
                    );
                if(st.oneshot) {
                    removes.Add(st);
                }
            }
        }

        foreach(SpawnTrigger rem in removes) {
            SpawnTriggers.Remove(rem);
        }

        bool shouldSpawn = (Position.RuntimeValue + Offset) % BeatMod < (lastPosition + Offset) % BeatMod;
        lastPosition = Position.RuntimeValue;
        return shouldSpawn ? Optional<SpawnInfo>.of(spawnInfo) : Optional<SpawnInfo>.none();

(do
  (clear-pool)
  (clear-spawner)
  (repeating 
  (create-glow-light Color/cyan 400) 
  (v3 4 2 8) 8 0)
  (repeating 
  (create-glow-light Color/red 400) 
  (v3 -4 2 8) 8 4)
  )