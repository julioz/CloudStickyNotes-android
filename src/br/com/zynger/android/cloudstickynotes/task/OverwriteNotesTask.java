package br.com.zynger.android.cloudstickynotes.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import br.com.zynger.android.cloudstickynotes.R;
import br.com.zynger.android.cloudstickynotes.model.Note;
import br.com.zynger.android.cloudstickynotes.util.DropboxHelper;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OverwriteNotesTask extends AsyncTask<Void, Void, Boolean> {
	private List<Note> notes;
	private DropboxAPI<AndroidAuthSession> mApi;
	private Context mContext;

	public OverwriteNotesTask(List<Note> notes, DropboxHelper db) {
		this.notes = notes;
		this.mContext = db.getContext();
		this.mApi = db.getAPI();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			File file = File.createTempFile(GetNotesTask.FILENAME, null);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.append(buildJsonString(notes));
			writer.close();
			
			FileInputStream inputStream = new FileInputStream(file);
			try {
				mApi.putFileOverwrite("/" + GetNotesTask.FILENAME, inputStream,
						file.length(), null);
				file.deleteOnExit();
				file.delete();
			} finally {
				inputStream.close();
			}
			return true;
		} catch (IOException e) {
			Toast.makeText(mContext, "Could not upload JSON to server", Toast.LENGTH_SHORT).show();
		} catch (DropboxException d) {
			Toast.makeText(mContext, "Could not upload JSON to server", Toast.LENGTH_SHORT).show();
		}
		
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			Toast.makeText(mContext, R.string.uploadsuccessful, Toast.LENGTH_SHORT).show();
		}
		
		super.onPostExecute(result);
	}

	private String buildJsonString(List<Note> notes) {
		ObjectMapper jsonMapper = new ObjectMapper();
		StringBuffer buffer = new StringBuffer("{\"notes\": [");
		int index = 0;
		for (Note note : notes) {
			try {
				buffer.append(jsonMapper.writeValueAsString(note));
				index++;
				if (index < notes.size()) {
					buffer.append(',');
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		buffer.append("]}");
		return buffer.toString();
	
	}

}
