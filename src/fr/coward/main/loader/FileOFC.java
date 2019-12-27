package fr.coward.main.loader;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import fr.coward.main.model.Transaction;
import fr.coward.main.ui.utils.StringUtil;

public class FileOFC implements FileLoader {
	
	private static final String TRANSACTION_TAG_STARTER = "<STMTTRN>";
	private static final String TRANSACTION_TAG_ENDER = "</STMTTRN>";
	
	private static final String TRANSACTION_TAG_DATE = "<DTPOSTED>";
	private static final String TRANSACTION_TAG_AMOUNT = "<TRNAMT>";
	private static final String TRANSACTION_TAG_LABEL = "<NAME>";
	private static final String TRANSACTION_CHQ_TAG_LABEL = "<CHKNUM>";
	private static final String TRANSACTION_TAG_ID = "<FITID>";
	
	private static final String FILE_TAG_STARTDATE = "<DTSTART>";
	private static final String FILE_TAG_ENDDATE = "<DTEND>";
	
	private Date startDate;
	private Date endDate;
	private List<String> content;
	private SimpleDateFormat dateFormatter;

	private FileOFC(){
		this.setContent(new LinkedList<String>());
		this.setDateFormatter(new SimpleDateFormat("yyyyMMdd"));
	}

	public FileOFC(String fileName) throws IOException{
		this();
		try {
			this.setContent(Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8));
		} catch(MalformedInputException e){
			try {
				this.setContent(Files.readAllLines(Paths.get(fileName), StandardCharsets.ISO_8859_1));
			} catch(MalformedInputException nestedEx){
				try {
					this.setContent(Files.readAllLines(Paths.get(fileName), StandardCharsets.US_ASCII));
				} catch(MalformedInputException nestedEx2){
					throw new IOException("Unnown charset. Try converting the file into UTF-8 charset.",nestedEx2);
				}
			}
		}
	}
	
	public List<Transaction> parse(){
		
		LinkedList<Transaction> foundTransactions = new LinkedList<>();
		
		Transaction transaction = null;
		for(String line : this.getContent()){
			
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
				transaction.setAmount(Double.parseDouble(value));
				break;
				
			case TRANSACTION_TAG_LABEL:
				transaction.setLabel(value);
				break;
				
			case TRANSACTION_TAG_ID :
				transaction.setIdentifier(value);
				break;
				
			case TRANSACTION_CHQ_TAG_LABEL:
				if(StringUtil.isNullOrEmpty(transaction.getLabel())){
					transaction.setLabel(value);
				}
				break;
				
			case TRANSACTION_TAG_ENDER:
				foundTransactions.add(transaction);
				break;
				
			default:
				break;
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
