package at.crowdware.sml

class SmlLexer(private val input: String) {
    private var i = 0
    private var line = 1
    private var col = 1

    private fun peek(offset: Int = 0): Char? = if (i + offset < input.length) input[i + offset] else null
    private fun advance(n: Int = 1) {
        repeat(n) {
            if (i < input.length) {
                val c = input[i]
                i++
                if (c == '\n') { line++; col = 1 } else col++
            }
        }
    }

    private fun spanStart() = Span(i, line, col)
    private fun token(type: TokenType, start: Span, text: String) = Token(type, text, start, Span(i, line, col))

    fun next(): Token {
        if (i >= input.length) return Token(TokenType.EOF, "", Span(i, line, col), Span(i, line, col))

        // Whitespace
        if (peek()?.isWhitespace() == true) {
            val start = spanStart()
            val sb = StringBuilder()
            while (peek()?.isWhitespace() == true) { sb.append(peek()); advance() }
            return token(TokenType.WS, start, sb.toString())
        }

        // Line comment // ...
        if (peek() == '/' && peek(1) == '/') {
            val start = spanStart()
            val sb = StringBuilder()
            while (peek() != null && peek() != '\n') { sb.append(peek()); advance() }
            return token(TokenType.LINE_COMMENT, start, sb.toString())
        }

        // Block comment /* ... */ (non-greedy)
        if (peek() == '/' && peek(1) == '*') {
            val start = spanStart()
            advance(2)
            val sb = StringBuilder("/*")
            while (true) {
                val c = peek() ?: break
                if (c == '*' && peek(1) == '/') { sb.append("*/"); advance(2); break }
                sb.append(c); advance()
            }
            return token(TokenType.BLOCK_COMMENT, start, sb.toString())
        }

        return when (val c = peek()) {
            '{' -> { val s = spanStart(); advance(); token(TokenType.LBRACE, s, "{") }
            '}' -> { val s = spanStart(); advance(); token(TokenType.RBRACE, s, "}") }
            ':' -> { val s = spanStart(); advance(); token(TokenType.COLON, s, ":") }
            '"' -> lexString()
            null -> Token(TokenType.EOF, "", Span(i, line, col), Span(i, line, col))
            else -> when {
                c.isLetter() || c == '_' -> lexIdentOrBool()
                c.isDigit() -> lexNumber()
                else -> throw SmlParseException("Unexpected character '$c'", Span(i, line, col))
            }
        }
    }

    private fun lexString(): Token {
        val start = spanStart()
        val sb = StringBuilder()
        val quote = peek()
        advance() // opening quote
        while (true) {
            val c = peek() ?: throw SmlParseException("Unterminated string literal", Span(i, line, col))
            if (c == quote) { advance(); break }
            // No escapes per grammar. If needed later, add \ handling here.
            if (c == '\n') throw SmlParseException("Newline in string literal", Span(i, line, col))
            sb.append(c)
            advance()
        }
        return Token(TokenType.STRING, sb.toString(), start, Span(i, line, col))
    }

    private fun lexIdentOrBool(): Token {
        val start = spanStart()
        val sb = StringBuilder()
        var first = true
        while (true) {
            val c = peek()
            if (c == null) break
            val ok = if (first) (c.isLetter() || c == '_') else (c.isLetterOrDigit() || c == '_')
            if (!ok) break
            sb.append(c)
            advance()
            first = false
        }
        val text = sb.toString()
        return if (text == "true" || text == "false") Token(TokenType.BOOL, text, start, Span(i, line, col))
        else Token(TokenType.IDENT, text, start, Span(i, line, col))
    }

    private fun lexNumber(): Token {
        val start = spanStart()
        val sb = StringBuilder()
        while (peek()?.isDigit() == true) { sb.append(peek()); advance() }
        if (peek() == '.' && peek(1)?.isDigit() == true) {
            sb.append('.')
            advance()
            while (peek()?.isDigit() == true) { sb.append(peek()); advance() }
            return Token(TokenType.FLOAT, sb.toString(), start, Span(i, line, col))
        }
        return Token(TokenType.INT, sb.toString(), start, Span(i, line, col))
    }
}