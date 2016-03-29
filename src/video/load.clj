(ns video.load
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

;;TODO: Constants for all files
;;Constants
(def movie-file "movies_db.clj")
(def rent-file "rent_db.clj")
(def fs (System/getProperty "file.separator"))
(def home (System/getProperty "user.home"))
(def movie-path (str home fs movie-file))
(def rent-path (str home fs rent-file))
;;TODO: qnt-atom has validator to raise alert when qnt < 0
(def qnt-atom (atom 1))


(def tdate (t/today))
(def ndate (-> 14 t/days t/from-now))
(def dt (t/day tdate))
(def mt (t/month tdate))
(def dn (t/day ndate))
(def mn (t/month ndate))

;;-------------------------------------------------------------------------------
;;----------------         READ MOVIE DB        ---------------------------------
;;-------------------------------------------------------------------------------

;;Make storage directory if it doesn't exist
;;PATH: $HOME/video_inventory
(defn mk-storage-dir [path]
  (if-not (fs/exists? path) (fs/mkdir path))
  path)


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
(defn get-last-id []
  (if @movie-atom
    (first (sort > (keys @movie-atom)))
    100))

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
