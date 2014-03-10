(ns levycurve.core
  (:gen-class
   :name levycurve.core
   (:import (java.awt.Dimension)
            (java.lang.Math)
            )
   (:require '[clojure.zip :as zip])
   :extends javax.swing.JFrame
   ))
(def ^:dynamic *path* (zip/vector-zip [[100 250] [400 250]]))
(def ^:dynamic *matrix* (gen-matrix 45))


(defn -main
  "Setup UI components
   Initialize Data Structure"
  [& args]
  )

(defn reset []
  (.clearRect g 0 0 500 500)
  )

(defn do-iteration []
  (loop [node (zip/next *path*)]
    (let [p1 (zip/node node)
          p2 (-> node zip/right zip/node)
          p3 (gen-coord p1 p2 *matrix*)
          new (zip/insert-right node p3)
          [x1 y1 x2 y2 x3 y3] (flatten [p1 p3 p2])
          ]
      (.drawLine g x1 y1 x2 y2)
      (.drawLine g x2 y2 x3 y3)
      (if (-> node zip/right zip/right)
        (recur (-> new zip/right zip/right))
        (def ^:dynamic *path* (zip/vector-zip (zip/root new)))
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
