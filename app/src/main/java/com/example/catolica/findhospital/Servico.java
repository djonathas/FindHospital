package com.example.catolica.findhospital;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by catolica on 08/10/16.
 */
public class Servico extends Service {
    private static String TAG = "servico";
    public List<Trabalhador> trabalhadores = new ArrayList<>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate:");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Trabalhador trabalhador = new Trabalhador(startId);
        trabalhador.start();
        trabalhadores.add(trabalhador);
        Log.i(TAG, "onStartCommand:");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy:");
        for(Trabalhador trabalhador : trabalhadores) {
            trabalhador.ativo = false;
        }
    }

    class Trabalhador extends Thread {
        public int quantidadeExecucao = 0;
        public int startID;
        public boolean ativo = true;

        public Trabalhador(int startID) {
            this.startID = startID;
        }

        //envia notificação ao usuário
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        private void sendNotification(String title, String message) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(message);
            mBuilder.setVibrate(new long[] { 500, 500 });
            mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

            Intent resultIntent = new Intent(Servico.this, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(10, mBuilder.build());
        }

        public void run() {
            while (ativo) {
                try {
                    //envia a notificação a cada 30 dias
                    Thread.sleep(AlarmManager.INTERVAL_DAY * 30);
                    sendNotification("FindHospital", "Está na hora de você visitar seu médico!");
                    Log.i(TAG, "Chamando a thread");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopSelf(startID);
        }
    }
}
