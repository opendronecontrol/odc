import sbt._

import Keys._

import com.typesafe.sbt.SbtProguard._
import sbtunidoc.Plugin._

object Settings {
  
  lazy val common = Defaults.defaultSettings ++ Seq (
    version := "0.1",
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
    scalacOptions += "-Xexperimental"
    //libraryDependencies ++= Seq()
  )

  lazy val odc = Settings.common ++ Seq (
    downloadLibsTask,
    libraryDependencies ++= Seq(
      "de.sciss" %% "scalaosc" % "1.1.+"
    )
  )

  lazy val javadrone = Settings.common ++ Seq(
    libraryDependencies ++= Seq(
      "xuggle" % "xuggle-xuggler" % "5.4"
      // "org.slf4j" % "slf4j-api" % "1.7.2"
      //"org.slf4j" % "slf4j-jdk14" % "1.7.2"
    )
  )

  lazy val maxmsp = Settings.common ++ Seq (
    fork in Compile := true
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

  // Proguard options for minimizing classes into a single jar
  import ProguardKeys.{ mergeStrategies, merge, options }
  import ProguardOptions.keepMain

  lazy val proguard = proguardSettings ++ Seq(
    options in Proguard += "-dontoptimize",
    merge in Proguard := true,
    mergeStrategies in Proguard += ProguardMerge.discard("META-INF/.*".r),
    options in Proguard += """ 
      -keep class OpenDroneControl { *; }
      -keep class org.opendronecontrol.** {*;}
      -keep class com.cycling74.max.** {*;}
      -keep class com.cycling74.jitter.** {*;}
      #-keep class com.cycling74.max.MaxObject




      #
      # scala
      #

      -keepclassmembers class * { ** MODULE$; }

      -keepclassmembernames class scala.concurrent.forkjoin.ForkJoinPool {
        long ctl;
      }

      -keepclassmembernames class scala.concurrent.forkjoin.ForkJoinPool$WorkQueue {
        int runState;
      }

      -keepclassmembernames class scala.concurrent.forkjoin.LinkedTransferQueue {
        scala.concurrent.forkjoin.LinkedTransferQueue$Node head;
        scala.concurrent.forkjoin.LinkedTransferQueue$Node tail;
        int sweepVotes;
      }

      -keepclassmembernames class scala.concurrent.forkjoin.LinkedTransferQueue$Node {
        java.lang.Object item;
        scala.concurrent.forkjoin.LinkedTransferQueue$Node next;
        java.lang.Thread waiter;
      }

      -dontnote scala.xml.**
      -dontnote scala.concurrent.forkjoin.ForkJoinPool
      -dontwarn scala.**

      ###
      -dontwarn ch.qos.**
      -dontwarn com.cycling74.max.MXJDecompiler

      """
  )
      // -keep class org.opendronecontrol.spatial.Vec3
      // -keep class org.opendronecontrol.spatial.Quat
      // -keep class org.opendronecontrol.spatial.Pose
      // -keep class org.opendronecontrol.platforms.ardrone.ARDrone
      // -keep class org.opendronecontrol.tracking.PositionTrackingController
  // val proguardScala =

  val oldscala = """
  -keepclasseswithmembers public class * {
        public static void main(java.lang.String[]);
      }

      -keep class * implements org.xml.sax.EntityResolver

      -keepclassmembernames class scala.concurrent.forkjoin.ForkJoinPool {
        long eventCount;
        int  workerCounts;
        int  runControl;
        scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode syncStack;
        scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode spareStack;
      }

      -keepclassmembernames class scala.concurrent.forkjoin.ForkJoinWorkerThread {
        int base;
        int sp;
        int runState;
      }

      -keepclassmembernames class scala.concurrent.forkjoin.ForkJoinTask {
        int status;
      }

      -keepclassmembernames class scala.concurrent.forkjoin.LinkedTransferQueue {
        scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference head;
        scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference tail;
        scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference cleanMe;
      }
  """

  // download unmanaged dependencies
  val downloadLibs = TaskKey[Unit]("download-libs", "Downloads/Updates required libs")
  val downloadLibsTask = downloadLibs <<= streams map { (s: TaskStreams) => doDownloadLibs(s) }

  def doDownloadLibs(s:TaskStreams) = {
    import Process._
    import java.io._
    import java.net.URL
    
    // Declare names
    val baseUrl = "http://fishuyo.com/stuff"
    val zipName = "odcSeerLibs.zip"
    val zipFile = new java.io.File(zipName)

    // Fetch the file.
    s.log.info("Pulling %s" format(zipName))
    val url = new URL("%s/%s" format(baseUrl, zipName))
    IO.download(url, zipFile)

    // Extract jars into their respective lib folders.
    val simDest = file("apps/droneSimulator/lib")
    val leapDest = file("apps/leapController/lib")
    val faceDest = file("apps/faceTracking/lib")
    val nativeDest = file("apps/lib")
    val nativeFilter =  new ExactFilter("libGlulogicMT.jnilib") | new ExactFilter("libLeap.dylib") | 
                        new ExactFilter("libLeapJava.dylib") | new ExactFilter("libopencv_java245.dylib")
                      
                
    val seerFilter =  new ExactFilter("GlulogicMT.jar") | new ExactFilter("seer-core_2.10-0.1.jar") | new ExactFilter("LeapJava.jar") |
                      new ExactFilter("seer-desktop_2.10-0.1.jar") | new ExactFilter("monido-core_2.10-0.1.2.jar") | new ExactFilter("gdx.jar") |
                      new ExactFilter("gdx-natives.jar") | new ExactFilter("gdx-backend-lwjgl.jar") | new ExactFilter("gdx-backend-lwjgl-natives.jar") |
                      new ExactFilter("opencv-245.jar") | new ExactFilter("seer-leap_2.10-0.1.jar") | new ExactFilter("seer-multitouch_2.10-0.1.jar") |
                      new ExactFilter("seer-opencv_2.10-0.1.jar") | new ExactFilter("seer-kinect_2.10-0.1.jar") | new ExactFilter("seer-video_2.10-0.1.jar")
    
    val maxDest = file("apps/maxmsp-external/lib")
    val maxFilter = new ExactFilter("max.jar") | new ExactFilter("jitter.jar")
    IO.unzip(zipFile, simDest, seerFilter)
    IO.unzip(zipFile, leapDest, seerFilter)
    IO.unzip(zipFile, faceDest, seerFilter)
    IO.unzip(zipFile, maxDest, maxFilter)
    IO.unzip(zipFile, nativeDest, nativeFilter)

    // Destroy the file.
    zipFile.delete
    s.log.info("Complete")
  }
}


// Project definitions

object odcBuild extends Build {

  lazy val aaall = Project(
    "all",
    file("."),
    settings = Settings.common ++ unidocSettings
  ) aggregate( odc, backend_ardrone, maxmsp_external )

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
    settings = Settings.maxmsp ++ Settings.odc ++ Settings.proguard
  ) dependsOn(backend_ardrone)


  // examples
  lazy val examples = Project(
    "examples",
    file("./examples")
  ) dependsOn( backend_ardrone )



  lazy val droneSimulator = Project (
    "DroneSimulator",
    file("apps/droneSimulator"),
    settings = Settings.seer //++ Settings.proguard
  ) dependsOn(backend_ardrone)

  lazy val leapController = Project (
    "LeapController",
    file("apps/leapController"),
    settings = Settings.seer //++ Settings.proguard
  ) dependsOn(backend_ardrone)

  lazy val faceTracking = Project (
    "FaceTracking",
    file("apps/faceTracking"),
    settings = Settings.seer //++ Settings.proguard
  ) dependsOn(backend_ardrone)

  // lazy val droneVisionTracking = Project (
  //   "droneVisionTracking",
  //   file("apps/droneVisionTracking"),
  //   settings = Settings.seer //++ Settings.proguard
  // ) dependsOn(backend_ardrone)


}
