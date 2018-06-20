name := """play-stream-timeout-bug"""
organization := "timeoutbug.play"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies += guice

PlayKeys.devSettings += "play.server.http.idleTimeout " -> "50 seconds"