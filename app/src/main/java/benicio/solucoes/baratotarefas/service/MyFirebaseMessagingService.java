package benicio.solucoes.baratotarefas.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Lidar com a mensagem recebida aqui
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Log.d("mayara", "onMessageReceived: " + title + " body " + body);

        // Exibir a notificação no sistema
        NotificationUtils.showNotification(getApplicationContext(), title, body);
    }
}