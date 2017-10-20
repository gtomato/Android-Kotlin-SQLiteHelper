package hinl.kotlin.database.helper

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import java.math.*
import java.util.*


@Target (AnnotationTarget.CLASS)
annotation class Database(val tableName: String = "")

@Target (AnnotationTarget.FIELD)
@Retention (AnnotationRetention.RUNTIME)
annotation class Schema(val isGeneratedId: Boolean = false,
                        val isNonNullable: Boolean = false,
                        val isAutoIncrease: Boolean = false,
                        val unique: Boolean = false,
                        val field: String)

internal fun KClass<*>.getTableName(): String {
    val dataBase = this.findAnnotation<Database>()
    return dataBase?.tableName ?: ""
}

internal fun KClass<*>.getDataBaseField(): HashMap<String, KProperty1<*, *>> {
    val schemaMap = HashMap<String, KProperty1<*, *>>()
    for (field in this.memberProperties ){//.java.declaredFields) {
        val schema = field.javaField?.annotations?.find { it is Schema } as? Schema
        if (schema != null) {
            schemaMap.put(schema.field, field)
        }
    }
    return schemaMap
}

internal fun KProperty1<*, *>.isDataBaseFieldGeneratedId(): Boolean? {
    val schema = (this.javaField?.annotations?.find { it is Schema } as? Schema)
    if (schema != null) {
        return schema.isGeneratedId
    } else {
        return false
    }
}

internal fun KProperty1<*, *>.isDataBaseFieldNonNullable(): Boolean? {
    val schema = (this.javaField?.annotations?.find { it is Schema } as? Schema)
    if (schema != null) {
        return schema.isNonNullable
    } else {
        return false
    }
}

internal fun KProperty1<*, *>.isDataBaseFieldAutoIncrease(): Boolean? {
    val schema = (this.javaField?.annotations?.find { it is Schema } as? Schema)
    if (schema != null) {
        return schema.isAutoIncrease
    } else {
        return false
    }
}

internal fun KProperty1<*, *>.isDataBaseFieldUnique(): Boolean? {
    val schema = (this.javaField?.annotations?.find { it is Schema } as? Schema)
    if (schema != null) {
        return schema.unique
    } else {
        return false
    }
}

internal fun Any.getDataBaseFieldValue(key: String): Any? {
    return this::class.memberProperties.find {
        (it.javaField?.annotations?.find { it is Schema } as? Schema)?.field == key
    }?.getter?.call(this)
}

internal fun KClass<*>.getDataBaseFieldType(): String {
    when (this) {
        String::class -> return "TEXT"
        Date::class -> return "BIGINT"
        Boolean::class -> return "BOOLEAN"
        Char::class -> return "CHAR"
        Byte::class -> return "TINYINT"
        Short::class -> return "SMALLINT"
        Int::class -> return "INTEGER"
        Long::class -> return "BIGINT"
        Float::class -> return "FLOAT"
        Double::class -> return "DOUBLE PRECISION"
        ByteArray::class -> return "BLOB"
        BigDecimal::class -> return "NUMERIC"
        else -> return ""
    }
}