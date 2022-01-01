package dev.vrba.generator4iz210.service.fulltext

import org.junit.jupiter.api.Test

class FulltextSearchTaskGeneratorTests {

    @Test
    fun testExampleInput() {
        // Taken from https://vse-my.sharepoint.com/:x:/g/personal/klit01_vse_cz/EdNudfDoQYJFtQbBHsN-JeAB3BDmJTyeNf25gr8DLPuPKA?e=HnuaB9
        val inputs = listOf(
            "kachna kachna kachna",
            "jídlo kachna kachna peking",
            "kachna kachna králík recept",
            "králík recept",
            "jídlo kachna peking recept"
        )

        // There is no need to specify variations
        val query = listOf("jídlo", "kachna", "králík", "peking", "recept").associateWith { emptyList<String>() }

        val output = FulltextSearchTaskGenerator().generateTaskOutput(inputs, query)
    }

    @Test
    fun testMyInput() {
        val inputs = listOf(
            "Logitech G Pro (2019), GX Blue, US, 3 199 Kč, Mechanická herní klávesnice s inteligentním LIGHTSYNC RGB podsvícením. Tenké provedení s výškou 34 mm. Odpojitelný kabel, USB konektor, 12 programovatelných kláves, herní režim, nemá numerickou klávesnici. Spínače GX Blue - aktivační dráha 2 mm, aktivační síla 50 g. Rozměry 153 x 360 x 34,4 mm. Hmotnost 980 g.",
            "CHERRY MX Board 8.0, TKL, Cherry MX Red, RGB, US, 1 990 Kč, Stylová moderní klávesnice s RGB podsvícením, která je osazena přesnými MX Red spínači (87 kláves). Podporuje n-key rollover a anti-ghosting. Kompaktní velikost zaručuje úsporu místa na herním stole. Rozhraní: USB port.",
            "Glorious GMMK Tenkeyless, Gateron Brown, US, 2 435 Kč, Herní klávesnice se stylovým designem, modulární mechanická, barevné nastavitelné podsvícení kláves, USB, plný anti-ghosting, 1,5 m kabel, možnost vyměnitelných spínačů a US rozložení kláves. Dodává se bez numeriky s klávesami s Gateron Brown spínači. 87 kláves bez numeriky.",
            "CHERRY G80-3000S, Cherry MX Brown, US, 1 389 Kč, Špičková herní mechanická klávesnice (rozložení kláves US international) osazená přesnými Cherry MX spínači (87+1 kláves). Bez numerického bloku. Podporuje n-key rollover a anti-ghosting. Kompaktní velikost zaručuje úsporu místa na stole. Rozhraní: USB port."
        )

        val query = mapOf(
            "rgb" to listOf(),
            "brown" to listOf(),
            "mechanická" to listOf(),
            "herní" to listOf("herním"),
            "klávesnice" to listOf("klávesnici"),
        )

        val output = FulltextSearchTaskGenerator().generateTaskOutput(inputs, query)
    }
}