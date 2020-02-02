(ns game.props
  (use arcadia.core arcadia.linear hard.core game.core)
  (:import [UnityEngine Debug Resources GameObject]
           ))


(defn add-render [tag fab]
  (let [p (v3 (?f -32 32) 1 (?f -32 32))]
  (add-obj
    :uniq
    tag
    (instantiate-in-area (Resources/Load (str "Prefabs/FindEachother/Objects/" fab)))
    false
    p)
  (add-obj
    :uniq
    tag
    (UnityEngine.Object/Instantiate (Resources/Load (str "Prefabs/FindEachother/Objects/" fab)) (.transform render-area))
    false
    p)))



(defn add-cubewall []
  (add-render :wall "CubeWall"))

(defn add-column []
  (add-render :wall "Column"))

(defn add-rectwall []
  (add-render :wall "RectWall"))

(defn add-moveable-box []
  (add-render :movable "Movable Box"))

(defn add-movable-wedge []
  (add-render :movable "Movable Wedge"))

(defn add-big-consumable []
  (add-render :consumable "Big consumable"))

(defn add-small-consumable []
  (add-render :consumable "Small consumable"))

(defn add-random-prop []
  (let [n (rand-int 7)]
  (case n
      0 (add-cubewall)
      1 (add-column)
      2 (add-rectwall)
      3 (add-moveable-box)
      4 (add-movable-wedge)
      5 (add-big-consumable)
      6 (add-small-consumable))))

