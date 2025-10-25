package at.crowdware.sml


interface SmlHandler {
    /** Called when an element starts: e.g., `Page {` -> startElement("Page"). */
    fun startElement(name: String)

    /** Emitted for `key: value` entries encountered in element content. */
    fun onProperty(name: String, value: PropertyValue)

    /** Called when an element ends: e.g., `}`. */
    fun endElement(name: String)
}