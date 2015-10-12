(ns tsukihi.core
  (:require [schema.core :as s]
            [datomic.api :as d]
            [cljc.susumu.core :as sc]))

;; generic

(def Domain (s/cond-pre s/Keyword s/Str))

(s/defn eid :- datomic.db.DbId
  []
  (d/tempid :db.part/user))

(s/defn ->id-key :- s/Keyword
  [domain :- Domain]
  (keyword (sc/as-?name domain) "id"))

(s/defn new? :- s/Bool
  [m   :- {s/Keyword s/Any}]
  (not (contains? m :db/id)))

;; transform

(s/defn from-datomic :- {s/Keyword s/Any}
  [m :- {s/Keyword s/Any}]
  (sc/map-kvs m (fn [k v]
                  (let [new-k (-> k name keyword)]
                    [new-k v]))))

(s/defn to-datomic :- {s/Keyword s/Any}
  [m :- {s/Keyword s/Any}
   n :- Domain]
  (sc/map-kvs m (fn [k v]
                  (let [new-n (sc/as-?name n)
                        new-k (sc/as-?name k)]
                    [(keyword new-n new-k) v]))))

;; io

(s/defn save :- datomic.ListenableFuture
  [domain :- Domain
   conn   :- datomic.peer.LocalConnection
   m      :- {s/Keyword s/Any}]
  (if (new? m)
    (d/transact conn [[:auto-inc-id
                       domain
                       m]])
    (d/transact conn [m])))

(s/defn delete :- datomic.ListenableFuture
  [domain :- Domain
   conn   :- datomic.peer.LocalConnection
   id     :- s/Int]
  (d/transact conn [[:db.fn/retractEntity [(->id-key domain) id]]]))
