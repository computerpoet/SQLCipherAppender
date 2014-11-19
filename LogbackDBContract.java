package com.torointl.wasabi.testproject;

import android.provider.BaseColumns;

/**
 * @author NNelson MELINA <nelsonmelina@6inthemorning.com> Created on 18/11/2014.
 */
public final class LogbackDBContract {

    /**
     * To prevent someone from accidentally instantiating the contract class.
     */
    private LogbackDBContract() { }

    /**
     * Inner class that defines a table's contents.
     */
    public abstract static class LoggingEventEntry implements BaseColumns {

        /**
         * Table's name in the database.
         */
        public static final String TABLE_NAME = "logging_event";

        /**
         * Name of the column for the event's id.
         */
        public static final String COLUMN_NAME_EVENT_ID = "event_id";

        /**
         * Name of the column for the event's timestamp.
         */
        public static final String COLUMN_NAME_TIMESTAMP = "timestmp";

        /**
         * Name of the column for the event's formatted message.
         */
        public static final String COLUMN_NAME_FORMATTED_MESSAGE
                = "formatted_message";

        /**
         * Name of the column for the name of the logger that logged this event.
         */
        public static final String COLUMN_NAME_LOGGER_NAME = "logger_name";

        /**
         * Name of the column for the event's level (in String format).
         */
        public static final String COLUMN_NAME_LEVEL_STRING = "level_string";

        /**
         * Name of the column for the event's thread name.
         */
        public static final String COLUMN_NAME_THREAD_NAME = "thread_name";

        /**
         * Name of the column for the event's reference flag.
         * It indicate whether MDC properties or exception info
         * are available for the event.
         */
        public static final String COLUMN_NAME_REFERENCE_FLAG
                = "reference_flag";

        /**
         * Name of the column for the filename of the caller of the event.
         */
        public static final String COLUMN_NAME_CALLER_FILENAME
        = "caller_filename";

        /**
         * Name of the column for the class of the caller of the event.
         */
        public static final String COLUMN_NAME_CALLER_CLASS = "caller_class";

        /**
         * Name of the column for the method of the caller of the event.
         */
        public static final String COLUMN_NAME_CALLER_METHOD = "caller_method";

        /**
         * Name of the column for the line number of the caller of the event.
         */
        public static final String COLUMN_NAME_CALLER_LINE = "caller_line";
    }

    /**
     * Inner class that defines a table's contents.
     */
    public abstract static class LoggingEventExceptionEntry
            implements BaseColumns {

        /**
         * Table's name in the database.
         */
        public static final String TABLE_NAME = "logging_event_exception";

        /**
         * Name of the column for the event's id.
         */
        public static final String COLUMN_NAME_EVENT_ID = "event_id";

        /**
         * Name of the column for the index of a trace line.
         */
        public static final String COLUMN_NAME_I = "i";

        /**
         * Name of the column for the trace line.
         */
        public static final String COLUMN_NAME_TRACE_LINE = "trace_line";

    }

    /**
     * Inner class that defines a table's contents.
     */
    public abstract static class LoggingEventPropertyEntry
            implements BaseColumns {

        /**
         * Table's name in the database.
         */
        public static final String TABLE_NAME = "logging_event_property";

        /**
         * Name of the column for the event's id.
         */
        public static final String COLUMN_NAME_EVENT_ID = "event_id";

        /**
         * Name of the column for the index of a trace line.
         */
        public static final String COLUMN_NAME_MAPPED_KEY = "mapped_key";

        /**
         * Name of the column for the trace line.
         */
        public static final String COLUMN_NAME_MAPPED_VALUE = "mapped_value";

    }
}
