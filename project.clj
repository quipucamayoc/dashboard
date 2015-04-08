(defproject dashboard "0.5.0"
            :description "A viewer into the OSC universe"
            :url "http://quipucamayoc.com/"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}

            :dependencies [[org.clojure/clojure "1.7.0-alpha6"]
                           [org.clojure/clojurescript "0.0-3178"]
                           [org.clojure/core.async "0.1.346.0-17112a-alpha"]]

            :node-dependencies [[source-map-support "0.2.10"]
                                [osc-min "0.2.0"]
                                [blessed "0.0.51"]
                                [blessed-contrib "2.0.2"]]

            :plugins [[lein-cljsbuild "1.0.6-SNAPSHOT"]
                      [lein-ancient "0.6.6"]
                      [lein-cljfmt "0.1.10"]
                      [lein-marginalia "0.8.0"]
                      [lein-npm "0.5.0"]]

            :source-paths ["src"]

            :main "run/out/dash.js"

            :clean-targets ["run/out" "run/dash.js" "run/dash.js.map"]

            :cljsbuild {
                        :builds [{:id           "core"
                                  :source-paths ["src"]
                                  :compiler     {:source-map    "run/dash.js.map"
                                                 :output-to     "run/dash.js"
                                                 :output-dir    "run/out"
                                                 :target        :nodejs
                                                 :optimizations :none
                                                 :main          dashboard.display
                                                 :pretty-print  true}}]})
