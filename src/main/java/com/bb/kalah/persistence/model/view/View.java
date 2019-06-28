/**
 * This is a view fragmentation class in a hierarchical structure .
 * Each view is meant for a response by this we are only expose to the detail which only required by the client.
 * GameCreationView - This only used for the create post request.
 * StatusView - this gives the kalah pits status details including all the requisite for make move api invocation.Status
 *              view is a child view of the GameCreationView .
 * @author Sambed
 * @date 23/05/2019
 * @date last update - 
 * @change by -
 */
package com.bb.kalah.persistence.model.view;

public class View {
	/**
	 *This view used start game interface and provides those detail only. 
	 */
	public interface GameCreationView {}
	
	/**
	 *This view provides the makeMove operational view which inherits creation view too. 
	 */
	public interface StatusView extends GameCreationView {}
}
