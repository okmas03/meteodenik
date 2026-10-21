package cz.utb.meteodenik.cv04.delegaty

import kotlin.reflect.KProperty

class HlidanyText(pocatecni: String) {
    private var hodnota: String = over(pocatecni, "počáteční hodnota")

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        println("čtu ${property.name}")
        return hodnota
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("zapisuji ${property.name} = \"$value\"")
        hodnota = over(value, property.name)
    }

    private fun over(text: String, kde: String): String {
        val orezany = text.trim()
        require(orezany.isNotEmpty()) { "$kde nesmí být prázdný text" }
        return orezany
    }
}

class Nastaveni {
    var jednotka: String by HlidanyText("C")
    var vychoziMesto: String by HlidanyText("Zlín")

    val nazevZarizeni: String by lazy {
        println("počítám nazevZarizeni…")
        "MeteoDeník-zařízení"
    }
}

fun main() {
    val n = Nastaveni()

    println("Jednotka: ${n.jednotka}")

    n.vychoziMesto = "  Brno  "
    println("Město: [${n.vychoziMesto}]")

    try {
        n.jednotka = " "
    } catch (e: IllegalArgumentException) {
        println("Odmítnuto: ${e.message}")
    }
    println("Jednotka stále: ${n.jednotka}")

    println("1. čtení: ${n.nazevZarizeni}")
    println("2. čtení: ${n.nazevZarizeni}")
}