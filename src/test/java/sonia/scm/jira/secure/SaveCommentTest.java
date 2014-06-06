package sonia.scm.jira.secure;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;

public class SaveCommentTest {
	
	private String author;
	private Calendar created;
	private String roleLevel;
	private String token;
	private String body;
	private String issueId;
	private String jiraUrl;
	
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
		jiraUrl = "https://km.test.de/jira";
		
		saveComment = new SaveComment();
		
		Changeset changeset = new Changeset();
		changeset.setDate(created.getTimeInMillis());
		changeset.setId(issueId);
		changeset.setDescription(body);
		Repository repository = null;
		commentData = new CommentData(author, body, created, issueId, roleLevel, token, jiraUrl, changeset, repository);
		expectedFileName = "comments/" + author + "_" + issueId + "_" + created.getTimeInMillis() + ".xml";
	}
	
	@Test
	public void testGetFileName() {
		String actualFileName = SaveComment.getFileName(commentData, "comments/");
		assertEquals("The file name is not correctly build.", expectedFileName, actualFileName);
	}
	
	@Test
	public void testSave() throws JiraSaveCommentException, IOException {
		saveComment.save(commentData, "comments/");
		
		File file = new File(expectedFileName);
		assertTrue("The file does not exist", file.exists());
		
		file.delete();
	}
}
