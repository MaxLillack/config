package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValueType;

final class Tokens {
    static private class Value extends Token {

        private AbstractConfigValue value;

        Value(AbstractConfigValue value) {
            super(TokenType.VALUE);
            this.value = value;
        }

        AbstractConfigValue value() {
            return value;
        }

        @Override
        public String toString() {
            String s = tokenType().name() + "(" + value.valueType().name()
                    + ")";
            if (value instanceof ConfigObject || value instanceof ConfigList) {
                return s;
            } else {
                return s + "='" + value().unwrapped() + "'";
            }
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof Value;
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other) && ((Value) other).value.equals(value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + value.hashCode();
        }
    }

    static private class Line extends Token {
        private int lineNumber;

        Line(int lineNumber) {
            super(TokenType.NEWLINE);
            this.lineNumber = lineNumber;
        }

        int lineNumber() {
            return lineNumber;
        }

        @Override
        public String toString() {
            return "NEWLINE@" + lineNumber;
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof Line;
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other)
                    && ((Line) other).lineNumber == lineNumber;
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + lineNumber;
        }
    }

    // This is not a Value, because it requires special processing
    static private class UnquotedText extends Token {
        private ConfigOrigin origin;
        private String value;

        UnquotedText(ConfigOrigin origin, String s) {
            super(TokenType.UNQUOTED_TEXT);
            this.origin = origin;
            this.value = s;
        }

        ConfigOrigin origin() {
            return origin;
        }

        String value() {
            return value;
        }

        @Override
        public String toString() {
            return tokenType().name() + "(" + value + ")";
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof UnquotedText;
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other)
                    && ((UnquotedText) other).value.equals(value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + value.hashCode();
        }
    }

    static boolean isValue(Token token) {
        return token instanceof Value;
    }

    static AbstractConfigValue getValue(Token token) {
        if (token instanceof Value) {
            return ((Value) token).value();
        } else {
            throw new ConfigException.BugOrBroken(
                    "tried to get value of non-value token " + token);
        }
    }

    static boolean isValueWithType(Token t, ConfigValueType valueType) {
        return isValue(t) && getValue(t).valueType() == valueType;
    }

    static boolean isNewline(Token token) {
        return token instanceof Line;
    }

    static int getLineNumber(Token token) {
        if (token instanceof Line) {
            return ((Line) token).lineNumber();
        } else {
            throw new ConfigException.BugOrBroken(
                    "tried to get line number from non-newline " + token);
        }
    }

    static boolean isUnquotedText(Token token) {
        return token instanceof UnquotedText;
    }

    static String getUnquotedText(Token token) {
        if (token instanceof UnquotedText) {
            return ((UnquotedText) token).value();
        } else {
            throw new ConfigException.BugOrBroken(
                    "tried to get unquoted text from " + token);
        }
    }

    static ConfigOrigin getUnquotedTextOrigin(Token token) {
        if (token instanceof UnquotedText) {
            return ((UnquotedText) token).origin();
        } else {
            throw new ConfigException.BugOrBroken(
                    "tried to get unquoted text from " + token);
        }
    }

    /*
     * static ConfigString newStringValueFromTokens(Token... tokens) {
     * StringBuilder sb = new StringBuilder(); for (Token t : tokens) { if
     * (isValue(t)) { ConfigValue v = getValue(t); if (v instanceof
     * ConfigString) { sb.append(((ConfigString) v).unwrapped()); } else { //
     * FIXME convert non-strings to string throw new
     * ConfigException.BugOrBroken( "not handling non-strings here"); } } else
     * if (isUnquotedText(t)) { String s = getUnquotedText(t); sb.append(s); }
     * else { throw new ConfigException. } } }
     */

    static Token START = new Token(TokenType.START);
    static Token END = new Token(TokenType.END);
    static Token COMMA = new Token(TokenType.COMMA);
    static Token COLON = new Token(TokenType.COLON);
    static Token OPEN_CURLY = new Token(TokenType.OPEN_CURLY);
    static Token CLOSE_CURLY = new Token(TokenType.CLOSE_CURLY);
    static Token OPEN_SQUARE = new Token(TokenType.OPEN_SQUARE);
    static Token CLOSE_SQUARE = new Token(TokenType.CLOSE_SQUARE);

    static Token newLine(int lineNumberJustEnded) {
        return new Line(lineNumberJustEnded);
    }

    static Token newUnquotedText(ConfigOrigin origin, String s) {
        return new UnquotedText(origin, s);
    }

    static Token newValue(AbstractConfigValue value) {
        return new Value(value);
    }

    static Token newString(ConfigOrigin origin, String value) {
        return newValue(new ConfigString(origin, value));
    }

    static Token newInt(ConfigOrigin origin, int value) {
        return newValue(new ConfigInt(origin, value));
    }

    static Token newDouble(ConfigOrigin origin, double value) {
        return newValue(new ConfigDouble(origin, value));
    }

    static Token newLong(ConfigOrigin origin, long value) {
        return newValue(new ConfigLong(origin, value));
    }

    static Token newNull(ConfigOrigin origin) {
        return newValue(new ConfigNull(origin));
    }

    static Token newBoolean(ConfigOrigin origin, boolean value) {
        return newValue(new ConfigBoolean(origin, value));
    }
}
