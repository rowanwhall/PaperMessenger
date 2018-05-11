package personal.rowan.paperforspotify.ui.fragment.dialog;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.model.Chat;

public class ChangeChatNameDialogFragment
		extends BaseDialogFragment {

	public static final String TAG = "ChangeChatNameDialog";
	public static final String ARGS_ORIGINAL_CHAT_NAME = "ARGS_ORIGINAL_CHAT_NAME";

	private String mChatName;
	private TextWatcher mChatNameTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void afterTextChanged(Editable editable) {
			invalidateButton(editable.length());
		}
	};

	private TextInputEditText tietChatName;
	private Button btnPositive;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		if(args != null) {
			mChatName = args.getString(ARGS_ORIGINAL_CHAT_NAME);
		}
	}

	@Override
	protected int layoutResource() {
		return R.layout.dialog_frag_change_chat_name;
	}

	@Override
	protected void inflateLayout(View view) {
		tietChatName = (TextInputEditText) view.findViewById(R.id.dialog_frag_change_chat_name_tiet);
		tietChatName.setText(mChatName);
	}

	@Override
	protected void afterShown() {
		btnPositive = getPositiveButton();
		invalidateButton(tietChatName.getText().toString().length());
		tietChatName.addTextChangedListener(mChatNameTextWatcher);
	}

	@Override
	protected String title() {
		return getString(R.string.dialog_frag_change_chat_name_title);
	}

	@Override
	public String tag() {
		return TAG;
	}

	@Override
	protected String positiveButtonText() {
		return getString(R.string.dialog_frag_change_chat_name_positive_btn);
	}

	@Override
	protected String negativeButtonText() {
		return getString(R.string.dialog_frag_change_chat_name_negative_btn);
	}

	public String getName() {
		return tietChatName.getText().toString();
	}

	private void invalidateButton(int length) {
		boolean enabled = length > 0 && length <= Chat.MAX_CHAT_NAME_LENGTH;
		btnPositive.setEnabled(enabled);
		btnPositive.setAlpha(enabled ? 1f : .5f);
	}
}
