(ns video.dbfn
  (:gen-class)
  (:require [me.raynes.fs :as fs]
            [clj-time.core :as t]))

;;Function declarations
(declare get-mv-idx)
(declare get-movies)
(declare mk-storage-dir)
(declare load-movies)
(declare write-movie)
(declare write)

;;Constants
(def movie-file "movies_db.clj")
(def rent-file "rent_db.clj")
(def fs (System/getProperty "file.separator"))
(def home (System/getProperty "user.dir"))
(def movie-path (str home fs movie-file))
(def rent-path (str home fs rent-file))

;;-------------------------------------------------------------------------------
;;----------------         READ MOVIE DB        ---------------------------------
;;-------------------------------------------------------------------------------

;;Load movies-data into memory
(defn load-db [file-path]
  (if (fs/exists? file-path)
    (with-open [r (java.io.PushbackReader. (clojure.java.io/reader file-path))]
      (binding [*in* r
              *read-eval* false]
        (read)))
    nil))

;;Global movie-atom used by all function
(def movie-atom (atom (load-db movie-path)))

;; Write to disk
;; TODO: efficient write
(defn writer [file-path movies]
  (let []
    (with-open [w (clojure.java.io/writer file-path :append false)]
      ;;(println "WRITE BACK on change: " w file-path "\n" movies)
      (binding [*out* w]
        (pr movies)))))

;; Get movie-map index
(defn get-mv-idx [id movies]
  (first (first (filter #(= id (:id (second %))) (map-indexed vector @movies)))))

;; Get id of last movie + 1
;; If map is empty -> then 100 to start ids from 101
(defn get-last-id []
  (let [ks (keys @movie-atom)]
    (if ks
      (first (sort > ks))
      100)))

;; Get movie-id from name
;; If name matches return key else return 0
(defn get-movie-id [name]
  (first
   (for [k (keys @movie-atom)
        :when (= name (:name (@movie-atom k)))]
    k)))


(defn get-movie-name [id]
  (:name (@movie-atom id)))

;;Get quantity
(defn get-qnt [id]
  (:qnt (@movie-atom id)))

;;Get due date
(defn due-date []
  (let [ndate (-> 14 t/days t/from-now)
        dd (t/day ndate)
        mm (t/month ndate)
        yy (t/year ndate)]
    (str mm "-" dd "-" yy)))

;;-------------------------------------------------------------------------------
;;----------------         READ RENTER DB        ---------------------------------
;;-------------------------------------------------------------------------------
(def rent-atom (atom (load-db rent-path)))

(defn is-not-rented [name]
   (empty?
     (for [k (keys @rent-atom)
         :when (= name (:movie k))]
       k)))
