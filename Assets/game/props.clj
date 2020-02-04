(ns game.props
  (use arcadia.core arcadia.linear hard.core game.core)
  (:import [UnityEngine Debug Resources GameObject]
           ))


(defn add-render 
  ([tag fab y] (add-render tag fab y 32))
  ([tag fab y dist]
    (let [p (v3 (?f (* -1 dist) dist) y (?f (* -1 dist) dist))]
    (add-obj
      :uniq
      tag
      (instantiate-in-area (Resources/Load (str "Prefabs/FindEachother/Objects/" fab)))
      false
      p))))



(defn add-cubewall []
  (add-render :wall "CubeWall" 1))

(defn add-column []
  (add-render :wall "Column" 1))

(defn add-rectwall []
  (add-render :wall "RectWall" 1))

(defn add-moveable-box []
  (add-render :movable "Movable Box" 0.5))

(defn add-movable-wedge []
  (add-render :movable "Movable Wedge" 0.5))

(defn add-big-consumable []
  (add-render :consumable "Big consumable" 0.5 24))

(defn add-small-consumable []
  (add-render :consumable "Small consumable" 0.5 24))

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

