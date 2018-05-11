/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.personal.rowan.paperforspotify.backend;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.labs.repackaged.com.google.common.base.Pair;
import com.personal.rowan.paperforspotify.backend.model.Chat;
import com.personal.rowan.paperforspotify.backend.model.ChatMembership;
import com.personal.rowan.paperforspotify.backend.model.ChatMessage;
import com.personal.rowan.paperforspotify.backend.model.Friend;
import com.personal.rowan.paperforspotify.backend.model.Friendship;
import com.personal.rowan.paperforspotify.backend.model.ReadReceipt;
import com.personal.rowan.paperforspotify.backend.model.User;
import com.personal.rowan.paperforspotify.backend.model.UserSearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint to send messages to devices registered with the backend
 *
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 *
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */

/*

./gradlew backend:appengineUpdate

*/

@Api(
  name = "messaging",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.paperforspotify.rowan.personal.com",
    ownerName = "backend.paperforspotify.rowan.personal.com",
    packagePath=""
  )
)
public class MessagingEndpoint {

	private static final String PUSH_KEY_MESSAGE = "PUSH_KEY_MESSAGE";
	private static final String PUSH_KEY_CHAT = "PUSH_KEY_CHAT";
	private static final String PUSH_KEY_CHAT_DETAIL = "PUSH_KEY_CHAT_DETAIL";
	private static final String PUSH_KEY_FRIEND_REQUEST = "PUSH_KEY_FRIEND_REQUEST";
	private static final String PUSH_KEY_FRIEND_ACCEPT = "PUSH_KEY_FRIEND_ACCEPT";
	private static final String PUSH_KEY_READ_RECEIPT = "PUSH_KEY_READ_RECEIPT";

	private static final String ADMINISTRATOR_ID = "ADMINISTRATOR_ID";
	private static final String ADMINISTRATOR_NAME = "Paper Admin";
	private static final String ADMINISTRATOR_IMAGE_URL = "null";

