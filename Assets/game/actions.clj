(ns game.actions
  (use arcadia.core arcadia.linear game.spawner game.cam game.palette tween.core)
  (:import
   [UnityEngine Resources Color Light Renderer Vector4]
   [UnityEngine.Animations LookAtConstraint]
   [UnityEngine.Experimental.VFX VisualEffect]
   [UnityEngine.Experimental.Rendering.HDPipeline HDAdditionalLightData]
   [UnityEngine.Rendering.PostProcessing
    PostProcessManager
    PostProcessLayer
    PostProcessVolume]
   Invert
   GlitchOnCollision
   CharacterControl
   [game.spawner SpawnComp]))

(defn play-impact [obj k col] (when-player col #(play-shake (new-impact-shake))))

(defn invert-on-collision [obj k col] 
  (let [profile (.. PostProcessManager instance
                  (GetHighestPriorityVolume (cmpt (.. Camera main gameObject) PostProcessLayer)) profile)
        settings (->> (.settings profile) (filter (comp #(= Invert %) #(.GetType %))) (first))]
    (when-player col #(set! (.. settings invert value) (Mathf/Abs (- (.. settings invert value) (float 1)))))))

(defrole invert-toggle-role :on-trigger-enter #'invert-on-collision)

(defn player-vfx-toggle-oncol [obj k col] (when-player col
  #(let [pvfx (object-named "PlayerVFX")
        current (state pvfx :vfx-active)
        curornil (or current false)]
    (println col)
    (state+ pvfx :vfx-active (not curornil))
    (set! (.visualEffectAsset (cmpt pvfx VisualEffect)) (if curornil nil (Resources/Load "VFX/PlayerParticles")))
    (destroy obj))))

(defn movecam [cam duration]
    (timeline* (tween {:local {:position (cam :position)}} (:cam game-state) duration {:in pow3}))
    (timeline* (tween {:lookatrotation (cam :rotation)} (cmpt (:cam game-state) LookAtConstraint) duration {:in pow3})))

(defn movecam-oncol [obj k col]
  (when-player col #(movecam ((state obj k) :cam-move) ((state obj k) :cam-dur))))

(defn movecam-role [cam dur]
  {:state {:cam-move cam :cam-dur dur} :on-trigger-enter #'movecam-oncol})

(defn create-glow-light ([lumens] (create-glow-light lumens (Color/white)))
  ([lumens col] (let [gob (spawnable (GameObject/Instantiate (Resources/Load "Prefabs/GlowLight")) {} [] -8)
        light (cmpt (first (children gob)) Light)
        hdlight (cmpt (first (children gob)) HDAdditionalLightData)
        mat (.material (cmpt gob Renderer))]
    (set! (.intensity hdlight) lumens)
    (set! (.color light) col)
    (.SetFloat mat "_Lumens" lumens)
    (.SetColor mat "_Color" col)
    gob)))

(defn update-glowlight-color [color-fn]
  (fn [gob gstate] 
    (let [light (cmpt (first (children gob)) Light)
          hdlight (cmpt (first (children gob)) HDAdditionalLightData)
          mat (.material (cmpt gob Renderer))
          newcol (Color/Lerp (Color/black) (v4-to-color (color-fn gstate)) 0.01)]
      (set! (.color light) newcol)
      (.SetColor mat "_Color" newcol)
      gob)))

(defn circlelights [gposmod lspeed lumens palette pspeed] 
  (repeat-trigger
   (create-glow-light lumens)
   [(update-glowlight-color #(palette-lerp palette (gpos-mod %1 pspeed 1 1)))
    (trigger-setpos-fn #(v3 (gpos-cos %1 1 lspeed) (Math/Abs (gpos-sin %1 1 3)) 16))]
   gposmod 0))

(defn toggle-lights-on-trigger-enter [obj k col]
  (when-player col
   #(let [{:keys [gposmod lspeed lumens palette pspeed]} (state obj k)]
       (toggle-trigger :lights (circlelights gposmod lspeed lumens palette pspeed))
       (destroy obj))))

(defn toggle-lights-role [gposmod lspeed lumens palette pspeed]
  {:state {:gposmod gposmod :lspeed lspeed :lumens lumens :palette palette :pspeed pspeed}
   :on-trigger-enter #'toggle-lights-on-trigger-enter}
  )

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

(defn slowplayer-on-trigger-enter [obj k col]
  (when-player col #(.OnSlow (cmpt (.gameObject col) CharacterControl) ((state obj k) :amount))))

(defn slowplayer-role [amt] {:state {:amount amt} :on-trigger-enter #'slowplayer-on-trigger-enter})

(defn spawned-vfx-comp [^Vector4 color]
  (SpawnComp. 
    VisualEffect 
    (fn [vfxcmpt]
      (set! (.enabled vfxcmpt) false)
      (set! (.visualEffectAsset vfxcmpt) (Resources/Load "VFX/SpawnImpulse"))
      (.SetVector4 vfxcmpt "Color" color)
      vfxcmpt)))

(defn spawn-vfx-toggle-on-trigger-enter [obj k col]
  (when-player 
   col 
   #(do 
      (set! (.enabled (cmpt obj VisualEffect)) true)
      (set! (.enabled (cmpt obj Renderer)) false)
      (role- obj :groundmotion)
      (role+ obj :destroyafter (destroy-after-time-role 10)))))

(defrole spawn-vfx-toggle-role :on-trigger-enter #'spawn-vfx-toggle-on-trigger-enter)


