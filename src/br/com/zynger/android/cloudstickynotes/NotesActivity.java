package br.com.zynger.android.cloudstickynotes;

import java.util.List;

import net.louislam.android.InputListener;
import net.louislam.android.L;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import br.com.zynger.android.cloudstickynotes.model.Note;
import br.com.zynger.android.cloudstickynotes.task.GetNotesTask;
import br.com.zynger.android.cloudstickynotes.task.GetNotesTask.NotesDownloadable;
import br.com.zynger.android.cloudstickynotes.util.DropboxHelper;

public class NotesActivity extends Activity implements NotesDownloadable {

	private DropboxHelper dbHelper;

	private NoteListFragment noteListFragment;
	private MessageFragment loadingMessageFragment;
	private MessageFragment loginMessageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DropboxHelper(this);
        setContentView(R.layout.activity_notes);

        noteListFragment = new NoteListFragment();
        loadingMessageFragment = MessageFragment.newInstance(R.string.loading_title, R.drawable.img_hourglass, R.string.loading_message);
        loginMessageFragment = MessageFragment.newInstance(R.string.pleaselogin_title, R.drawable.img_lock, R.string.pleaselogin_message);
        
        ObjectAnimator loadingAnimator = ObjectAnimator.ofFloat(null, "rotation", 0f, 360f).setDuration(2500);
        loadingMessageFragment.setImageAnimator(loadingAnimator);
    }
    
    private void replaceFragment(Fragment fragment) {
    	FragmentManager fragmentManager = getFragmentManager();
    	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    	fragmentTransaction.replace(R.id.activity_notes_fragmentholder, fragment);
    	fragmentTransaction.commit();
	}

    @Override
    protected void onResume() {
        super.onResume();
        dbHelper.onResumeContext();
        
        if (dbHelper.isLoggedIn()) {
        	new GetNotesTask(this).execute();
        } else {
        	replaceFragment(loginMessageFragment);
        }
    }
    
	@Override
	public DropboxHelper getDropboxHelper() {
		return dbHelper;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notes, menu);
        
        MenuItem loginItem = menu.findItem(R.id.action_login);
        MenuItem addNoteItem = menu.findItem(R.id.action_add);
        if (dbHelper.isLoggedIn()) {
        	loginItem.setTitle(R.string.action_logout);
        	addNoteItem.setVisible(true);
        } else {
        	loginItem.setTitle(R.string.action_login);
        	addNoteItem.setVisible(false);
        }
        
        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.action_add:
        		createNote();
        		return true;
            case R.id.action_login:
            	if (dbHelper.isLoggedIn()) {
            		L.confirmDialog(this, getString(R.string.confirmlogout), new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								dbHelper.logout();
								replaceFragment(loginMessageFragment);
								invalidateOptionsMenu();
							} else {
								dialog.dismiss();
							}
						}
					});
                } else {
                    dbHelper.login();
                    invalidateOptionsMenu();
                }
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

	private void createNote() {
		L.inputDialog(this, getString(R.string.action_newnote), null, new InputListener() {
			@Override
			public void inputResult(String result) {
				if (result.trim().equals("")) {
					Toast.makeText(NotesActivity.this, getString(R.string.editnote_emptyvalue), Toast.LENGTH_SHORT).show();
					return;
				}
				
				Note note = createNoteInternal();
				note.setText(result);
				dbHelper.addNote(note);
				noteListFragment.setNotes();
			}
		});
	}
	
	public Note createNoteInternal() {
        Note note = new Note();
        note.setId(String.valueOf(System.currentTimeMillis()));
        Note.Location location = new Note.Location();
        location.setLocation(10, 10);
        note.setLocation(location);
        Note.Size size = new Note.Size();
        size.setSize(150, 150);
		note.setSize(size);
        note.setColor(Note.Color.YELLOW);
        return note;
    }

	@Override
	public void willDownloadNotes() {
		replaceFragment(loadingMessageFragment);
	}

	@Override
	public void hasDownloadedNotes(List<Note> notes) {
		invalidateOptionsMenu();
		for (Note note : notes) {
			Log.e("CloudStickyNotes", note.toString());
		}
		dbHelper.setNotes(notes);
        replaceFragment(noteListFragment);
	}
}
