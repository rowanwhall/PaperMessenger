package personal.rowan.paperforspotify.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.model.Track;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.ui.activity.MainActivity;
import personal.rowan.paperforspotify.ui.activity.MessagingActivity;

public class NotificationUtil {

	public static final int NOTIFICATION_ID_MESSAGE = 0;
	public static final int NOTIFICATION_ID_CHAT = 1;
	public static final int NOTIFICATION_ID_FRIEND_REQUEST = 2;
	public static final int NOTIFICATION_ID_FRIEND_ACCEPT = 3;
	public static final int NOTIFICATION_ID_IMAGE_UPLOAD = 4;

	public static void showMessageNotification(String message, String chatName, String chatId) {
		Context context = PaperApplication.getInstance();
		Intent intent = new Intent(context, MessagingActivity.class);
		intent.putExtra(MessagingActivity.ARGS_CHAT_ID, chatId);
		intent.putExtra(MessagingActivity.ARGS_CHAT_NAME, chatName);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
				PendingIntent.FLAG_ONE_SHOT);

		showDefaultNotification(context, NOTIFICATION_ID_MESSAGE, chatName, message, pendingIntent);
	}

	public static void showChatNotification(String initiatorName, String chatName, String chatId, Track chatTrack) {
		Context context = PaperApplication.getInstance();
		Intent intent = new Intent(context, MessagingActivity.class);
		intent.putExtra(MessagingActivity.ARGS_CHAT_ID, chatId);
		intent.putExtra(MessagingActivity.ARGS_CHAT_NAME, chatName);
		intent.putExtra(MessagingActivity.ARGS_CHAT_SONG, chatTrack);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
				PendingIntent.FLAG_ONE_SHOT);

		showDefaultNotification(context,
				NOTIFICATION_ID_CHAT,
				initiatorName + " added you to " + chatName,
				pendingIntent);
	}

	public static void showFriendNotification(String name, boolean acceptance) {
		Context context = PaperApplication.getInstance();
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
				PendingIntent.FLAG_ONE_SHOT);

		showDefaultNotification(context,
				acceptance ? NOTIFICATION_ID_FRIEND_ACCEPT : NOTIFICATION_ID_FRIEND_REQUEST,
				name + (acceptance ? " accepted your friend request" : " sent you a friend request"),
				pendingIntent);
	}

	public static void showImageUploadNotification() {
		Context context = PaperApplication.getInstance();
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
				.setOngoing(true)
				.setProgress(0, 0, true)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(context.getString(R.string.progress_paper_store_image));

		NotificationManager notificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(NOTIFICATION_ID_IMAGE_UPLOAD, notificationBuilder.build());
	}

	public static void showImageUploadResult(boolean success) {
		Context context = PaperApplication.getInstance();
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(context.getString(success ? R.string.progress_paper_store_image_complete : R.string.error_network_store_image_paper));

		NotificationManager notificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(NOTIFICATION_ID_IMAGE_UPLOAD, notificationBuilder.build());
	}

	private static void showDefaultNotification(Context context, int id, String message, PendingIntent intent) {
		showDefaultNotification(context, id, context.getString(R.string.app_name), message, intent);
	}

	private static void showDefaultNotification(Context context, int id, String title, String message, PendingIntent intent) {
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(title)
				.setContentText(message)
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setContentIntent(intent);

		NotificationManager notificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(id, notificationBuilder.build());
	}

}
