(defproject tsukihi "0.1.0-SNAPSHOT"
  :description "Utility for Datomic"
  :url "http://github.com/farisnasution/tsukihi"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.0.1"]
                 [com.datomic/datomic-free "0.9.5302" :exclusions [joda-time]]]
  :plugins [[lein-git-deps "0.0.2-SNAPSHOT"]]
  :git-dependencies [["https://github.com/farisnasution/susumu.git"]])
