package com.zaroslikov.myconstruction.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast




class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    name, null, version
) {
    companion object {
        private const val name = MyConstanta.Constanta.DB_NAME
        private const val version = MyConstanta.Constanta.DB_VERSION
    }


    override fun onCreate(p0: SQLiteDatabase) {
        p0.execSQL(MyConstanta.TABLE_STRUCTURE_PROJECT)
        p0.execSQL(MyConstanta.TABLE_STRUCTURE_PRODUCT)
        p0.execSQL(MyConstanta.TABLE_STRUCTURE_PRODUCTPROJECT)
        p0.execSQL(MyConstanta.TABLE_STRUCTURE_ADD)
        p0.execSQL(MyConstanta.TABLE_STRUCTURE_WRITEOFF)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        Log.w(
            MyDatabaseHelper::class.java.name,
            "Upgrading database from version " + p1 + " to "
                    + p2 + ", which will destroy all old data"
        )
        p0.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_PROJECT")
        p0.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_PRODUCT")
        p0.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_PRODUCTPROJECT")
        p0.execSQL("DROP TABLE IF EXISTS  MyConstanta.DROP_TABLE_ADD")
        p0.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_WRITEOFF")
        onCreate(p0)
    }

    fun deleteAllDate() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM ${MyConstanta.Constanta.TABLE_NAME}")
        db.execSQL("DELETE FROM ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT}")
        db.execSQL("DELETE FROM ${MyConstanta.Constanta.TABLE_NAME_ADD}")
        db.execSQL("DELETE FROM ${MyConstanta.Constanta.TABLE_NAME_WRITEOFF}")
    }

    fun deleteDatabase(context: Context): Boolean {
        return context.deleteDatabase(name)
    }

    fun readProject(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM ${MyConstanta.Constanta.TABLE_NAME}", null)
    }

    fun readProduct(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM ${MyConstanta.Constanta.TABLE_NAME_PRODUCT}", null)
    }

    fun product(nameTable: String, nameCont: String, nameProduct: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM ${MyConstanta.Constanta.TABLE_NAME_PRODUCT}", null)
    }

    fun selectProductJoin(
        propertyId: Int,
        productName: String,
        tableName: String,
        suffix: String
    ): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT ${MyConstanta.Constanta.TITLEPRODUCT}, sum(${MyConstanta.Constanta.QUANTITY}), ${MyConstanta.Constanta.SUFFIX}" +
                    " FROM $tableName ad" +
                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} pp" +
                    " ON pp.${BaseColumns._ID}) = ad.${MyConstanta.Constanta.IDPP}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} + prod ON" +
                    " prod.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPRODUCT}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME} + proj" +
                    " ON proj.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPROJECT}" +

                    " WHERE proj.${BaseColumns._ID} = ? and ${MyConstanta.Constanta.TITLEPRODUCT} = ? and ${MyConstanta.Constanta.SUFFIX} =?" +
                    " group by ${MyConstanta.Constanta.TITLEPRODUCT}, ${MyConstanta.Constanta.SUFFIX}",
            arrayOf(propertyId.toString(), productName, suffix)
        )
    }

    fun selectProjectAllSum(propertyId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT sum(${MyConstanta.Constanta.PRICE})" +
                    " FROM ${MyConstanta.Constanta.TABLE_NAME_ADD} ad" +
                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} pp" +
                    " ON pp.${BaseColumns._ID} = ad.${MyConstanta.Constanta.IDPP}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} prod ON" +
                    " prod.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPRODUCT}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME} proj" +
                    " ON proj.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPROJECT}" +

                    " WHERE proj.${BaseColumns._ID} = ?",
            arrayOf(propertyId.toString())
        )
    }

    fun selectProjectAllSumCategory(propertyId: Int, category: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT ${MyConstanta.Constanta.CATEGORY}," +
                    " sum(${MyConstanta.Constanta.PRICE}), ${MyConstanta.Constanta.DATE}" +
                    " FROM ${MyConstanta.Constanta.TABLE_NAME_ADD} ad" +
                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} pp" +
                    " ON pp.${BaseColumns._ID} = ad.${MyConstanta.Constanta.IDPP}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} prod ON" +
                    " prod.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPRODUCT}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME} proj" +
                    " ON proj.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPROJECT}" +

                    " WHERE proj.${BaseColumns._ID} = ? and ${MyConstanta.Constanta.CATEGORY} =?",
            arrayOf(propertyId.toString(), category)
        )
    }

    fun selectProjectAllSumProduct(propertyId: Int, product: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT ${MyConstanta.Constanta.TITLEPRODUCT}, " +
                    "${MyConstanta.Constanta.SUFFIX}, sum(${MyConstanta.Constanta.PRICE}), ${MyConstanta.Constanta.DATE} " +
                    " FROM ${MyConstanta.Constanta.TABLE_NAME_ADD} ad" +
                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} pp" +
                    " ON pp.${BaseColumns._ID} = ad.${MyConstanta.Constanta.IDPP}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} prod ON" +
                    " prod.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPRODUCT}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME} proj" +
                    " ON proj.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPROJECT}" +

                    " WHERE proj.${BaseColumns._ID} =? and ${MyConstanta.Constanta.TITLEPRODUCT} =?",
            arrayOf(propertyId.toString(), product)
        )
    }

    fun selectProjectAllSumProductAndCount(propertyId: Int, product: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT ${MyConstanta.Constanta.TITLEPRODUCT}, ${MyConstanta.Constanta.SUFFIX}, sum(${MyConstanta.Constanta.PRICE})," +
                    " ${MyConstanta.Constanta.DATE}, sum(${MyConstanta.Constanta.QUANTITY})" +
                    " FROM ${MyConstanta.Constanta.TABLE_NAME_ADD} ad " +
                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} pp " +
                    " ON pp.${BaseColumns._ID} = ad.${MyConstanta.Constanta.IDPP} " +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} prod" +
                    " ON prod.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPRODUCT}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME} proj" +
                    " ON proj.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPROJECT}" +

                    " WHERE proj.${BaseColumns._ID} =? and ${MyConstanta.Constanta.TITLEPRODUCT} =?",
            arrayOf(propertyId.toString(), product)
        )
    }

    fun selectProjectAllProductAndCategoryAdd(propertyId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT ${MyConstanta.Constanta.TITLEPRODUCT}, ${MyConstanta.Constanta.SUFFIX}, ${MyConstanta.Constanta.CATEGORY}" +
                    " FROM ${MyConstanta.Constanta.TABLE_NAME_ADD} ad" +
                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} pp" +
                    " ON pp.${BaseColumns._ID} = ad.${MyConstanta.Constanta.IDPP}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} prod ON" +
                    " prod.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPRODUCT}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME} proj " +
                    "ON proj.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPROJECT}" +

                    " WHERE proj.${BaseColumns._ID} = ?" +
                    " group by ${MyConstanta.Constanta.TITLEPRODUCT}, ${MyConstanta.Constanta.SUFFIX}",
            arrayOf(propertyId.toString())
        )
    }

    fun seachProduct(productName: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} Where ${MyConstanta.Constanta.TITLEPRODUCT} =?",
            arrayOf(productName)
        )
    }


    fun seachProductAndSuffix(productName: String, suffix: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} Where ${MyConstanta.Constanta.TITLEPRODUCT} =? and ${MyConstanta.Constanta.SUFFIX} =?",
            arrayOf(
                productName, suffix
            )
        )
    }

    fun seachPP(idProject: Int, idProduct: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} Where ${MyConstanta.Constanta.IDPROJECT} =? and ${MyConstanta.Constanta.IDPRODUCT} =?",
            arrayOf(idProject.toString(), idProduct.toString())
        )
    }

    fun seachCategory(idProject: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT ${MyConstanta.Constanta.CATEGORY} FROM ${MyConstanta.Constanta.TABLE_NAME_ADD} ad" +
                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} pp" +
                    " ON pp.${BaseColumns._ID} = ad.${MyConstanta.Constanta.IDPP}" +
                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} prod" +
                    " ON prod.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPRODUCT}" +
                    " JOIN ${MyConstanta.Constanta.TABLE_NAME} proj" +
                    " ON proj.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPROJECT}" +
                    " WHERE proj.${BaseColumns._ID} =? ", arrayOf(idProject.toString())
        )
    }

    fun seachProductToProject(idProject: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT ${MyConstanta.Constanta.TITLEPRODUCT},${MyConstanta.Constanta.CATEGORY}" +
                    " FROM ${MyConstanta.Constanta.TABLE_NAME_ADD} ad" +
                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} pp" +
                    " ON pp.${BaseColumns._ID} = ad.${MyConstanta.Constanta.IDPP}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} prod" +
                    " ON prod.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPRODUCT}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME} proj" +
                    " ON proj.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPROJECT}" +

                    " WHERE proj.${BaseColumns._ID}=?", arrayOf(idProject.toString())
        )
    }


    fun readAddMagazine(idProject: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT ad.${BaseColumns._ID}, ${MyConstanta.Constanta.TITLEPRODUCT}, ${MyConstanta.Constanta.CATEGORY}, ${MyConstanta.Constanta.QUANTITY}, ${MyConstanta.Constanta.PRICE}, ${MyConstanta.Constanta.DATE}, ${MyConstanta.Constanta.SUFFIX}" +
                    " FROM ${MyConstanta.Constanta.TABLE_NAME_ADD} ad" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} pp" +
                    " ON pp. ${BaseColumns._ID} =ad. ${MyConstanta.Constanta.IDPP}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} prod" +
                    " ON prod.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPRODUCT}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME} proj" +
                    " ON proj.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPROJECT}" +
                    " WHERE proj.${BaseColumns._ID} = ?", arrayOf(idProject.toString())
        )
    }

    fun readWriteOffMagazine(idProject: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT ad.${BaseColumns._ID}, ${MyConstanta.Constanta.TITLEPRODUCT}, ${MyConstanta.Constanta.CATEGORY}, ${MyConstanta.Constanta.QUANTITY}, ${MyConstanta.Constanta.PRICE}, ${MyConstanta.Constanta.SUFFIX}" +
                    " FROM ${MyConstanta.Constanta.TABLE_NAME_WRITEOFF} ad" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} pp" +
                    " ON pp. ${BaseColumns._ID} =ad. ${MyConstanta.Constanta.IDPP}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} prod" +
                    " ON prod.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPRODUCT}" +

                    " JOIN ${MyConstanta.Constanta.TABLE_NAME} proj" +
                    " ON proj.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPROJECT}" +
                    " WHERE proj.${BaseColumns._ID} = ?", arrayOf(idProject.toString())
        )
    }

    fun seachAdd(count: Double, category: String, price: Double, date: String, idPP: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM ${MyConstanta.Constanta.TABLE_NAME_ADD} " +
                    "Where ${MyConstanta.Constanta.QUANTITY} =?" +
                    " and ${MyConstanta.Constanta.CATEGORY} =?" +
                    " and ${MyConstanta.Constanta.PRICE} =?" +
                    " and ${MyConstanta.Constanta.DATE} =? " +
                    "and ${MyConstanta.Constanta.IDPP} =?",
            arrayOf(count.toString(), category, price.toString(), date, idPP.toString())
        )
    }

    fun seachWriteOff(count: Double, category: String, date: String, idPP: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM ${MyConstanta.Constanta.TABLE_NAME_ADD} " +
                    "Where ${MyConstanta.Constanta.QUANTITY} =?" +
                    " and ${MyConstanta.Constanta.CATEGORY} =?" +
                    " and ${MyConstanta.Constanta.PRICE} =?" +
                    " and ${MyConstanta.Constanta.DATE} =? " +
                    "and ${MyConstanta.Constanta.IDPP} =?",
            arrayOf(count.toString(), category, date, idPP.toString())
        )
    }


    fun insertToDbProject(title: String, date: String, status: Int) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyConstanta.Constanta.TITLEPRODUCT, title)
        cv.put(MyConstanta.Constanta.DATEBEGINPROJECT, date)
        cv.put(MyConstanta.Constanta.DATEFINALPROJECT, date)
        cv.put(MyConstanta.Constanta.PICTUREROJECT, 0)
        cv.put(MyConstanta.Constanta.STATUSPROJECT, status)
        db.insert(MyConstanta.Constanta.TABLE_NAME_ADD, null, cv)
    }

    fun insertToDbProduct(name: String, suffix: String): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyConstanta.Constanta.TITLEPRODUCT, name)
        cv.put(MyConstanta.Constanta.SUFFIX, suffix)
        return db.insert(MyConstanta.Constanta.TABLE_NAME_PRODUCT, null, cv)
    }

    fun insertToDbProjectProduct(idProject: Int, idProduct: Int): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyConstanta.Constanta.IDPROJECT, idProject)
        cv.put(MyConstanta.Constanta.IDPRODUCT, idProduct)
        return db.insert(MyConstanta.Constanta.TABLE_NAME_PRODUCT, null, cv)
    }

    fun insertToDbProductAdd(
        count: Double,
        category: String,
        price: Double,
        date: String,
        idPP: Int
    ) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyConstanta.Constanta.QUANTITY, count)
        cv.put(MyConstanta.Constanta.CATEGORY, category)
        cv.put(MyConstanta.Constanta.PRICE, price)
        cv.put(MyConstanta.Constanta.DATE, date)
        cv.put(MyConstanta.Constanta.IDPP, idPP)
        db.insert(MyConstanta.Constanta.TABLE_NAME_ADD, null, cv)
    }

    fun insertToDbProductWriteOff(count: Double, category: String?, date: String?, idPP: Int) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyConstanta.Constanta.QUANTITY, count)
        cv.put(MyConstanta.Constanta.CATEGORY, category)
        cv.put(MyConstanta.Constanta.DATE, date)
        cv.put(MyConstanta.Constanta.IDPP, idPP)
        db.insert(MyConstanta.Constanta.TABLE_NAME_WRITEOFF, null, cv)
    }

    fun updateToDbProjectProduct(idPP: Int, idProject: Int, idProduct: Int): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyConstanta.Constanta.IDPROJECT, idProject)
        cv.put(MyConstanta.Constanta.IDPRODUCT, idProduct)
        val id = db.update(
            MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT,
            cv,
            "id=?",
            arrayOf<String>(idPP.toString())
        ).toLong()
