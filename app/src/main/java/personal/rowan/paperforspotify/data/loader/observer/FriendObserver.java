package personal.rowan.paperforspotify.data.loader.observer;

import personal.rowan.paperforspotify.data.loader.FriendLoader;
import personal.rowan.paperforspotify.data.gcm.PaperGcmListenerService;

public class FriendObserver
		extends BaseObserver<FriendLoader> {

	public FriendObserver(FriendLoader loader) {
		super(loader, PaperGcmListenerService.ACTION_UPDATE_FRIEND);
	}

}
