package com.example.selfmadekid.services;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.selfmadekid.MainActivity;
import com.example.selfmadekid.R;
import com.example.selfmadekid.background_async_tasks.RemoveGoal;
import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.Goal;
import com.example.selfmadekid.data.OneTimeTaskList;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.threeten.bp.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "FireBaseMsgService";
    //public static boolean
    private Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("onMessageReceived");
        if (remoteMessage.getData() != null) {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            sendNotification(remoteMessage.getData());
        }
    }


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        this.context = this;
        Log.d(TAG, "Refreshed token: " + token);
        System.out.println("token " + token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        if (AppData.getCurrentUserID() != -1){
            new UpdateToken(token).execute();
        }

    }

    private void handleNow(){
        Log.d(TAG, "Short lived task its done");
    }

    private void sendNotification(Map <String, String> data){
        String title = "";
        if (Objects.requireNonNull(data.get("message")).equals("one_time_task_checked")){
            title = onOneTimeTaskConfirmedNotification(data);
        }else if (Objects.requireNonNull(data.get("message")).equals("repetitive_task_checked") ){
            title =  onRepetitiveTaskConfirmedNotification(data);
        }else if (Objects.requireNonNull(data.get("message")).equals("goal_add") ){
            title = onAddGoalNotification(data);
        }else if (Objects.requireNonNull(data.get("message")).equals("goal_update") ){
            title = onUpdateGoalNotification(data);
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(this, "chanelID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager  = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("chanelID", "firebase", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    private class UpdateToken extends AsyncTask<Void, Void, Boolean> {
        private int id;
        private String token;
        private int state;

        public UpdateToken(String token) {
            this.id = AppData.getCurrentUserID();
            this.token = token;
            this.state = AppData.getCurrentState();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest;
                stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.update_token),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    System.out.println("UpdateToken server" + response);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", Integer.valueOf(id).toString() );
                        params.put("token", token);
                        params.put("state", Integer.valueOf(state).toString() );
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }catch (Exception e) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        }

        @Override
        protected void onCancelled() {
        }
    }

    private String onOneTimeTaskConfirmedNotification(Map <String, String> data){
        String title = "";
        if (Integer.valueOf(Objects.requireNonNull(data.get("state"))) == 2){
            title = getString(R.string.task_confirmed_by_parent);
            if (AppData.getChildren().get(AppData.getCurrentUserID()) != null){
                Goal goal = AppData.getChildren().get(AppData.getCurrentUserID()).getCurrentGoal();
                if (goal != null){
                    OneTimeTaskList oneTimeTasks = goal.getOneTimeTaskContainer();
                    if (oneTimeTasks!=null){
                        int task_id =  Integer.valueOf(Objects.requireNonNull(data.get("task_id")));
                        for (int i = 0; i<oneTimeTasks.size(); i++ ){
                            if (oneTimeTasks.get(i).getTask_id() == task_id){
                                oneTimeTasks.get(i).setConfirmed(2);
                                if (goal.addPoints(oneTimeTasks.get(i).getFinishReward())  ){
                                    new RemoveGoal(AppData.getCurrentUserID(), goal.getGoal_id(), context).execute();
                                    AppData.getChildren().get(AppData.getCurrentUserID()).setCurrentGoal(null);

                                }
                                break;
                            }
                        }
                    }
                }
            }

        }else if (Integer.valueOf(Objects.requireNonNull(data.get("state"))) == 1){
            title = getString(R.string.child_finished_task);
            if (AppData.getChildren().get(Integer.valueOf(Objects.requireNonNull(data.get("user_id")))) != null){
                Goal goal = AppData.getChildren().get(Integer.valueOf(Objects.requireNonNull(data.get("user_id")))).getCurrentGoal();
                if (goal != null){
                    OneTimeTaskList oneTimeTasks = goal.getOneTimeTaskContainer();
                    if (oneTimeTasks!=null){
                        int task_id =  Integer.valueOf(Objects.requireNonNull(data.get("task_id")));
                        for (int i = 0; i<oneTimeTasks.size(); i++ ){
                            if (oneTimeTasks.get(i).getTask_id() == task_id){
                                oneTimeTasks.get(i).setConfirmed(1);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return title;
    }

    private String onRepetitiveTaskConfirmedNotification(Map <String, String> data){
        String title = "";
        if (Integer.valueOf(Objects.requireNonNull(data.get("state"))) == 2){
            title = getString(R.string.task_confirmed_by_parent);
            if (AppData.getChildren().get(AppData.getCurrentUserID()) != null) {
                Goal goal = AppData.getChildren().get(AppData.getCurrentUserID()).getCurrentGoal();
                if (goal!= null){
                    int task_id = Integer.valueOf(data.get("task_id"));
                    LocalDate localDate = LocalDate.of(
                            Integer.valueOf(data.get("year")),
                            Integer.valueOf(data.get("month")),
                            Integer.valueOf(data.get("day")) );
                    goal.getCheckedDates().get(task_id).put(localDate, Integer.valueOf(data.get("state")) );
                    for (int i=0; i<goal.getDayOfTheWeekContainer(localDate.getDayOfWeek()).size(); i++){
                        if ( goal.getDayOfTheWeekContainer(localDate.getDayOfWeek() ).get(i).getTask_id() == task_id){
                            if ( goal.addPoints( goal.getDayOfTheWeekContainer(localDate.getDayOfWeek() ).get(i).getFinishReward()) ){
                                new RemoveGoal(AppData.getCurrentUserID(), goal.getGoal_id(), context).execute();
                                AppData.getChildren().get(AppData.getCurrentUserID()).setCurrentGoal(null);
                            }
                            break;
                        }
                    }

                }
            }
        }else if (Integer.valueOf(Objects.requireNonNull(data.get("state"))) == 1){
            title = getString(R.string.child_finished_task);
            Goal goal = AppData.getChildren().get(  Integer.valueOf(data.get("user_id")) ).getCurrentGoal();
            if (goal!= null){
                goal.getCheckedDates().get(Integer.valueOf(data.get("task_id"))).put(LocalDate.of(
                        Integer.valueOf(data.get("year")),
                        Integer.valueOf(data.get("month")),
                        Integer.valueOf(data.get("day")) ),
                        Integer.valueOf(data.get("state")) );
            }

        }
        return title;
    }

    private String onAddGoalNotification(Map <String, String> data){
        String title = getString(R.string.child_have_been_adde_goal);
        Goal goal = new Goal(
                data.get("goal_name"),
                0,
                Integer.valueOf(data.get("goal_id")));
        AppData.getChildren().get( Integer.valueOf(data.get("user_id"))).setCurrentGoal(goal);
        return title;
    }

    private String onUpdateGoalNotification(Map <String, String> data){
        String title = getString(R.string.parent_confirmed_goal);
        Goal goal = AppData.getChildren().get( AppData.getCurrentUserID()).getCurrentGoal();
        if (goal != null){
            if (goal.getGoal_id() == Integer.valueOf(data.get("goal_id"))){
                goal.setFinishPoints( Integer.valueOf(data.get("points")));
            }
        }
        return title;
    }
}
