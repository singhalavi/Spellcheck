package org.hs18.vaadin.spellcheck;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ClientConnector.AttachEvent;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.ui.TextField;

public class SpellCheck  implements SpellCheckListener{
	private static String defaultDicFile = "/dict/english.0";
	private SpellChecker spellChecker = null;
	private static SpellDictionary defaultDictionary = null;
	
	public SpellCheck() throws FileNotFoundException, IOException, URISyntaxException {
		spellChecker = new SpellChecker(getDefaultDictionary());
		spellChecker.addSpellCheckListener(this);
	}
	
	public SpellCheck(SpellDictionary dictionary){
		spellChecker = new SpellChecker(dictionary);
		spellChecker.addSpellCheckListener(this);
	}
	
	public boolean isCorrect(String line){
		try{
			spellChecker
			.checkSpelling(new StringWordTokenizer(line));
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public void addSpellCheck(final TextField textField){
		
		textField.addAttachListener(new AttachListener() {
			
			@Override
			public void attach(AttachEvent event) {
				if(textField.getValue() != null && !textField.getValue().isEmpty()){
					if(isCorrect(textField.getValue())){
						textField.removeStyleName("spellcheck-error");
					}else{
						textField.addStyleName("spellcheck-error");
					}
				}
			}
		});
		
		textField.addTextChangeListener(new TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				if(event.getText() != null && !event.getText().isEmpty()){
					if(isCorrect(event.getText())){
						textField.removeStyleName("spellcheck-error");
					}else{
						textField.addStyleName("spellcheck-error");
					}
				}else{
					textField.removeStyleName("spellcheck-error");
				}
			}
		});
	}
	
	private SpellDictionary getDefaultDictionary() throws FileNotFoundException, IOException, URISyntaxException{
		if(defaultDictionary == null){
			synchronized (defaultDicFile) {
				if(defaultDictionary == null)
				{
					InputStream is = SpellCheck.class.getResourceAsStream(defaultDicFile);
					SpellDictionary dictionary;
					dictionary = new SpellDictionaryHashMap(new InputStreamReader(is));
					defaultDictionary =  dictionary;
				}
			}
		}
		return defaultDictionary;
	}

	public SpellChecker getSpellChecker() {
		return spellChecker;
	}

	public void setSpellChecker(SpellChecker spellChecker) {
		this.spellChecker = spellChecker;
	}

	@Override
	public void spellingError(SpellCheckEvent event) {
		List suggestions = event.getSuggestions();
		if (suggestions.size() > 0) {
			System.out.println("MISSPELT WORD: "
					+ event.getInvalidWord());
			for (Iterator suggestedWord = suggestions.iterator(); suggestedWord
					.hasNext();) {
				System.out.println("\tSuggested Word: "
						+ suggestedWord.next());
			}
		} else {
			System.out.println("MISSPELT WORD: "
					+ event.getInvalidWord());
			System.out.println("\tNo suggestions");
		}
		// Null actions
	}
	
	public boolean addToDictionary(String word) {
		return spellChecker.addToDictionary(word);
	}
	
	public void addDictionary(SpellDictionary dictionary){
		spellChecker.addDictionary(dictionary);
	}
}
