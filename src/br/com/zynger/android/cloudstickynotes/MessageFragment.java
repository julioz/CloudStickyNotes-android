package br.com.zynger.android.cloudstickynotes;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageFragment extends BaseFragment {

	private final static String BUNDLE_TITLE_RES = "title";
	private final static String BUNDLE_IMAGE_RES = "image";
	private final static String BUNDLE_MESSAGE_RES = "message";
	
	private int titleResourceId;
	private int imageResourceId;
	private int messageResourceId;
	private ObjectAnimator imageAnimator;

	public static MessageFragment newInstance(int titleRes, int imageRes, int messageRes) {
		MessageFragment frag = new MessageFragment();

		Bundle args = new Bundle();
		args.putInt(BUNDLE_TITLE_RES, titleRes);
		args.putInt(BUNDLE_IMAGE_RES, imageRes);
		args.putInt(BUNDLE_MESSAGE_RES, messageRes);
	    frag.setArguments(args);
	    return frag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle args = getArguments();
		titleResourceId = args.getInt(BUNDLE_TITLE_RES, 0);
		imageResourceId = args.getInt(BUNDLE_IMAGE_RES, 0);
		messageResourceId = args.getInt(BUNDLE_MESSAGE_RES, 0);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_message, null);

		ImageView iv = (ImageView) view.findViewById(R.id.fragment_message_image);
		TextView tvTitle = (TextView) view.findViewById(R.id.fragment_message_title);
		TextView tvMessage = (TextView) view.findViewById(R.id.fragment_message_message);
		
		iv.setImageResource(imageResourceId);
		tvTitle.setText(titleResourceId);
		tvMessage.setText(messageResourceId);
		
		if (this.imageAnimator != null) {
			imageAnimator.setTarget(iv);
			imageAnimator.start();
		}
		
		return view;
	}
	
	public void setImageAnimator(ObjectAnimator imageAnimator) {
		this.imageAnimator = imageAnimator;
	}
	
	@Override
	public int getTitleResourceId() {
		return R.string.app_name;
	}

}
