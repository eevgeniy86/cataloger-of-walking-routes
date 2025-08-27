rootProject.name = "cataloger-of-walking-routes"

pluginManagement {
    val foojayResolverConvention: String by settings
    val dependencyManagement: String by settings
    val johnrengelmanShadow: String by settings
    val springframeworkBoot: String by settings
//    val protobufVer: String by settings
    val spotless: String by settings
    val jgitver: String by settings
    val sonarlint: String by settings
    val foojayresolver: String by settings

    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version foojayResolverConvention
        id("com.github.johnrengelman.shadow") version johnrengelmanShadow
        id("io.spring.dependency-management") version dependencyManagement
        id("org.springframework.boot") version springframeworkBoot
//        id("com.google.protobuf") version protobufVer
        id("com.diffplug.spotless") version spotless
        id("fr.brouillard.oss.gradle.jgitver") version jgitver
        id("name.remal.sonarlint") version sonarlint
        id("org.gradle.toolchains.foojay-resolver") version foojayresolver


    }
}
