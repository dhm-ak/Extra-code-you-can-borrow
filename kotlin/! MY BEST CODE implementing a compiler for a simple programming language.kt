// Token.kt
sealed class Token {
    object Plus : Token()
    object Minus : Token()
    object Times : Token()
    object Divide : Token()
    object Assign : Token()
    object If : Token()
    object Else : Token()
    object While : Token()
    object LParen : Token()
    object RParen : Token()
    object LBrace : Token()
    object RBrace : Token()
    object Comma : Token()
    object Semicolon : Token()
    data class Ident(val name: String) : Token()
    data class Number(val value: Int) : Token()
}

// Lexer.kt
class Lexer(private val input: String) {
    private var position = 0
    
    fun nextToken(): Token? {
        while (position < input.length) {
            when (val c = input[position]) {
                '+', '-', '*', '/' -> {
                    position++
                    return when (c) {
                        '+' -> Token.Plus
                        '-' -> Token.Minus
                        '*' -> Token.Times
                        '/' -> Token.Divide
                        else -> throw Exception("Unexpected character $c")
                    }
                }
                '=' -> {
                    position++
                    return if (position < input.length && input[position] == '=') {
                        position++
                        Token.Eq
                    } else {
                        Token.Assign
                    }
                }
                '<' -> {
                    position++
                    return if (position < input.length && input[position] == '=') {
                        position++
                        Token.Le
                    } else {
                        Token.Lt
                    }
                }
                '>' -> {
                    position++
                    return if (position < input.length && input[position] == '=') {
                        position++
                        Token.Ge
                    } else {
                        Token.Gt
                    }
                }
                '(', ')' -> {
                    position++
                    return when (c) {
                        '(' -> Token.LParen
                        ')' -> Token.RParen
                        else -> throw Exception("Unexpected character $c")
                    }
                }
                '{', '}' -> {
                    position++
                    return when (c) {
                        '{' -> Token.LBrace
                        '}' -> Token.RBrace
                        else -> throw Exception("Unexpected character $c")
                    }
                }
                ',' -> {
                    position++
                    return Token.Comma
                }
                ';' -> {
                    position++
                    return Token.Semicolon
                }
                in '0'..'9' -> {
                    var value = 0
                    while (position < input.length && input[position] in '0'..'9') {
                        value = value * 10 + (input[position] - '0')
                        position++
                    }
                    return Token.Number(value)
                }
                in 'a'..'z', in 'A'..'Z' -> {
                    var name = ""
                    while (position < input.length && input[position] in 'a'..'z' || input[position] in 'A'..'Z') {
                        name += input[position]
                        position++
                    }
                    return when (name) {
                        "if" -> Token.If
                        "else" -> Token.Else
                        "while" -> Token.While
                        else -> Token.Ident(name)
                    }
                }
                else -> {
                    position++
                    throw Exception("Unexpected character $c")
                }
            }
        }
        return null
    }
}

// Parser.kt
class Parser(private val lexer: Lexer) {
    private var currentToken: Token? = null

    private fun eat(token: Token) {
        if (currentToken == token) {
            currentToken = lexer.nextToken()
        } else {
            throw Exception("Expected token $token, but found $currentToken")
        }
    }

    private fun factor(): Expr {
        val token = currentToken
        return when {
            token is Token.Number -> {
                eat(token)
                NumberExpr(token.value)
            }
            token is Token.Ident -> {
                eat(token)
                if (currentToken == Token.LParen) {
                    eat(Token.LParen)
                    val args = mutableListOf<Expr>()
                    if (currentToken != Token.RParen) {
                        args.add(expr())
                        while (currentToken == Token.Comma) {
                            eat(Token.Comma)
                            args.add(expr())
                        }
                    }
                    eat(Token.RParen)
                    CallExpr(token.name, args)
                } else {
                    VarExpr(token.name)
                }
            }
            token == Token.LParen -> {
                eat(Token.LParen)
                val expr = expr()
                eat(Token.RParen)
                expr
            }
            else -> throw Exception("Expected factor, but found $token")
        }
    }

    private fun term(): Expr {
        var expr = factor()
        while (currentToken in listOf(Token.Times, Token.Divide)) {
            val token = currentToken
            eat(token!!)
            expr = BinaryExpr(token, expr, factor())
        }
        return expr
    }

    private fun expr(): Expr {
        var expr = term()
        while (currentToken in listOf(Token.Plus, Token.Minus)) {
            val token = currentToken
            eat(token!!)
            expr = BinaryExpr(token, expr, term())
        }
        return expr
    }

    private fun block(): Stmt {
        val stmts = mutableListOf<Stmt>()
        eat(Token.LBrace)
        while (currentToken != Token.RBrace) {
            stmts.add(statement())
        }
        eat(Token.RBrace)
        return BlockStmt(stmts)
    }

    private fun statement(): Stmt {
        val token = currentToken
        return when {
            token == Token.If -> {
                eat(Token.If)
                eat(Token.LParen)
                val condition = expr()
                eat(Token.RParen)
                val thenBranch = block()
                val elseBranch = if (currentToken == Token.Else) {
                    eat(Token.Else)
                    block()
                } else {
                    null
                }
                IfStmt(condition, thenBranch, elseBranch)
            }
            token == Token.While -> {
                eat(Token.While)
                eat(Token.LParen)
                val condition = expr()
                eat(Token.RParen)
                val body = block()
                WhileStmt(condition, body)
            }
            token is Token.Ident -> {
                eat(token)
                if (currentToken == Token.Assign) {
                    eat(Token.Assign)
                    val value = expr()
                    AssignStmt(token.name, value)
                } else {
                    error("Expected assignment operator, but found $currentToken")
                }
            }
            else -> error("Expected statement, but found $currentToken")
        }
    }

    fun parse(): Stmt {
        currentToken = lexer.nextToken()
        return block()
    }
}

// Expr.kt
sealed class Expr {
    abstract fun eval(env: Env): Int
}

data class NumberExpr(val value: Int) : Expr() {
    override fun eval(env: Env): Int = value
}

data class VarExpr(val name: String) : Expr() {
    override fun eval(env: Env): Int = env[name] ?: error("Undefined variable: $name")
}

data class BinaryExpr(val op: Token, val left: Expr, val right: Expr) : Expr() {
    override fun eval(env: Env): Int {
        val leftValue = left.eval(env)
        val rightValue = right.eval(env)
        return when (op) {
            Token.Plus -> leftValue + rightValue
            Token.Minus -> leftValue - rightValue
            Token.Times -> leftValue * rightValue
            Token.Divide -> leftValue / rightValue
            else -> error("Unsupported binary operator: $op")
        }
    }
}

data class CallExpr(val name: String, val args: List<Expr>) : Expr() {
    override fun eval(env: Env): Int {
        val func = env[name] as? Func ?: error("Undefined function: $name")
        if (func.params.size != args.size) {
            error("Wrong number of arguments for function $name")
        }
        val newEnv = env.copy()
        func.params.zip(args).forEach { (param, arg) ->
            newEnv[param] = arg.eval(env)
        }
        return func.body.eval(newEnv)
    }
}
