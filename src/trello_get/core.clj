(ns trello-get.core
  (:require [trello.core :as tr]
            [trello.util :as tru]
            [clj-time.format :as ctf]
            [clojure.tools.cli :as cli]
            [clj-time.core :as ct])
  (:gen-class :main true))

(def boards-by-id
  (let [b-all (tr/all-boards)]
    (apply merge
           (map #(hash-map (keyword (:id %)) %) b-all))))
(def cards (tr/get-all :cards))
(def end-of-day (let [n (ct/now) ] (ct/date-time (ct/year n) (ct/month n) (ct/day n) 23 59 59 999)))
(def cards-due-today
  (filter #(if (nil? (:due %))
            false
            (ct/before?
             (ctf/parse (:due %))
             end-of-day))
          cards))

#_
(cli/cli cli/args
     ["-h" "--help" ])

(defn -main
  "return list of cards due by today."
  [& args]
  
  (let [[options args banner]
        (cli/cli args
                 ["-h" "--help" "Show help" :default false :flag true])]
    (when (:help options)
      (println banner)
      (System/exit 0))
    (doseq [c cards-due-today]
     (prn (str
                                        ;(-> boards-by-id (:id c) :name)
           (:name c) ": " (:url c))))))
