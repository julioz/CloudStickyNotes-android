package br.com.zynger.android.cloudstickynotes;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.Card.OnLongCardClickListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.List;

import net.louislam.android.InputListener;
import net.louislam.android.L;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import br.com.zynger.android.cloudstickynotes.model.Note;
import br.com.zynger.android.cloudstickynotes.task.GetNotesTask.NotesDownloadable;
import br.com.zynger.android.cloudstickynotes.util.DropboxHelper;
import br.com.zynger.android.cloudstickynotes.view.NoteCard;

public class NoteListFragment extends BaseFragment {

	private CardListView listView;

	@Override
	public int getTitleResourceId() {
		return R.string.app_name;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_notelist, container, false);
		listView = (CardListView) view.findViewById(R.id.fragment_notelist_listview);
		
		setNotes();
		return view;
	}

	private void setListAdapter(List<Note> notes) {
		CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), buildCardList(notes));
		listView.setAdapter(mCardArrayAdapter);
	}

	private ArrayList<Card> buildCardList(List<Note> notes) {
		ArrayList<Card> cards = new ArrayList<Card>();
		for (int i = 0; i < notes.size(); i++) {
			final Note note = notes.get(i);
			NoteCard card = new NoteCard(getActivity(), note);
			addCardListeners(note, card);
			cards.add(card);
		}
		return cards;
	}

	private void addCardListeners(final Note note, NoteCard card) {
		card.setOnClickListener(new OnCardClickListener() {
			@Override
			public void onClick(Card card, final View view) {
				final Context c = view.getContext();
				L.inputDialog(c, c.getString(R.string.editnote), null, new InputListener() {
					@Override
					public void inputResult(String result) {
						if (result.trim().equals("")) {
							Toast.makeText(c, c.getString(R.string.editnote_emptyvalue), Toast.LENGTH_SHORT).show();
							return;
						}
						
						if (result.trim().equals(note.getText().trim())) {
							return;
						}
						
						if (getActivity() instanceof NotesDownloadable) {
							NotesDownloadable delegate = (NotesDownloadable) getActivity();
							DropboxHelper db = delegate.getDropboxHelper();
							db.editNoteText(note, result);
							setListAdapter(db.getNotes());
						}
					}
				}, note.getText());
			}
		});
		
		card.setOnLongClickListener(new OnLongCardClickListener() {
			@Override
			public boolean onLongClick(Card card, View view) {
				Context c = view.getContext();
				L.confirmDialog(c, c.getString(R.string.confirmdialog_notedeletion), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							if (getActivity() instanceof NotesDownloadable) {
								NotesDownloadable delegate = (NotesDownloadable) getActivity();
								DropboxHelper db = delegate.getDropboxHelper();
								db.deleteNote(note);
								setListAdapter(db.getNotes());
							}
						} else {
							dialog.dismiss();
						}
					}
				});
				return true;
			}
		});
	}

	public void setNotes() {
		if (getActivity() instanceof NotesDownloadable) {
			NotesDownloadable delegate = (NotesDownloadable) getActivity();
			DropboxHelper db = delegate.getDropboxHelper();
			setListAdapter(db.getNotes());
		}
	}

}