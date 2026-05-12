package com.example.pennywise.data.mock

import com.example.pennywise.domain.model.CurrencyRate
import com.example.pennywise.domain.model.CurrencyRates
import java.nio.charset.Charset
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class MockCurrencyService {
    fun fetchRates(): CurrencyRates {
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        val bytes = MOCK_XML.toByteArray(Charset.forName("windows-1251"))
        parser.setInput(bytes.inputStream(), "windows-1251")

        var eventType = parser.eventType
        var date = ""
        val rates = mutableListOf<CurrencyRate>()

        var charCode = ""
        var name = ""
        var nominal = 1
        var value = 0.0
        var unitRate = 0.0

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "ValCurs" -> {
                            date = parser.getAttributeValue(null, "Date") ?: ""
                        }
                        "Valute" -> {
                            charCode = ""
                            name = ""
                            nominal = 1
                            value = 0.0
                            unitRate = 0.0
                        }
                        "CharCode" -> charCode = readText(parser)
                        "Name" -> name = readText(parser)
                        "Nominal" -> nominal = readText(parser).toIntOrNull() ?: 1
                        "Value" -> value = parseNumber(readText(parser))
                        "VunitRate" -> unitRate = parseNumber(readText(parser))
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "Valute" && charCode.isNotBlank()) {
                        rates.add(
                            CurrencyRate(
                                charCode = charCode,
                                name = name,
                                nominal = nominal,
                                value = value,
                                unitRate = unitRate
                            )
                        )
                    }
                }
            }
            eventType = parser.next()
        }

        return CurrencyRates(date = date, rates = rates)
    }

    private fun readText(parser: XmlPullParser): String {
        return if (parser.next() == XmlPullParser.TEXT) {
            val result = parser.text ?: ""
            parser.nextTag()
            result.trim()
        } else {
            ""
        }
    }

    private fun parseNumber(raw: String): Double {
        return raw.replace(" ", "").replace(",", ".").toDoubleOrNull() ?: 0.0
    }

    private companion object {
        val MOCK_XML = listOf(
            "<?xml version=\"1.0\" encoding=\"windows-1251\"?>",
            "<ValCurs Date=\"13.05.2026\" name=\"Foreign Currency Market\">",
            "    <Valute ID=\"R01010\">",
            "        <NumCode>036</NumCode>",
            "        <CharCode>AUD</CharCode>",
            "        <Nominal>1</Nominal>",
            "        <Name>\u0410\u0432\u0441\u0442\u0440\u0430\u043b\u0438\u0439\u0441\u043a\u0438\u0439 \u0434\u043e\u043b\u043b\u0430\u0440</Name>",
            "        <Value>53,3560</Value>",
            "        <VunitRate>53,356</VunitRate>",
            "    </Valute>",
            "    <Valute ID=\"R01020A\">",
            "        <NumCode>944</NumCode>",
            "        <CharCode>AZN</CharCode>",
            "        <Nominal>1</Nominal>",
            "        <Name>\u0410\u0437\u0435\u0440\u0431\u0430\u0439\u0434\u0436\u0430\u043d\u0441\u043a\u0438\u0439 \u043c\u0430\u043d\u0430\u0442</Name>",
            "        <Value>43,4046</Value>",
            "        <VunitRate>43,4046</VunitRate>",
            "    </Valute>",
            "    <Valute ID=\"R01030\">",
            "        <NumCode>012</NumCode>",
            "        <CharCode>DZD</CharCode>",
            "        <Nominal>100</Nominal>",
            "        <Name>\u0410\u043b\u0436\u0438\u0440\u0441\u043a\u0438\u0445 \u0434\u0438\u043d\u0430\u0440\u043e\u0432</Name>",
            "        <Value>55,8103</Value>",
            "        <VunitRate>0,558103</VunitRate>",
            "    </Valute>",
            "</ValCurs>"
        ).joinToString("\n")
    }
}
