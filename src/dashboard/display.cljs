(ns dashboard.display
  "An in-terminal dashboard for simple device monitoring.

  Uses the blessed and blessed-contrib libraries for node."
  (:require [cljs.nodejs :as nodejs]
            ;[galileo.comm :as comm :refer [pass->]]
            [cljs.core.async :as a :refer [<!]]
            [goog.object :as o])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defonce dashboard (atom {}))

(defonce blessed
         (nodejs/require "blessed"))

(defonce contrib
         (nodejs/require "blessed-contrib"))

(defonce screen
         (.screen blessed))

(defonce grid
         (new contrib.grid #js {:rows 12 :cols 12 :screen screen}))

(defn- create-layout []
  (reset! dashboard
          {:device-table
           (.set grid 0 0 6 6
                 contrib.table
                 #js {:label         "Connected Beans"
                      :fg            "green"
                      :columnWidth   #js [10 5 5 5]
                      :columnSpacing 1
                      :keys          true})
           :event-log
           (.set grid 6 0 6 6
                 contrib.log
                 #js {:label      "Event Log"
                      :fg         "green"
                      :selectedFg "green"})
           :sensor-history
           (.set grid 0 0 12 6
                 contrib.sparkline
                 #js {:label "Sensor History"
                      :tags  true
                      :style #js {:fg "blue"}})}))

(defonce device-table
         (.set grid 0 0 6 6
                            contrib.table
                            #js {:label         "Connected Beans"
                                 :fg            "green"
                                 :columnWidth   #js [10 5 5 5]
                                 :columnSpacing 1
                                 :keys          true}))
(defonce sensor-history
  (.set grid 0 0 12 6
        contrib.sparkline
        #js {:label "Sensor History"
             :tags  true
             :style #js {:fg "blue"}}))

(defonce event-log
         (.set grid 6 0 6 6
               contrib.log
               #js {:label      "Event Log"
                    :fg         "green"
                    :selectedFg "green"}))

(defn prog-log
  "Prints data from the program into the dashboard."
  [& args]
  (let [entry (apply str args)]
    (.log (:event-log @dashboard) entry)))

(defn osc-log
  "Prints what is sent via OSC into the dashboard."
  [& args]
  (let [entry (apply str args)]
    #_(.log (:event-log @dashboard) entry)))

(defn start-osc-log
  "Listens for new messages on comm/osc-chan."
  []
  #_(go-loop []
           (when-let [v (<! comm/osc-chan)]
             (comment osc-log (:msg v))                             ;; :msg contains the above :response
             (recur))))

(defn start-prog-log
  "Listens for new messages on comm/log-chan."
  []
  #_(go-loop []
           (when-let [v (<! comm/log-chan)]
             (prog-log (:msg v))                            ;; :msg contains the above :response
             (recur))))

(defn readable-device-key
  "Takes the device UUID and displays just the first four characters."
  [device-key]
  (->> device-key
       name
       (take 4)
       (apply str)))

(defn clean-axis [axis]
  (concat [350 0]
          (if (nil? axis)
            [0]
            axis)))

(defn device-history-data
  "Takes a device map and returns axis data. Todo, return all sensors agnostically."
  [[_ {:keys [sensors]}]]
  (let [{x :x y :y z :z} sensors]
    [(clean-axis x) (clean-axis y) (clean-axis z)]))

(defn device-history-names
  "Merges device UUIDs and sensor names."
  [peripherals]
  (apply concat
         (map (fn [device-key]
                (map #(str ":  " (readable-device-key device-key) " { " (name %) " }  ")
                     (keys (:sensors (device-key peripherals)))))
              (keys peripherals))))

(defn clean-last-axis [axis]
  (let [last-axis (last axis)]
    (if (nil? last-axis)
      0
      last-axis)))

(defn build-row
  "Returns a row containing only latest sensor values from device. TO-DO: Remove hardcoded sensors"
  [[_ {:keys [localName sensors]}]]
  (let [{x :x y :y z :z} sensors]
    [(str localName) (clean-last-axis x) (clean-last-axis y) (clean-last-axis z)]))

(defn update-graphs
  "Updates the gaphics that represent real-time & over-time device status and sensor values."
  [peripherals]
  (let [hist-header (device-history-names peripherals)
        hist-data (mapv vec (apply concat (map device-history-data peripherals)))
        device-table-data (mapv build-row peripherals)]

    ; Update list of devices and sensor values.
    (.setData (:device-table @dashboard)
              (clj->js {:headers ["name" "x" "y" "z"]
                        :data    device-table-data}))
    ; Update sparkline of sensor values.
    (.setData (:sensor-history @dashboard)
              (clj->js hist-header)
              (clj->js hist-data))
    ; Needed to update the screen when graphics change.
    (.render screen)))

(defn- init-data
  "Starts the Dashboard." []
  (let [hist-header (clj->js [])
        hist-data (clj->js
                    [[]])
        table-data (clj->js
                     {:headers ["name" "x" "y" "z"]
                      :data    [["no-devices" 0 0 0]]})]
    (.setData device-table table-data)
    (.focus device-table)
    (.setData sensor-history hist-header hist-data)))

(defn exit-handler [keys]
  (.key screen (clj->js keys)
        (fn [ch key]
          (.exit js/process 0))))

(defn main []
  ;(create-layout)
  (start-prog-log)
  (start-osc-log)
  (init-data)
  (exit-handler ["escape" "q" "C-c"])
  (.render screen))

(set! *main-cli-fn* main)