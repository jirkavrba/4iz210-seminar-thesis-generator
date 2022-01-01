package dev.vrba.generator4iz210.service.fulltext

import org.junit.jupiter.api.Test

class FulltextSearchTaskGeneratorTests {

    @Test
    fun testSampleInput() {
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