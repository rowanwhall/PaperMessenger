package personal.rowan.paperforspotify.data.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;

import okhttp3.Response;
import personal.rowan.paperforspotify.data.model.Track;
import personal.rowan.paperforspotify.network.NetworkCallback;
import personal.rowan.paperforspotify.network.paper.request.PaperSendSongMessageRequest;
import personal.rowan.paperforspotify.network.paper.request.PaperSendTextOnlyMessageRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperSendSongMessageResponse;
import personal.rowan.paperforspotify.network.paper.response.PaperSendTextOnlyMessageResponse;

public class MessageSenderService
		extends Service {

	private HashMap<String, IMessageSenderService> mCallbackMap = new HashMap<>();
	private int mNumTasks = 0;
	private boolean mStopWhenFinished;

	private MessageSenderServiceBinder mBinder = new MessageSenderServiceBinder();

	public class MessageSenderServiceBinder extends Binder {
		public MessageSenderService getService() {
			return MessageSenderService.this;
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void sendMessage(final String messageId, String chatId, String userId, String chatName, String userName, String userImageUrl, String message) {
		mNumTasks++;
		new PaperSendTextOnlyMessageRequest(messageId, chatId, userId, chatName, userName, userImageUrl, message)
				.execute(new NetworkCallback<PaperSendTextOnlyMessageResponse>() {
			@Override
			public void onResponse(Response response, PaperSendTextOnlyMessageResponse responseObject) {
				mNumTasks--;
				String messageId = responseObject.getMessage().getMessageId();
				IMessageSenderService callback = mCallbackMap.get(messageId);
				if(callback != null) {
					callback.onResponse(responseObject);
					unregisterListener(messageId);
				}
				if(mStopWhenFinished && mNumTasks == 0) {
					stopSelf();
				}
			}

			@Override
			public void onFailure(Exception e) {
				mNumTasks--;
				IMessageSenderService callback = mCallbackMap.get(messageId);
				if(callback != null) {
					callback.onFailure(messageId);
					unregisterListener(messageId);
				}
				if(mStopWhenFinished && mNumTasks == 0) {
					stopSelf();
				}
			}
		});
	}

	public void sendSongMessage(final String messageId, String chatId, String userId, String chatName, String userName, String userImageUrl, String message, Track track) {
		mNumTasks++;
		new PaperSendSongMessageRequest(messageId, chatId, userId, chatName, userName, userImageUrl, message, track.getUri(), track.getUrl(), track.getImageUrlMedium(), track.getName(), track.getArtistName())
				.execute(new NetworkCallback<PaperSendSongMessageResponse>() {
					@Override
					public void onResponse(Response response, PaperSendSongMessageResponse responseObject) {
						mNumTasks--;
						String messageId = responseObject.getMessage().getMessageId();
						IMessageSenderService callback = mCallbackMap.get(messageId);
						if(callback != null) {
							callback.onResponse(responseObject);
							unregisterListener(messageId);
						}
						if(mStopWhenFinished && mNumTasks == 0) {
							stopSelf();
						}
					}

					@Override
					public void onFailure(Exception e) {
						mNumTasks--;
						IMessageSenderService callback = mCallbackMap.get(messageId);
						if(callback != null) {
							callback.onFailure(messageId);
							unregisterListener(messageId);
						}
						if(mStopWhenFinished && mNumTasks == 0) {
							stopSelf();
						}
					}
				});
	}

	public void registerListener(String messageId, IMessageSenderService callback) {
		mCallbackMap.put(messageId, callback);
	}

	public void unregisterListener(String messageId) {
		mCallbackMap.remove(messageId);
	}

	public void unregisterAllListeners() {
		mCallbackMap.clear();
	}

	public void stopSelfWhenFinished() {
		// called when the service should be stopped
		// stop if there are no more messages to be sent
		// otherwise, flip flag that will stop when all messages are sent or failed to send
		if(mNumTasks == 0) {
			stopSelf();
		} else {
			mStopWhenFinished = true;
		}
	}

	public interface IMessageSenderService {
		void onResponse(PaperSendTextOnlyMessageResponse responseObject);

		void onFailure(String messageId);
	}

}
