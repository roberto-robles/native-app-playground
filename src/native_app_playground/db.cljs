(ns native-app-playground.db
  (:require [clojure.spec.alpha :as s]))

;; spec of app-db
(s/def ::greeting string?)

(s/def ::username string?)

(s/def ::view keyword?)

(s/def ::app-db
  (s/keys :req-un [::greeting ::username ::view]))

;; initial state of app-db
(def app-db {:greeting "Hello Clojure in iOS and Android!"
             :username ""
             :password ""
             :view :form
             :date (js/Date.)
             :starter "bulbasaur"
             :percentage 100})

