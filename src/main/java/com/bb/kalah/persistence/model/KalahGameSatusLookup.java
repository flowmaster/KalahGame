/**
 * This is an ORM and keeps the detail for game status which act as a look up table on each.
 * @author Sambed
 * @date 23/05/2019
 * @date last update - 
 * @change by -
 */
package com.bb.kalah.persistence.model;

import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@ApiModel(description="All details about the BB_KALAH_GAME_STATUS_LOOKUP ")
public class KalahGameSatusLookup {
	
	/**
	 * This is integer value which refers to a status of game .And this is auto populate with this is primary key.
	 */ 
	@Id
	private int statusId;
	
	/**
	 * This is the status which was represented above as pending or complete .
	 */
	private String status;
	
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
