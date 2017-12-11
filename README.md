# Android-Kotlin SQLiteHelper

Android-Kotlin SQLiteHelper is a Kotlin library for simplifly android sqlite database construction, read, write process

#Feature
>Simple way to construct table
* Simply using annotation to create table and set column properties, [see also](#Construct-Table)
>One-line read or write data
* You can use one line only for read, insert, update and delete row in table
>Easy Selection case
* for more selection case, Where class can help you read or write data, [see also](#Selection-cause)

## Getting Started


### Installation
To use Android-Kotlin SQLiteHelper in gradle implemented project, 
```
dependencies {
	...
	implementation 'com.hinl:kotlindatabasehelper:1.0.0'
	...
}
```

### Usage

#### Implement SQLiteDatabaseHelper
Before using the database,
There should be a child class extended hinl.kotlin.database.helper.SQLiteDatabaseHelper and implement onCreate and onUpgrade functions for database versioning

```kotlin
	fun onCreate(db: SQLiteDatabase?)
	fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
```

For Example:
```kotlin
	class DataBase(context: Context): SQLiteDatabaseHelper(context = Context, 
	                                                       name = "DataBaseName.db", 
                                                           factory = null,
                                                           version = 1) {
		fun onCreate(db: SQLiteDatabase?) {
			//Initialize the database file
			//You can do some modification of database initializing in here
		}
		fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
			//Versioning the database
			//you can migration the database with different version in this function
		}
	}
	
```
#### Construct Table

For constructing database Table,
Kotlin Data class must be used.

For Example:

```kotlin
	@Database(tableName = "Example")data class Example(
			@Schema(generatedId = true, field = "Id", autoIncrease = true, nonNullable = true) val id: Int? = 0,
			@Schema(field = "columnOne") var columnOne: String?,
			@Schema(field = "columnTwo") var columnTwo: Int?,
			@Schema(field = "columnThree") var columnThree: Date? = null) {
	}
```
In @Database annotation, you can set the tableName of this class. And this is needed to construct the table.

@Schema Annotation have the following params can config
>field
* the column name of this variable
>generatedId (default false)
* the field will become primary key when it set to true
>nonNullable (default false)
* the field will become non-nullable in database when it set to true
>autoIncrease (default false)
* the field will become auto-increase when it set to true (same as 'autoincrement' in sql queue)
>unique (default false)
* the field will become unique combo when it set to true (same as 'unique()' in sql queue) 


And the init the table by this way 

```kotlin
    val dataBase = DataBase(context)
    database.createTable(Example::class)
```

#### Insert Row

For inserting Data into database,

```kotlin
    val dataBase = DataBase(context)
    val exampleObject = Example(columnOne = "One", columnTwo = 1, columnThree = Date())
    dataBase.insert(exampleObject)
```

#### Read Row

For Reading the content in database,

```kotlin
    val dataBase = DataBase(context)
    val exampleList = dataBase.get(Example::class)
```

For more selection case

```kotlin
    val dataBase = DataBase(context)
    val exampleList = dataBase.get(Example::class) {
        between("Id", 1, 20)
        eq("columnOne", "One")
        orderBy(key = "Id", order = ISelectionOperator.Order.Descending)
    }
```

#### Update Row

For Update row in database

```kotlin
    val dataBase = DataBase(context)
    originExampleObj.columnOne = "Two"
    dataBase.update(originExampleObj)
```

#### Delete Row

For Delete Row in database 

```kotlin
    val dataBase = DataBase(context)
    dataBase.delete(deleteExampleObj)
```

Or 

```kotlin
    val dataBase = DataBase(context)
    dataBase.delete(Example::class) {
        between("Id", 1, 20)
        eq("columnOne", "One")
    }
    
    // If you want to Delete All Row
    dataBase.delete(Example::class)
```

#### Count

For Counting the entry in database

```kotlin
    val dataBase = DataBase(context)
    val count = dataBase.count(Example::class)
```

For more selection case

```kotlin
    val dataBase = DataBase(context)
    val count = dataBase.count(Example::class) {
        between("Id", 1, 20)
        eq("columnOne", "One")
    }
```
#### Selection cause

In most of the function in this library, you can add Where class to be the selection cause in sql queue as lambda function

For Example

```kotlin
    val exampleList = dataBase.get(Example::class) {
            between("Id", 1, 20)
            eq("columnOne", "One")
            orderBy(key = "Id", order = ISelectionOperator.Order.Descending)
        }
```

Where class have the following functions

```kotlin
/**
* 
* This function is for equal statement
* @param key   Column Name in the table
*  @param value   the target row value
*/
    eq(key: String, value: Any)
    
/**
* 
* This function is for Not equal statement
* @param key   Column Name in the table
*  @param value   the target row value
*/
    notEq(key: String, value: Any)
    
/**
* 
* This function is for Greater Than statement
* @param key   Column Name in the table
*  @param value   the target row value in Number
*/
    greaterThan(key: String, value: Number)
    
/**
* 
* This function is for Greater Than Or Equal statement
* @param key   Column Name in the table
*  @param value   the target row value in Number
*/
    greaterThanOrEq(key: String, value: Number)

/**
* 
* This function is for Smaller Than statement
* @param key   Column Name in the table
*  @param value   the target row value in Number
*/
    smallerThan(key: String, value: Number)
    
/**
* 
* This function is for Smaller Than Or Equal statement
* @param key   Column Name in the table
*  @param value   the target row value in Number
*/
    smallerThanOrEq(key: String, value: Number)
    
/**
* 
* This function is for Between statement
* @param key   Column Name in the table
* @param first   the target row value in Number/Date
* @param second   the target row value in Number/Date
*/  
    between(key: String, first: Number/Date, second: Number/Date)
    
/**
* 
* This function is for LIKE statement
* @param key   Column Name in the table
*  @param value   the target row value in String
*/    
    containString(key: String, value: String)
    
/**
* This function is for link up different statement with AND
*/
    and()
/**
* This function is for link up different statement with OR
*/    
    or()
```

## More

For more example, you can take a look in /app module

## Authors

* **[Hin Lai](https://github.com/hinls1007)** - *Initial work*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
