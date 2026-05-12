package com.example.pennywise.data.network

import com.example.pennywise.domain.model.CurrencyRate
import com.example.pennywise.domain.model.CurrencyRates
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class CbrCurrencyService {
    fun fetchRates(): CurrencyRates {
        val url = URL("https://www.cbr.ru/scripts/XML_daily.asp")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000

        return connection.inputStream.use { stream ->
            parseRates(stream)
        }
    }

    private fun parseRates(stream: InputStream): CurrencyRates {
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(stream, "windows-1251")

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
                        val perUnit = if (unitRate > 0.0) unitRate else value / nominal
                        rates.add(
                            CurrencyRate(
                                charCode = charCode,
                                name = name,
                                nominal = nominal,
                                value = value,
                                unitRate = perUnit
                            )
                        )
                    }
                }
            }
            eventType = parser.next()
        }

        val withRub = mutableListOf(
            CurrencyRate(
                charCode = "RUB",
                name = "Российский рубль",
                nominal = 1,
                value = 1.0,
                unitRate = 1.0
            )
        )
        withRub.addAll(rates)

        return CurrencyRates(date = date, rates = withRub)
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
}
