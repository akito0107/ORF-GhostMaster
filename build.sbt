name := "GhostMaster"

version := "1.0"

scalaVersion := "2.11.2"

resolvers += "Github" at "http://akito0107.github.io/ORF-GhostCommonLib-mvn-repo/"

val infinispanVersion = "6.0.2.Final"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.typesafe.akka" %% "akka-remote" % "2.3.6",
  "org.infinispan" % "infinispan-client-hotrod" % infinispanVersion excludeAll(
    ExclusionRule(organization = "org.jboss.marshalling", name = "jboss-marshalling-river"),
    ExclusionRule(organization = "org.jboss.logging", name = "jboss-logging")
    ),
  "org.infinispan" % "infinispan-core" % infinispanVersion excludeAll(
    ExclusionRule(organization = "org.jgroups", name = "jgroups"),
    ExclusionRule(organization = "org.jboss.marshalling", name = "jboss-marshalling-river"),
    ExclusionRule(organization = "org.jboss.marshalling", name = "jboss-marshalling"),
    ExclusionRule(organization = "org.jboss.logging", name = "jboss-logging"),
    ExclusionRule(organization = "org.jboss.spec.javax.transaction", name = "jboss-transaction-api_1.1_spec")
    ),
  "org.jgroups" % "jgroups" % "3.4.1.Final",
  "org.jboss.spec.javax.transaction" % "jboss-transaction-api_1.1_spec" % "1.0.1.Final",
  "org.jboss.marshalling" % "jboss-marshalling-river" % "1.4.4.Final",
  "org.jboss.marshalling" % "jboss-marshalling" % "1.4.4.Final",
  "org.jboss.logging" % "jboss-logging" % "3.1.2.GA",
  "net.jcip" % "jcip-annotations" % "1.0",
  "GhostCommonLib" % "GhostCommonLib" % "0.23-BETA"
)