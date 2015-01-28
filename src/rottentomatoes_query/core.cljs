(ns rottentomatoes-query.core
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

(def request (nodejs/require "request"))

(def api-key (-> (.-env nodejs/process) js->clj
                 ;; Feel free to substitute your own API key in your
                 ;; environment, and just change the name here.
                 (get "RT_API_KEY")))

(def api-base "http://api.rottentomatoes.com/api/public/v1.0")

(defn -main [& args]
  (let [rq (str api-base "/movies.json" "?q=" (first args) "&apikey=" api-key)]
    (.get request rq
          (fn [err resp]
            (let [d (-> resp .-body js/JSON.parse js->clj)
                  movies (get d "movies")
                  titles (map #(get % "title") movies)
                  cast-url (get-in (first movies) ["links" "cast"])
                  cast-url (str cast-url "?apikey=" api-key)]
              (.get request cast-url
                    (fn [err resp]
                      (let [data (-> resp .-body js/JSON.parse js->clj)
                            cast (get data "cast")]
                        (dorun (map #(prn (get % "name")) cast))))))))))

(set! *main-cli-fn* -main)
