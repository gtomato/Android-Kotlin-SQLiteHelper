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
annotation class Schema(val field: String,
                        val generatedId: Boolean = false,
                        val nonNullable: Boolean = false,
                        val autoIncrease: Boolean = false,
                        val unique: Boolean = false)

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
        return schema.generatedId
    } else {
        return false
    }
}

internal fun KProperty1<*, *>.isDataBaseFieldNonNullable(): Boolean? {
    val schema = (this.javaField?.annotations?.find { it is Schema } as? Schema)
    if (schema != null) {
        return schema.nonNullable
    } else {
        return false
    }
}

internal fun KProperty1<*, *>.isDataBaseFieldAutoIncrease(): Boolean? {
    val schema = (this.javaField?.annotations?.find { it is Schema } as? Schema)
    if (schema != null) {
        return schema.autoIncrease
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
    return when (this) {
        String::class -> "TEXT"
        Date::class -> "BIGINT"
        Boolean::class -> "BOOLEAN"
        Char::class -> "CHAR"
        Byte::class -> "TINYINT"
        Short::class -> "SMALLINT"
        Int::class -> "INTEGER"
        Long::class -> "BIGINT"
        Float::class -> "FLOAT"
        Double::class -> "DOUBLE PRECISION"
        ByteArray::class -> "BLOB"
        BigDecimal::class -> "NUMERIC"
        else -> ""
    }
}