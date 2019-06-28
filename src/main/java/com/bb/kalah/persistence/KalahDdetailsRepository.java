/**
 * This acts as repository which although provides all inbuilt operation of JPA repo along with provides
 * extended feature to implement any user define query for the database.
 * @author Sambed
 * @date 23/05/2019
 * @date last update - 
 * @change by - 
 */

package com.bb.kalah.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bb.kalah.persistence.model.KalahDetails;

@Repository
public interface KalahDdetailsRepository extends JpaRepository<KalahDetails, Long> {

	/**
	 * Here we are going to get max game number and url.
	 * @param
	 * @return List<KalahDetails>
	 */
	@Query("select kd.id, kd.uri from KalahDetails kd")
	List<KalahDetails> getKalahGameId();
	
	/**
	 * This gives all the active games which are waiting to join the opponent player.
	 * @param
	 * @return List<KalahDetails>
	 */
	@Query("select kd from KalahDetails kd where kd.userTwoId = '' or kd.userTwoId is NULL")
	List<KalahDetails> getKalahActiveGame();
	
	/**
	 * This gives the maximum game id recorded in database .
	 * @param
	 * @return Long
	 */
	@Query("select coalesce(max(kd.id), 0) from KalahDetails kd  where kd.id < 1000 ")
	Long getMaxId();
}
