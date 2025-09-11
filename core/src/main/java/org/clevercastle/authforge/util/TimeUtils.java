package org.clevercastle.authforge.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    private static final Logger log = LoggerFactory.getLogger(TimeUtils.class);
    private static final char[] INVALID_TIME_CHARS = {'{', '}'};
    public static final DateTimeFormatter zuluFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX");
    public static final DateTimeFormatter zuluMicroFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXXXX");
    public static final DateTimeFormatter zuluSecondFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXXXX");
    public static final DateTimeFormatter defaultDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final List<DateTimeFormatter> datetimeFormatters = List.of(DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            zuluFormatter, zuluMicroFormatter, zuluSecondFormatter);
    public static final OffsetDateTime MIN_OFFSET_DATETIME = OffsetDateTime.parse("1900-01-01T00:00:00.000Z");
    public static final OffsetDateTime MAX_OFFSET_DATETIME = OffsetDateTime.parse("3000-01-01T00:00:00.000Z");
    public static final int SECONDS_PER_DAY = 3600 * 24;

    @Nonnull
    public static OffsetDateTime now() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    public static OffsetDateTime yesterday() {
        return OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
    }

    @Nonnull
    public static String nowAsString() {
        return format(now());
    }

    public static long currentNano() {
        OffsetDateTime now = now();
        long epochNanos = TimeUnit.NANOSECONDS.convert(now.toEpochSecond(), TimeUnit.SECONDS);
        epochNanos += now.getNano();
        return epochNanos;
    }

    public static LocalDate today() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    @Nullable
    public static String format(@Nullable OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return zuluFormatter.format(offsetDateTime);
    }

    @Nullable
    public static String format(@Nullable OffsetDateTime offsetDateTime, DateTimeFormatter formatter) {
        if (offsetDateTime == null) {
            return null;
        }
        OffsetDateTime utcTime = offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC);
        return formatter.format(utcTime);
    }


    @Nullable
    public static OffsetDateTime parse(String timeStr, DateTimeFormatter... formatters) {
        if (StringUtils.isBlank(timeStr) || StringUtils.containsAny(timeStr, INVALID_TIME_CHARS)) {
            return null;
        }
        DateTimeFormatter[] datetimeFormattersForParses = formatters;
        OffsetDateTime result = null;
        if (datetimeFormattersForParses == null || datetimeFormattersForParses.length == 0) {
            datetimeFormattersForParses = datetimeFormatters.toArray(new DateTimeFormatter[0]);
        }
        for (DateTimeFormatter fmt : datetimeFormattersForParses) {
            try {
                result = OffsetDateTime.parse(timeStr, fmt);
                break;
            } catch (Exception e) {
                log.debug("failed to parse {} with format {}.", timeStr, fmt);
            }
        }
        if (result == null) {
            log.error("Fail to parse string {} to offset datetime", timeStr);
            return null;
        }
        return result.withOffsetSameInstant(ZoneOffset.UTC);
    }

    @Nonnull
    public static OffsetDateTime dayStart(@Nonnull OffsetDateTime time) {
        return time.truncatedTo(ChronoUnit.DAYS);
    }

    @Nonnull
    public static OffsetDateTime dayEnd(@Nonnull OffsetDateTime time) {
        return OffsetDateTime.of(time.toLocalDate(), LocalTime.MAX, ZoneOffset.UTC);
    }

    public static OffsetDateTime convertToOffsetDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atOffset(ZoneOffset.UTC);
    }

    public static LocalDate convertToLocalDate(String date) {
        LocalDate localDate = null;
        try {
            localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            log.error("parse date error", e);
        }
        return localDate;
    }

    public static OffsetDateTime getFirstDayOfMonth() {
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
        return OffsetDateTime.of(firstDay.atStartOfDay(), ZoneOffset.UTC);
    }

    public static long between(OffsetDateTime t1, OffsetDateTime t2) {
        if (t1 == null || t2 == null) {
            return Long.MAX_VALUE;
        }
        return Math.abs(Duration.between(t1, t2).toSeconds());
    }

    public static int dayBetween(OffsetDateTime t1, OffsetDateTime t2) {
        if (t1 == null || t2 == null) {
            return Integer.MAX_VALUE;
        }
        return (int) Math.ceil((double) (Duration.between(t1, t2).toSeconds()) / (double) (SECONDS_PER_DAY));
    }

    public static boolean isBefore(OffsetDateTime t1, OffsetDateTime t2, boolean nullAsEarliest) {
        if (t1 == t2) {
            return false;
        }
        if (t1 == null) {
            return nullAsEarliest;
        }
        if (t2 == null) {
            return !nullAsEarliest;
        }
        return t1.isBefore(t2);
    }

    public static boolean isBefore(OffsetDateTime t1, OffsetDateTime t2) {
        return isBefore(t1, t2, true);
    }


}
