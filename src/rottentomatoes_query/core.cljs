(ns rottentomatoes-query.core
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

(def request (nodejs/require "request"))

(def api-key (-> (.-env nodejs/process) js->clj
                 ;; Feel free to substitute your own API key in your
                 ;; environment, and just change the name here.
                 (get "RT_API_KEY")))

(def api-base "http://api.rottentomatoes.com/api/public/v1.0")

(def title "The Sting")

(defn -main [& args]
  (let [rq (str api-base "/movies.json" "?q=" title "&apikey=" api-key)]
    (.get request rq
          (fn [err resp]
            (let [d (-> resp .-body js/JSON.parse js->clj)
                  movies (get d "movies")]
              (dorun (map #(println :title (get % "title") "\n\t"
                                    :cast-query-url (get-in % ["links" "cast"])) movies)))))))

(set! *main-cli-fn* -main)
