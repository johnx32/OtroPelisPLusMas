package org.kaizoku.otropelisplusmas.database.typeconverters;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateLongConverter {
    @TypeConverter
    public static Date fromTimestamp(Long fecha) {
        return fecha == null ? null : new Date(fecha);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
