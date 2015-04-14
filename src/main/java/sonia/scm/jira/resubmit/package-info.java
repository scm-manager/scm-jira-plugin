/**
 * The resubmit package provide store and resubmit functionalities.
 * The resubmit package provide functionalities to store and resubmit JIRA 
 * comments, which could not be added to JIRA, because of an error. Failed 
 * comments are stored with DataStore and could later be send again by calling
 * the {@link ResubmitCommentsResource}.
 */
package sonia.scm.jira.resubmit;