import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.net.URI

plugins {
    `java-library`
    id("com.gradleup.shadow") version "8.3.5"
    idea
    `maven-publish`
    signing
}

group = "dev.aurelium"
version = project.property("projectVersion") as String

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.helpch.at/releases/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    api("org.spongepowered:configurate-yaml:4.1.2") {
        exclude("org.yaml", "snakeyaml")
    }
    api("net.kyori:adventure-text-minimessage:4.16.0")
    api("net.kyori:adventure-platform-bukkit:4.3.2")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.mojang:authlib:1.5.25")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

configurations.all {
    exclude("org.yaml", "snakeyaml")
}

tasks {
    withType<ShadowJar> {
        val projectVersion: String by project

        exclude("plugin.yml")
    }

    javadoc {
        title = "Slate API (${project.version})"
        source = sourceSets.main.get().allSource
        classpath = files(sourceSets.main.get().compileClasspath)
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
            overview("javadoc/overview.html")
            encoding("UTF-8")
            charset("UTF-8")
        }
    }

    build {
        dependsOn(shadowJar)
        dependsOn(javadoc)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}


if (project.hasProperty("sonatypeUsername") && project.hasProperty("sonatypePassword")) {
    publishing {
        repositories {
            maven {
                val releasesRepoUrl = URI.create("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = project.property("sonatypeUsername").toString()
                    password = project.property("sonatypePassword").toString()
                }
            }
        }

        publications.create<MavenPublication>("mavenJava") {
            groupId = "dev.aurelium"
            artifactId = "slate"
            version = project.version.toString()

            pom {
                name.set("Slate")
                description.set("API for building user-configurable GUI menus")
                url.set("https://wiki.aurelium.dev/slate")
                licenses {
                    license {
                        name.set("The GNU General Public License, Version 3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }
                developers {
                    developer {
                        id.set(project.property("developerId").toString())
                        name.set(project.property("developerUsername").toString())
                        email.set(project.property("developerEmail").toString())
                        url.set(project.property("developerUrl").toString())
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Archy-X/Slate.git")
                    developerConnection.set("scm:git:git://github.com/Archy-X/Slate.git")
                    url.set("https://github.com/Archy-X/Slate/tree/master")
                }
            }

            from(components["java"])
        }
    }

    signing {
        sign(publishing.publications.getByName("mavenJava"))
        isRequired = true
    }
}