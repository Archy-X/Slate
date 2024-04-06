import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    idea
    `maven-publish`
}

group = "com.archyx"
version = project.property("projectVersion") as String

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://github.com/deanveloper/SkullCreator/raw/mvn-repo/")
}

dependencies {
    api("org.spongepowered:configurate-yaml:4.1.2") {
        exclude("org.yaml", "snakeyaml")
    }
    implementation("com.github.Archy-X:SmartInvs:f77ddde177")
    implementation("dev.dbassett:skullcreator:3.0.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.2")
    compileOnly("de.tr7zw:item-nbt-api:2.11.3")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.10.10")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}

tasks.withType<ShadowJar> {
    val projectVersion: String by project
    archiveFileName.set("Slate-${projectVersion}.jar")

    relocate("dev.dbassett.skullcreator", "com.archyx.slate.skullcreator")

    exclude("plugin.yml")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}