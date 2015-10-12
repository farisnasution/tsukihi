(ns tsukihi.db-fn
  (:require [schema.core :as s]
            [datomic.api :as d]
            [tsukihi.core :as c]))

(s/defn get-last-id :- s/Int
  [id-key :- s/Keyword
   db     :- datomic.db.Db]
  (let [result (d/q '[:find [?i ...]
                      :in $ ?idk
                      :where [_ ?idk ?i]]
                    db
                    id-key)]
    (if (empty? result)
      0
      (apply max result))))

(s/def auto-inc-id :- datomic.function.Function
  (d/function '{:lang :clojure
                :params [db domain m]
                :code (let [idk (c/->id-key domain)
                            last-id (get-last-id idk db)
                            new-id (inc last-id)]
                        [(assoc e idk new-id)])}))

(def db-fn-list
  [{:db/id (c/eid)
    :db/doc "Db function to increment :[namespace]/id"
    :db/ident :auto-inc-id
    :db/fn auto-inc-id}])

(s/defn boostrap :- datomic.ListenableFuture
  [conn :- datomic.peer.LocalConnection]
  (d/transact conn db-fn-list))
