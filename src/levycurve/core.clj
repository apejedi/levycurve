(ns levycurve.core
  (:gen-class
   :name levycurve.core
   (:import (java.awt.Dimension)
            (java.lang.Math))
   :extends javax.swing.JFrame
   ))

(defn -main
  "Setup UI components
   Initialize Data Structure"
  [& args]
  (println "Hello, World!"))

(defn gen-matrix [theta]
  (let [radians (Math/toRadians theta)
        rotation [[(Math/cos radians) (Math/sin radians)]
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
        (let [[x1 y1] p1
              [x2 y2] p2
              x (Math/abs (- x2 x1))
              y (Math/abs (- y2 y1)) ; Line relative to origin (0,0)
              apply-matrix (fn [x y matrix]
                             (let [[a1 a2 a3 a4] (flatten matrix)]
                               [(+ (* x a1) (* y a2))
                                (+ (* x a3) (* y a4))]))
              [x y] (apply-matrix x y matrix)]
          [(+ x x1) (+ y y1)]
          )
        )
