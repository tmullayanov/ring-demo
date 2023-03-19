(ns ring-demo.core
  (:require [ring-demo.lib :refer [safe]]
            [org.httpkit.server :refer [run-server]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring-demo.routes :refer :all]
            [ring-demo.middleware :refer :all]
            [ring-demo.proxy :as proxy])
  (:gen-class))

(defroutes app-routes
           (GET "/hello" [] hello-page)
           (GET "/seen-page" [] seen-page)
           (GET "/hi/:name" [] hi-named-page)
           (GET "/counter-page" [] counter-page)
           (POST "/event" [] log-event)
           (GET "/event/all" [] get-events)
           (context "/proxy" []
             (GET "/colormind" [] proxy/get-colormind-models-list)
             (GET "/cern" [] proxy/get-first-website))
           (route/not-found "Sorry! No such path found!"))

(def app (-> app-routes
             wrap-keyword-params
             wrap-params
             wrap-cookies
             wrap-session
             wrap-json-response
             (wrap-json-body {:keywords? true})
             wrap-exception
             ))

(defn -main []
  (let [port (safe (Integer/parseInt (System/getenv "PORT"))
                   :or 3000)]
    (run-server #'app {:port port})
    (println (format "Server started at 127.0.0.1:%s/" port))))
