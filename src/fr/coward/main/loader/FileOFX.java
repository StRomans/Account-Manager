package fr.coward.main.loader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import fr.coward.main.model.Transaction;

public class FileOFX implements FileLoader {
	
	private static final String TRANSACTION_TAG_STARTER = "<STMTTRN>";
	private static final String TRANSACTION_TAG_ENDER = "</STMTTRN>";
	
	private static final String TRANSACTION_TAG_DATE = "<DTPOSTED>";
	private static final String TRANSACTION_TAG_AMOUNT = "<TRNAMT>";
	private static final String TRANSACTION_TAG_LABEL = "<NAME>";
	private static final String TRANSACTION_TAG_ID = "<FITID>";
	
	private static final String FILE_TAG_STARTDATE = "<DTSTART>";
	private static final String FILE_TAG_ENDDATE = "<DTEND>";
	
	private Date startDate;
	private Date endDate;
	private List<String> content;
	private SimpleDateFormat dateFormatter;

	private FileOFX(){
		this.setContent(new LinkedList<String>());
		this.setDateFormatter(new SimpleDateFormat("yyyyMMdd"));
	}

	public FileOFX(String fileName) throws IOException{
		this();
		
		try {
			loadAndNormalizeFile(fileName, StandardCharsets.UTF_8);
		} catch(MalformedInputException e){
			try {
				loadAndNormalizeFile(fileName, StandardCharsets.ISO_8859_1);
			} catch(MalformedInputException nestedEx){
				try {
					loadAndNormalizeFile(fileName, StandardCharsets.US_ASCII);
				} catch(MalformedInputException nestedEx2){
					throw new IOException("Unnown charset. Try converting the file into UTF-8 charset.",nestedEx2);
				}
			}
		}
	}
	
	private void loadAndNormalizeFile(String fileName, Charset charset) throws IOException{
		
		Path path = Paths.get(fileName);

		String fileContent = new String(Files.readAllBytes(path), charset);
		fileContent = fileContent.replaceAll("<", "\n<");
		Files.write(path, fileContent.getBytes(charset));
		
		this.setContent(Files.readAllLines(Paths.get(fileName), charset));
	}
	
	public List<Transaction> parse(){
		
		LinkedList<Transaction> foundTransactions = new LinkedList<>();
		
		Transaction transaction = null;
		for(String line : this.getContent()){
			
			// Ne pas parser l'entête
			if(line.contains("<") && line.contains(">")){
				// Extraire le tag via regex
				String tag = line.substring(line.indexOf("<"),line.indexOf(">") +1);
				String value = line.replaceFirst(tag, "");
				
				switch (tag) {
				
				case FILE_TAG_STARTDATE:
					this.setStartDate(this.getDate(value));
					break;
					
				case FILE_TAG_ENDDATE:
					this.setEndDate(this.getDate(value));
					break;
				
				case TRANSACTION_TAG_STARTER:
					transaction = new Transaction();
					break;
					
				case TRANSACTION_TAG_DATE:
					transaction.setDate(this.getDate(value));
					break;
				
				case TRANSACTION_TAG_AMOUNT:
					// pour remplacer 1000,00 par 1000.00
					String safeValue = value.replaceAll(",", ".");
					// pour remplacer 1 000.00 par 1000.00
					safeValue = safeValue.replaceAll(" ", "");
					transaction.setAmount(Double.parseDouble(safeValue));
					break;
					
				case TRANSACTION_TAG_LABEL:
					transaction.setLabel(value);
					break;
					
				case TRANSACTION_TAG_ID :
					transaction.setIdentifier(value);
					break;
					
				case TRANSACTION_TAG_ENDER:
					foundTransactions.add(transaction);
					break;
					
				default:
					break;
				}
			}
		}
		
		return foundTransactions;
	}
	
	/**
	 * @return the content
	 */
	public List<String> getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(List<String> content) {
		this.content = content;
	}

	/**
	 * @param simpleDateFormat
	 */
	private void setDateFormatter(SimpleDateFormat simpleDateFormat) {
		this.dateFormatter = simpleDateFormat;
	}
	
	/**
	 * @return the content
	 * @throws ParseException 
	 */
	private Date getDate(String strDate) {
		
		Date date = null;
		try {
			date = this.dateFormatter.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
