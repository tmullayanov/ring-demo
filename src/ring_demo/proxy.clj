(ns ring-demo.proxy
  (:require [clj-http.client :as client]
            [compojure.core :refer :all]))

(defn get-colormind-models-list [request]
  (-> "http://colormind.io/list/"
      (client/get {:stream? true})
      (select-keys [:status :body :headers])
      (update :headers select-keys ["Content-Type"])))

(defn get-first-website [request]
  (-> "http://info.cern.ch/"
      (client/get {:stream? true})
      (select-keys [:status :body :headers])
      (update :headers select-keys ["Content-Type"])))
