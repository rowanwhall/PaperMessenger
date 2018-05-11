package personal.rowan.paperforspotify.ui.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.ui.fragment.ChatListFragment;
import personal.rowan.paperforspotify.ui.fragment.FriendsListFragment;

public class MainActivityViewPagerAdapter
		extends FragmentStatePagerAdapter {

	private static final int NUM_PAGES = 2;

	public static final int POSITION_CHAT_LIST = 0;
	public static final int POSITION_FRIENDS_LIST = 1;

	SparseArray<Fragment> registeredFragments = new SparseArray<>();

	public MainActivityViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch(position) {
			case POSITION_FRIENDS_LIST:
				return new FriendsListFragment();
			case POSITION_CHAT_LIST:
			default:
				return new ChatListFragment();
		}
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}

	public int getDrawableResourceId(int position) {
		switch (position) {
			case POSITION_FRIENDS_LIST:
				return R.drawable.ic_people_white_24dp;
			case POSITION_CHAT_LIST:
			default:
				return R.drawable.ic_message_white_24dp;
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = (Fragment) super.instantiateItem(container, position);
		registeredFragments.put(position, fragment);
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		registeredFragments.remove(position);
		super.destroyItem(container, position, object);
	}

	private Fragment getRegisteredFragment(int position) {
		return registeredFragments.get(position);
	}

	public ChatListFragment getChatListFragment() {
		return (ChatListFragment) getRegisteredFragment(POSITION_CHAT_LIST);
	}

	public FriendsListFragment getFriendsListFragment() {
		return (FriendsListFragment) getRegisteredFragment(POSITION_FRIENDS_LIST);
	}

}
