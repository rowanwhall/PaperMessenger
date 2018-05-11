package personal.rowan.paperforspotify.network.paper.request;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import personal.rowan.paperforspotify.data.model.Friend;
import personal.rowan.paperforspotify.network.paper.response.PaperCreateChatResponse;

public class PaperCreateChatRequest
		extends BasePaperRequest<PaperCreateChatResponse> {

	private static final String PATH = "storeChat";

	private String mChatName;
	private String mCreatorId;
	private String mCreatorName;
	private List<Friend> mFriends;

	public PaperCreateChatRequest(String chatName, String creatorId, String creatorName, List<Friend> friends) {
		mChatName = chatName;
		mCreatorId = creatorId;
		mCreatorName = creatorName;
		mFriends = friends;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mChatName, mCreatorId, mCreatorName, createFriendsCsv());
	}

	@Override
	protected PaperCreateChatResponse buildResponse(String responseString, Exception e) {
		return new PaperCreateChatResponse(responseString, e);
	}

	private String createFriendsCsv() {
		StringBuilder b = new StringBuilder();

		for(Friend friend : mFriends) {
			try {
				b.append(URLEncoder.encode(friend.getUserId(), "UTF-8"));
				b.append(",");
			} catch(UnsupportedEncodingException e) {
				Log.e(PaperCreateChatRequest.this.getClass().getName(), "URL encoding error");
			}
		}

		String extraCommaString = b.toString();
		return extraCommaString.substring(0, extraCommaString.length() - 1);
	}
}
