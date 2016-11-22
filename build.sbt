

organization := "com.geishatokyo"

name := "typesafeconfig-scala"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

crossScalaVersions := List("2.11.8","2.12.0")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1" % "provided",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)


libraryDependencies <++= (scalaVersion)(projectScalaVersion => {
  Seq("org.scala-lang" % "scala-reflect" % projectScalaVersion)
})

val publichToMavenCentralCommand = Command.command("publishToMavenCentral")( state => {
  // ローカル設定と干渉を避けるために、コマンド内だけでRepositoryを切り替える
  val ext = Project.extract(state)
  val nexus = "https://oss.sonatype.org/"
  val snapShot_? = ext.get(isSnapshot)
  // 芸者東京では、global.sbtなどに社内レポジトリを登録しており、その設定と干渉するため、
  // このように後からデプロイ設定を更新しています。
  val resolver = if(snapShot_?){
    Some("snapshots"  at nexus + "content/repositories/snapshots")
  }else{
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  }
  val publishState = ext.append(Seq(
    publishTo := resolver
  ),state)
  val lastState = Command.process("publishSigned",publishState)
  state
})

commands ++= Seq(publichToMavenCentralCommand)


pomExtra := {
  <url>https://github.com/geishatokyo/typesafeconfig-scala</url>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:git@github.com:geishatokyo/typesafeconfig-scala.git</connection>
    <developerConnection>scm:git:git@github.com:geishatokyo/typesafeconfig-scala.git</developerConnection>
    <url>scm:git:git@github.com:geishatokyo/typesafeconfig-scala.git</url>
  </scm>
  <developers>
    <developer>
      <id>takeshita</id>
      <name>Yositeru Takeshita</name>
      <email>takezoux2@gmail.com</email>
    </developer>
  </developers>
}

