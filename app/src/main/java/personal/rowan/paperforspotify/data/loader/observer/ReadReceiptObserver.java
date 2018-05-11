package personal.rowan.paperforspotify.data.loader.observer;

import personal.rowan.paperforspotify.data.gcm.PaperGcmListenerService;
import personal.rowan.paperforspotify.data.loader.ReadReceiptLoader;

public class ReadReceiptObserver
		extends BaseObserver<ReadReceiptLoader> {

	public ReadReceiptObserver(ReadReceiptLoader loader) {
		super(loader, PaperGcmListenerService.ACTION_UPDATE_READ_RECEIPT);
	}

}
