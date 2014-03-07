package sonia.scm.jira.secure;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

public class SaveCommentTest {
	
	private String author;
	private Calendar created;
	private String roleLevel;
	private String token;
	private String body;
	private String issueId;
	
	private SaveComment saveComment;
	
	private CommentData commentData;
	private String expectedFileName;
	
	@Before
	public void init() {
		author = "Max Meier";
		created = new GregorianCalendar();
		roleLevel = "Developers";
		token = "TestToken";
		body = "This is a test comment.";
		issueId = "TEST-42";
		
		saveComment = new SaveComment();
		
		commentData = new CommentData(author, body, created, issueId, roleLevel, token);
		expectedFileName = SaveComment.SAVE_MESSAGE_PATH + author + "_" + issueId + "_" + created.getTimeInMillis() + ".xml";
	}
	
	@Test
	public void testGetFileName() {
		String actualFileName = saveComment.getFileName(commentData);
		assertEquals("The file name is not correctly build.", expectedFileName, actualFileName);
	}
	
	@Test
	public void testSave() throws JiraSaveCommentException, IOException {
		saveComment.save(commentData);
		
		File file = new File(expectedFileName);
		assertTrue("The file does not exist", file.exists());
		
		file.delete();
	}
}
