(ns ring-demo.logs
  (:require [clojure.spec.alpha :as s]
            [clojure.instant :refer [read-instant-timestamp]])
  (:import (java.util UUID)))


(def logs (atom []))

(defrecord Log [operation-id system-name log-level event-name message date])

(s/def ::non-empty-string (s/and string? #(seq %)))

(s/def :logs/system-name ::non-empty-string)

(s/def :logs/event-name ::non-empty-string)

(s/def :logs/message ::non-empty-string)

(s/def :logs/operation-id
  (s/conformer
    (fn [val]
      (try
        (UUID/fromString val)
         (catch Exception e ::s/invalid
           )))))

(s/def :logs/log-level #{"log" "info" "warn" "error" "debug"})

(s/def :logs/date
  (s/and
    ::non-empty-string
    (s/conformer
      (fn [value]
        (try
          (read-instant-timestamp value)
          (catch Exception e
            ::s/invalid))))))

(s/def :logs/logs (s/keys :req-un [:logs/system-name
                                   :logs/event-name
                                   :logs/date
                                   :logs/operation-id
                                   :logs/log-level
                                   :logs/message]))


(defn add-logs [log]
  (let [log* (s/conform :logs/logs log)]
    (if (not= ::s/invalid log*)
      (do
        (swap! logs conj (map->Log log*))
        {:status "ok"})
      {:status "invalid" :explain (s/explain :logs/logs log)})))


