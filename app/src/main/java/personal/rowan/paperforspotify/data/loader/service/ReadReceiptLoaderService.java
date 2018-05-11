package personal.rowan.paperforspotify.data.loader.service;

import android.content.Intent;

import personal.rowan.paperforspotify.data.loader.BaseLoader;
import personal.rowan.paperforspotify.data.loader.LoaderId;
import personal.rowan.paperforspotify.data.loader.ReadReceiptLoader;
import personal.rowan.paperforspotify.data.model.ReadReceipt;

public class ReadReceiptLoaderService
		extends BaseLoaderService<ReadReceipt> {

	public static final String ARGS_CHAT_ID = "ARGS_CHAT_ID";
	public static final String ARGS_USER_ID = "ARGS_USER_ID";

	@Override
	protected BaseLoader<ReadReceipt> buildLoaderFromIntent(Intent intent) {
		String chatId = intent.getStringExtra(ARGS_CHAT_ID);
		String userId = intent.getStringExtra(ARGS_USER_ID);
		return new ReadReceiptLoader(getApplicationContext(), chatId, userId);
	}

	@Override
	protected int loaderId() {
		return LoaderId.READ_RECEIPT;
	}

}
