(ns ring-demo.routes
  (:require [ring-demo.logs :as logs]))

(defn hello-page [req]
  (let [name (or (get-in req [:params :name])
                 "user")]
    {
     :status 200
     :headers {"Content-Type" "text/html"}
     :body (format "<b>Hello %s!</b>" name)
     }))

(defn seen-page [req]
  (let [{:keys [cookies]} req
        seen-path ["seen" :value]
        seen? (get-in cookies seen-path)
        cookies* (assoc cookies "seen" {:value true :http-only true})]
    {
     :status 200
     :headers {"Content-Type" "text/html"}
     :cookies cookies*
     :body (if seen?
             "You've been here before"
             "This is your first time here")
     }))

(defn hi-named-page [req]
  (let [name (-> req :params :name)]
    {
     :status 200
     :body (format "Hi, %s" name)
     }))

(defn counter-page [req]
  (let [{:keys [session]} req
        session* (update session :counter (fnil inc 0))
        counter (:counter session*)]
    {
     :status 200
     :headers {"Content-Type" "application/json"}
     :session session*
     :body {:text (format "You've been here %s time(s)" counter)
            :count counter}
     }))

(defn log-event [req]
  (let [log (-> req :body)
        result (logs/add-logs log)
        {:keys [status explain]} result]
    (println result)
    (if (= status "ok")
      {
       :status 200
       :body {
              :msg "log added successfully"
              }
       }
      {
       :status  400
       :body {
              :msg "log was not added due to incorrect format",
              :explain explain
              }
       }
    )))

(defn get-events [_]
  {
   :status 200
   :headers {"Content-Type" "application/json"}
   :body {
          :events @logs/logs
          }
   }
  )