	private static final String DATASTORE_KEY_NAME = "todo.txt";

    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());

	/**
	 *
	 * Cloud Messaging
	 *
	 */

    private static final String API_KEY = System.getProperty("gcm.api.key");

	@ApiMethod(name = "sendMessageToUser")
	public dummyObject sendMessageToUser(@Named("pushkey") String pushkey,
	                                     @Named("regId") String regId,
	                                     @Named("message") String message,
	                                     @Named("title") String title,
	                                     @Named("senderId") String senderId) {
		if (message == null || message.trim().length() == 0 || regId == null || regId.trim().length() == 0) {
			log.warning("Not sending message because it is empty");
			return new dummyObject(0);
		}
		// crop longer messages
		if (message.length() > 1000) {
			message = message.substring(0, 1000) + "[...]";
		}
		Sender sender = new Sender(API_KEY);
		Message msg = new Message.Builder()
				.addData("key", pushkey)
				.addData("message", message)
				.addData("title", title)
				.addData("senderId", senderId)
				.build();
		try {
			sender.send(msg, regId, 5);
			return new dummyObject(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new dummyObject(0);
	}

	private class dummyObject {
		public int dummyParameter;
		public dummyObject(int dummyParameter) {
			this.dummyParameter = dummyParameter;
		}
	}

	/**
	 *
	 * API methods
	 *
	 */

	/**
	 * User methods - Public facing
	 */

	@ApiMethod(name = "login")
	public User login(@Named(User.KEY_ID) String userId,
	                  @Named(User.KEY_NAME) String name,
	                  @Named(User.KEY_IMAGE_URL) String imageUrl) {

		//We use csvs for other purposes and therefore can't allow them within ids
		userId = userId.replace(",", "");

		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query.Filter filter = new Query.FilterPredicate(User.KEY_ID, Query.FilterOperator.EQUAL, userId);
		Query query = new Query(User.KEY_ENTITY).setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		ArrayList<User> users = new ArrayList<>();
		for (Entity result : results) {
			User user = new User();
			user.setId((String) result.getProperty(com.personal.rowan.paperforspotify.backend.model.User.KEY_ID));
			user.setName((String) result.getProperty(com.personal.rowan.paperforspotify.backend.model.User.KEY_NAME));
			user.setImageUrl((String) result.getProperty(com.personal.rowan.paperforspotify.backend.model.User.KEY_IMAGE_URL));
			users.add(user);
		}

		if(users.size() > 0) {
			return users.get(0);
		} else {
			return storeUser(userId, name, imageUrl, 1, "null");
		}
	}

	@ApiMethod(name = "storeRegId")
	public User storeRegId(@Named(User.KEY_ID) String userId,
	                       @Named(User.KEY_REG_ID) String registrationId) {
		List<User> deletedUsers = clearUsersById(userId);
		if(deletedUsers != null && !deletedUsers.isEmpty()) {
			User user = deletedUsers.get(0);
			return storeUser(user.getId(), user.getName(), user.getImageUrl(), 0, registrationId);
		}
		return null;
	}

	@ApiMethod(name = "storeName")
	public User storeName(@Named(User.KEY_ID) String userId,
	                       @Named(User.KEY_NAME) String name) {
		List<User> deletedUsers = clearUsersById(userId);
		if(deletedUsers != null && !deletedUsers.isEmpty()) {
			User user = deletedUsers.get(0);
			return storeUser(user.getId(), name, user.getImageUrl(), 0, user.getRegId());
		}
		return null;
	}

	@ApiMethod(name = "storeImageUrl")
	public User storeImageUrl(@Named(User.KEY_ID) String userId,
	                       @Named(User.KEY_IMAGE_URL) String imageUrl) {
		List<User> deletedUsers = clearUsersById(userId);
		if(deletedUsers != null && !deletedUsers.isEmpty()) {
			User user = deletedUsers.get(0);
			return storeUser(user.getId(), user.getName(), imageUrl, 0, user.getRegId());
		}
		return null;
	}

	@ApiMethod(name = "allUsers")
	public List<User> allUsers(@Named(User.KEY_ID) String searcherId) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query.Filter filter = new Query.FilterPredicate(User.KEY_ID, Query.FilterOperator.NOT_EQUAL, searcherId);
		Query query = new Query(User.KEY_ENTITY).setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		List<User> users = new ArrayList<>();
		for (Entity result : results) {
			User user = new User();
			user.setId((String) result.getProperty(com.personal.rowan.paperforspotify.backend.model.User.KEY_ID));
			user.setName((String) result.getProperty(com.personal.rowan.paperforspotify.backend.model.User.KEY_NAME));
			user.setImageUrl((String) result.getProperty(com.personal.rowan.paperforspotify.backend.model.User.KEY_IMAGE_URL));
			users.add(user);
		}

		return users;
	}

	@ApiMethod(name = "searchUsers")
	public List<UserSearchResult> searchUsers(@Named(User.KEY_ID) String searcherId,
	                                          @Named(User.KEY_NAME) String name) {
		List<User> users = allUsers(searcherId);

		//todo: find way to filter better
		List<User> filteredUsers = new ArrayList<>();
		for(User user : users) {
			if(user.getName().toUpperCase().contains(name.toUpperCase())) {
				filteredUsers.add(user);
			}
		}

		List<Friend> friends = friendsForUser(searcherId);
		HashMap<String, Friend> friendMap = new HashMap<>();
		for(Friend friend : friends) {
			friendMap.put(friend.getUser().getId(), friend);
		}

		List<UserSearchResult> searchResults = new ArrayList<>();
		for(User user : filteredUsers) {
			Friend friend = friendMap.get(user.getId());
			if(friend == null ||
					friend.rejectedByMe() ||
					friend.isPendingMe()) {
				searchResults.add(new UserSearchResult(user, "1"));
			} else if(friend.isPendingThem() ||
					friend.rejectedByThem() ||
					friend.acceptedFriendship()) {
				searchResults.add(new UserSearchResult(user, "0"));
			}
		}

		return searchResults;
	}

	/**
	 * User methods - Private helpers
	 */

	private User storeUser(String userId,
	                       String name,
	                       String imageUrl,
	                       int firstRegister,
	                       String regId) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastoreService.beginTransaction();
		try {
			Key parentKey = KeyFactory.createKey(User.KEY_PARENT, DATASTORE_KEY_NAME);
			Entity userEntity = new Entity(User.KEY_ENTITY, parentKey);
			userEntity.setProperty(User.KEY_ID, userId);
			userEntity.setProperty(User.KEY_NAME, name);
			userEntity.setProperty(User.KEY_IMAGE_URL, imageUrl);
			userEntity.setProperty(User.KEY_REG_ID, regId);
			datastoreService.put(userEntity);
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

		if(firstRegister == 1 && !"rhall1705".equals(userId)) {
			storeFriendship("rhall1705", userId, 1, 0);
		}

		User user = new User();
		user.setId(userId);
		user.setName(name);
		user.setImageUrl(imageUrl);
		user.setRegId(regId);

		return user;
	}

	private List<User> clearUsersById(String id) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query.Filter filter = new Query.FilterPredicate(User.KEY_ID, Query.FilterOperator.EQUAL, id);
		Query query = new Query(User.KEY_ENTITY);
		query.setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		Transaction txn = datastoreService.beginTransaction();
		for (Entity result : results) {
			datastoreService.delete(result.getKey());
		}
		txn.commit();

		ArrayList<User> users = new ArrayList<>();
		for (Entity result : results) {
			User user = new User();
			user.setId((String) result.getProperty(User.KEY_ID));
			user.setName((String) result.getProperty(User.KEY_NAME));
			user.setImageUrl((String) result.getProperty(User.KEY_IMAGE_URL));
			user.setRegId((String) result.getProperty(User.KEY_REG_ID));
			users.add(user);
		}

		return users;
	}

	private List<User> usersByIds(String[] ids) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query query;
		if(ids.length > 1) {
			List<Query.Filter> queryFilters = new ArrayList<>();
			for (String id : ids) {
				Query.Filter filter = new Query.FilterPredicate(User.KEY_ID, Query.FilterOperator.EQUAL, id);
				queryFilters.add(filter);
			}
			Query.CompositeFilter compositeFilter = new Query.CompositeFilter(Query.CompositeFilterOperator.OR, queryFilters);
			query = new Query(User.KEY_ENTITY).setFilter(compositeFilter);
		} else if (ids.length > 0){
			query = new Query(User.KEY_ENTITY).setFilter(new Query.FilterPredicate(User.KEY_ID, Query.FilterOperator.EQUAL, ids[0]));
		} else {
			return new ArrayList<>();
		}
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		List<User> users = new ArrayList<>();
		for (Entity result : results) {
			User user = new User();
			user.setId((String) result.getProperty(User.KEY_ID));
			user.setName((String) result.getProperty(User.KEY_NAME));
			user.setImageUrl((String) result.getProperty(User.KEY_IMAGE_URL));
			user.setRegId((String) result.getProperty(User.KEY_REG_ID));
			users.add(user);
		}

		return users;
	}

	private HashMap<String, User> usersForFriendships(List<Friendship> friendships, boolean initiated) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		List<Query.Filter> userIdFilters = new ArrayList<>();
		for(Friendship friendship : friendships) {
			String userId = initiated ? friendship.getReceiverId() : friendship.getInitiatorId();
			userIdFilters.add(new Query.FilterPredicate(User.KEY_ID, Query.FilterOperator.EQUAL, userId));
		}

		Query.Filter compositeFilter;
		if(userIdFilters.isEmpty()) {
			return null;
		} else if(userIdFilters.size() == 1) {
			compositeFilter = userIdFilters.get(0);
		} else {
			compositeFilter = Query.CompositeFilterOperator.or(userIdFilters);
		}
		Query query = new Query(User.KEY_ENTITY);
		query.setFilter(compositeFilter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
		HashMap<String, User> users = new HashMap<>();
		for (Entity result : results) {
			User user = new User();
			String id = (String) result.getProperty(User.KEY_ID);
			user.setId(id);
			user.setName((String) result.getProperty(User.KEY_NAME));
			user.setImageUrl((String) result.getProperty(User.KEY_IMAGE_URL));
			users.put(id, user);
		}

		return users;
	}

	/**
	 * Friendship methods - Public facing
	 */

	@ApiMethod(name = "requestFriendship")
	public Friendship requestFriendship(@Named(Friendship.KEY_INITIATOR_ID) String initiatorId,
	                                    @Named(Friendship.KEY_RECEIVER_ID) String receiverId) {

		//Check for existing friendship
		Pair<List<Friendship>, List<Friendship>> friendships = friendshipsById(initiatorId);
		List<Friendship> initiatedFriendships = friendships.getFirst();
		List<Friendship> receivedFriendships = friendships.getSecond();

		for(Friendship friendship : initiatedFriendships) {
			if(friendship.getReceiverId().equals(receiverId)) {
				//Do nothing - this user has already initiated a request with the receiver
				return null;
			}
		}

		for(Friendship friendship : receivedFriendships) {
			if(friendship.getInitiatorId().equals(receiverId)) {
				if(friendship.getPending().equals("1") && friendship.getRejected().equals("0")) {
					//The receiver has already requested this friendship so we accept it
					return answerFriendship(receiverId, initiatorId, 0);
				} else if(friendship.getPending().equals("0") && friendship.getRejected().equals("1")) {
					//The receiver had previously made a request but been rejected, so we treat this as accepting the original
					return answerFriendship(receiverId, initiatorId, 0);
				} else {
					//Do nothing - the existing request has already been resolved
					return null;
				}
			}
		}

		Friendship friendship = storeFriendship(initiatorId, receiverId, 1, 0);

		List<User> users = usersByIds(new String[] {initiatorId, receiverId});
		sendMessageToUser(PUSH_KEY_FRIEND_REQUEST, users.get(1).getRegId(), users.get(0).getName(), "", "");

		return friendship;
	}

	@ApiMethod(name = "answerFriendship")
	public Friendship answerFriendship(@Named(Friendship.KEY_INITIATOR_ID) String initiatorId,
	                                   @Named(Friendship.KEY_RECEIVER_ID) String receiverId,
	                                   @Named(Friendship.KEY_REJECTED) int reject) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query.Filter filter = new Query.FilterPredicate(Friendship.KEY_INITIATOR_ID, Query.FilterOperator.EQUAL, initiatorId);
		Query.Filter filter2 = new Query.FilterPredicate(Friendship.KEY_RECEIVER_ID, Query.FilterOperator.EQUAL, receiverId);
		Query.Filter compositeFilter = Query.CompositeFilterOperator.and(filter, filter2);
		Query query = new Query(Friendship.KEY_ENTITY);
		query.setFilter(compositeFilter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		if(results.size() > 0) {
			List<Friendship> friendships = new ArrayList<>();
			Transaction txn = datastoreService.beginTransaction();
			for (Entity result : results) {
				result.setProperty(Friendship.KEY_PENDING, "0");
				result.setProperty(Friendship.KEY_REJECTED, String.valueOf(reject));
				datastoreService.put(result);

				Friendship friendship = new Friendship();
				friendship.setInitiatorId((String) result.getProperty(Friendship.KEY_INITIATOR_ID));
				friendship.setReceiverId((String) result.getProperty(Friendship.KEY_RECEIVER_ID));
				friendship.setPending((String) result.getProperty(Friendship.KEY_PENDING));
				friendship.setRejected((String) result.getProperty(Friendship.KEY_REJECTED));
				friendships.add(friendship);
			}
			txn.commit();

			if(reject == 0) {
				List<User> users = usersByIds(new String[] {initiatorId, receiverId});
				sendMessageToUser(PUSH_KEY_FRIEND_ACCEPT, users.get(1).getRegId(), users.get(0).getName(), "", "");
			}

			return friendships.get(0);
		} else {
			//Trying to answer a friendship that doesn't exist
			return null;
		}
	}

	@ApiMethod(name = "friendsForUser")
	public List<Friend> friendsForUser(@Named(User.KEY_ID) String userId) {
		List<Friend> friends = new ArrayList<>();
		Pair<List<Friendship>, List<Friendship>> friendships = friendshipsById(userId);
		List<Friendship> initiatedFriendships = friendships.getFirst();
		List<Friendship> receivedFriendships = friendships.getSecond();
		HashMap<String, User> initiatedFriends = usersForFriendships(initiatedFriendships, true);
		HashMap<String, User> receivedFriends = usersForFriendships(receivedFriendships, false);

		if(initiatedFriends != null) {
			for (int i = 0; i < initiatedFriendships.size() && i < initiatedFriends.size(); i++) {
				Friendship friendship = initiatedFriendships.get(i);
				User user = initiatedFriends.get(friendship.getReceiverId());
				friends.add(new Friend(user, friendship.getPending(), friendship.getRejected(), "1"));
			}
		}

		if(receivedFriends != null) {
			for (int i = 0; i < receivedFriendships.size() && i < receivedFriends.size(); i++) {
				Friendship friendship = receivedFriendships.get(i);
				User user = receivedFriends.get(friendship.getInitiatorId());
				friends.add(new Friend(user, friendship.getPending(), friendship.getRejected(), "0"));
			}
		}

		return friends;
	}

	/**
	 * Friendship methods - Private helpers
	 */

	private Friendship storeFriendship(String initiatorId,
	                                  String receiverId,
	                                  int pending,
	                                  int rejected) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastoreService.beginTransaction();
		try {
			Key friendshipParentKey = KeyFactory.createKey(Friendship.KEY_PARENT, DATASTORE_KEY_NAME);
			Entity friendshipEntity = new Entity(Friendship.KEY_ENTITY, friendshipParentKey);
			friendshipEntity.setProperty(Friendship.KEY_INITIATOR_ID, initiatorId);
			friendshipEntity.setProperty(Friendship.KEY_RECEIVER_ID, receiverId);
			friendshipEntity.setProperty(Friendship.KEY_PENDING, String.valueOf(pending));
			friendshipEntity.setProperty(Friendship.KEY_REJECTED, String.valueOf(rejected));
			datastoreService.put(friendshipEntity);
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

		Friendship friendship = new Friendship();
		friendship.setInitiatorId(initiatorId);
		friendship.setReceiverId(receiverId);
		friendship.setPending(String.valueOf(pending));
		friendship.setRejected(String.valueOf(rejected));

		return friendship;
	}


	private Pair<List<Friendship>, List<Friendship>> friendshipsById(String userId) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query.Filter filter = new Query.FilterPredicate(Friendship.KEY_INITIATOR_ID, Query.FilterOperator.EQUAL, userId);
		Query query = new Query(Friendship.KEY_ENTITY).setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		List<Friendship> initiatedFriendships = new ArrayList<>();
		for (Entity result : results) {
			Friendship friendship = new Friendship();
			friendship.setInitiatorId((String) result.getProperty(Friendship.KEY_INITIATOR_ID));
			friendship.setReceiverId((String) result.getProperty(Friendship.KEY_RECEIVER_ID));
			friendship.setPending((String) result.getProperty(Friendship.KEY_PENDING));
			friendship.setRejected((String) result.getProperty(Friendship.KEY_REJECTED));
			initiatedFriendships.add(friendship);
		}

		filter = new Query.FilterPredicate(Friendship.KEY_RECEIVER_ID, Query.FilterOperator.EQUAL, userId);
		query = new Query(Friendship.KEY_ENTITY).setFilter(filter);
		results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		List<Friendship> receivedFriendships = new ArrayList<>();
		for (Entity result : results) {
			Friendship friendship = new Friendship();
			friendship.setInitiatorId((String) result.getProperty(Friendship.KEY_INITIATOR_ID));
			friendship.setReceiverId((String) result.getProperty(Friendship.KEY_RECEIVER_ID));
			friendship.setPending((String) result.getProperty(Friendship.KEY_PENDING));
			friendship.setRejected((String) result.getProperty(Friendship.KEY_REJECTED));
			receivedFriendships.add(friendship);
		}

		return new Pair<>(initiatedFriendships, receivedFriendships);
	}

	/**
	 * Chat methods - Public facing
	 */

	@ApiMethod(name = "storeChat")
	public Chat storeChat(@Named(Chat.KEY_NAME) String chatName,
	                      @Named(Chat.KEY_CREATOR_ID) String creatorId,
	                      @Named(Chat.KEY_CREATOR_NAME) String creatorName,
	                      @Named(ChatMembership.KEY_CSV_USER_IDS) String csvRecipientIds) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastoreService.beginTransaction();

		Chat chat = new Chat(chatName, creatorId, creatorName);

		try {
			Key chatParentKey = KeyFactory.createKey(Chat.KEY_PARENT, DATASTORE_KEY_NAME);
			Entity chatEntity = new Entity(Chat.KEY_ENTITY, chatParentKey);
			chatEntity.setProperty(Chat.KEY_ID, chat.getId());
			chatEntity.setProperty(Chat.KEY_NAME, chat.getName());
			chatEntity.setProperty(Chat.KEY_CREATOR_ID, chat.getCreatorId());
			chatEntity.setProperty(Chat.KEY_CREATOR_NAME, chat.getCreatorName());
			chatEntity.setProperty(Chat.KEY_LAST_MESSAGE, chat.getLastMessage());
			chatEntity.setProperty(Chat.KEY_UPDATED_TIMESTAMP, chat.getUpdatedTimestamp());
			chatEntity.setProperty(Chat.KEY_CHAT_SONG_URI, chat.getChatSongUri());
			chatEntity.setProperty(Chat.KEY_CHAT_SONG_URL, chat.getChatSongUrl());
			chatEntity.setProperty(Chat.KEY_CHAT_SONG_IMAGE_URL, chat.getChatSongImageUrl());
			chatEntity.setProperty(Chat.KEY_CHAT_SONG_NAME, chat.getChatSongName());
			chatEntity.setProperty(Chat.KEY_CHAT_SONG_ARTIST_NAME, chat.getChatArtistName());
			datastoreService.put(chatEntity);
			txn.commit();

			String membershipIds = creatorId + "," + csvRecipientIds;

			addUsersToChat(creatorId, creatorName, chatName, chat.getId(), membershipIds);
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

		return chat;
	}

	@ApiMethod(name = "addUsersToChat")
	public List<ChatMembership> addUsersToChat(@Named(Chat.KEY_CREATOR_ID) String creatorId,
	                                           @Named(Chat.KEY_CREATOR_NAME) String creatorName,
	                                           @Named(Chat.KEY_NAME) String chatName,
	                                           @Named(Chat.KEY_ID) String chatId,
	                                           @Named(ChatMembership.KEY_CSV_USER_IDS) String csvChatIds) {

		List<ChatMembership> memberships = new ArrayList<>();
		String[] userIds = csvChatIds.split(",");

		for(String userId : userIds) {
			DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
			Transaction txn = datastoreService.beginTransaction();

			ChatMembership chatMembership = new ChatMembership();
			chatMembership.setChatId(chatId);
			chatMembership.setUserId(userId);
			chatMembership.setHasRead("0");
			memberships.add(chatMembership);

			try {
				Key chatMembershipParentKey = KeyFactory.createKey(ChatMembership.KEY_PARENT, DATASTORE_KEY_NAME);
				Entity chatMembershipEntity = new Entity(ChatMembership.KEY_ENTITY, chatMembershipParentKey);
				chatMembershipEntity.setProperty(Chat.KEY_ID, chatMembership.getChatId());
				chatMembershipEntity.setProperty(User.KEY_ID, chatMembership.getUserId());
				chatMembershipEntity.setProperty(ChatMembership.KEY_READ_RECEIPT, chatMembership.getHasRead());
				datastoreService.put(chatMembershipEntity);
				txn.commit();
			} finally {
				if (txn.isActive()) {
					txn.rollback();
				}
			}
		}

		for (User user : usersByIds(userIds)) {
			if (!user.getId().equals(creatorId)) {
				String regId = user.getRegId();
				if (regId != null && regId.length() > 0 && !regId.equals("null")) {
					sendMessageToUser(PUSH_KEY_CHAT, regId, creatorName, chatName, chatId);
				}
			}
		}

		return memberships;
	}

	@ApiMethod(name = "clearUserFromChat")
	public List<ChatMembership> clearUserFromChat(@Named(Chat.KEY_ID) String chatId,
	                                              @Named(Chat.KEY_NAME) String chatName,
	                                              @Named(User.KEY_ID) String userId,
	                                              @Named(User.KEY_NAME) String userName) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query.Filter filter = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, chatId);
		Query.Filter filter2 = new Query.FilterPredicate(User.KEY_ID, Query.FilterOperator.EQUAL, userId);
		Query.Filter compositeFilter = Query.CompositeFilterOperator.and(filter, filter2);
		Query query = new Query(ChatMembership.KEY_ENTITY);
		query.setFilter(compositeFilter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		Transaction txn = datastoreService.beginTransaction();
		for (Entity result : results) {
			datastoreService.delete(result.getKey());
		}
		txn.commit();

		List<ChatMembership> memberships = new ArrayList<>();

		for (Entity result : results) {
			ChatMembership membership = new ChatMembership();
			membership.setChatId((String) result.getProperty(Chat.KEY_ID));
			membership.setUserId((String) result.getProperty(User.KEY_ID));
			membership.setHasRead((String) result.getProperty(ChatMembership.KEY_READ_RECEIPT));
			memberships.add(membership);
		}

		sendAdminMessage(chatId, chatName, userName + " has left " + chatName);

		return memberships;
	}

	@ApiMethod(name = "chatByUserId")
	public List<Chat> chatByUserId(@Named(User.KEY_ID) String userId) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(ChatMembership.KEY_ENTITY);
		Query.Filter filter = new Query.FilterPredicate(User.KEY_ID, Query.FilterOperator.EQUAL, userId);
		query.setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		ArrayList<ChatMembership> memberships = new ArrayList<>();
		for (Entity result : results) {
			ChatMembership membership = new ChatMembership();
			membership.setChatId((String) result.getProperty(Chat.KEY_ID));
			membership.setUserId((String) result.getProperty(User.KEY_ID));
			membership.setHasRead((String) result.getProperty(ChatMembership.KEY_READ_RECEIPT));
			memberships.add(membership);
		}

		ArrayList<Chat> chats = new ArrayList<>();
		for(ChatMembership membership : memberships) {
			Query.Filter filter2 = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, membership.getChatId());
			Query query2 = new Query(Chat.KEY_ENTITY).setFilter(filter2);
			List<Entity> results2 = datastoreService.prepare(query2).asList(FetchOptions.Builder.withDefaults());

			for (Entity result : results2) {
				Chat chat = new Chat();
				chat.setId((String) result.getProperty(Chat.KEY_ID));
				chat.setName((String) result.getProperty(Chat.KEY_NAME));
				chat.setLastMessage((String) result.getProperty(Chat.KEY_LAST_MESSAGE));
				chat.setUpdatedTimestamp((Long) result.getProperty(Chat.KEY_UPDATED_TIMESTAMP));
				chat.setCreatorId((String) result.getProperty(Chat.KEY_CREATOR_ID));
				chat.setCreatorName((String) result.getProperty(Chat.KEY_CREATOR_NAME));
				chat.setChatSongUri((String) result.getProperty(Chat.KEY_CHAT_SONG_URI));
				chat.setChatSongUrl((String) result.getProperty(Chat.KEY_CHAT_SONG_URL));
				chat.setChatSongImageUrl((String) result.getProperty(Chat.KEY_CHAT_SONG_IMAGE_URL));
				chat.setChatSongName((String) result.getProperty(Chat.KEY_CHAT_SONG_NAME));
				chat.setChatArtistName((String) result.getProperty(Chat.KEY_CHAT_SONG_ARTIST_NAME));
				//public value not stored in datastore
				chat.hasRead = membership.getHasRead();
				chats.add(chat);
			}
		}

		Collections.sort(chats, new Comparator<Chat>() {
			@Override
			public int compare(Chat o1, Chat o2) {
				if(o1.getUpdatedTimestamp() > o2.getUpdatedTimestamp()) {
					return -1;
				} else if(o1.getUpdatedTimestamp() < o2.getUpdatedTimestamp()) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		return chats;
	}

	@ApiMethod(name = "chatById")
	public Chat chatById(@Named(Chat.KEY_ID) String chatId) {
		ArrayList<Chat> chats = new ArrayList<>();
		Query.Filter filter = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, chatId);
		Query query = new Query(Chat.KEY_ENTITY).setFilter(filter);
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		for (Entity result : results) {
			Chat chat = new Chat();
			chat.setId((String) result.getProperty(Chat.KEY_ID));
			chat.setName((String) result.getProperty(Chat.KEY_NAME));
			chat.setLastMessage((String) result.getProperty(Chat.KEY_LAST_MESSAGE));
			chat.setUpdatedTimestamp((Long) result.getProperty(Chat.KEY_UPDATED_TIMESTAMP));
			chat.setCreatorId((String) result.getProperty(Chat.KEY_CREATOR_ID));
			chat.setCreatorName((String) result.getProperty(Chat.KEY_CREATOR_NAME));
			chat.setChatSongUri((String) result.getProperty(Chat.KEY_CHAT_SONG_URI));
			chat.setChatSongUrl((String) result.getProperty(Chat.KEY_CHAT_SONG_URL));
			chat.setChatSongImageUrl((String) result.getProperty(Chat.KEY_CHAT_SONG_IMAGE_URL));
			chat.setChatSongName((String) result.getProperty(Chat.KEY_CHAT_SONG_NAME));
			chat.setChatArtistName((String) result.getProperty(Chat.KEY_CHAT_SONG_ARTIST_NAME));
			chat.hasRead = "1";
			chats.add(chat);
		}

		return chats.get(0);
	}

	@ApiMethod(name = "usersByChatId")
	public List<User> usersByChatId(@Named(Chat.KEY_ID) String chatId) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(ChatMembership.KEY_ENTITY);
		Query.Filter filter = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, chatId);
		query.setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		ArrayList<ChatMembership> memberships = new ArrayList<>();
		for (Entity result : results) {
			ChatMembership membership = new ChatMembership();
			membership.setChatId((String) result.getProperty(Chat.KEY_ID));
			membership.setUserId((String) result.getProperty(User.KEY_ID));
			membership.setHasRead((String) result.getProperty(ChatMembership.KEY_READ_RECEIPT));
			memberships.add(membership);
		}

		ArrayList<User> users = new ArrayList<>();
		for(ChatMembership membership : memberships) {
			Query.Filter filter2 = new Query.FilterPredicate(User.KEY_ID, Query.FilterOperator.EQUAL, membership.getUserId());
			Query query2 = new Query(User.KEY_ENTITY).setFilter(filter2);
			List<Entity> results2 = datastoreService.prepare(query2).asList(FetchOptions.Builder.withDefaults());

			for (Entity result : results2) {
				User user = new User();
				user.setId((String) result.getProperty(User.KEY_ID));
				user.setName((String) result.getProperty(User.KEY_NAME));
				user.setImageUrl((String) result.getProperty(User.KEY_IMAGE_URL));
				user.setRegId((String) result.getProperty(User.KEY_REG_ID));
				users.add(user);
			}
		}

		return users;
	}

	@ApiMethod(name="addSongToChat")
	public Chat addSongToChat(@Named(Chat.KEY_ID) String chatId, 
	                          @Named(Chat.KEY_CHAT_SONG_URI) String chatSongUri,
	                          @Named(Chat.KEY_CHAT_SONG_URL) String chatSongUrl,
	                          @Named(Chat.KEY_CHAT_SONG_IMAGE_URL) String chatSongImageUrl, 
	                          @Named(Chat.KEY_CHAT_SONG_NAME) String chatSongName, 
	                          @Named(Chat.KEY_CHAT_SONG_ARTIST_NAME) String chatArtistName,
	                          @Named(User.KEY_ID) String userId,
	                          @Named(User.KEY_NAME) String userName) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(Chat.KEY_ENTITY);
		Query.Filter filter = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, chatId);
		query.setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		Transaction txn = datastoreService.beginTransaction();
		ArrayList<Chat> chats = new ArrayList<>();
		for (Entity result : results) {
			result.setProperty(Chat.KEY_CHAT_SONG_URI, chatSongUri);
			result.setProperty(Chat.KEY_CHAT_SONG_URL, chatSongUrl);
			result.setProperty(Chat.KEY_CHAT_SONG_IMAGE_URL, chatSongImageUrl);
			result.setProperty(Chat.KEY_CHAT_SONG_NAME, chatSongName);
			result.setProperty(Chat.KEY_CHAT_SONG_ARTIST_NAME, chatArtistName);
			datastoreService.put(result);

			Chat chat = new Chat();
			chat.setId((String) result.getProperty(Chat.KEY_ID));
			chat.setName((String) result.getProperty(Chat.KEY_NAME));
			chat.setLastMessage((String) result.getProperty(Chat.KEY_LAST_MESSAGE));
			chat.setUpdatedTimestamp((Long) result.getProperty(Chat.KEY_UPDATED_TIMESTAMP));
			chat.setCreatorId((String) result.getProperty(Chat.KEY_CREATOR_ID));
			chat.setCreatorName((String) result.getProperty(Chat.KEY_CREATOR_NAME));
			chat.setChatSongUri((String) result.getProperty(Chat.KEY_CHAT_SONG_URI));
			chat.setChatSongUrl((String) result.getProperty(Chat.KEY_CHAT_SONG_URL));
			chat.setChatSongImageUrl((String) result.getProperty(Chat.KEY_CHAT_SONG_IMAGE_URL));
			chat.setChatSongName((String) result.getProperty(Chat.KEY_CHAT_SONG_NAME));
			chat.setChatArtistName((String) result.getProperty(Chat.KEY_CHAT_SONG_ARTIST_NAME));
			chats.add(chat);
		}
		txn.commit();

		Chat chat = chats.get(0);
		List<User> users = usersByChatId(chatId);
		for(User user : users) {
			String regId = user.getRegId();
			if (regId != null && regId.length() > 0 && !regId.equals("null") && !userId.equals(user.getId())) {
				sendMessageToUser(PUSH_KEY_CHAT_DETAIL, regId, userName, chat.getName(), chatId);
			}
		}

		sendAdminMessage(chatId, chat.getName(), userName + " has changed the song for " + chat.getName() + " to \"" + chatSongName + "\"");

		return chat;
	}

	@ApiMethod(name = "addTextOnlyMessageToChat")
	public ChatMessage addTextOnlyMessageToChat(@Named(ChatMessage.KEY_ID) String messageId,
	                                            @Named(Chat.KEY_ID) String chatId,
			                                    @Named(User.KEY_ID) String userId,
		                                        @Named(Chat.KEY_NAME) String chatName,
			                                    @Named(User.KEY_NAME) String name,
			                                    @Named(User.KEY_IMAGE_URL) String imageUrl,
			                                    @Named(ChatMessage.KEY_MESSAGE) String message) {
		return addMessageToChat(messageId,
				chatId,
				userId,
				chatName,
				name,
				imageUrl,
				message,
				"", "", "", "", "");
	}

	@ApiMethod(name = "addMessageToChat")
	public ChatMessage addMessageToChat(@Named(ChatMessage.KEY_ID) String messageId,
	                                    @Named(Chat.KEY_ID) String chatId,
	                                    @Named(User.KEY_ID) String userId,
	                                    @Named(Chat.KEY_NAME) String chatName,
	                                    @Named(User.KEY_NAME) String name,
	                                    @Named(User.KEY_IMAGE_URL) String imageUrl,
	                                    @Named(ChatMessage.KEY_MESSAGE) String message,
	                                    @Named(ChatMessage.KEY_SONG_URI) String songUri,
	                                    @Named(ChatMessage.KEY_SONG_URL) String songUrl,
	                                    @Named(ChatMessage.KEY_SONG_IMAGE_URL) String songImageUrl,
	                                    @Named(ChatMessage.KEY_SONG_NAME) String songName,
	                                    @Named(ChatMessage.KEY_ARTIST_NAME) String artistName) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastoreService.beginTransaction();

		final ChatMessage chatMessage = new ChatMessage(messageId, chatId, userId, chatName, name, imageUrl, message);
		chatMessage.addSong(songUri, songUrl, songImageUrl, songName, artistName);

		try {
			Key chatMessageParentKey = KeyFactory.createKey(ChatMessage.KEY_PARENT, DATASTORE_KEY_NAME);
			Entity chatMessageEntity = new Entity(ChatMessage.KEY_ENTITY, chatMessageParentKey);
			chatMessageEntity.setProperty(ChatMessage.KEY_ID, chatMessage.getId());
			chatMessageEntity.setProperty(Chat.KEY_ID, chatMessage.getChatId());
			chatMessageEntity.setProperty(User.KEY_ID, chatMessage.getUserId());
			chatMessageEntity.setProperty(Chat.KEY_NAME, chatMessage.getChatName());
			chatMessageEntity.setProperty(User.KEY_NAME, chatMessage.getName());
			chatMessageEntity.setProperty(User.KEY_IMAGE_URL, chatMessage.getImageUrl());
			chatMessageEntity.setProperty(ChatMessage.KEY_MESSAGE, chatMessage.getMessage());
			chatMessageEntity.setProperty(ChatMessage.KEY_TIMESTAMP, chatMessage.getTimeStamp());
			chatMessageEntity.setProperty(ChatMessage.KEY_SONG_URI, chatMessage.getMessageSongUri());
			chatMessageEntity.setProperty(ChatMessage.KEY_SONG_URL, chatMessage.getMessageSongUrl());
			chatMessageEntity.setProperty(ChatMessage.KEY_SONG_IMAGE_URL, chatMessage.getMessageSongImageUrl());
			chatMessageEntity.setProperty(ChatMessage.KEY_SONG_NAME, chatMessage.getMessageSongName());
			chatMessageEntity.setProperty(ChatMessage.KEY_ARTIST_NAME, chatMessage.getMessageArtistName());
			datastoreService.put(chatMessageEntity);
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}


		//Send push notifications and update chat object's last message in background thread
		/*Thread thread = ThreadManager.createBackgroundThread(new Runnable() {
			public void run() {*/
				String lastMessage = "";
				if (songName != null && songName.length() > 0) {
					lastMessage = songName;
				} else if (message != null && message.length() > 0) {
					lastMessage = message;
				}
				if (name != null && name.length() > 0) {
					lastMessage = name + ": " + lastMessage;
				}

				List<User> users = usersByChatId(chatId);
				for (User user : users) {
					if (!user.getId().equals(userId)) {
						String regId = user.getRegId();
						if (regId != null && regId.length() > 0 && !regId.equals("null")) {
							sendMessageToUser(PUSH_KEY_MESSAGE, regId, lastMessage, chatName, chatId);
						}
					}
				}

				updateLastChatMessage(chatId, lastMessage, chatMessage.getTimeStamp());
				clearReadReceipts(chatId, userId);
			/*}
		});
		thread.start();*/



		return chatMessage;
	}

	@ApiMethod(name = "messagesByChat")
	public List<ChatMessage> messagesByChat(@Named(Chat.KEY_ID) String chatId,
	                                        @Named(User.KEY_ID) String userId,
	                                        @Named("doNotMark") String doNotMark) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(ChatMessage.KEY_ENTITY);
		Query.Filter filter = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, chatId);
		query.setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		ArrayList<ChatMessage> chatMessages = new ArrayList<>();
		for (Entity result : results) {
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setId((String) result.getProperty(ChatMessage.KEY_ID));
			chatMessage.setChatId((String) result.getProperty(Chat.KEY_ID));
			chatMessage.setUserId((String) result.getProperty(User.KEY_ID));
			chatMessage.setChatName((String) result.getProperty(Chat.KEY_NAME));
			chatMessage.setName((String) result.getProperty(User.KEY_NAME));
			chatMessage.setImageUrl((String) result.getProperty(User.KEY_IMAGE_URL));
			chatMessage.setMessage((String) result.getProperty(ChatMessage.KEY_MESSAGE));
			chatMessage.setTimeStamp((Long) result.getProperty(ChatMessage.KEY_TIMESTAMP));
			chatMessage.setMessageSongUri((String) result.getProperty(ChatMessage.KEY_SONG_URI));
			chatMessage.setMessageSongUrl((String) result.getProperty(ChatMessage.KEY_SONG_URL));
			chatMessage.setMessageSongImageUrl((String) result.getProperty(ChatMessage.KEY_SONG_IMAGE_URL));
			chatMessage.setMessageSongName((String) result.getProperty(ChatMessage.KEY_SONG_NAME));
			chatMessage.setMessageArtistName((String) result.getProperty(ChatMessage.KEY_ARTIST_NAME));
			chatMessages.add(chatMessage);
		}

		Collections.sort(chatMessages, new Comparator<ChatMessage>() {
			@Override
			public int compare(ChatMessage lhs, ChatMessage rhs) {
				if (rhs.getTimeStamp() < lhs.getTimeStamp()) {
					return 1;
				} else if (rhs.getTimeStamp() > lhs.getTimeStamp()) {
					return -1;
				}
				return 0;
			}
		});

		if(!"1".equals(doNotMark)) {
			markAsRead(chatId, userId);

			List<User> chatParticipants = usersByChatId(chatId);
			if(chatParticipants != null) {
				for (User user : chatParticipants) {
					sendMessageToUser(PUSH_KEY_READ_RECEIPT, user.getRegId(), chatId, "", "");
				}
			}
		}

		return chatMessages;
	}

	@ApiMethod(name = "messagesByChatUpdate")
	public List<ChatMessage> messagesByChatUpdate(@Named(Chat.KEY_ID) String chatId,
	                                              @Named(ChatMessage.KEY_TIMESTAMP) Long timestamp,
	                                              @Named(User.KEY_ID) String userId,
	                                              @Named("doNotMark") String doNotMark) {
		/*DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(ChatMessage.KEY_ENTITY);
		Query.Filter filter = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, chatId);
		Query.Filter filter2 = new Query.FilterPredicate(ChatMessage.KEY_TIMESTAMP, Query.FilterOperator.GREATER_THAN, timestamp);
		Query.CompositeFilter compositeFilter = Query.CompositeFilterOperator.and(filter, filter2);
		query.setFilter(compositeFilter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		ArrayList<ChatMessage> chatMessages = new ArrayList<>();
		for (Entity result : results) {
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setChatId((String) result.getProperty(Chat.KEY_ID));
			chatMessage.setUserId((String) result.getProperty(User.KEY_ID));
			chatMessage.setName((String) result.getProperty(User.KEY_NAME));
			chatMessage.setImageUrl((String) result.getProperty(User.KEY_IMAGE_URL));
			chatMessage.setMessage((String) result.getProperty(ChatMessage.KEY_MESSAGE));
			chatMessage.setTimeStamp((Long) result.getProperty(ChatMessage.KEY_TIMESTAMP));
			chatMessage.setMessageSongUri((String) result.getProperty(ChatMessage.KEY_SONG_URI));
			chatMessage.setMessageSongUrl((String) result.getProperty(ChatMessage.KEY_SONG_URL));
			chatMessage.setMessageSongImageUrl((String) result.getProperty(ChatMessage.KEY_SONG_IMAGE_URL));
			chatMessage.setMessageSongName((String) result.getProperty(ChatMessage.KEY_SONG_NAME));
			chatMessage.setMessageArtistName((String) result.getProperty(ChatMessage.KEY_ARTIST_NAME));
			chatMessages.add(chatMessage);
		}

		Collections.sort(chatMessages, new Comparator<ChatMessage>() {
			@Override
			public int compare(ChatMessage lhs, ChatMessage rhs) {
				if(rhs.getTimeStamp() < lhs.getTimeStamp()) {
					return 1;
				} else if(rhs.getTimeStamp() > lhs.getTimeStamp()) {
					return -1;
				}
				return 0;
			}
		});

		return chatMessages;*/

		List<ChatMessage> messages = messagesByChat(chatId, userId, doNotMark);
		List<ChatMessage> filteredMessages = new ArrayList<>();
		for(int i = messages.size() - 1; i >= 0; i--) {
			ChatMessage message = messages.get(i);
			if(message.getTimeStamp() > timestamp) {
				filteredMessages.add(message);
			} else {
				break;
			}
		}
		Collections.reverse(filteredMessages);

		if(filteredMessages.size() > 200) {
			int size = filteredMessages.size();
			filteredMessages = filteredMessages.subList(size - 199, size);
		}

		return filteredMessages;
	}

	@ApiMethod(name = "readReceiptMessage")
	public ReadReceipt readReceiptMessageForChat(@Named(Chat.KEY_ID) String chatId,
	                                             @Named(User.KEY_ID) String userId) {

		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(ChatMembership.KEY_ENTITY);
		Query.Filter filter = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, chatId);
		query.setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		ArrayList<ChatMembership> memberships = new ArrayList<>();
		for (Entity result : results) {
			ChatMembership membership = new ChatMembership();
			membership.setChatId((String) result.getProperty(Chat.KEY_ID));
			membership.setUserId((String) result.getProperty(User.KEY_ID));
			membership.setHasRead((String) result.getProperty(ChatMembership.KEY_READ_RECEIPT));
			memberships.add(membership);
		}

		List<ChatMessage> messages = messagesByChat(chatId, "", "1");
		String lastMessageId = "";
		String lastMessageUserId = "";
		if(messages != null && !messages.isEmpty()) {
			ChatMessage lastMessage = messages.get(messages.size() - 1);
			lastMessageId = lastMessage.getId();
			lastMessageUserId = lastMessage.getUserId();
		}
		List<String> userIds = new ArrayList<>();
		Set<String> duplicateSet = new HashSet<>();
		for(ChatMembership membership : memberships) {
			String memberId = membership.getUserId();
			if("1".equals(membership.getHasRead()) &&
					!userId.equals(memberId) &&
					!lastMessageUserId.equals(memberId) &&
					!duplicateSet.contains(memberId)) {
				userIds.add(memberId);
				duplicateSet.add(memberId);
			}
		}

		List<User> users = usersByIds(userIds.toArray(new String[userIds.size()]));
		if(users == null || users.isEmpty()) {
			return new ReadReceipt(chatId, lastMessageId, "");
		}
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("Seen+by+");
		for(int i = 0; i < users.size(); i++) {
			if(users.size() > 1 && i == users.size() - 1) {
				messageBuilder
						.append("and+")
						.append(users.get(i).getFirstName());
			} else if(users.size() > 2) {
				messageBuilder
						.append(users.get(i).getFirstName())
						.append(",+");
			} else {
				messageBuilder
						.append(users.get(i).getFirstName())
						.append("+");
			}
		}

		if(messageBuilder.charAt(messageBuilder.length() - 1) == '+') {
			messageBuilder.deleteCharAt(messageBuilder.length() - 1);
		}

		return new ReadReceipt(chatId, lastMessageId, messageBuilder.toString());
	}

	@ApiMethod(name="storeChatName")
	public Chat storeChatName(@Named(Chat.KEY_ID) String chatId,
	                          @Named(Chat.KEY_NAME) String chatName,
	                          @Named(User.KEY_ID) String userId,
	                          @Named(User.KEY_NAME) String userName) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(Chat.KEY_ENTITY);
		Query.Filter filter = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, chatId);
		query.setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		List<Chat> chats = new ArrayList<>();
		String oldName = "";
		if(!results.isEmpty()) {
			Transaction txn = datastoreService.beginTransaction();
			for (Entity result : results) {
				oldName = (String) result.getProperty(Chat.KEY_NAME);
				result.setProperty(Chat.KEY_NAME, chatName);
				datastoreService.put(result);

				Chat chat = new Chat();
				chat.setId((String) result.getProperty(Chat.KEY_ID));
				chat.setName(chatName);
				chat.setLastMessage((String) result.getProperty(Chat.KEY_LAST_MESSAGE));
				chat.setUpdatedTimestamp((Long) result.getProperty(Chat.KEY_UPDATED_TIMESTAMP));
				chat.setCreatorId((String) result.getProperty(Chat.KEY_CREATOR_ID));
				chat.setCreatorName((String) result.getProperty(Chat.KEY_CREATOR_NAME));
				chat.setChatSongUri((String) result.getProperty(Chat.KEY_CHAT_SONG_URI));
				chat.setChatSongUrl((String) result.getProperty(Chat.KEY_CHAT_SONG_URL));
				chat.setChatSongImageUrl((String) result.getProperty(Chat.KEY_CHAT_SONG_IMAGE_URL));
				chat.setChatSongName((String) result.getProperty(Chat.KEY_CHAT_SONG_NAME));
				chat.setChatArtistName((String) result.getProperty(Chat.KEY_CHAT_SONG_ARTIST_NAME));
				chats.add(chat);
			}
			txn.commit();
		}

		Chat chat = chats.get(0);
		List<User> users = usersByChatId(chatId);
		for(User user : users) {
			String regId = user.getRegId();
			if (regId != null && regId.length() > 0 && !regId.equals("null") && !userId.equals(user.getId())) {
				sendMessageToUser(PUSH_KEY_CHAT_DETAIL, regId, userName, chat.getName(), chatId);
			}
		}

		sendAdminMessage(chatId, chat.getName(), userName + " has changed the chat name from \"" + oldName + "\" to \"" + chatName + "\"");

		return chat;
	}

	/**
	 * Chat methods - Private helpers
	 */

	private void updateLastChatMessage(String chatId, String lastMessage, Long timestamp) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(Chat.KEY_ENTITY);
		Query.Filter filter = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, chatId);
		query.setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		if(!results.isEmpty()) {
			Transaction txn = datastoreService.beginTransaction();
			for (Entity result : results) {
				result.setProperty(Chat.KEY_LAST_MESSAGE, lastMessage);
				result.setProperty(Chat.KEY_UPDATED_TIMESTAMP, timestamp);
				datastoreService.put(result);
			}
			txn.commit();
		}
	}

	private void clearReadReceipts(String chatId, String userIdToExclude) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(ChatMembership.KEY_ENTITY);
		Query.Filter filter = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, chatId);
		query.setFilter(filter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		if(!results.isEmpty()) {
			Transaction txn = datastoreService.beginTransaction();
			for (Entity result : results) {
				String userId = (String) result.getProperty(User.KEY_ID);
				if(userId.equals(userIdToExclude)) {
					continue;
				}
				result.setProperty(ChatMembership.KEY_READ_RECEIPT, "0");
				datastoreService.put(result);
			}
			txn.commit();
		}
	}

	private void markAsRead(String chatId, String userId) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(ChatMembership.KEY_ENTITY);
		Query.Filter filter = new Query.FilterPredicate(Chat.KEY_ID, Query.FilterOperator.EQUAL, chatId);
		Query.Filter filter2 = new Query.FilterPredicate(User.KEY_ID, Query.FilterOperator.EQUAL, userId);
		Query.Filter compositeFilter = Query.CompositeFilterOperator.and(filter, filter2);
		query.setFilter(compositeFilter);
		List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

		if(!results.isEmpty()) {
			Transaction txn = datastoreService.beginTransaction();
			for (Entity result : results) {
				result.setProperty(ChatMembership.KEY_READ_RECEIPT, "1");
				datastoreService.put(result);
			}
			txn.commit();
		}
	}

	private void sendAdminMessage(String chatId, String chatName, String message) {
		addTextOnlyMessageToChat(UUID.randomUUID().toString(),
				chatId,
				ADMINISTRATOR_ID,
				chatName,
				ADMINISTRATOR_NAME,
				ADMINISTRATOR_IMAGE_URL,
				message);
	}

}
