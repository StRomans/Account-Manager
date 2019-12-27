package fr.coward.main.theme;

public enum Theme {
	
	BLEU("blue.css"), 
	CLASSIQUE("classic.css");
	
	private String fileName;
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	Theme(String fileName){
		this.fileName = fileName;
	}
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
	
	public static Theme getFromFileName(String fileName){
		
		for(Theme theme : Theme.values()){
			if(theme.getFileName().equals(fileName)){
				return theme;
			}
		}
		
		return null;
	}
}
