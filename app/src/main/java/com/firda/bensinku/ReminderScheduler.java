package com.firda.bensinku;

import android.app.*;
import android.content.*;
import java.util.Calendar;

public class ReminderScheduler {
    private static PendingIntent pending(Context c, String type, int day, int hour, int minute) {
        Intent i = new Intent(c, ReminderReceiver.class);
        i.putExtra("type", type);
        i.putExtra("day", day);
        i.putExtra("hour", hour);
        i.putExtra("minute", minute);
        return PendingIntent.getBroadcast(c, Math.abs(type.hashCode()), i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public static void cancel(Context c, String type) {
        AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pending(c, type, 1, 0, 0));
    }

    public static void scheduleDaily(Context c, String type, boolean enabled, int hour, int minute) {
        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi=pending(c,type,1,hour,minute);
        am.cancel(pi);
        if(!enabled) return;
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,hour); cal.set(Calendar.MINUTE,minute); cal.set(Calendar.SECOND,0); cal.set(Calendar.MILLISECOND,0);
        if(cal.getTimeInMillis()<=System.currentTimeMillis()) cal.add(Calendar.DAY_OF_YEAR,1);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pi);
    }

    public static void scheduleWeekly(Context c, String type, boolean enabled, int hour, int minute) {
        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi=pending(c,type,1,hour,minute);
        am.cancel(pi);
        if(!enabled) return;
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,hour); cal.set(Calendar.MINUTE,minute); cal.set(Calendar.SECOND,0); cal.set(Calendar.MILLISECOND,0);
        if(cal.getTimeInMillis()<=System.currentTimeMillis()) cal.add(Calendar.DAY_OF_YEAR,1);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),7L*AlarmManager.INTERVAL_DAY,pi);
    }

    public static void scheduleMonthly(Context c, boolean enabled, int day, int hour, int minute) {
        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi=pending(c,"monthly",day,hour,minute);
        am.cancel(pi);
        if(!enabled) return;
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Math.min(day, cal.getActualMaximum(Calendar.DAY_OF_MONTH)));
        cal.set(Calendar.HOUR_OF_DAY,hour); cal.set(Calendar.MINUTE,minute); cal.set(Calendar.SECOND,0); cal.set(Calendar.MILLISECOND,0);
        if(cal.getTimeInMillis()<=System.currentTimeMillis()) {
            cal.add(Calendar.MONTH,1);
            cal.set(Calendar.DAY_OF_MONTH, Math.min(day, cal.getActualMaximum(Calendar.DAY_OF_MONTH)));
        }
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pi);
    }
}
