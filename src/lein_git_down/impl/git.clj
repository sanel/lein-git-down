(ns lein-git-down.impl.git
  (:refer-clojure :exclude [resolve])
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.gitlibs :as git]
            [clojure.tools.gitlibs.impl :as git-impl]
            [leiningen.core.main :as lein]
            [robert.hooke :as hooke])
  (:import (org.eclipse.jgit.api TransportConfigCallback Git)
           (java.io File)))

(defn dev-null-hook
  [_ & _])

(defn procure
  "Monkey patches gitlibs/procure to writes a meta-data file with information about the source."
  [uri mvn-coords rev]
  (hooke/with-scope
    (hooke/add-hook #'git-impl/printerrln #'dev-null-hook)
    (let [path (git/procure uri mvn-coords rev)
          meta (io/file path ".lein-git-down")]
      (when-not (.exists meta)
        (spit meta {:uri uri :mvn-coords mvn-coords :rev rev}))
      path)))

(defn resolve
  "Monkey patches gitlibs/resolve to silence errors."
  [uri version]
  (hooke/with-scope
    (hooke/add-hook #'git-impl/printerrln #'dev-null-hook)
    (git/resolve uri version)))

(defn init
  "Initializes a fresh git repository at `project-dir` and sets HEAD to the
  provided rev, which allows tooling to retrieve the correct HEAD commit in
  the gitlibs repo directory. Returns the .git directory."
  [^File project-dir rev]
  (let [git-dir (.. (Git/init)
                    (setDirectory project-dir)
                    call
                    getRepository
                    getDirectory)]
    (spit (io/file git-dir "HEAD") rev)
    git-dir))

(defn rm
  "Removes the .git directory returning a checkout back to just the checked
  out code."
  [^File git-dir]
  (when (and (.exists git-dir) (= ".git" (.getName git-dir)))
    (->> (file-seq git-dir)
         reverse
         (run! #(when (.exists %) (.delete %))))))
