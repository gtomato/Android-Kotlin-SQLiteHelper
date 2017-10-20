package hinl.kotlin.database.helper

import java.util.*


interface ISelectionOperator<WHERE:ISelectionOperator<WHERE, OPERATOR>,
        OPERATOR: ISelectionOperator<WHERE, OPERATOR>> {
    fun eq(key: String, value: Any): WHERE
    fun notEq(key: String, value: Any): WHERE
    fun greaterThan(key: String, value: Number): WHERE
    fun greaterThanOrEq(key: String, value: Number): WHERE
    fun smallerThan(key: String, value: Number): WHERE
    fun smallerThanOrEq(key: String, value: Number): WHERE
    fun between(key: String, first: Number, second: Number): WHERE
    fun between(key: String, first: Date, second: Date): WHERE
    fun containString(key: String, value: String): WHERE
    fun and(): OPERATOR
    fun or(): OPERATOR

    enum class Order {
        Ascending,
        Descending;

        fun getClauseString(): String {
            when (this) {
                Ascending -> return "ASC"
                Descending -> return "DESC"
            }
        }
    }
}

class Where: ISelectionOperator<Where, Where.Operator> {
    companion object {
        val SPACE = " "
    }
    private val mStatements = ArrayList<IStatement>()
    private var mOrder: IStatement? = null

    override fun eq(key: String, value: Any): Where {
        mStatements.add(EqStatement(key, value))
        return this
    }

    override fun notEq(key: String, value: Any): Where {
        mStatements.add(NotEqStatement(key, value))
        return this
    }

    override fun greaterThan(key: String, value: Number): Where {
        mStatements.add(GreaterThanStatement(key, value))
        return this
    }

    override fun greaterThanOrEq(key: String, value: Number): Where {
        mStatements.add(GreaterThanStatement(key, value, true))
        return this
    }

    override fun smallerThan(key: String, value: Number): Where {
        mStatements.add(SmallerThanStatement(key, value))
        return this
    }

    override fun smallerThanOrEq(key: String, value: Number): Where {
        mStatements.add(SmallerThanStatement(key, value, true))
        return this
    }

    override fun between(key: String, first: Number, second: Number): Where {
        mStatements.add(BetweenStatement(key, first, second))
        return this
    }

    override fun between(key: String, first: Date, second: Date): Where {
        mStatements.add(BetweenStatement(key, first, second))
        return this
    }

    override fun containString(key: String, value: String): Where {
        mStatements.add(LikeStatement(key, value))
        return this
    }

    private fun addOperatorFuntion(operatorFunction: IStatement): Where {
        if (mStatements.isEmpty()) {
            throw IllegalArgumentException("And statement cannot be the first params")
        }
        mStatements.add(operatorFunction)
        return this
    }

    fun orderBy(order: ISelectionOperator.Order, vararg key: String): Where {
        this.mOrder = OrderStatement(key.toList(), order)
        return this
    }

    override fun and(): Operator {
        return Operator(this).and()
    }

    override fun or(): Operator {
        return Operator(this).or()
    }

    fun getClauseString(): String? {
        val sb = StringBuilder()
        mStatements.forEach {
            sb.append(it.getStatementString())
            sb.append(SPACE)
        }
        if (sb.isNotEmpty()) {
            return sb.toString()
        } else {
            return null
        }
    }

    fun getArgs(): Array<String>? {
        val stringArr = ArrayList<String>()
        mStatements.forEach {
            val arr = it.getArgs()
            if (arr != null) {
                stringArr.addAll(arr)
            }
        }
        if (stringArr.isNotEmpty()) {
            return stringArr.toArray(arrayOfNulls(stringArr.size))
        } else {
            return null
        }
    }

    fun getOrder(): String? {
        return mOrder?.getStatementString()
    }

    class Operator internal constructor(val where: Where): ISelectionOperator<Where, Where.Operator> {
        override fun eq(key: String, value: Any): Where {
            where.eq(key, value)
            return where
        }

        override fun notEq(key: String, value: Any): Where {
            where.notEq(key, value)
            return where
        }

        override fun greaterThan(key: String, value: Number): Where {
            where.greaterThan(key, value)
            return where
        }

