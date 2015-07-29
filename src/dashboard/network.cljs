(ns dashboard.network
  (:require [cljs.nodejs :as nodejs]
            [cljs.reader :as edn]
            [cljs.core.async :as a :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(enable-console-print!)

(defonce fs (nodejs/require "fs"))

(defonce port-config (.readFile fs "port-record/mpr.edn" "utf8"
                                (fn [err data]
                                  (println (edn/read-string data)))))

(defn init []
  #_(.log js/console port-config))