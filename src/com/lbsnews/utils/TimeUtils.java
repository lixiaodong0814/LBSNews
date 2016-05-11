package com.lbsnews.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	private static final String TAG = "*******TimeUtils******";

	private static final long ONE_MINUTE = 60000L;
	private static final long ONE_HOUR = 3600000L;
//	private static final long ONE_DAY = 86400000L;
//	private static final long ONE_WEEK = 604800000L;

	private static final String ONE_JUST_NOW = "�ոշ���";
	private static final String ONE_SECOND_AGO = "��ǰ";
	private static final String ONE_MINUTE_AGO = "����ǰ";
	private static final String ONE_HOUR_AGO = "Сʱǰ";
	private static final String YESTERDAY = "����";
/*	private static final String ONE_DAY_AGO = "��ǰ";
	private static final String ONE_MONTH_AGO = "��ǰ";
	private static final String ONE_YEAR_AGO = "��ǰ";*/

	public static Date getCurrentTime() {
		Date date = new Date();
		return date;
	}

	public static String DateToString(Date date) {
		return format(date);
	}

	private static String format(Date date) {
		long delta = new Date().getTime() - date.getTime();
		if (delta < (1L * ONE_MINUTE / 2)) {
			return ONE_JUST_NOW;
		}
		if (delta < 1L * ONE_MINUTE && delta > (1L * ONE_MINUTE / 2)) {
			long seconds = toSeconds(delta);
			return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
		}
		if (delta < 45L * ONE_MINUTE) {
			long minutes = toMinutes(delta);
			return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
		}
		if (delta < 24L * ONE_HOUR) {
			long hours = toHours(delta);
			return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
		}
		if (delta < 48L * ONE_HOUR) {
			return YESTERDAY;
		} else {
			return getRealTime(date);
		}
	/*	if (delta < 30L * ONE_DAY) {
			long days = toDays(delta);
			return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
		}
		if (delta < 12L * 4L * ONE_WEEK) {
			long months = toMonths(delta);
			return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
		} else {
			long years = toYears(delta);
			return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
		}*/
	}

	private static String getRealTime(Date date) {
		SimpleDateFormat formatRealTime = new SimpleDateFormat("yyyy��MM��dd��");
		return formatRealTime.format(date);
	}

	private static long toSeconds(long date) {
		return date / 1000L;
	}

	private static long toMinutes(long date) {
		return toSeconds(date) / 60L;
	}

	private static long toHours(long date) {
		return toMinutes(date) / 60L;
	}

	/*
	private static long toDays(long date) {
		return toHours(date) / 24L;
	}

	private static long toMonths(long date) {
		return toDays(date) / 30L;
	}

	private static long toYears(long date) {
		return toMonths(date) / 365L;
	}
	*/

}