        override fun greaterThanOrEq(key: String, value: Number): Where {
            where.greaterThanOrEq(key, value)
            return where
        }

        override fun smallerThan(key: String, value: Number): Where {
            where.smallerThan(key, value)
            return where
        }

        override fun smallerThanOrEq(key: String, value: Number): Where {
            where.smallerThanOrEq(key, value)
            return where
        }

        override fun between(key: String, first: Number, second: Number): Where {
            where.between(key, first, second)
            return where
        }

        override fun between(key: String, first: Date, second: Date): Where {
            where.between(key, first, second)
            return where
        }

        override fun containString(key: String, value: String): Where {
            where.containString(key, value)
            return where
        }

        override fun and(): Operator {
            where.addOperatorFuntion(Condition.And)
            return this
        }

        override fun or(): Operator {
            where.addOperatorFuntion(Condition.Or)
            return this
        }
    }

    private data class EqStatement(val key: String,
                                   val value: Any): IStatement {
        override fun getStatementString(): String {
            return key + IStatement.Equal
        }

        override fun getArgs(): Array<String>? {
            return arrayOf(value.toString())
        }
    }

    private data class NotEqStatement(val key: String,
                                      val value: Any): IStatement {
        override fun getStatementString(): String {
            return key + IStatement.NotEqual
        }

        override fun getArgs(): Array<String>? {
            return arrayOf(value.toString())
        }
    }

    private data class GreaterThanStatement(val key: String,
                                            val value: Number,
                                            val isEqual: Boolean = false): IStatement {
        override fun getStatementString(): String {
            return key + when(isEqual){
                true -> IStatement.GreaterThanOrEq
                false -> IStatement.GreaterThan
            }
        }

        override fun getArgs(): Array<String>? {
            return arrayOf(value.toString())
        }
    }

    private data class SmallerThanStatement(val key: String,
                                            val value: Number,
                                            val isEqual: Boolean = false): IStatement {
        override fun getStatementString(): String {
            return key + when(isEqual){
                true -> IStatement.SmallerThanOrEq
                false -> IStatement.SmallerThan
            }
        }

        override fun getArgs(): Array<String>? {
            return arrayOf(value.toString())
        }
    }

    private data class BetweenStatement(val key: String,
                                        val first: Any,
                                        val second: Any): IStatement{
        override fun getStatementString(): String {
            return key + IStatement.Between
        }

        override fun getArgs(): Array<String>? {
            when (first) {
                is Date -> {
                    when (second) {
                        is Date -> return arrayOf(first.time.toString(), second.time.toString())
                        else -> return null
                    }
                }
                else -> return arrayOf(first.toString(), second.toString())
            }
        }
    }

    private data class LikeStatement(val key: String,
                                     val value: String): IStatement {
        override fun getStatementString(): String {
            return key + IStatement.Like
        }

        override fun getArgs(): Array<String>? {
            return arrayOf("%$value%")
        }
    }

    private data class OrderStatement(val key: List<String>,
                                      val order: ISelectionOperator.Order): IStatement {
        override fun getStatementString(): String {
            val sb = StringBuilder()
            key.forEachIndexed {
                index, s ->
                sb.append(s)
                if (index < key.size - 1) {
                    sb.append(",")
                }
            }
            sb.append(SPACE)
            sb.append(order.getClauseString())
            return sb.toString()
        }

        override fun getArgs(): Array<String>? {
            return null
        }
    }

    enum class Condition: IStatement {
        And,
        Or,
        Between,
        Larger,
        Smaller;

        override fun getArgs(): Array<String>? {
            return null
        }

        override fun getStatementString(): String {
            when (this) {
                And -> return "AND"
                Or -> return "OR"
                else -> return ""
            }
        }
    }


    internal interface IStatement {
        companion object {
            val Equal = " = ?"
            val NotEqual = " != ?"
            val GreaterThan = " > ?"
            val GreaterThanOrEq = " >= ?"
            val SmallerThan = " < ?"
            val SmallerThanOrEq = " <= ?"
            val Between = " BETWEEN ? AND ?"
            val Like = " LIKE ?"
        }
        fun getStatementString(): String
        fun getArgs(): Array<String>?
    }
}

internal data class Statements(
        val whereClause: String?,
        val whereArgs: Array<String>?,
        val order: String?
)