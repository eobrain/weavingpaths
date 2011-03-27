import sbt._

class WormsJarFetcher(info: ProjectInfo) extends DefaultProject(info)
{

  //JAR files to be fetched
  val casbah = "com.mongodb.casbah" %% "casbah" % "2.1.0"
  val scalaj_collection = "org.scalaj" %% "scalaj-collection" % "1.0"

}
