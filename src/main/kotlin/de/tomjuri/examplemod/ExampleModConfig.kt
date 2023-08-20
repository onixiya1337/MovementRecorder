package de.tomjuri.examplemod

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Text
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType

class ExampleModConfig : Config(Mod("examplemod", ModType.UTIL_QOL), "examplemod.json") {

    init {
        initialize()
    }

    @Text(name = "Just some example text")
    var exampleText = "Hello World!"
}