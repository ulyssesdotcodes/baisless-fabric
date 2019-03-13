(ns game.palette
  (use arcadia.core arcadia.linear hard.core)
  (:import [UnityEngine Color Vector4]))

(defn css-color [color] 
  (let [ colors (rest (clojure.string/split color #""))
        red (take 2 colors)
        green (take 2 (drop 2 colors))
        blue (take 2 (drop 4 colors))]
    (map #(-> (conj % "0x") (clojure.string/join) (read-string)) [red green blue])))

(defn color-to-v3 [color] (apply v4 (concat (map float color) [0])))

(defn v4-to-color [color] (Color. (.x color) (.y color) (.z color) (.w color)))

(defn palette-lerp [pal val]
    (let [palp (conj pal (last pal))
          sx (* val (count pal))
          floored (Mathf/Floor sx)] 
    (Vector4/Lerp 
     (nth palp floored)
     (nth palp (Mathf/Ceil sx))
     (- sx floored))))

(def tealcontrast [(v4 188 242 246 0) (v4 50 107 113 0) (v4 211 90 30 0) (v4 209 122 43 0) (v4 188 242 246 0)])
(def purplish [(v4 150 110 100 0) (v4 223 143 67 0) (v4 76 73 100  0) (v4 146 118 133 0) (v4 165 148 180 0)])
(def sunset [(v4 185 117 19 0) (v4 228 187 108 0) (v4 251 162 1 0) (v4 255 243 201 0)])
(def coolpink [(v4 215 40 26 0) (v4 157 60 121 0) (v4 179 83 154 0) (v4 187 59 98 0)])
(def darkestred [(v4 153 7 17 0) (v4 97 6 11 0) (v4 49 7 8 0) (v4 13 7 7 0) (v4 189 5 13 0)])
(def nature [(v4 63 124 7 0) (v4 201 121 66 0) (v4 213 101 23 0) (v4 177 201 80 0) (v4 180 207 127 0)])
(def greenpurple [(v4 42 4 74 0) (v4 11 46 89 0) (v4 13 103 89 0) (v4 122 179 23 0) (v4 160 197 95 0)])
(def tealblue [(v4 188 242 246 0) (v4 50 107 113 0) (v4 188 242 246 0) (v4 165 148 180 0)])
(def bnw [(v4 255, 255, 255 0), (v4 0, 0, 0 0)])
(def neon  (map (comp #'color-to-v3 #'css-color) ["A9336B", "5F2F88", "CB673D", "87BB38"]))
(def fire  (map (comp #'color-to-v3 #'css-color) ["F07F13", "800909", "F27D0C", "FDCF58"]))
(def buddhist  (map (comp #'color-to-v3 #'css-color) ["0000FF", "FFFF00", "FF0000", "FFFFFF", "FF9800"]))
(def flower  (map (comp #'color-to-v3 #'css-color) ["000E00", "003D00", "E4A900", "FEDEEF", "C99CB8"]))
(def bluepink  (map (comp #'color-to-v3 #'css-color)  ["F2C6F2", "F8F0F0", "A6D1FF", "3988E1", "4C8600"]))
(def lime  (map (comp #'color-to-v3 #'css-color) ["FF4274", "DCD549", "ABDFAB", "437432", "033B45"]))