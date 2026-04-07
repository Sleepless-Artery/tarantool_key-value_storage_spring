import com.google.protobuf.gradle.*

plugins {
    java
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.google.protobuf") version "0.9.4"
}

group = "org.sleepless_artery"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.spring.io/milestone")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("io.grpc:grpc-netty-shaded:1.79.0")
    implementation("io.netty:netty-all:4.2.12.Final")
    implementation("io.netty:netty-handler:4.2.12.Final")
    implementation("io.netty:netty-transport:4.2.12.Final")
    implementation("io.netty:netty-common:4.2.12.Final")

    implementation("io.grpc:grpc-protobuf:1.60.0")
    implementation("io.grpc:grpc-stub:1.60.0")
    implementation("io.grpc:grpc-services:1.60.0")

    implementation("io.netty:netty-codec:4.2.12.Final")
    implementation("io.netty:netty-buffer:4.2.12.Final")
    implementation("io.netty:netty-resolver:4.2.12.Final")

    implementation("com.google.protobuf:protobuf-java-util:3.25.3")

    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("io.tarantool:tarantool-client:1.5.0")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.60.0"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}