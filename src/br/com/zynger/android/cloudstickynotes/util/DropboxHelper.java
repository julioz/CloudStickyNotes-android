package br.com.zynger.android.cloudstickynotes.util;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;
import br.com.zynger.android.cloudstickynotes.Constants;
import br.com.zynger.android.cloudstickynotes.model.Note;
import br.com.zynger.android.cloudstickynotes.task.OverwriteNotesTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

public class DropboxHelper {
	
	final static private String APP_KEY = Constants.DROPBOX_APP_KEY;
	final static private String APP_SECRET = Constants.DROPBOX_APP_SECRET;

	final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    private static final boolean USE_OAUTH1 = false;
	private static final String TAG = "CloudStickyNotes";

    private DropboxAPI<AndroidAuthSession> mApi;
    private boolean mLoggedIn;
	private Context mContext;
	private List<Note> notes;
    
    public DropboxHelper(Context context) {
		this.mContext = context;
		
		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI<AndroidAuthSession>(session);
		
		mLoggedIn = mApi.getSession().isLinked();
	}
	
	private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        
        SharedPreferences prefs = mContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (!(key == null || secret == null || key.length() == 0 || secret.length() == 0)) {
        	if (key.equals("oauth2:")) {
        		// If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
        		session.setOAuth2AccessToken(secret);
        	} else {
        		// Still support using old OAuth 1 tokens.
        		session.setAccessTokenPair(new AccessTokenPair(key, secret));
        	}
        }
        
        return session;
    }
	
	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}
	
	public List<Note> getNotes() {
		return notes;
	}
	
	public void onResumeContext() {
		AndroidAuthSession session = mApi.getSession();

        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                storeAuth(session);
                mLoggedIn = true;
			} catch (IllegalStateException e) {
				Toast.makeText(
						mContext,
						"Couldn't authenticate with Dropbox:"
								+ e.getLocalizedMessage(), Toast.LENGTH_LONG)
						.show();
				Log.i(TAG, "Error authenticating", e);
            }
        }
	}
	
	/**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = mContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = mContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = mContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
    
    @SuppressWarnings("deprecation")
	public void login() {
    	if (USE_OAUTH1) {
    		mApi.getSession().startAuthentication(mContext);
    	} else {
    		mApi.getSession().startOAuth2Authentication(mContext);
    	}
    }
    
    public void logout() {
    	mApi.getSession().unlink();
    	clearKeys();
    	mLoggedIn = false;
    }
    
    public void addNote(Note note) {
    	notes.add(note);
    	overwriteFile();
    }
    
    public void deleteNote(Note note) {
    	notes.remove(note);
    	overwriteFile();
    }

    public void editNoteText(Note note, String newText) {
		for (Note n : notes) {
			if (n.getText().equals(note.getText())) {
				n.setText(newText);
			}
		}

		overwriteFile();
    }
    
    private void overwriteFile() {
		new OverwriteNotesTask(notes, this).execute();
	}

	public boolean isLoggedIn() {
		return mLoggedIn;
	}
    
    public DropboxAPI<AndroidAuthSession> getAPI() {
		return mApi;
	}
    
    public Context getContext() {
		return mContext;
	}
}
