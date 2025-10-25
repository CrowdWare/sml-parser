package at.crowdware.sml


class SmlSaxParser(private val text: String) {
    private val lexer = SmlLexer(text)
    private var lookahead: Token = lexer.next()

    fun parse(handler: SmlHandler) {
        skipIgnorables()
        while (lookahead.type != TokenType.EOF) {
            parseElement(handler)
            skipIgnorables()
        }
    }

    private fun parseElement(handler: SmlHandler) {
        val name = expect(TokenType.IDENT).text
        skipIgnorables()
        expect(TokenType.LBRACE)
        handler.startElement(name)
        skipIgnorables()

        while (lookahead.type != TokenType.RBRACE && lookahead.type != TokenType.EOF) {
            when (lookahead.type) {
                TokenType.IDENT -> {
                    val ident = consume()
                    skipIgnorables()
                    if (lookahead.type == TokenType.COLON) {
                        consume() // ':'
                        skipIgnorables()
                        val value = parseValue()
                        handler.onProperty(ident.text, value)
                        skipIgnorables()
                    } else {
                        // nested element
                        if (lookahead.type != TokenType.LBRACE) {
                            throw SmlParseException("Expected '{' after nested element name '${ident.text}'", lookahead.start)
                        }
                        consume() // '{'
                        handler.startElement(ident.text)
                        skipIgnorables()
                        parseElementBody(handler)
                        expect(TokenType.RBRACE)
                        handler.endElement(ident.text)
                        skipIgnorables()
                    }
                }
                else -> throw SmlParseException("Expected property or element", lookahead.start)
            }
        }

        expect(TokenType.RBRACE)
        handler.endElement(name)
    }

    private fun parseElementBody(handler: SmlHandler) {
        while (lookahead.type != TokenType.RBRACE && lookahead.type != TokenType.EOF) {
            when (lookahead.type) {
                TokenType.IDENT -> {
                    val id = consume()
                    skipIgnorables()
                    if (lookahead.type == TokenType.COLON) {
                        consume()
                        skipIgnorables()
                        val v = parseValue()
                        handler.onProperty(id.text, v)
                        skipIgnorables()
                    } else {
                        if (lookahead.type != TokenType.LBRACE) {
                            throw SmlParseException("Expected '{' after nested element name '${id.text}'", lookahead.start)
                        }
                        consume() // '{'
                        handler.startElement(id.text)
                        skipIgnorables()
                        parseElementBody(handler)
                        expect(TokenType.RBRACE)
                        handler.endElement(id.text)
                        skipIgnorables()
                    }
                }
                else -> throw SmlParseException("Expected property or element", lookahead.start)
            }
        }
    }

    private fun parseValue(): PropertyValue = when (lookahead.type) {
        TokenType.STRING -> PropertyValue.StringValue(consume().text)
        TokenType.FLOAT  -> PropertyValue.FloatValue(consume().text.toFloat())
        TokenType.INT    -> PropertyValue.IntValue(consume().text.toInt())
        TokenType.BOOL   -> PropertyValue.BooleanValue(consume().text == "true")
        else -> throw SmlParseException("Expected value", lookahead.start)
    }

    private fun expect(type: TokenType): Token {
        if (lookahead.type != type) {
            throw SmlParseException("Expected $type but found ${lookahead.type}", lookahead.start)
        }
        return consume()
    }

    private fun consume(): Token {
        val t = lookahead
        lookahead = lexer.next()
        return t
    }

    private fun skipIgnorables() {
        while (
            lookahead.type == TokenType.WS ||
            lookahead.type == TokenType.LINE_COMMENT ||
            lookahead.type == TokenType.BLOCK_COMMENT
        ) {
            lookahead = lexer.next()
        }
    }
}