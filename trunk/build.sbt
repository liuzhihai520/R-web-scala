name := """trunk"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  filters,
  specs2 % Test,
  "com.typesafe.play"           %%    "anorm"                    %        "2.4.0",
  "mysql"                       %     "mysql-connector-java"     %        "5.1.37",
  "com.google.code.gson"        %     "gson"                     %        "2.4",
  "com.squareup.okhttp"         %     "okhttp"                   %        "2.6.0",
  "commons-io"                  %     "commons-io"               %        "2.4",
  "commons-codec"               %     "commons-codec"            %        "1.6",
  "commons-collections"         %     "commons-collections"      %        "3.2.1",
  "commons-beanutils"           %     "commons-beanutils"        %        "1.8.3",
  "commons-lang"                %     "commons-lang"             %        "2.6",
  "net.sf.ezmorph"              %     "ezmorph"                  %        "1.0.6",
  "log4j"                       %     "log4j"                    %        "1.2.17",
  "com.drewnoakes"              %     "metadata-extractor"       %        "2.6.2",
  "org.scala-lang"              %     "scala-actors"             %        "2.11.6"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
