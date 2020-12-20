import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.4.21"
}

group = "net.davidtanzer"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
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
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	languageVersion = "1.4"
}
