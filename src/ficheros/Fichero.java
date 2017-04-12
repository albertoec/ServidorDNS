package ficheros;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Fichero {

	private File file;
	private HashMap<Integer, ArrayList<String>> answers = new HashMap<>();
	private HashMap<Integer, ArrayList<String>> questions = new HashMap<>();

	public Fichero(File file) {
		this.file = file;
	}

	public Fichero(File file, HashMap<Integer, ArrayList<String>> answers, HashMap<Integer, ArrayList<String>> questions) {
		super();
		this.file = file;
		this.answers = answers;
		this.questions = questions;
	}

	public void inicialScreen(){
		
	}
	
	public void readQuestions() {

		try {
			BufferedReader bf = new BufferedReader(new FileReader(this.file));
			ArrayList<String> infoConsultas = new ArrayList<>();
			String line = "";
			int i = 0;

			while ((line = bf.readLine()) != null) {
				//if (line.contains("#"))
					//continue;
				
				String[] splitLine = line.split("\\s* \\s*");
				//Añadimos los campos RRTipe y NOMBRE al ArrayList en ese orden
				for (String part : splitLine) {
					infoConsultas.add(part);
					
				}
				
				questions.put(i, infoConsultas);
				i++;
			}
			
			bf.close();
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public HashMap<Integer, ArrayList<String>> getAnswers() {
		return answers;
	}

	public void setAnswers(HashMap<Integer, ArrayList<String>> answers) {
		this.answers = answers;
	}

	public HashMap<Integer, ArrayList<String>> getQuestions() {
		return questions;
	}

	public void setQuestions(HashMap<Integer, ArrayList<String>> questions) {
		this.questions = questions;
	}

}
