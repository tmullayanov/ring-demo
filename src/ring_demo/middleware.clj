(ns ring-demo.middleware
  (:require [clojure.tools.logging :as log]))

(defn wrap-exception [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable e
        (let [{:keys [uri request-method]} request]
          (log/errorf e "Error, method %s path %s" request-method uri))))))