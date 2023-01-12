version = "0.2.0"

project.extra["PluginName"] = "Warp Gauntlet"
project.extra["PluginDescription"] = "Gear and prayer swapper for Hunlef/Corrupted"

tasks {
    jar {
        manifest {
            attributes(mapOf(
                    "Plugin-Version" to project.version,
                    "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                    "Plugin-Provider" to project.extra["PluginProvider"],
                    "Plugin-Description" to project.extra["PluginDescription"],
                    "Plugin-License" to project.extra["PluginLicense"]
            ))
        }
    }
}