package personal.rowan.paperforspotify.dagger;

import javax.inject.Singleton;

import dagger.Component;
import personal.rowan.paperforspotify.data.gcm.PaperGcmListenerService;
import personal.rowan.paperforspotify.data.gcm.PaperRegistrationIntentService;
import personal.rowan.paperforspotify.data.loader.ChatLoader;
import personal.rowan.paperforspotify.data.loader.FriendLoader;
import personal.rowan.paperforspotify.data.loader.LoaderFactory;
import personal.rowan.paperforspotify.data.loader.MessageLoader;
import personal.rowan.paperforspotify.data.loader.ReadReceiptLoader;
import personal.rowan.paperforspotify.data.service.ImageUploadService;
import personal.rowan.paperforspotify.manager.SpotifyManager;
import personal.rowan.paperforspotify.ui.activity.ChatParticipantActivity;
import personal.rowan.paperforspotify.ui.activity.FriendSearchActivity;
import personal.rowan.paperforspotify.ui.activity.MainActivity;
import personal.rowan.paperforspotify.ui.activity.MessagingActivity;
import personal.rowan.paperforspotify.ui.activity.SettingsActivity;
import personal.rowan.paperforspotify.ui.activity.SpotifyActivity;
import personal.rowan.paperforspotify.ui.adapter.list.ChatMessageListAdapter;
import personal.rowan.paperforspotify.ui.adapter.list.FriendsListAdapter;
import personal.rowan.paperforspotify.ui.fragment.ChatListFragment;
import personal.rowan.paperforspotify.ui.fragment.FriendsListFragment;
import personal.rowan.paperforspotify.ui.fragment.dialog.CreateChatDialogFragment;
import personal.rowan.paperforspotify.ui.fragment.dialog.EditProfilePictureDialogFragment;
import personal.rowan.paperforspotify.ui.fragment.dialog.SpotifySongSearchDialogFragment;

@Singleton
@Component(modules = { ApplicationModule.class })
public interface ApplicationComponent {

	// Managers
	void inject(SpotifyManager spotifyManager);

	void inject(LoaderFactory loaderFactory);

	// Loaders
	void inject(ChatLoader chatLoader);

	void inject(FriendLoader friendLoader);

	void inject(MessageLoader messageLoader);

	void inject(ReadReceiptLoader readReceiptLoader);

	// Activities
	void inject(MainActivity mainActivity);

	void inject(MessagingActivity messagingActivity);

	void inject(ChatParticipantActivity chatParticipantActivity);

	void inject(FriendSearchActivity friendSearchActivity);

	void inject(SpotifyActivity spotifyActivity);

	void inject(SettingsActivity settingsActivity);

	// Fragments
	void inject(ChatListFragment chatListFragment);

	void inject(FriendsListFragment friendsListFragment);

	void inject(SettingsActivity.SettingsFragment settingsFragment);

	// Dialogs
	void inject(CreateChatDialogFragment createChatDialogFragment);

	void inject(SpotifySongSearchDialogFragment spotifySongSearchDialogFragment);

	void inject(EditProfilePictureDialogFragment editProfilePictureDialogFragment);

	// Adapters
	void inject(ChatMessageListAdapter chatMessageListAdapter);

	void inject(FriendsListAdapter friendsListAdapter);

	// Services
	void inject(PaperGcmListenerService paperGcmListenerService);

	void inject(PaperRegistrationIntentService paperRegistrationIntentService);

	void inject(ImageUploadService imageUploadService);

}
