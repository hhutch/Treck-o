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
  (let [n (ct/now)
        start-of-day (ct/date-time (ct/year n) (ct/month n) (ct/day n))
        end-of-day (ct/date-time (ct/year n) (ct/month n) (ct/day n) 23 59 59 999)
;        end-of-day (ct/plus start-of-day (ct/hours 23) (ct/hours 59) (ct/minutes 59))
        ]
    (filter #(if (nil? (:due %))
               false
               (ct/within? (ct/interval start-of-day end-of-day)
                           (ctf/parse (:due %))))
            cards)))
(def cards-due-tomorrow
  (let [n (ct/plus (ct/now) (ct/days 1))
        start-of-day (ct/date-time (ct/year n) (ct/month n) (ct/day n))
        end-of-day (ct/date-time (ct/year n) (ct/month n) (ct/day n) 23 59 59 999)]
    (filter #(if (nil? (:due %))
               false
               (ct/within? (ct/interval start-of-day end-of-day)
                           (ctf/parse (:due %))))
            cards)))
(def cards-overdue
  (let [n (ct/now)
        over-due (ct/minus n (ct/minutes 1))]
    (filter #(if (nil? (:due %))
               false
               (ct/before? (ctf/parse (:due %)) over-due))
            cards)))


(defn -main
  "return list of cards due by today."
  [& args]
  
  (let [[options args banner]
        (cli/cli args
                 ["-t" "--today" "cards due today" :default false :flag true]
                 ["-T" "--tomorrow" "cards due tomorrow" :default false :flag true]
                 ["-o" "--overdue" "cards overdue" :default false :flag true]
                 ["-h" "--help" "Show help" :default false :flag true])]
    (when (:help options)
      (println banner)
      (System/exit 0))
    (cond
     (:today options) (doseq [c cards-due-today]
                        (println (str (:name c) ": " (:url c))))
     (:tomorrow options) (doseq [c cards-due-tomorrow]
                        (println (str (:name c) ": " (:url c))))
     (:overdue options) (doseq [c cards-overdue]
                          (println (str (:name c) ": " (:url c))))
     )))
