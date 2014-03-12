(ns levycurve.core
  (:import
   (java.lang.Math)
   )
  (:require [clojure.zip :as zip])
  )

(defn gen-matrix [theta]
  "Generates a matrix using the given rotation angle used to generate a new coordinate point
  e.g. (gen-matrix 45) produces a combined rotation-scale matrix with angle 45 degrees"
  (let [radians (Math/toRadians theta)
        rotation [[(Math/cos radians) (- (Math/sin radians))]
                  [(Math/sin radians) (Math/cos radians)]]
        scale [[(/ 1 (Math/sqrt 2)) 0]
               [0 (/ 1 (Math/sqrt 2))]]
        mult-matrices (fn [m1 m2]
                        (let [[a b c d] (flatten m1)
                              [p q r s] (flatten m2)]
                          [[(+ (* a p) (* b r)) (+ (* a q) (* b s))]
                           [(+ (* c p) (* d r)) (+ (* c q) (* d s))]]
                          ))
        ]
    (mult-matrices rotation scale)
    )
  )

(defn gen-coord [p1 p2 matrix]
  "Generates a new coordinate given two coordinates and a transformation matrix
   e.g. (gen-coord [100 250] [400 250] [[a b] [c d]])"
  (let [
        [x1 y1] p1
        [x2 y2] p2
        x (- x2 x1)
        y (- y2 y1) ; Line relative to origin (0,0)
        apply-matrix (fn [x y matrix]
                       (let [[a1 a2 a3 a4] (flatten matrix)]
                         [(+ (* x a1) (* y a2))
                          (+ (* x a3) (* y a4))]))
        [x y] (apply-matrix x y matrix)]
    [(+ x x1) (+ y y1)]
    )
  )

(defn do-iteration [path matrix]
  "Iterates over a doubly linked list adding nodes along the way"
  (let [path (zip/vector-zip path)]
      (loop [node (-> path zip/root zip/next)]
        (let [p1 (zip/node node)
              p2 (-> node zip/right zip/node)
              new (zip/insert-right node (gen-coord p1 p2 matrix))
              ]
          (if (-> node zip/right zip/right)
            (recur (-> new zip/right zip/right))
            (zip/root new)
            )
          )
        )
    )
  )


;; (defn do-iteration []
;;   (let [draw (fn [p1 p2]
;;                  (let [[x1 y1] (flatten p1)
;;                        [x2 y2] (flatten p2)
;;                        [x3 y3] (flatten (gen-coord p1 p2 matrix))
;;                        ]
;;                    (.drawLine g x1 y1 x3 y3)
;;                    (.drawLine g x2 y2 x3 y3)
;;                    [x3 y3]
;;                    ))
;;              result (pmap draw path1 path2)
;;         ]
;;     (def res result)
;;     (def low 0)
;;     (def path1 (vec (concat path1 path2)))
;;     (def path2 (vec (concat result result)))
;;     ))
