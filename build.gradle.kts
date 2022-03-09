import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
	repositories {
		maven("https://plugins.gradle.org/m2/")
		mavenCentral()
	}

	dependencies {
		classpath("gradle.plugin.com.ewerk.gradle.plugins:querydsl-plugin:1.0.10")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
	}
}


plugins {
	id("org.springframework.boot") version "2.6.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.asciidoctor.jvm.convert") version "3.3.2"

	val kotlinVersion = "1.6.0"

	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
	kotlin("kapt") version kotlinVersion
	kotlin("plugin.allopen") version kotlinVersion

	idea
}

group = "com.notworking"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8
val qeurydslVersion = "4.4.0" // <= 추가 설정

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
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	//implementation("io.jsonwebtoken:jjwt:0.9.1")
	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation( "io.github.microutils:kotlin-logging-jvm:2.1.20")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	//runtimeOnly("com.h2database:h2")
	//runtimeOnly("mysql:mysql-connector-java")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")


	implementation("com.querydsl:querydsl-jpa")
	kapt(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")
	sourceSets.main {
		withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
			kotlin.srcDir("$buildDir/generated/source/kapt/main")
		}
	}

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
		//exclude("**/*")
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