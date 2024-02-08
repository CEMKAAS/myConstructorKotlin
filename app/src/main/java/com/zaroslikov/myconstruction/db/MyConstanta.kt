package com.zaroslikov.myconstruction.db

import android.provider.BaseColumns

object MyConstanta {

    object Constanta : BaseColumns {
        const val DB_NAME = "my_dbConst.db" //База данных

        const val DB_VERSION = 1 //Версия базы данных

        const val TABLE_NAME = "Project" // Название таблицы

        const val _ID = "id" // Индефикатор НУМЕРАЦИЯ СТРОК

        const val TITLEPROJECT = "NameProject" // Название описание (название Проекта)

        const val DATEBEGINPROJECT = "DateBegin" // Дата создания проекта

        const val DATEFINALPROJECT = "DateFinal" // Дата создания проекта

        const val PICTUREROJECT = "Picture" // Картинка проекта

        const val STATUSPROJECT = "Status" // Дата создания проекта

        const val TABLE_NAME_PRODUCT = "Product" // Название таблицы

        const val TITLEPRODUCT =
            "NameProduct" // Название описание (название продукта) название проданного товара

        const val SUFFIX =
            "Suffix" // Название описание (название продукта) название проданного товара

        const val TABLE_NAME_PROJECT_PRODUCT = "ProjectProduct" // Название таблицы

        const val IDPRODUCT = "idProduct" // Айди продукта

        const val IDPROJECT = "idProject" // Айди продукта

        const val TABLE_NAME_ADD = "AddProd" // Название таблицы

        const val TABLE_NAME_WRITEOFF = "WriteOffProd" // Название таблицы

        const val QUANTITY = "Quantity" //Заголовок (кол-во)

        const val CATEGORY = "Category" //Заголовок (Категория)

        const val PRICE = "Price" //Заголовок (Цена)

        const val DATE = "Date" //Заголовок (Дата)

        const val IDPP = "idPP" //Заголовок (Ключ)
    }

    const val TABLE_STRUCTURE_PROJECT =
        "CREATE TABLE IF NOT EXISTS ${Constanta.TABLE_NAME} (${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${Constanta.TITLEPRODUCT} TEXT, " +
                "${Constanta.DATEBEGINPROJECT} TEXT, " +
                "${Constanta.DATEFINALPROJECT} TEXT, " +
                "${Constanta.PICTUREROJECT} INTEGER, " +
                "${Constanta.STATUSPROJECT} INTEGER)"

    const val TABLE_STRUCTURE_PRODUCT =
        "CREATE TABLE IF NOT EXISTS  ${Constanta.TABLE_NAME_PRODUCT}" +
                " (${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${Constanta.TITLEPRODUCT} TEXT, " +
                "${Constanta.SUFFIX} TEXT)"

    const val TABLE_STRUCTURE_PRODUCTPROJECT =
        "CREATE TABLE IF NOT EXISTS ${Constanta.TABLE_NAME_PROJECT_PRODUCT}" +
                " (${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${Constanta.IDPROJECT} INTEGER, " +
                "${Constanta.IDPRODUCT} INTEGER, " +
                "FOREIGN KEY (${Constanta.IDPROJECT}) REFERENCES ${Constanta.TABLE_NAME} (${BaseColumns._ID}), FOREIGN KEY (${Constanta.IDPRODUCT}) REFERENCES ${Constanta.TABLE_NAME_PRODUCT} (${BaseColumns._ID}))"


    const val TABLE_STRUCTURE_ADD =
        "CREATE TABLE IF NOT EXISTS  ${Constanta.TABLE_NAME_ADD}" +
                " (${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${Constanta.QUANTITY} REAL, " +
                "${Constanta.CATEGORY} TEXT, " +
                "${Constanta.PRICE} REAL, " +
                "${Constanta.DATE} TEXT, " +
                "${Constanta.IDPP} INTEGER, FOREIGN KEY (${Constanta.IDPP}) REFERENCES ${Constanta.TABLE_NAME_PROJECT_PRODUCT} (${BaseColumns._ID}))"

    const val TABLE_STRUCTURE_WRITEOFF =
        "CREATE TABLE IF NOT EXISTS  ${Constanta.TABLE_NAME_WRITEOFF}" +
                " (${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${Constanta.QUANTITY} REAL, " +
                "${Constanta.CATEGORY} TEXT, " +
                "${Constanta.DATE} TEXT, " +
                "${Constanta.IDPP} INTEGER, FOREIGN KEY (${Constanta.IDPP}) REFERENCES ${Constanta.TABLE_NAME_PROJECT_PRODUCT} (${BaseColumns._ID}))"

    const val DROP_TABLE_PROJECT = "DROP TABLE IF EXISTS  ${Constanta.TABLE_NAME}" // сброс продаж
    const val DROP_TABLE_PRODUCT = "DROP TABLE IF EXISTS ${Constanta.TABLE_NAME_PRODUCT}" // сброс покупок
    const val DROP_TABLE_PRODUCTPROJECT = "DROP TABLE IF EXISTS ${Constanta.TABLE_NAME_PROJECT_PRODUCT}" // сброс обычной
    const val DROP_TABLE_ADD = "DROP TABLE IF EXISTS ${Constanta.TABLE_NAME_ADD}" // сброс обычной
    const val DROP_TABLE_WRITEOFF = "DROP TABLE IF EXISTS ${Constanta.TABLE_NAME_WRITEOFF}" // сброс цен


}