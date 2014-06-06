package sonia.scm.jira.secure;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import sonia.scm.jira.secure.CommentData;

public class ExtractComment {

	public ExtractComment() {
		
	}

	/**
	 * Extract the data for a comment given in the xml file.
	 * @param file The file to extract the data from.
	 * @return The extracted comment data.
	 * @throws JAXBException
	 */
	public CommentData getCommentFromXML(File file) throws JAXBException {
		
		//Create JAXB Content
		JAXBContext jaxbContext = JAXBContext.newInstance(CommentData.class);
		
		//Read from xml
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		CommentData commentData = (CommentData) unmarshaller.unmarshal(file);
		
		return commentData;
	}
	
	/**
	 * Get all comments in the given directory.
	 * @param directory The given directory to search in.
	 * @return A list of comment data extracted from the files in the given directory.
	 * @throws JAXBException
	 */
	public List<CommentData> getAllComments(String directory) {
		List<CommentData> allComments = new ArrayList<CommentData>();
		
		File dir = new File(directory);
		if(dir.isDirectory()) {
			File[] fileList = dir.listFiles();
			for(File file : fileList) {
				try {
					allComments.add(getCommentFromXML(file));
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			}
		}
		
		return allComments;
	}
	
	public boolean deleteCommentFile(String filePath) {
		File file = new File(filePath);
		if(file.exists()) {
			return file.delete();
		} else {
			return false;
		}
	}
}
