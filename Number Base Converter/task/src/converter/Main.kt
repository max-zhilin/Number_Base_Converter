package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

fun main() {
    do {
        val input1 = input("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        if (input1 != "/exit") {
            val (sourceBase, targetBase) = input1.split(" ").filterNot { it.isBlank() }.map { it.toInt() }
            do {
                val input2 = input("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back) ")
                if (input2 != "/back") {
                    println("Conversion result: ${input2.convertFromBaseToBase(sourceBase, targetBase)}")
                }
            } while (input2 != "/back")
        }
    } while (input1 != "/exit")
}

fun input(s: String): String {
    print(s)
    return readLine()!!
}

fun String.convertFromBaseToBase(sourceBase: Int, targetBase: Int): String {
    val integer = this.substringBefore('.')
    val fractional = this.substringAfter('.', "")
    return convertIntegerPart(integer, sourceBase, targetBase) +
            if (fractional.isBlank()) "" else "." + convertFractionalPart(fractional, sourceBase, targetBase)
}

fun convertIntegerPart(part: String, sourceBase: Int, targetBase: Int): String {
    var num = convertToDecimal(part, sourceBase)
    var result = ""
    val targetBaseBigInteger = targetBase.toBigInteger()
    while (num > BigInteger.ZERO) {
        result = (num % targetBaseBigInteger).toInt().mapToChar() + result
        num /= targetBaseBigInteger
    }
    return if (result == "") "0" else result
}

fun convertToDecimal(part: String, sourceBase: Int): BigInteger {
    val charList: List<Char> = part.reversed().toList()
    var num = BigInteger.ZERO
    var powerOfBase = BigInteger.ONE
    for (c in charList) {
        num += powerOfBase * c.mapToInt()
        powerOfBase *= sourceBase.toBigInteger()
    }
    return num
}

fun convertFractionalPart(part: String, sourceBase: Int, targetBase: Int): String {
    val dividend = convertToDecimal(part, sourceBase).toBigDecimal().setScale(10)
    val divisor = sourceBase.toBigDecimal().pow(part.length)
    var num = dividend / divisor

    var result = ""
    while (result.length < 5) {
        val product = num * targetBase
        val integerPart = product.setScale(0, RoundingMode.DOWN)
        result += integerPart.toInt().mapToChar()
        num = product - integerPart
    }
    return result
}

fun Int.mapToChar(): Char = if (this > 9) 'a' + this - 10 else '0' + this

fun Char.mapToInt() = if (this.toLowerCase() >= 'a') this.toLowerCase() - 'a' + 10 else this - '0'

operator fun BigInteger.times(other: Int): BigInteger = this.multiply(other.toBigInteger())

operator fun BigDecimal.times(other: Int): BigDecimal = this.multiply(other.toBigDecimal())
