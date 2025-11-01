plugins {
    kotlin("multiplatform") version "2.0.20"
    `maven-publish`
}

group = "at.crowdware" // change to your org
version = "1.11"

kotlin {
    jvm()
    js(IR) {
        browser()
        nodejs()
    }
    // WASM target (Kotlin 2.0+)
    wasmJs {
        browser()
        nodejs()
    }
    // Minimal native set; add more as needed
    macosArm64()
    linuxX64()
    mingwX64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                name.set("sml")
                description.set("Streaming SML SAX parser (KMP)")
                url.set("https://github.com/yourorg/sml")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("art-crowdware")
                        name.set("art@crowdware.info")
                    }
                }
                scm {
                    url.set("https://github.com/CrowdWare/sml")
                }
            }
        }
    }
}