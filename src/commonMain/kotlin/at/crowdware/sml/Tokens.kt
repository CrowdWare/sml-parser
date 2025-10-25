package at.crowdware.sml


enum class TokenType { IDENT, LBRACE, RBRACE, COLON, STRING, INT, FLOAT, BOOL, LINE_COMMENT, BLOCK_COMMENT, WS, EOF }

data class Span(val index: Int, val line: Int, val col: Int)

data class Token(
    val type: TokenType,
    val text: String,
    val start: Span,
    val end: Span
)

class SmlParseException(message: String, val span: Span) : Exception("$message at line ${span.line}, col ${span.col}")