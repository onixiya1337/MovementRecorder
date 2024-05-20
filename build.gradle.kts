plugins {
    id("cc.polyfrost.loom") version "0.10.0.5"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.kyori.blossom") version "1.3.1"
    id("maven-publish")
}

group = "xyz.yuro"
version = "1.0.6"

repositories {
    mavenCentral()
    maven("https://repo.polyfrost.cc/releases")
    maven("https://repo.spongepowered.org/repository/maven-public")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

val embed: Configuration by configurations.creating
configurations.implementation.get().extendsFrom(embed)

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    compileOnly("cc.polyfrost:oneconfig-1.8.9-forge:0.2.2-alpha+")
    embed("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta+")

    compileOnly("org.spongepowered:mixin:0.8.5-SNAPSHOT")
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")

    modRuntimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.2.0")
}

blossom {
    replaceToken("%%VERSION%%", version)
}

loom {
    runConfigs {
        named("client") {
            ideConfigGenerated(true)
        }
    }

    launchConfigs {
        getByName("client") {
            arg("--tweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
            property("devauth.enabled", "true")
        }
    }

    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        mixinConfig("mixins.movementrecorder.json")
    }
}

tasks {
    jar {
        manifest.attributes(
                mapOf(
                        "ModSide" to "CLIENT",
                        "TweakOrder" to "0",
                        "ForceLoadAsMod" to true,
                        "TweakClass" to "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker",
                        "MixinConfigs" to "mixins.movementrecorder.json"
                )
        )
        dependsOn(shadowJar)
    }

    remapJar {
        input.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
    }

    shadowJar {
        configurations = listOf(embed)
    }

    processResources {
        inputs.property("version", version)
        filesMatching(listOf("mcmod.info")) {
            expand(mapOf("version" to version))
        }
    }

    build.configure {
        dependsOn("removeShadowArtifact")
    }
    register("removeShadowArtifact", Delete::class) {
        doLast {
            project.file("build/libs/MovementRecorder-$version-all.jar").delete()
        }
    }

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "xyz.yuro"
            artifactId = "MovementRecorder"
            version = project.version.toString()
            from(components["java"])
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

java.toolchain.languageVersion = JavaLanguageVersion.of(8)
