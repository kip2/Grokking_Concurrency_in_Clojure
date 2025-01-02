(ns chapter4.multithreading
  (:gen-class)
  (:require [clojure.string :as str]))

;; PID取得のための関数
(defn get-pid []
  (let [name (.getName (java.lang.management.ManagementFactory/getRuntimeMXBean))]
    (first (str/split name #"@"))))

(defn cpu-waster [i obj]
  (let [name (.getName (Thread/currentThread))]
    (locking  obj (println name " doing " i " work"))
    (Thread/sleep 3000)))

(defn display-threads []
  (let [current-thread (Thread/currentThread)
        thread-group (.getThreadGroup current-thread)]
    (println "----------")
    (println "Current process PID:" (get-pid))
    (println "Thread Count: " (.activeCount thread-group))
    (println "Active threads:")
    (doseq [t (.keySet (Thread/getAllStackTraces))]
      (println (.getName t)))))

(defn main [num-threads]
  (display-threads)
  (println "Starting " num-threads " CPU wasters...")
  (let [lock-object (Object.)
        threads (mapv (fn [i]
                        (let [thread (Thread. #(cpu-waster i lock-object))]
                          (.start thread)
                          thread))
                      (range num-threads))]
    (doseq [thread threads]
      (.join thread)))
  (display-threads))

(defn -main []
  (let [num-threads 5]
    (main num-threads)))


