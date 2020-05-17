package com.mikadocs.minicalc

import org.antlr.v4.runtime.ANTLRInputStream
import org.junit.Assert.assertEquals
import java.io.StringReader
import java.util.*

// Utilities included only for completeness
fun tokensContent(lexer: MiniCalcLexer): List<String> {
    val tokens = LinkedList<String>()
    do {
        val t = lexer.nextToken()
        when (t.type) {
            -1 -> tokens.add("EOF")
            else -> if (t.type != MiniCalcLexer.WS) tokens.add(lexer.text)
        }
    }while (t.type != -1)
    return tokens
}

fun lexerForCode(code: String) = MiniCalcLexer(ANTLRInputStream(StringReader(code)))

fun tokensNames(lexer: MiniCalcLexer): List<String> {
    val tokens = LinkedList<String>()
    do {
        val t = lexer.nextToken()
        when (t.type) {
            -1 -> tokens.add("EOF")
            else -> if (t.type != MiniCalcLexer.WS) tokens.add(lexer.vocabulary.getSymbolicName(t.type))
        }
    } while (t.type != -1)
    return tokens
}

// End of utilities, here it starts the real test code

class MiniCalcLexerTest {
    @org.junit.Test
    fun parseVarDeclarationAssignedAnIntegerLiteral() {
        assertEquals(listOf("VAR", "ID", "ASSIGN", "INTLIT", "EOF"),
                tokensNames(lexerForCode("var a = 1")))
    }

    @org.junit.Test
    fun parseVarDeclarationAssignedADecimalLiteral() {
        assertEquals(listOf("VAR", "ID", "ASSIGN", "DECLIT", "EOF"), tokensNames(lexerForCode("var a = 1.23")))
    }

    @org.junit.Test
    fun parseVarDeclarationAssignedASum() {
        assertEquals(listOf("VAR", "ID", "ASSIGN", "INTLIT", "PLUS", "INTLIT", "EOF"), tokensNames(lexerForCode("var a = 1 + 2")))
    }

    @org.junit.Test
    fun parseMathematicalExpression() {
        assertEquals(listOf("INTLIT", "PLUS", "ID", "ASTERISK", "INTLIT", "DIVISION", "INTLIT", "MINUS", "INTLIT", "EOF"), tokensNames(lexerForCode("1 + a * 3 / 4 - 5")))
    }

    @org.junit.Test
    fun parseMathematicalExpressionWithParenthesis() {
        assertEquals(listOf("INTLIT", "PLUS", "LPAREN", "ID", "ASTERISK", "INTLIT", "RPAREN", "MINUS", "DECLIT", "EOF"),
                tokensNames(lexerForCode("1 + (a * 3) - 5.12")))
    }

    @org.junit.Test
    fun parseCast() {
        assertEquals(listOf("ID", "ASSIGN", "ID", "AS", "INT", "EOF"),
                tokensNames(lexerForCode("a = b as Int")))
    }

    @org.junit.Test
    fun parseSimpleString() {
        assertEquals(listOf("STRING_OPEN", "STRING_CONTENT", "STRING_CLOSE", "EOF"),
                tokensNames(lexerForCode("\"hi!\"")))
    }

    @org.junit.Test
    fun parseStringWithNewlineEscape() {
        val code = "\"hi!\\n\""
        assertEquals(listOf("\"", "hi!", "\\n", "\"", "EOF"),
                tokensContent(lexerForCode(code)))
        assertEquals(listOf("STRING_OPEN", "STRING_CONTENT", "ESCAPE_NEWLINE", "STRING_CLOSE", "EOF"),
                tokensNames(lexerForCode(code)))
    }

    @org.junit.Test
    fun parseStringWithSlashEscape() {
        assertEquals(listOf("STRING_OPEN", "STRING_CONTENT", "ESCAPE_SLASH", "STRING_CLOSE", "EOF"),
                tokensNames(lexerForCode("\"hi!\\\\\"")))
    }

    @org.junit.Test
    fun parseStringWithDelimiterEscape() {
        assertEquals(listOf("STRING_OPEN", "STRING_CONTENT", "ESCAPE_STRING_DELIMITER", "STRING_CLOSE", "EOF"),
                tokensNames(lexerForCode("\"hi!\\\"\"")))
    }

    @org.junit.Test
    fun parseStringWithSharpEscape() {
        assertEquals(listOf("STRING_OPEN", "STRING_CONTENT", "ESCAPE_SHARP", "STRING_CLOSE", "EOF"),
                tokensNames(lexerForCode("\"hi!\\#\"")))
    }

    @org.junit.Test fun parseStringWithInterpolation() {
        val code = "\"hi #{name}. This is a number: #{5 * 4}\""
        assertEquals(listOf("\"", "hi ", "#{", "name", "}", ". This is a number:", "#{", "5", "*", "4", "}", "\"", "EOF"),
        tokensContent(lexerForCode(code)))
        assertEquals(listOf("STRING_OPEN", "STRING_CONTENT", "INTERPOLATION_OPEN", "ID", "INTERPOLATION_CLOSE",
                "STRING_CONTENT", "INTERPOLATION_OPEN", "INTLIT", "ASTERISK", "INTLIT", "INTERPOLATION_CLOSE",
                "STRING_CLOSE", "EOF"),
        tokensNames(lexerForCode(code)))
    }
}
