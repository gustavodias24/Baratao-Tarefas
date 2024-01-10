package benicio.solucoes.baratotarefas.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Random;

import benicio.solucoes.baratotarefas.R;
import benicio.solucoes.baratotarefas.TarefasActivity;


public class NotificationUtils {
    public static void showNotification(Context context, String title, String body) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "default_channel";

        // Criar um canal de notificação (necessário para Android 8.0 e versões posteriores)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel myNotificationChannel = new NotificationChannel(channelId, "my notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            myNotificationChannel.setDescription("só pra testar");
            myNotificationChannel.enableLights(true);
            myNotificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(myNotificationChannel);

        }
        NotificationCompat.Builder builder=
                new NotificationCompat.Builder(context.getApplicationContext(),
                        channelId);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(body);

        PendingIntent pIntent = PendingIntent.getActivity(
                context.getApplicationContext(),
                0,
                new Intent(
                        context.getApplicationContext(),
                        TarefasActivity.class
                ), PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pIntent);
        Random random = new Random();
        notificationManager.notify(random.nextInt(201), builder.build());
    }
}
