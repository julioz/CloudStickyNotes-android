package br.com.zynger.android.cloudstickynotes.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import br.com.zynger.android.cloudstickynotes.model.Note;
import br.com.zynger.android.cloudstickynotes.util.DropboxHelper;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {
	
	public static final String FILENAME = "cloudstickynotes.json";
	
	public interface NotesDownloadable {
		DropboxHelper getDropboxHelper();
		void willDownloadNotes();
		void hasDownloadedNotes(List<Note> notes);
	}

	private DropboxAPI<AndroidAuthSession> mDBApi;
	private Context mContext;
	private NotesDownloadable delegate;

	public GetNotesTask(NotesDownloadable delegate) {
		this.delegate = delegate;
		this.mDBApi = delegate.getDropboxHelper().getAPI();
		this.mContext = delegate.getDropboxHelper().getContext();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		delegate.willDownloadNotes();
	}

	@Override
	protected List<Note> doInBackground(Void... params) {
		try {
			mDBApi.metadata("/" + FILENAME, 1, null, false, null);
			File file = getFile();
			String content = readFile(file);
			return parseNotes(content);
		} catch (DropboxException d) {
			d.printStackTrace();
			Toast.makeText(mContext, "Could not parse JSON from server", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(mContext, "Could not parse JSON from server", Toast.LENGTH_SHORT).show();
		}
		return null;
	}
	
	private List<Note> parseNotes(String content) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper jsonMapper = new ObjectMapper();
		JsonNode node = jsonMapper.readTree(content).get("notes");
		TypeReference<List<Note>> typeRef = new TypeReference<List<Note>>() { };
		List<Note> notes = jsonMapper.readValue(node.traverse(), typeRef);
		return notes;
	}

	@Override
	protected void onPostExecute(List<Note> result) {
		super.onPostExecute(result);
		delegate.hasDownloadedNotes(result);
	}
	
	private String readFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}

		reader.close();
		return stringBuilder.toString();
	}
	
	private File getFile() throws DropboxException, IOException {
		File file = new File(mContext.getCacheDir().getAbsolutePath() + "/"
				+ FILENAME);
		FileOutputStream outputStream = new FileOutputStream(file);
		mDBApi.getFile("/" + FILENAME, null, outputStream, null);
		outputStream.close();
		return file;
	}
}
