/**
 * This is the entity class which works as an ORM object for the database operation.
 * Here used the embedded database , so this class contains the table detail and and relation to model.
 * It contains column like 
 * id ( gameid - primary key)
 * userOne ,userTwo - the game can be played uniquely by two player
 * statusDetail - contains all the marble count on each pit at any instance of time
 * gameStatus - By this we can identify the archive game status
 * nextTurn - The next turn going to be for which house
 * @author Sambed
 * @date 23/05/2019
 * @date last update - 
 * @change by - 
 */

package com.bb.kalah.persistence.model;

import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.bb.kalah.persistence.model.view.View;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiModel;

@Entity
@ApiModel(description="All details about the BB_KALAH_DETAILS ")
public class KalahDetails {
	
	public KalahDetails() {
		super();
	}

	public KalahDetails(Long id, String userOneId, String userTwoId,String uri,int statusId) {
		super();
		this.id = id;
		this.userOneId = userOneId;
		this.userTwoId = userTwoId;
		this.uri = uri;
		this.statusId = statusId;
	}
	
	public KalahDetails(String userOneId, String userTwoId,String uri,int statusId) {
		super();
		this.userOneId = userOneId;
		this.userTwoId = userTwoId;
		this.uri = uri;
		this.statusId = statusId;
	}
	
	public KalahDetails(Long id, String uri,Map<Integer,Integer> status){
		super();
		this.id=id;
		this.uri=uri;
		this.status=status;
	}
	
	/**
	 * This is used for the game id which is auto increment value with one.
	 */
	@Id
	@GeneratedValue
	@JsonView(View.GameCreationView.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long id;
	
	/**
	 * This is used to identify one user and keeps its ip as the identifier
	 */
	@JsonIgnore
	private String userOneId;
	
	/**
	 * This is used to identify opponent user and keeps its ip as the identifier
	 */
	@JsonIgnore
	private String userTwoId;
	
	/**
	 * This is the string variable populate the url.
	 */
	@JsonView(View.GameCreationView.class)
	private String uri;
	
	/**
	 * This maintains the status of game and respective look up table exists ,Kalah_Game_Satus_Lookup .
	 */
	private int statusId;
	
	/**
	 * This contains the next turn of game , the house name whose needs to play.
	 */
	@JsonIgnore
	private String nextTurnHouse ;
	
	/**
	 * This populate the count of marble in each pit for each iteration which includes SOUTH and NORTH kalah too.
	 */
	@ElementCollection
	@JsonView(View.StatusView.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Map<Integer,Integer> status;
	

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserOneId() {
		return userOneId;
	}
	public void setUserOneId(String userOneId) {
		this.userOneId = userOneId;
	}
	public String getUserTwoId() {
		return userTwoId;
	}
	public void setUserTwoId(String userTwoId) {
		this.userTwoId = userTwoId;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	
	public Map<Integer, Integer> getStatus() {
		return status;
	}

	public void setStatus(Map<Integer, Integer> status) {
		this.status = status;
	}
	public String getNextTurnHouse() {
		return nextTurnHouse;
	}

	public void setNextTurnHouse(String nextTurnHouse) {
		this.nextTurnHouse = nextTurnHouse;
	}
}
