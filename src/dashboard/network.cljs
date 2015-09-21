(ns dashboard.network
  (:require [cljs.nodejs :as nodejs]
            [cljs.reader :as reader]
            [cljs.core.async
             :as a
             :refer [>! <! chan buffer close! alts! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(defonce input-chan (chan))

(defonce fs (nodejs/require "fs"))

(defn read-config []
  (.readFile fs "port-record/current/mpr.edn"
             "utf8"
             (fn [err data]
               (go
                 (>! input-chan data)))))

(defn assess-communication-targets [edn]
  (let [processes (reader/read-string edn)]
    (println processes)))

(defn init []
  (go
    (read-config)
    (assess-communication-targets (<! input-chan))
    (close! input-chan)))