import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.asciidoctor.jvm.convert") version "3.3.2"

	kotlin("jvm") version "1.6.0"
	kotlin("plugin.spring") version "1.6.0"
	kotlin("plugin.jpa") version "1.6.0"
}

group = "com.notworking"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation( "io.github.microutils:kotlin-logging-jvm:2.1.20")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("mysql:mysql-connector-java")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

	testImplementation("org.springframework.restdocs:spring-restdocs-asciidoctor")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

	implementation("io.springfox:springfox-swagger2:2.9.2")
	implementation("io.springfox:springfox-swagger-ui:2.9.2")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict", "-Xskip-prerelease-check")
		jvmTarget = "1.8"
	}
}
/*
tasks.withType<Test> {
	useJUnitPlatform()
}
*/
tasks {
	val snippetsDir = file("$buildDir/generated-snippets")

	clean {
		delete("src/main/resources/static/docs")
	}

	test {
		useJUnitPlatform()
		systemProperty("org.springframework.restdocs.outputDir", snippetsDir)
		outputs.dir(snippetsDir)
	}

	build {
		dependsOn("copyDocument")
	}

	asciidoctor {
		dependsOn(test)

		attributes(
			mapOf("snippets" to snippetsDir)
		)
		inputs.dir(snippetsDir)

		doFirst {
			delete("src/main/resources/static/docs")
		}
	}

	register<Copy>("copyDocument") {
		dependsOn(asciidoctor)

		destinationDir = file(".")
		from(asciidoctor.get().outputDir) {
			into("src/main/resources/static/docs")
		}
	}

	bootJar {
		dependsOn(asciidoctor)
		duplicatesStrategy = DuplicatesStrategy.INCLUDE
		from(asciidoctor.get().outputDir) {
			into("BOOT-INF/classes/static/docs")
		}
	}
}