# 🌱 SML Parser — Kotlin Multiplatform (SAX-Style)

Ein kompakter, **SAX-ähnlicher Parser für SML (Simple Markup Language)**,  
komplett in **Kotlin Multiplatform** implementiert – ohne Dependencies.  
Er funktioniert auf **JVM, JS, WASM und Native**.

---

## 🚀 Features

✅ SAX-ähnlicher Event-Parser (kein DOM notwendig)  
✅ Einfach erweiterbare Grammatik  
✅ Unterstützt Kommentare (`//`, `/* ... */`)  
✅ Zahlen, Strings, Floats, Booleans  
✅ Kompatibel mit Maven Local / Maven Central  

---

## 📦 Installation

Veröffentliche zuerst lokal:
```bash
./gradlew publishToMavenLocal
```

Dann in deinem Projekt:
```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("at.crowdware:sml:1.11")
}
```

---

## 🧩 Beispielcode

```kotlin
import sml.*

fun main() {
    val text = """
        Page {
            id: "main"
            title: "Hello World"
            visible: true
            width: 800
            height: 600

            Row {
                spacing: 10
                Label { text: "Hello" }
                Label { text: "SML" }
            }
        }
    """.trimIndent()

    val parser = SmlSaxParser(text)

    parser.parse(object : SmlHandler {
        override fun startElement(name: String) {
            println("<start $name>")
        }

        override fun onProperty(name: String, value: PropertyValue) {
            println("  @$name = $value")
        }

        override fun endElement(name: String) {
            println("</end $name>")
        }
    })
}
```

### 🧾 Ausgabe:

```
<start Page>
  @id = StringValue(value=main)
  @title = StringValue(value=Hello World)
  @visible = BooleanValue(value=true)
  @width = IntValue(value=800)
  @height = IntValue(value=600)
<start Row>
  @spacing = IntValue(value=10)
<start Label>
  @text = StringValue(value=Hello)
</end Label>
<start Label>
  @text = StringValue(value=SML)
</end Label>
</end Row>
</end Page>
```

---

## 🧠 Projektstruktur

```
sml/
 ├─ build.gradle.kts
 ├─ settings.gradle.kts
 ├─ src/commonMain/kotlin/sml/
 │   ├─ PropertyValue.kt
 │   ├─ Tokens.kt
 │   ├─ Lexer.kt
 │   ├─ SaxHandler.kt
 │   ├─ SaxParser.kt
 │   └─ Dom.kt (optional)
 └─ README.md
```

---

## 🧪 DOM-Builder (optional)

Wenn du lieber einen kompletten Baum möchtest:

```kotlin
val handler = DomBuildingHandler()
SmlSaxParser(text).parse(handler)
println(handler.roots.first())
```

Ergebnis:
```
SmlNode(name=Page, properties={id=StringValue(main), ...}, children=[...])
```

---

## 🛠️ Erweiterungen

- Escapes in Strings (`\"`, `\\n`)  
- Arrays oder Listenwerte  
- Custom-Error-Handler  
- Streaming-Parsing (Chunk-weise Input)

---

## 📄 Lizenz

GPL3 License - see [LICENSE](LICENSE) file for details.


**Made with ❤️ by [CrowdWare](https://crowdware.info)**  
**Contact:** art@crowdware.info
