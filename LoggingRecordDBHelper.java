package com.torointl.wasabi.testproject;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * @author Nelson MELINA <nelsonmelina@6inthemorning.com> Created on 17/11/2014.
 */
public final class LoggingRecordDBHelper extends SQLiteOpenHelper {

    /**
     * If you change the database schema, you must increment the database version.
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * name of the database
     */
    public static final String DATABASE_NAME = "logback.db";

    /**
     * SQL string to create the table.
     */
    private static final String SQL_CREATE_ENTRIES_LOGGING_EVENT = "CREATE TABLE "+
            LogbackDBContract.LoggingEventEntry.TABLE_NAME + "( "
            + LogbackDBContract.LoggingEventEntry.COLUMN_NAME_EVENT_ID
            + "  INTEGER PRIMARY KEY, "
            + LogbackDBContract.LoggingEventEntry.COLUMN_NAME_TIMESTAMP
            + "  INTEGER, "
            + LogbackDBContract.LoggingEventEntry.COLUMN_NAME_FORMATTED_MESSAGE
            + "  TEXT, "
            + LogbackDBContract.LoggingEventEntry.COLUMN_NAME_LOGGER_NAME
            + "  TEXT, "
            + LogbackDBContract.LoggingEventEntry.COLUMN_NAME_LEVEL_STRING
            + "  TEXT, "
            + LogbackDBContract.LoggingEventEntry.COLUMN_NAME_THREAD_NAME
            + "  TEXT, "
            + LogbackDBContract.LoggingEventEntry.COLUMN_NAME_REFERENCE_FLAG
            + "  INTEGER, "
            + LogbackDBContract.LoggingEventEntry.COLUMN_NAME_CALLER_FILENAME
            + "  TEXT, "
            + LogbackDBContract.LoggingEventEntry.COLUMN_NAME_CALLER_CLASS
            + "  TEXT, "
            + LogbackDBContract.LoggingEventEntry.COLUMN_NAME_CALLER_METHOD
            + "  TEXT, "
            + LogbackDBContract.LoggingEventEntry.COLUMN_NAME_CALLER_LINE
            + "  TEXT"
            + ")";

    /**
     * SQL string to create the table.
     */
    private static final String SQL_CREATE_ENTRIES_LOGGING_EVENT_EXCEPTION = "CREATE TABLE "+
            LogbackDBContract.LoggingEventExceptionEntry.TABLE_NAME + "( "
            + LogbackDBContract.LoggingEventExceptionEntry.COLUMN_NAME_EVENT_ID
            + "  INTEGER PRIMARY KEY, "
            + LogbackDBContract.LoggingEventExceptionEntry.COLUMN_NAME_I
            + "  INTEGER, "
            + LogbackDBContract.LoggingEventExceptionEntry.COLUMN_NAME_TRACE_LINE
            + "  TEXT"
            + ")";

    /**
     * SQL string to create the table.
     */
    private static final String SQL_CREATE_ENTRIES_LOGGING_EVENT_PROPERTY = "CREATE TABLE "+
            LogbackDBContract.LoggingEventPropertyEntry.TABLE_NAME + "( "
            + LogbackDBContract.LoggingEventPropertyEntry.COLUMN_NAME_EVENT_ID
            + "  INTEGER PRIMARY KEY, "
            + LogbackDBContract.LoggingEventPropertyEntry.COLUMN_NAME_MAPPED_KEY
            + "  TEXT, "
            + LogbackDBContract.LoggingEventPropertyEntry.COLUMN_NAME_MAPPED_VALUE
            + "  TEXT"
            + ")";

    /**
     *
     * @param context to use to open or create the database
     * @param cursorFactory to use for creating cursor objects, or null for the default
     */
    public LoggingRecordDBHelper(final Context context, final SQLiteDatabase.CursorFactory
            cursorFactory) {
        super(context, DATABASE_NAME, cursorFactory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_LOGGING_EVENT);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_LOGGING_EVENT_EXCEPTION);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_LOGGING_EVENT_PROPERTY);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int i, final int i2) {
        // TODO implement process to upgrade the database to a new schema when it's modified;
    }

}
