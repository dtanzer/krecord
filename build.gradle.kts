import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.4.21"
	id("com.jfrog.bintray") version "1.8.5"
	id("maven-publish")
}

buildscript {
	repositories {
		mavenCentral()
		jcenter()
	}
}

group = "net.davidtanzer"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
	jcenter()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
	testImplementation("org.assertj:assertj-core:3.18.1")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks {
	test {
		useJUnitPlatform()

		testLogging {
			events("passed", "skipped", "failed")
		}
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable")
		jvmTarget = "1.8"
	}
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	languageVersion = "1.4"
}


publishing {
	publications {
		register("mavenJava", MavenPublication::class) {
			from(components["kotlin"])
			groupId = "net.davidtanzer"
			artifactId = "krecord"
			version = "0.0.1"
			pom.withXml {
				val root = asNode()
				root.appendNode("description", "Immutable Records for Kotlin and Java")
				root.appendNode("name", "krecord")
				root.appendNode("url", "https://github.com/dtanzer/krecord")
			}
		}
	}
}

bintray {
	user = System.getProperty("bintray.user")
	key = System.getProperty("bintray.key")
	setPublications("mavenJava")

	pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {
		repo = "krecord"
		name = "krecord"
		userOrg = "dtanzer"
		setLicenses("MIT")
		vcsUrl = "https://github.com/dtanzer/krecord"
	})
}
