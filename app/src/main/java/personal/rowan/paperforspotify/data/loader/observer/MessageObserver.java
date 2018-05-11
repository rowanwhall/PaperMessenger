package personal.rowan.paperforspotify.data.loader.observer;

import personal.rowan.paperforspotify.data.gcm.PaperGcmListenerService;
import personal.rowan.paperforspotify.data.loader.MessageLoader;

public class MessageObserver
		extends BaseObserver<MessageLoader> {

	public MessageObserver(MessageLoader loader, String chatId) {
		super(loader, PaperGcmListenerService.ACTION_UPDATE_MESSAGE + chatId);
	}

}