//        if (id == -1L) {
//            Toast.makeText(context, "Ошибка!", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(context, "Успешно обновлено!", Toast.LENGTH_SHORT).show()
//        }
        return id
    }

    fun updateToDbAdd(
        count: Double,
        category: String?,
        price: Double,
        date: String?,
        idPP: Int,
        idAdd: Int
    ) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyConstanta.Constanta.QUANTITY, count)
        cv.put(MyConstanta.Constanta.CATEGORY, category)
        cv.put(MyConstanta.Constanta.PRICE, price)
        cv.put(MyConstanta.Constanta.DATE, date)
        cv.put(MyConstanta.Constanta.IDPP, idPP)
        db.update(MyConstanta.Constanta.TABLE_NAME_ADD, cv, "id=?", arrayOf(idAdd.toString()))
    }


    fun updateToDbWriteOff(count: Double, category: String, date: String, idPP: Int, idWO: Int) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyConstanta.Constanta.QUANTITY, count)
        cv.put(MyConstanta.Constanta.CATEGORY, category)
        cv.put(MyConstanta.Constanta.DATE, date)
        cv.put(MyConstanta.Constanta.IDPP, idPP)
        db.update(MyConstanta.Constanta.TABLE_NAME_WRITEOFF, cv, "id=?", arrayOf(idWO.toString()))
    }

    fun updateToDbProduct(
        oldName: String,
        name: String,
        suffix: String
    ): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyConstanta.Constanta.TITLEPRODUCT, name)
        cv.put(MyConstanta.Constanta.SUFFIX, suffix)
        return db.update(
            MyConstanta.Constanta.TABLE_NAME_PRODUCT,
            cv,
            "NameProduct=?",
            arrayOf<String>(oldName)
        ).toLong()
    }

    fun updateToDbProject(id: Int, status: Int, dateEnd: String) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyConstanta.Constanta.DATEFINALPROJECT, dateEnd)
        cv.put(MyConstanta.Constanta.STATUSPROJECT, status)
        db.update(
            MyConstanta.Constanta.TABLE_NAME,
            cv,
            BaseColumns._ID + "= ?",
            arrayOf<String>(id.toString())
        )
    }

    fun deleteOneRowAdd(row_id: Int, nameTable: String) {
        val db = this.writableDatabase
        val result = db.delete(nameTable, "id=?", arrayOf(row_id.toString())).toLong()
//        if (result == -1L) {
//            Toast.makeText(context, "Ошибка.", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(context, "Успешно удаленно.", Toast.LENGTH_SHORT).show()
//        }
    }

}

