package sonia.scm.jira.secure;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class SaveComment {
	
	public static String SAVE_COMMENTS_PATH = "comments/";	//TODO: Set folder via properties
	
	/**
	 * Save the given comment using JAXB.
	 * @param commentData The data of the given comment to save.
	 * @throws JiraSaveCommentException The saving of the commentData went wrong.
	 */
	public void save(CommentData commentData) throws JiraSaveCommentException {
		FileOutputStream fileOutputStream = null;
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CommentData.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			fileOutputStream = new FileOutputStream(getFileName(commentData));
			marshaller.marshal(commentData, fileOutputStream);
			
		} catch (JAXBException jaxbException) {
			throw new JiraSaveCommentException("JAXB could not marshall the xml file", jaxbException);
		} catch (FileNotFoundException fileNotFoundException) {
			throw new JiraSaveCommentException("The xml file could not be created", fileNotFoundException);
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException ioException) {
				throw new JiraSaveCommentException("The file stream could not be closed", ioException);
			}
		}
	}
	
	/**
	 * Returns a unique file name with the used file path.
	 * @param commentData The data used to create a unique name.
	 * @return file name with file path.
	 */
	public String getFileName(CommentData commentData) {
		String fileName = commentData.getAuthor() + "_" + commentData.getIssueId() + "_" + commentData.getCreated().getTimeInMillis() + ".xml";
		
		return SAVE_COMMENTS_PATH + fileName;
	}
}
