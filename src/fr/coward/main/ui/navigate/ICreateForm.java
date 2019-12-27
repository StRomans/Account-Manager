package fr.coward.main.ui.navigate;

public interface ICreateForm {
	
	/**
	 * Effectue l'initialisation des valeurs des composants du formulaire
	 * @param isFirstInit
	 */
	public void initComponents(boolean isFirstInit);
	
	/**
	 * Effectue la validation du formulaire. Lance une exception si une donn�e est invalide
	 * @throws Exception
	 */
	public void validate() throws Exception;
	
	/**
	 * D�clenche la sauvegarde du formulaire
	 */
	public boolean save();
}
