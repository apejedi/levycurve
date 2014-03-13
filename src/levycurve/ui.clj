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
             [reset [] void]
             ]
   :exposes-methods {paint superpaint}
   :extends javax.swing.JFrame
   )
  (:import
   (javax.swing JButton JTextField JLabel JOptionPane)
   (java.io File)
   (javax.imageio ImageIO)
   (javax.swing.event DocumentListener)
   (java.awt Toolkit Dimension Color FlowLayout)
   (java.awt.event ActionListener MouseAdapter MouseMotionAdapter)
   (java.awt.image BufferedImage)
   )
  (:require [levycurve.core :as fractal])
  )
(declare -start)
(declare -step)
(declare -paint)
(declare -set-state)
(declare -get-state)
(declare -reset)
(declare -save)

(defn -main
  [& args]
  (levycurve.ui.)
  )

(defn -init []
  "Sets Initial State"
  [["Levy Curve Generator"] (atom {:path [], :angle 45, :iterations 15, :color 16711680})]
  )

(defn -setup [ui]
  "Setup UI components, listeners"
  (let [screen-size (.getScreenSize (Toolkit/getDefaultToolkit))
        width (- (.width screen-size) 400)
        height (- (.height screen-size) 100)
        comp-size (Dimension. 100 20) ;Component Size
        components {:angle-label (JLabel. "Angle")
                    :angle (JTextField. "45")
                    :step (JButton. "Step")
                    :save (JButton. "Save")
                    :reset (JButton. "Reset")}
        create-listener (fn [f & args]
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (apply f args)))
                          )
        actions {:step (create-listener -step ui)
                 :reset (create-listener -reset ui true)
                 :save (create-listener -save ui)
                 }
        img (BufferedImage. width height BufferedImage/TYPE_INT_RGB)
        graphics (.createGraphics img)
        ]
    (doto ui ;Set size and add state
      (.setSize (Dimension. width height))
      (.setVisible true)
      (.setLayout (FlowLayout.))
      (.setState :components components)
      (.setState :img img)
      (.setState :graphics graphics)
      (.addMouseListener (proxy [MouseAdapter] [] ;Listener to collect initial points set by user
                           (mouseClicked [e]
                             (let [
                                   path (.getState ui :path)
                                   ]
                               (if (<= (count path) 1)
                                 (.setState ui :path (conj path [(.getX e) (.getY e)]))
                                 )
                               )
                             )
                           ))
      (.addMouseMotionListener (proxy [MouseMotionAdapter] [] ;Listener to display initial line being drawn by user
                                 (mouseMoved [e]
                                   (let [
                                         path (.getState ui :path)
                                         g (.createGraphics (.getState ui :img))
                                         [x y] (flatten (first path))
                                         ]
                                     (if (= (count path) 1)
                                       (do (.reset ui)
                                           (.drawLine g x y (.getX e) (.getY e))
                                           (.repaint ui))
                                       )
                                     )
                                   )
                                 ))
      )
    (doseq [[name component] components] ;Add Components and their action listeners
      (.setSize component comp-size)
      (.add ui component)
      (if (contains? actions name)
        (.addActionListener component (get actions name))
        ))
    (.addDocumentListener (.getDocument (:angle components))
                          (proxy [DocumentListener] []
                            (changedUpdate [e]
                              (.setState ui :angle (Integer/parseInt (.getText (:angle components))))
                              )
                            (insertUpdate [e]
                              (.setState ui :angle (Integer/parseInt (.getText (:angle components))))
                              )
                            (removeUpdate [e]
                              )
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
  (let [
        size (.getSize this)
        img (.getState this :img)
        ]
    (if (or (not= (.getWidth this) (.getWidth img)) (not= (.getHeight this) (.getHeight img)))
      (.fillRect (.getGraphics this) 0 100 (.getWidth this) (.getHeight this))
      )
    (.drawImage (.getGraphics this) img 0 70 nil)
    )
  )
(defn -save [ui]
  (ImageIO/write (.getState ui :img) "png" (File. "Fractal.png"))
  )
(defn -reset [this & clearpath]
  (let [
        img (.getState this :img)
        graphics (.createGraphics img)
        ]
    (.flush img)
    (.setColor graphics Color/black)
    (.fillRect graphics 0 0 (.getWidth img) (.getHeight img))
    (.repaint this)
    )
  (if clearpath
    (do
      (.setState this :path [])
      (.reset this)))
  )

(defn -step [ui]
  (let[
       path (.getState ui :path)
       color (.getState ui :color)
       img (.getState ui :img)
       graphics (.createGraphics img)
       angle (.getState ui :angle)
       matrix (fractal/gen-matrix angle)
       ]
    (.reset ui)
    (if (< (count path) 2)
      (JOptionPane/showMessageDialog nil "Please select two points in the drawing area" "No Points Selected" JOptionPane/INFORMATION_MESSAGE)
      (loop [p path col color]
        (let [[x1 y1 x2 y2] (flatten (take 2 p))]
          (.setColor graphics (Color. col))
          (.drawLine graphics x1 y1 x2 y2)
          (if (seq (-> p rest rest))
            (recur (rest p) (+ col 10))
            (do
              (.repaint ui)
              (.setState ui :color col)
              (.setState ui :path (fractal/do-iteration path matrix))
              ))
          )
        ))
   )
  )
