package at.crowdware.sml

import kotlin.jvm.JvmInline

sealed interface PropertyValue {
    @JvmInline
    value class IntValue(val value: Int) : PropertyValue
    @JvmInline value class FloatValue(val value: Float) : PropertyValue
    @JvmInline value class BooleanValue(val value: Boolean) : PropertyValue
    @JvmInline value class StringValue(val value: String) : PropertyValue
}