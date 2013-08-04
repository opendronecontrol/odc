import sbt._

import Keys._

// import ProguardPlugin._

object Settings {
  
  lazy val common = Defaults.defaultSettings ++ Seq (
    version := "0.4.3",
    scalaVersion := "2.10.2",
    resolvers ++= Seq(
      "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
      "NativeLibs4Java Repository" at "http://nativelibs4java.sourceforge.net/maven/",
      "xuggle repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java/",
      "Sonatypes OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "ScalaNLP Maven2" at "http://repo.scalanlp.org/repo",
      "maven.org" at "http://repo1.maven.org/maven2"
    ),
    autoCompilerPlugins := true,
    scalacOptions += "-Xexperimental",
    downloadLibsTask
    //libraryDependencies ++= Seq()
  )

  lazy val odc = Settings.common ++ Seq (
    //libraryDependencies ++= Seq()
  )

  lazy val javadrone = Settings.common ++ Seq(
    libraryDependencies ++= Seq(
      "xuggle" % "xuggle-xuggler" % "5.4"
    )
  )

  lazy val maxmsp = Settings.common ++ Seq (
    fork in Compile := true,
    libraryDependencies ++= Seq(
      "log4j" % "log4j" % "1.2.16"
    )
  )

  lazy val osc = Settings.common ++ Seq(
    libraryDependencies ++= Seq(
      "de.sciss" %% "scalaosc" % "1.1.+"
    )
  )

  lazy val seer = Settings.common ++ Seq(
    fork in Compile := true,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.2.0-RC2",
      "org.scala-lang" % "scala-actors" % "2.10.2",
      "org.jruby" % "jruby" % "1.7.3",
      "de.sciss" %% "scalaaudiofile" % "1.2.0"

    )
  )

  // lazy val proguard = proguardSettings ++ Seq(
  //   proguardOptions := Seq( 
  //     //"-libraryjars lib/max.jar:lib/jitter.jar",
  //     "-keep class DroneControl { *; }",
  //     "-keep class com.fishuyo.drone.DroneControl { *; }",
  //     "-keep class com.fishuyo.maths.Vec3",
  //     "-keep class com.fishuyo.maths.Quat",
  //     """-keepclasseswithmembers public class * {
  //       public static void main(java.lang.String[]);
  //     }

  //     -keep class * implements org.xml.sax.EntityResolver
  //     -keepclassmembers class * {
  //       ** MODULE$;
  //     }

  //     -keepclassmembernames class scala.concurrent.forkjoin.ForkJoinPool {
  //       long eventCount;
  //       int  workerCounts;
  //       int  runControl;
  //       scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode syncStack;
  //       scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode spareStack;
  //     }

  //     -keepclassmembernames class scala.concurrent.forkjoin.ForkJoinWorkerThread {
  //       int base;
  //       int sp;
  //       int runState;
  //     }

  //     -keepclassmembernames class scala.concurrent.forkjoin.ForkJoinTask {
  //       int status;
  //     }

  //     -keepclassmembernames class scala.concurrent.forkjoin.LinkedTransferQueue {
  //       scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference head;
  //       scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference tail;
  //       scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference cleanMe;
  //     }"""
  //   )
  // )

  val downloadLibs = TaskKey[Unit]("download-libs", "Downloads/Updates required libs")
  val downloadLibsTask = downloadLibs <<= streams map { (s: TaskStreams) => doDownloadLibs(s) }

  def doDownloadLibs(s:TaskStreams) = {
    import Process._
    import java.io._
    import java.net.URL
    
    // Declare names
    val baseUrl = "http://fishuyo.com/stuff"
    val zipName = "odcLibs.zip"
    val zipFile = new java.io.File(zipName)

    // Fetch the file.
    s.log.info("Pulling %s" format(zipName))
    val url = new URL("%s/%s" format(baseUrl, zipName))
    IO.download(url, zipFile)

    // Extract jars into their respective lib folders.
    val seerDest = file("apps/droneSimulator/lib")
    val maxDest = file("apps/maxmsp-external/lib")
    val odcDest = file("odc/lib")
    val seerFilter =  new ExactFilter("GlulogicMT.jar") | new ExactFilter("libGlulogicMT.dylib") | new ExactFilter("seer.jar") | 
                      new ExactFilter("seer-desktop.jar") | new ExactFilter("monido-core_2.10-0.1.2.jar") | new ExactFilter("gdx.jar") |
                      new ExactFilter("gdx-natives.jar") | new ExactFilter("gdx-backend-lwjgl.jar") | new ExactFilter("gdx-backend-lwjgl-natives.jar")
    val maxFilter = new ExactFilter("max.jar") | new ExactFilter("jitter.jar")
    val odcFilter = new ExactFilter("h264-decoder-1.0.jar")
    IO.unzip(zipFile, seerDest, seerFilter)
    IO.unzip(zipFile, maxDest, maxFilter)
    IO.unzip(zipFile, odcDest, odcFilter)

    // Destroy the file.
    zipFile.delete
    s.log.info("Complete")
  }
}



object odcBuild extends Build {

  lazy val aaall = Project(
    "all",
    file(".")
  ) aggregate( odc, backend_ardrone, maxmsp_external, apps )

  // common odc code
  lazy val odc = Project (
    "odc",
    file("odc"),
    settings = Settings.odc
  )

  // backends
  lazy val backend_ardrone = Project(
    "backend-ardrone",
    file("platforms/ardrone"),
    settings = Settings.javadrone
  ) dependsOn odc


  //max msp mxj external
  lazy val maxmsp_external = Project (
    "maxmsp-external",
    file("apps/maxmsp-external"),
    settings = Settings.maxmsp //++ Settings.proguard
  ) dependsOn(backend_ardrone)


  // desktop apps
  lazy val apps = Project(
    "apps",
    file("./apps")
  ) aggregate( droneOSC, droneSimulator )

  lazy val droneOSC = Project (
    "droneOSC",
    file("apps/droneOSC"),
    settings = Settings.osc //++ Settings.proguard
  ) dependsOn(backend_ardrone)

  lazy val droneSimulator = Project (
    "droneSimulator",
    file("apps/droneSimulator"),
    settings = Settings.osc ++ Settings.seer //++ Settings.proguard
  ) dependsOn(backend_ardrone)


}
