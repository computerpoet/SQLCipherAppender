package com.torointl.wasabi.testproject;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

/**
 *
 * @author Nelson MELINA <nelsonmelina@6inthemorning.com>
 *         Created on 09/09/2014.
 */
public abstract class SQLCipherAppender
        extends UnsynchronizedAppenderBase<ILoggingEvent> {

    /**
     * SQLCipher database.
     */
    private SQLiteDatabase db;

    /**
     *
     * @see ch.qos.logback.core.UnsynchronizedAppenderBase#start()
     */
    @Override
    public final void start() {
        this.started = false;

        LoggingRecordDBHelper helper =
                new LoggingRecordDBHelper(getApplicationContext(), null);
        this.db = helper.getWritableDatabase(getDbPassword());

        super.start();

        this.started = true;
    }

    /**
     *
     * @see ch.qos.logback.core.UnsynchronizedAppenderBase#stop()
     */
    @Override
    public final void stop() {
        this.db.close();
    }

    /**
     * Retrieve securely the password of the database.
     * @return password for the DB
     */
    abstract String getDbPassword();

    /**
     * Retrieve your application's context.
     * @return Context of the application
     */
    abstract Context getApplicationContext();

    /**
     * @see ch.qos.logback.core.UnsynchronizedAppenderBase#append
     * @param eventObject instance of the event object.
     */
    @Override
    public final void append(final ILoggingEvent eventObject) {
        if (isStarted()) {
            long eventId = subAppend(eventObject);
            if (eventId != -1) {
                secondarySubAppend(eventObject, eventId);
            }
        }
    }

    /**
     * Inserts the main details of a log event into the database.
     *
     * @param eventObject the event to insert
     * @return the row ID of the newly inserted event;
     * -1 if the insertion failed
     */
    private long subAppend(final ILoggingEvent eventObject) {

        ContentValues values = new ContentValues();
        values.put(
                LogbackDBContract.LoggingEventEntry.COLUMN_NAME_TIMESTAMP,
                eventObject.getTimeStamp());
        values.put(
                LogbackDBContract.LoggingEventEntry.
                        COLUMN_NAME_FORMATTED_MESSAGE,
                eventObject.getFormattedMessage());
        values.put(
                LogbackDBContract.LoggingEventEntry.COLUMN_NAME_TIMESTAMP,
                eventObject.getTimeStamp());
        values.put(
                LogbackDBContract.LoggingEventEntry.COLUMN_NAME_LOGGER_NAME,
                eventObject.getLoggerName());
        values.put(
                LogbackDBContract.LoggingEventEntry.COLUMN_NAME_THREAD_NAME,
                eventObject.getThreadName());

        values.put(LogbackDBContract.LoggingEventEntry
                        .COLUMN_NAME_REFERENCE_FLAG,
                computeReferenceMask(eventObject));

        StackTraceElement caller = eventObject.getCallerData()[0];

        values.put(LogbackDBContract.LoggingEventEntry
                        .COLUMN_NAME_CALLER_FILENAME,
                caller.getFileName());
        values.put(LogbackDBContract.LoggingEventEntry
                        .COLUMN_NAME_CALLER_CLASS,
                caller.getClassName());
        values.put(LogbackDBContract.LoggingEventEntry
                        .COLUMN_NAME_CALLER_METHOD,
                caller.getMethodName());
        values.put(LogbackDBContract.LoggingEventEntry
                        .COLUMN_NAME_CALLER_LINE,
                caller.getLineNumber());

        return db.insert(LogbackDBContract.LoggingEventEntry
                .TABLE_NAME, null, values);
    }

    /**
     * Updates an existing row of an event with the secondary
     * details of the event.
     * This includes MDC properties and any exception information.
     *
     * @param event the event containing the details to insert
     * @param eventId the row ID of the event to modify
     */
    private void secondarySubAppend(final ILoggingEvent event,
                                    final long eventId) {
        Map<String, String> mergedMap = mergePropertyMaps(event);
        insertProperties(mergedMap, eventId);

        if (event.getThrowableProxy() != null) {
            insertThrowable(event.getThrowableProxy(), eventId);
        }
    }

    /**
     * reference mask value if properties for this event exist.
     */
    private static final short PROPERTIES_EXIST = 0x01;

    /**
     * reference mask value if an exception for this event exists.
     */
    private static final short EXCEPTION_EXISTS = 0x02;

    /**
     * Computes the reference mask for a logging event, including
     * flags to indicate whether MDC properties or exception info
     * is available for the event.
     *
     * @param event the logging event to evaluate
     * @return the 16-bit reference mask
     */
    private static short computeReferenceMask(final ILoggingEvent event) {
        short mask = 0;

        int mdcPropSize = 0;
        if (event.getMDCPropertyMap() != null) {
            mdcPropSize = event.getMDCPropertyMap().keySet().size();
        }
        int contextPropSize = 0;
        if (event.getLoggerContextVO().getPropertyMap() != null) {
            contextPropSize = event.getLoggerContextVO()
                    .getPropertyMap().size();
        }

        if (mdcPropSize > 0 || contextPropSize > 0) {
            mask = PROPERTIES_EXIST;
        }
        if (event.getThrowableProxy() != null) {
            mask |= EXCEPTION_EXISTS;
        }
        return mask;
    }

    /**
     * Merges a log event's properties with the properties
     * of the logger context.
     * The context properties are first in the map, and then
     * the event's properties are appended.
     *
     * @param event the logging event to evaluate
     * @return the merged properties map
     */
    private Map<String, String> mergePropertyMaps(final ILoggingEvent event) {
        Map<String, String> mergedMap = new HashMap<String, String>();
        // we add the context properties first, then the event properties, since
        // we consider that event-specific properties should have priority over
        // context-wide properties.
        Map<String, String> loggerContextMap = event.getLoggerContextVO()
                .getPropertyMap();
        if (loggerContextMap != null) {
            mergedMap.putAll(loggerContextMap);
        }

        Map<String, String> mdcMap = event.getMDCPropertyMap();
        if (mdcMap != null) {
            mergedMap.putAll(mdcMap);
        }

        return mergedMap;
    }

    /**
     * Updates an existing row with property details
     * (context properties and event's properties).
     *
     * @param mergedMap the properties of the context
     *                  plus the event's properties
     * @param eventId the row ID of the event
     */
    private void insertProperties(final Map<String, String> mergedMap,
                                  final long eventId) {
        if (mergedMap.size() > 0) {
            ContentValues values;
            for (Entry<String, String> entry : mergedMap.entrySet()) {
                values = new ContentValues();
                values.put(LogbackDBContract.LoggingEventPropertyEntry._ID,
                        eventId);
                values.put(LogbackDBContract.LoggingEventPropertyEntry
                                .COLUMN_NAME_MAPPED_KEY,
                        entry.getKey());
                values.put(LogbackDBContract.LoggingEventPropertyEntry
                                .COLUMN_NAME_MAPPED_VALUE,
                        entry.getValue());
                db.insert(LogbackDBContract.LoggingEventPropertyEntry
                        .TABLE_NAME, null, values);
            }
        }
    }

    /**
     * Insert a trace line related to an exception into the DB.
     *
     * @param txt Trace line.
     * @param i index of the trace line
     * @param eventId event's id
     */
    private void insertException(final String txt, final short i,
                                 final long eventId) {

        ContentValues values = new ContentValues();
            values.put(LogbackDBContract.LoggingEventExceptionEntry._ID,
                    eventId);
            values.put(LogbackDBContract.LoggingEventExceptionEntry
                            .COLUMN_NAME_I,
                    i);
            values.put(LogbackDBContract.LoggingEventExceptionEntry
                            .COLUMN_NAME_TRACE_LINE,
                    txt);
            db.insert(LogbackDBContract.LoggingEventExceptionEntry
                    .TABLE_NAME, null, values);

    }

    /**
     *
     * @param tp exception related to the event
     * @param eventId event's id
     */
    private void insertThrowable(IThrowableProxy tp, final long eventId)  {

        short baseIndex = 0;
        for (; tp != null; tp = tp.getCause()) {
            StringBuilder buf = new StringBuilder();
            ThrowableProxyUtil.subjoinFirstLine(buf, tp);

            insertException(buf.toString(), baseIndex++, eventId);

            int commonFrames = tp.getCommonFrames();
            StackTraceElementProxy[] stepArray =
                    tp.getStackTraceElementProxyArray();

            for (int i = 0; i < stepArray.length - commonFrames; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(CoreConstants.TAB);
                ThrowableProxyUtil.subjoinSTEP(sb, stepArray[i]);
                insertException(sb.toString(), baseIndex++, eventId);
            }

            if (commonFrames > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(CoreConstants.TAB)
                        .append("... ")
                        .append(commonFrames)
                        .append(" common frames omitted");

                insertException(sb.toString(), baseIndex++, eventId);
            }
        }
    }
}

