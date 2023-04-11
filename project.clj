(defproject reifyhealth/lein-git-down "0.4.2"
  :description "A Leiningen plugin for resolving Clojure(Script) dependencies from a Git repository"
  :url "http://github.com/reifyhealth/lein-git-down"
  :license {:name "MIT"}
  :dependencies [[org.clojure/tools.gitlibs "2.5.190"]
                 [org.eclipse.jgit/org.eclipse.jgit "4.10.0.201712302008-r"]
                 [leiningen "2.9.4" :scope "provided" :exclusions [commons-codec org.apache.httpcomponents/httpclient org.slf4j/slf4j-api org.apache.httpcomponents/httpcore]]]
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :username :env/clojars_username
                                    :password :env/clojars_password
                                    :sign-releases false}]])
