package personal.rowan.paperforspotify.data.loader.observer;

import personal.rowan.paperforspotify.data.gcm.PaperGcmListenerService;
import personal.rowan.paperforspotify.data.loader.ChatLoader;

public class ChatObserver
		extends BaseObserver<ChatLoader> {

	public ChatObserver(ChatLoader loader) {
		super(loader, PaperGcmListenerService.ACTION_UPDATE_CHAT);
	}

	public ChatObserver(ChatLoader loader, String chatId) {
		super(loader, PaperGcmListenerService.ACTION_UPDATE_CHAT_DETAIL + chatId);
	}

}
