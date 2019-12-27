package fr.coward.main.loader;

import java.util.List;

import fr.coward.main.model.Transaction;

public interface FileLoader {

	public List<Transaction> parse();
}
