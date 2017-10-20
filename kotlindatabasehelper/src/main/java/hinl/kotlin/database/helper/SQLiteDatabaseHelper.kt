package hinl.kotlin.database.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.math.BigDecimal
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure


class SQLiteDatabaseHelper: SQLiteOpenHelper {
    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
//    constructor(context: Context?, name: String?, factory: ((db: SQLiteDatabase, masterQuery: SQLiteCursorDriver, editTable: String, query: SQLiteQuery) -> Cursor)?, version: Int) : super(context, name, factory, version)
//    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int, errorHandler: DatabaseErrorHandler?) : super(context, name, factory, version, errorHandler)
//    constructor(context: Context?, name: String?, factory: ((db: SQLiteDatabase, masterQuery: SQLiteCursorDriver, editTable: String, query: SQLiteQuery) -> Cursor)?, version: Int, errorHandler: ((dbObj: SQLiteDatabase) -> Unit)?) : super(context, name, factory, version, errorHandler)

    companion object {
        val SPACE = " "

        val CREATE_TABLE = "CREATE TABLE"
        val IF_NOT_EXIST = "IF NOT EXISTS"
        val IF_EXIST = "IF EXISTS"
        val NOT_NULL = "not null"
        val PRIMARY_KEY = "primary key"
        val AUTO_INCREMENT = "autoincrement"
        val UNIQUE = "unique"
    }

    fun createTable(tableClass: KClass<*>) {
        val (tableName) = validateValidClass(tableClass)

        val sb = StringBuilder()
        sb.append(CREATE_TABLE)
        sb.append(SPACE)
        sb.append(IF_NOT_EXIST)
        sb.append(SPACE)
        sb.append(tableClass.getTableName())
        sb.append("(")
        val fieldMap = tableClass.getDataBaseField()
        if (fieldMap.isEmpty()) {
            return
        }
        fieldMap.keys.forEachIndexed { index, key ->
            val obj = fieldMap[key]
            if (obj != null && obj.returnType.jvmErasure.getDataBaseFieldType().isNotEmpty()) {
                sb.append(key)
                sb.append(SPACE)
                sb.append(obj.returnType.jvmErasure.getDataBaseFieldType())
                sb.append(SPACE)
                if (obj.isDataBaseFieldGeneratedId()?: false) {
                    sb.append(PRIMARY_KEY)
                    sb.append(SPACE)
                }
                if (obj.isDataBaseFieldAutoIncrease()?: false) {
                    sb.append(AUTO_INCREMENT)
                    sb.append(SPACE)
                }
                if (obj.isDataBaseFieldNonNullable()?: false) {
                    sb.append(NOT_NULL)
                    sb.append(SPACE)
                }
                if (obj.isDataBaseFieldUnique()?: false) {
                    sb.append(UNIQUE)
                    sb.append(SPACE)
                }
                if (index != fieldMap.keys.size -1) {
                    sb.append(",")
                }
            }
        }
        sb.append( ");")
        writableDatabase.execSQL(sb.toString())
    }

    fun insert(obj: Any) {
        val (tableName, fieldMap) = validateValidClass(obj::class)

        val contentValues = ContentValues()
        for (key in fieldMap.keys) {
            if ((fieldMap[key]?.javaField?.annotations?.find { it is Schema } as? Schema)?.generatedId ?: false) {
                continue
            }
            val value = obj.getDataBaseFieldValue(key = key)
            if (value != null) {
                when (value) {
                    is String -> contentValues.put(key, value)
                    is Date -> contentValues.put(key, value.time)
                    is Boolean -> contentValues.put(key, value)
                    is Char -> contentValues.put(key, value.toString())
                    is Byte -> contentValues.put(key, value)
                    is Short -> contentValues.put(key, value)
                    is Int -> contentValues.put(key, value)
                    is Long -> contentValues.put(key, value)
                    is Float -> contentValues.put(key, value)
                    is Double -> contentValues.put(key, value)
                    is ByteArray -> contentValues.put(key, value)
                    is BigDecimal -> contentValues.put(key, value.toDouble())

                }
            }
        }
        writableDatabase.insert(tableName, null, contentValues)
    }

    fun update(obj: Any, where: (Where.() -> Where)? = null) {
        val (tableName, fieldMap) = validateValidClass(obj::class)

        var (whereClause, args) = getWhereStatement(where)

        val contentValues = ContentValues()
        for (key in fieldMap.keys) {
            val schema: Schema? = fieldMap[key]?.javaField?.annotations?.find { it is Schema } as? Schema
            if (schema?.generatedId ?: false) {
                val field = schema?.field
                if (where == null && field != null && obj.getDataBaseFieldValue(key = key) != null) {
                    whereClause = schema.field + Where.IStatement.Equal
                    args = arrayOf(obj.getDataBaseFieldValue(key = key).toString())
                }
                continue
            }
            val value = obj.getDataBaseFieldValue(key = key)
            if (value != null) {
                when (value) {
                    is String -> contentValues.put(key, value)
                    is Date -> contentValues.put(key, value.time)
                    is Boolean -> contentValues.put(key, value)
                    is Char -> contentValues.put(key, value.toString())
                    is Byte -> contentValues.put(key, value)
                    is Short -> contentValues.put(key, value)
                    is Int -> contentValues.put(key, value)
                    is Long -> contentValues.put(key, value)
                    is Float -> contentValues.put(key, value)
                    is Double -> contentValues.put(key, value)
                    is ByteArray -> contentValues.put(key, value)
                    is BigDecimal -> contentValues.put(key, value.toDouble())

                }
            }
        }
        writableDatabase.update(tableName, contentValues, whereClause, args)
    }

