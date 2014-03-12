(ns levycurve.ui
  (:gen-class
   :name levycurve.ui
   :init init
   :state state
   :post-init setup
   :import (javax.swing JFrame)
   :methods [
             [setState [clojure.lang.Keyword Object] void]
             [getState [clojure.lang.Keyword] Object]
             ]
   :exposes-methods {paint superpaint}
   :extends javax.swing.JFrame
   )
  (:import
   (javax.swing JButton JTextField JLabel)
   (java.awt Toolkit Dimension Color FlowLayout)
   (java.awt.event ActionListener)
   (java.awt.image BufferedImage)
   )
  (:require [levycurve.core :as fractal])
  )
(declare -start)
(declare -step)
(declare -paint)
(declare -set-state)
(declare -get-state)

(defn -main
  [& args]
  (levycurve.ui.)
  )

(defn -init []
  "Sets Initial State"
  [["Levy Curve Generator"] (atom {:path [[100 250] [400 250]], :angle 45, :iterations 15, :color 65330})]
  )

(defn -setup [ui]
  "Setup UI components, listeners"
  (let [screen-size (.getScreenSize (Toolkit/getDefaultToolkit))
        width (.width screen-size)
        height (.height screen-size)
        comp-size (Dimension. 100 20) ;Component Size
        components {:angle-label (JLabel. "Angle")
                    :angle (JTextField. "45")
                    :iterations-label (JLabel. "Number of Iterations(Max 15)")
                    :iterations (JTextField. "14")
                    :step (JButton. "Step")
                    :start (JButton. "Start")
                    :save (JButton. "Save")}
        create-listener (fn [f & args]
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (apply f args)))
                          )
        actions {:start (create-listener -start)
                 :step (create-listener -step ui)
                 }
        img (BufferedImage. width (- height 50) BufferedImage/TYPE_INT_RGB)
        graphics (.createGraphics img)
        ]
    (doto ui ;Set size and add state
      (.setSize (Dimension. width height))
      (.setVisible true)
      (.setLayout (FlowLayout.))
      (.setState :components components)
      (.setState :img img)
      (.setState :graphics graphics)
      )
    (doseq [[name component] components] ;Add Components and their action listeners
      (.setSize component comp-size)
      (.add ui component)
      (if (contains? actions name)
        (.addActionListener component (get actions name))
        ))
    (doto graphics
      (.setColor Color/black)
      (.fillRect 0 0 (.getWidth img) (.getHeight img))
      )
    )
  )
(defn -getState [this key]
  (get @(.state this) key)
  )
(defn -setState [this key val]
  (swap! (.state this) conj [key val])
  )
(defn -paint [this g]
  (.superpaint this g)
  (.drawImage (.getGraphics this) (get @(.state this) :img) 0 50 nil)
  )
(defn -start []
  (println "starting")
  )
(defn -step [ui]
  (let[
       path (.getState ui :path)
       color (.getState ui :color)
       graphics (.getState ui :graphics)
       angle (.getState ui :angle)
       matrix (fractal/gen-matrix angle)
       ]
    (loop [p path col color]
      (let [[x1 y1 x2 y2] (flatten (take 2 p))]
        (.setColor graphics (Color. col))
        (.drawLine graphics x1 y1 x2 y2)
        (.repaint ui)
        (if (seq (-> p rest rest))
          (recur (rest p) (+ col 10))
          (do
            (.setState ui :color col)
            (.setState ui :path (fractal/do-iteration path matrix))
            ))
       )
     )
   )
  )


(defn -draw []

  )
