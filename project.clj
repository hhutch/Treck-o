(defproject treck-o "0.1.0-SNAPSHOT"
  :description "Treck-o: trello commandline utility"
  :url "https://github.com/hhutch/Treck-o"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [clj-time "0.4.2"]
                 [org.clojure/tools.cli "0.2.1"]
                 [org.clojure/core.match "0.2.0-alpha9"]
                 [trello "0.1.0-SNAPSHOT"]]
  :main treck-o.core)
