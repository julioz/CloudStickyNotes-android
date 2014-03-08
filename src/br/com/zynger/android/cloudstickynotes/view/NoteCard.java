package br.com.zynger.android.cloudstickynotes.view;

/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

import it.gmariotti.cardslib.library.internal.Card;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.zynger.android.cloudstickynotes.R;
import br.com.zynger.android.cloudstickynotes.model.Note;

public class NoteCard extends Card {

	private final Note note;

	public NoteCard(Context context, Note note) {
		super(context, R.layout.view_cardcolor_inner_base_main);
		this.note = note;
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {
		TextView title = (TextView) parent
				.findViewById(R.id.card_color_inner_simple_title);

		if (title != null) {
			title.setText(note.getText());
		}
		
		switch (note.getColor()) {
		case YELLOW:
			setBackgroundResourceId(R.drawable.card_selector_yellow);
			break;
		case PURPLE:
			setBackgroundResourceId(R.drawable.card_selector_purple);
			break;
		case BLUE:
			setBackgroundResourceId(R.drawable.card_selector_blue);
			break;
		case GREEN:
			setBackgroundResourceId(R.drawable.card_selector_green);
			break;
		case RED:
			setBackgroundResourceId(R.drawable.card_selector_red);
			break;
		default:
			break;
		}

	}
}
