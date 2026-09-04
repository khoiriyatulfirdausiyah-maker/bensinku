package com.firda.bensinku;

import android.app.*;
import android.content.*;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "bensinku_reminders";

    @Override public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("type");
        if (type == null) type = "fuel";
        String title;
        String body;
        if ("price".equals(type)) {
            title = "Cek harga BBM terbaru";
            body = "Yuk cek apakah harga BBM di wilayahmu berubah.";
        } else if ("monthly".equals(type)) {
            title = "Ringkasan BensinKu bulan ini";
            body = "Lihat total pengeluaran, liter, dan efisiensi kendaraanmu.";
        } else {
            title = "Cek estimasi BBM kendaraanmu";
            body = "Buka BensinKu untuk melihat apakah BBM sudah mendekati batas pengingatmu.";
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "Pengingat BensinKu", NotificationManager.IMPORTANCE_DEFAULT);
            ch.setDescription("Pengingat isi BBM, harga BBM, dan ringkasan bulanan");
            nm.createNotificationChannel(ch);
        }

        Intent open = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, type.hashCode(), open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= 26) b = new Notification.Builder(context, CHANNEL_ID);
        else b = new Notification.Builder(context);
        b.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pi);
        nm.notify(1000 + Math.abs(type.hashCode()%500), b.build());

        if ("monthly".equals(type)) {
            int day = intent.getIntExtra("day", 1);
            int hour = intent.getIntExtra("hour", 19);
            int minute = intent.getIntExtra("minute", 0);
            ReminderScheduler.scheduleMonthly(context, true, day, hour, minute);
        }
    }
}