    fun delete(obj: Any, where: (Where.() -> Where)? = null) {
        val (tableName, fieldMap) = validateValidClass(obj::class)
        var (whereClause, args) = getWhereStatement(where)

        for (key in fieldMap.keys) {
            val schema: Schema? = fieldMap[key]?.javaField?.annotations?.find { it is Schema } as? Schema
            if (schema?.generatedId ?: false) {
                val field = schema?.field
                if (where == null && field != null && obj.getDataBaseFieldValue(key = key) != null) {
                    whereClause = schema.field + Where.IStatement.Equal
                    args = arrayOf(obj.getDataBaseFieldValue(key = key).toString())
                }
                break
            }
        }
        writableDatabase.delete(tableName, whereClause, args)
    }

    fun <Clazz: Any>get(obj: KClass<Clazz>, where: (Where.() -> Where)? = null): ArrayList<Clazz>? {
        val listOfObj = ArrayList<Clazz>()

        val (tableName, fieldMap) = validateValidClass(obj)
        val rDB = readableDatabase
        val outputArr = Array(fieldMap.keys.size, {
            i -> fieldMap.keys.elementAt(i)
        })

        val (whereClause, args, order) = getWhereStatement(where)

        val c = rDB.query(tableName, outputArr, whereClause, args, null, null, order)
        while (c.moveToNext()) {
            val constructor = obj.primaryConstructor
            val paramsMap = HashMap<KParameter, Any?>()
            if (constructor != null) {
                val properties = obj.memberProperties
                for (property in properties) {
                    val fieldName = (property.javaField?.annotations?.find { it is Schema } as? Schema)?.field
                    if (fieldName?.isNotEmpty()?: false) {
                        val data = when (property.returnType.jvmErasure) {
                            String::class -> c.getString(c.getColumnIndex(fieldName))
                            Date::class -> Date(c.getLong(c.getColumnIndex(fieldName)))
                            Boolean::class -> c.getInt(c.getColumnIndex(fieldName)) == 1
                            Char::class -> c.getString(c.getColumnIndex(fieldName))
                            Byte::class -> c.getInt(c.getColumnIndex(fieldName))
                            Short::class -> c.getInt(c.getColumnIndex(fieldName))
                            Int::class -> c.getInt(c.getColumnIndex(fieldName))
                            Long::class -> c.getLong(c.getColumnIndex(fieldName))
                            Float::class -> c.getFloat(c.getColumnIndex(fieldName))
                            Double::class -> c.getDouble(c.getColumnIndex(fieldName))
                            ByteArray::class -> c.getBlob(c.getColumnIndex(fieldName))
                            BigDecimal::class -> BigDecimal(c.getDouble(c.getColumnIndex(fieldName)))
                            else -> null
                        }
                        val kParams = constructor.findParameterByName(property.name)
                        if (kParams != null) {
                            paramsMap.put(kParams, data)
                        }
                    }
                }
                listOfObj.add(constructor.callBy(paramsMap))
            }
        }
        c.close()

        return listOfObj
    }

    fun closeDatabase() {
        if (writableDatabase.isOpen) {
            writableDatabase.close()
        }
        if (readableDatabase.isOpen) {
            readableDatabase.close()
        }
    }

    private fun validateValidClass(obj: KClass<*>): DataBaseSchema{
        if (!obj.isData) {
            throw IllegalArgumentException("Object Must Be Instance of Data Class")
        }
        val tableName = obj.getTableName()
        if (tableName.isEmpty()) {
            throw IllegalArgumentException("Object Must Be Contain Table Name")
        }
        val fieldMap = obj.getDataBaseField()
        if (fieldMap.isEmpty()) {
            throw IllegalArgumentException("Object Must Be Contain Field(s)")
        }
        return DataBaseSchema(tableName, fieldMap)
    }

    private fun getWhereStatement(where: (Where.() -> Where)? = null): Statements {
        var whereClause: String? = null
        var args: Array<String>? = null
        var order: String? = null
        if (where != null) {
            val whereObj = Where()
            whereObj.where()
            whereClause = whereObj.getClauseString()
            args = whereObj.getArgs()
            order = whereObj.getOrder()
        }
        return Statements(whereClause, args, order)
    }
}

internal data class DataBaseSchema(
        val tableName: String,
        val fieldMap: HashMap<String, KProperty1<*, *>>
)