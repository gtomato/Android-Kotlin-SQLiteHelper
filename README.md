# Android-Kotlin SQLiteHelper

Android-Kotlin SQLiteHelper is a Kotlin library for simplifly android sqlite database construction, read, write process

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
There should be a child class extended hinl.kotlin.database.helper.SQLiteDatabaseHelper and implement onCreate and onUngrade functions for database versioning

```kotlin
	fun onCreate(db: SQLiteDatabase?)
	fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
```

For Example:
```kotlin
	class DataBase(context: Context): SQLiteDatabaseHelper(context = Context
															, name = "DataBaseName.db"
															, factory = null
															, version = 1) {
		fun onCreate(db: SQLiteDatabase?) {
			//Do Some Stuff
		}
		fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
			//Do Some Stuff
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

## Authors

* **Hin Lai** - *Initial work*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
