package ceres.lang

import ceres.data.avlMapOf
import ceres.lang.smtlib.IntegerE
import ceres.lang.smtlib.SMTExpr
import ceres.lang.smtlib.list
import ceres.lang.smtlib.sym

private fun fnTy(retTy: Type, vararg params: Pair<String, Type>, smtEncoder: SmtEncoder? = null) =
    FunctionType(params.map { it.first to Literal(it.second, TypeType)}, Literal(retTy, TypeType), smtEncoder = smtEncoder)

private val dddFn =
    fnTy(DoubleType.default, "x" to DoubleType.default, "y" to DoubleType.default)

private val ddbFn =
    fnTy(BoolType.default, "x" to DoubleType.default, "y" to DoubleType.default)

private val iiiFn =
    fnTy(IntegerType.default, "x" to IntegerType.default, "y" to IntegerType.default)

private val iibFn =
    fnTy(BoolType.default, "x" to IntegerType.default, "y" to IntegerType.default)

private val bbbFn =
    fnTy(BoolType.default, "x" to BoolType.default)

fun smt2(f: String): (SMTExpr, SMTExpr) -> SMTExpr = { x, y -> list(sym(f), x, y) }

fun smtEncode2FpRNE(f: String): (SMTExpr, SMTExpr) -> SMTExpr = {x, y -> list(sym(f), sym("RNE"), x, y) }

val BaseEvalEnv = Env(avlMapOf(
    "+" to wrap2(iiiFn, { x:Integer, y:Integer -> x.add(y)}, smtEncoder = smt2("+")),
    "-" to wrap2(iiiFn, { x:Integer, y:Integer -> x.subtract(y)}, smtEncoder = smt2("-")),
    "*" to wrap2(iiiFn, { x:Integer, y:Integer -> x.multiply(y)}, smtEncoder = smt2("*")),
    "div" to wrap2(iiiFn, { x:Integer, y:Integer -> x.divide(y)}, smtEncoder = smt2("div")),
    "mod" to wrap2(iiiFn, { x:Integer, y:Integer -> x.remainder(y)}, smtEncoder = smt2("mod")),
    "<" to wrap2(iibFn, { x:Integer, y:Integer -> x < y}, smtEncoder = smt2("<")),
    ">" to wrap2(iibFn, { x:Integer, y:Integer -> x > y}, smtEncoder = smt2(">")),
    "<=" to wrap2(iibFn, { x:Integer, y:Integer -> x <= y}, smtEncoder = smt2("<=")),
    ">=" to wrap2(iibFn, { x:Integer, y:Integer -> x >= y}, smtEncoder = smt2(">=")),
    "==" to wrap2(bbbFn, { x:Integer, y:Integer -> x == y}, smtEncoder = smt2("=")),
    "+." to wrap2(dddFn, { x:Double, y:Double -> x + y}, smtEncoder = smtEncode2FpRNE("fp.add")),
    "-." to wrap2(dddFn, { x:Double, y:Double -> x - y}, smtEncoder = smtEncode2FpRNE("fp.sub")),
    "*." to wrap2(dddFn, { x:Double, y:Double -> x * y}, smtEncoder = smtEncode2FpRNE("fp.mul")),
    "div." to wrap2(dddFn, { x:Double, y:Double -> x / y}, smtEncoder = smtEncode2FpRNE("fp.div")),
    "<." to wrap2(ddbFn, { x:Double, y:Double -> x < y}, smtEncoder = smt2("fp.lt")),
    ">." to wrap2(ddbFn, { x:Double, y:Double -> x > y}, smtEncoder = smt2("fp.gt")),
    "<=." to wrap2(ddbFn, { x:Double, y:Double -> x <= y}, smtEncoder = smt2("fp.leq")),
    ">=." to wrap2(ddbFn, { x:Double, y:Double -> x >= y}, smtEncoder = smt2("fp.geq")),
    "==." to wrap2(bbbFn, { x:Any?, y:Any? -> x == y}, smtEncoder = smt2("fp.eq")),
    // TODO add decimal support
//TODO    "!=" to FunExpr(),
    "not" to wrap1(bbbFn, { x:Boolean -> !x}, smtEncoder = {list(sym("not"), it)}) // TODO: maybe impl as Expr
))

val CostEnv= Env( avlMapOf(
//    "cost" to wrap1(
////        fnTy(FunctionType)
//    )
))

