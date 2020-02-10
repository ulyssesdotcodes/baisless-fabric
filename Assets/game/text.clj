(ns game.text
  (use arcadia.core arcadia.linear hard.core game.core)
  (:import [UnityEngine Debug Resources GameObject TextAnchor]
           [UnityEngine.UI Text] 
           TransformEvent ResetEvent))

; Text

(defn create-text [ty text]
  (add-obj 
    ty
    :text
    (let [obj (new GameObject)
          txt (cmpt+ obj Text) ]
      (child+ (get-obj :names) obj)
      (set! (.text txt) text) 
      (set! (.font txt) (Resources/Load "Font/SpaceMono-Regular"))
      obj)))

(defn create-title-text [text]
  (let [obj (create-text :title text) 
        txt (cmpt obj Text)
        rect (.rectTransform txt)
        ]
    (set! (.. obj transform localPosition) (v3 0 -100 0))
    (set! (.. txt rectTransform sizeDelta) (v2 300 50))
    (set! (.. txt alignment) (TextAnchor/UpperCenter))
    (set! (.. rect anchorMax) (v2 0.5 1))
    (set! (.. rect anchorMin) (v2 0.5 1))
    ))

(defn create-score-text [ty text]
  (let [obj (create-text ty text)
        txt (cmpt obj Text)
        rect (.rectTransform txt)
        align (if (= ty :left) (TextAnchor/LowerLeft) (TextAnchor/LowerRight))]
    (set! (.. rect anchorMax) (v2 0.9 0.85))
    (set! (.. rect anchorMin) (v2 0.1 0.15))
    (set! (.. txt alignment) align)
    (set! (.. txt fontSize) (int 56))
    ))

(defn update-score-text [ty text]
  (let [obj (get-obj ty)
        txt (cmpt obj Text)]
    (set! (.text txt) text)))
