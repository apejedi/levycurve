(defproject levycurve "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :main levycurve.ui
  :aot [levycurve.ui]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
