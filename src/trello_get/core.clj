(ns trello-get.core
  (:require [trello.core :as tr]
            [trello.util :as tru]
            [clj-time.format :as ctf]
            [clojure.tools.cli :as cli]
            [clojure.core.match :as match]
            [clj-time.core :as ct])
  (:gen-class :main true))


(defn cards-in-range
  "Returns cards filtered by due date.
   Allows comparison by :after and :before"
  [deck & {:keys [after before]}]
  (let [jo org.joda.time.DateTime
        a (= jo (type after))
        b (= jo (type before))
        date-comp #(let [d (ctf/parse (:due %))]
                     (match/match
                      [a b]
                      [true true] (ct/within? (ct/interval after before) d)
                      [_ true] (ct/before? d before)
                      [true _] (ct/after? d after)))]
    (->> deck
        (filter #(not (nil? (:due %))))
        (filter date-comp))))


(defn -main
  "return list of cards due by parameter."
  [& args]
  (let [[options args banner]
        (cli/cli args
                 ["-t" "--today" "cards due today" :default false :flag true]
                 ["-T" "--tomorrow" "cards due tomorrow" :default false :flag true]
                 ["-o" "--overdue" "cards overdue" :default false :flag true]
                 ["-h" "--help" "Show help" :default false :flag true])
        boards-by-id (let [b-all (tr/all-boards)]
                       (apply merge
                              (map #(hash-map (keyword (:id %)) %) b-all)))
        cards (tr/get-all :cards)
        cards-due-today (let [n (ct/now)
                              start-of-day (ct/date-time (ct/year n) (ct/month n) (ct/day n))
                              end-of-day (ct/date-time (ct/year n) (ct/month n) (ct/day n) 23 59 59 999)]
                          (cards-in-range cards :after start-of-day :before end-of-day))
        cards-due-tomorrow (let [n (ct/plus (ct/now) (ct/days 1))
                                 start-of-day (ct/date-time (ct/year n) (ct/month n) (ct/day n))
                                 end-of-day (ct/date-time (ct/year n) (ct/month n) (ct/day n) 23 59 59 999)]
                             (cards-in-range cards :after start-of-day :before end-of-day))
        cards-overdue (let [n (ct/now)
                            over-due (ct/minus n (ct/minutes 1))]
                        (cards-in-range cards :before over-due))
        print-info #(println (str (:name %) ": " (:url %)))]
    (when (:help options)
      (println banner)
      (System/exit 0))
    (cond
     (:today options) (doseq [c cards-due-today] (print-info c))
     (:tomorrow options) (doseq [c cards-due-tomorrow] (print-info c))
     (:overdue options) (doseq [c cards-overdue] (print-info c)))))
