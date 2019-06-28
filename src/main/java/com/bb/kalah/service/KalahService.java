/**
 * This is a service class for which the request delegate from the controller and all embedded logic implemented here.
 * This also interacts with repository .All the events to repository done with the entity model or its attributes .
 * @author Sambed
 * @date 23/05/2019
 * @date last update - 
 * @change by -
 */

package com.bb.kalah.service;

import java.net.InetAddress;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bb.kalah.constants.SSRConstants;
import com.bb.kalah.exception.KalahException;
import com.bb.kalah.exception.OperationNotAllowedException;
import com.bb.kalah.persistence.KalahDdetailsRepository;
import com.bb.kalah.persistence.model.KalahDetails;


@Service
public class KalahService implements SSRConstants {
	private static final Logger LOGGER = LoggerFactory.getLogger(KalahService.class);

	/**
	 * This object helps in getting the request system host name detail
	 */
	@Autowired
	private HttpServletRequest request;

	/**
	 * Injecting the repository object
	 */
	@Autowired
	private KalahDdetailsRepository kalahRepo ;

	/**
	 * This method initialize the game and return the model to controller .
	 * @return KalahDetails
	 */
	public KalahDetails startGame()
	{
		KalahDetails savedKalahGame = kalahRepo.save(createGame());
		return savedKalahGame ;
	}

	/**
	 * This is the initial method to start sows operation in anticlockwise direction .
	 * This method comprise with all the Kalah game rules and its validation.
	 * @param id
	 * @param pitid
	 * @return KalahDetails
	 */
	public KalahDetails makeMove(long id,int pitid)
	{

		Optional<KalahDetails> kalahDetOpt = kalahRepo.findById(id);
		
		if (!kalahDetOpt.isPresent()){
			throw new KalahException(String.format(NOT_FOUND,id));
		}
		
		KalahDetails kalahDet = kalahDetOpt.get();
		nextTurnValidation(kalahDet,id,pitid);

		Map<Integer, Integer> updatedStatus = moveOperation(kalahDet.getStatus(), pitid);
		kalahDet = setNextHouse(updatedStatus,kalahDet);
		
		return kalahRepo.save(kalahDet) ;
	}

	/**
	 * This method perform check for whether the game is already completed or not then it through custom exception  
	 * Also check the present request for pit id movement is from valid house and if not then throws OperationNotAllowedException .
	 * @param kalahDet
	 * @param id
	 * @param pitid
	 */
	private void nextTurnValidation(KalahDetails kalahDet,long id,int pitid)
	{
		if(kalahDet.getStatusId() == GAME_COMPLETE){
			throw new OperationNotAllowedException(String.format(GAME_COMPLETED_MESSAGE,id));
		}

		if (NEXT_TURN_VALIDATION.equalsIgnoreCase(TRUE)
				&& kalahDet.getNextTurnHouse() != null)
		{
			boolean validHouse = validateTurn(pitid, kalahDet.getNextTurnHouse());
			if(!validHouse){
				throw new OperationNotAllowedException(String.format(NEXT_TURN_MESSAGE,kalahDet.getNextTurnHouse()));
			}
		}
	}
	
	/**
	 * It populate the next turn house name and the game status in the entity model.
	 * @param updatedStatus
	 * @param kalahDet
	 * @return KalahDetails
	 */
	private KalahDetails setNextHouse(Map<Integer, Integer> updatedStatus , KalahDetails kalahDet){
		if(updatedStatus != null)
		{
			if(updatedStatus.containsKey(GAME_STATUS))
			{
				kalahDet.setStatusId(updatedStatus.get(GAME_STATUS));
				updatedStatus.remove(GAME_STATUS);
			}
			if (updatedStatus.get(NEXT_TURN) > 0)
			{
				kalahDet.setNextTurnHouse(nextTurnHouse(updatedStatus.get(NEXT_TURN)));
				updatedStatus.remove(NEXT_TURN);
			}
			kalahDet.setStatus(updatedStatus);
		}
		return kalahDet;
	}
	
	/**
	 * It comprise with the logic that two individual should play from their own system and created a new game .
	 * Else the new user land into as a counter for one of the waiting user .
	 * @return KalahDetails
	 */
	private KalahDetails createGame()
	{
		KalahDetails kalaDetRecord = null;

		if (kalahRepo!= null 
				&& kalahRepo.getKalahActiveGame().size()>0){
			kalaDetRecord = (KalahDetails)kalahRepo.getKalahActiveGame().get(0);
			if (isExistWaitingUser(kalaDetRecord)){
				return kalaDetRecord ;
			}
			if (kalaDetRecord.getUserOneId() == null || kalaDetRecord.getUserOneId()== "")
			{
				kalaDetRecord.setUserOneId(request.getRemoteAddr());
			}
			else {
				kalaDetRecord.setUserTwoId(request.getRemoteAddr());
			}
		}

		else
		{
			LOGGER.info("Goind to create a new game id");
			kalaDetRecord = createNewGame(new KalahDetails());
		}

		return kalaDetRecord ;
	}

	/**
	 * It create a container (Map) with default count of marble in each pit , as here we are taking six marbles per pit 
	 * @return Map<Integer, Integer>
	 */
	private Map<Integer, Integer> createStatus(){

		Map<Integer, Integer> statusMap = IntStream.range(0, KALA_INIT_SCORE.size())
				.boxed()
				.collect(Collectors.toMap(i -> i+1, KALA_INIT_SCORE::get));
		return statusMap;
	}

	/**
	 * It populate the model with respective values along with address read on from the server.
	 * @param kalaDetRecord
	 * @return KalahDetails
	 */
	private KalahDetails createNewGame(KalahDetails kalaDetRecord){
		kalaDetRecord.setUserOneId(request.getRemoteAddr());
		Long gameId= kalahRepo.getMaxId()+1;
		int port = request.getServerPort();
		kalaDetRecord.setUri(String.format("http://%s:%s/games/%s", InetAddress.getLoopbackAddress().getHostName(), port, gameId));
		kalaDetRecord.setStatus(createStatus());
		kalaDetRecord.setStatusId(INITIAL_STATUS);
		return kalaDetRecord ;
	}

	/**
	 * This method validate whether the current user is a existing waiting user . 
	 * As this api allows 2 human players to play the game, each in his own computer. 
	 * @param kalaDetRecord
	 * @return
	 */
	private boolean isExistWaitingUser(KalahDetails kalaDetRecord)
	{
		boolean existingUser = false;
		if (SYSTEM_IP_VALIDATION.equalsIgnoreCase(TRUE)
				&& request.getRemoteAddr() != null
				&& (request.getRemoteAddr().equalsIgnoreCase(kalaDetRecord.getUserOneId())
						|| request.getRemoteAddr().equalsIgnoreCase(kalaDetRecord.getUserTwoId()))){
			existingUser = true;
		}
		return existingUser ;
	}

	/**
	 * It gives the pit nature , which shows the type , validity , house etc .
	 * This deals with enum and return based on the appropriate type .
	 * @param pitid
	 * @param pitScore
	 * @return KALAH_REMARK
	 */
	private KALAH_REMARK kalaRules(int pitid,Map<Integer, Integer> pitScore){
		if (SOUTH_KALAH == pitid){
			//south kalah pit not allowed for operation
			return KALAH_REMARK.SOUTH_KALAH;
		}
		if (NORTH_KALAH == pitid){
			//north kalah pit not allowed for operation
			return KALAH_REMARK.NORTH_KALAH;
		}
		else if(pitScore.get(pitid) != null 
				&& EMPTY_PITS == pitScore.get(pitid)){
			//can not move from empty pit
			return KALAH_REMARK.EMPTY_PIT;
		}
		else if (KALAH_SOUTH_PITS.contains(pitid)){
			//south pit
			return KALAH_REMARK.SOUTH_PIT;
		}
		else if (KALAH_NORTH_PITS.contains(pitid)){
			//north pit
			return KALAH_REMARK.NORTH_PIT;
		}
		else {
			//There is no such pits exists in this Kalah Game
			return KALAH_REMARK.INVALID_PIT;
		}
	}

	/**
	 * Based on the type of pit ,it perform move operation .However in case of validity it throws custom exception with detail message .
	 * @param pitScore
	 * @param pitid
	 * @return Map<Integer, Integer>
	 */
	private Map<Integer, Integer> moveOperation(Map<Integer, Integer> pitScore,int pitid){

		KALAH_REMARK remark = kalaRules(pitid,pitScore);
		Map<Integer, Integer> status = null ;
		switch (remark)
		{
		case SOUTH_PIT :
			status = moveMarble(pitScore,pitid,KALAH_REMARK.SOUTH_PIT);
			break;
		case NORTH_PIT :
			status = moveMarble(pitScore,pitid,KALAH_REMARK.NORTH_PIT);
			break;
		case SOUTH_KALAH :
			throw new OperationNotAllowedException(SOUTH_KALAH_MESSAGE);
		case NORTH_KALAH :
			throw new OperationNotAllowedException(NORTH_KALAH_MESSAGE);
		case EMPTY_PIT :
			throw new OperationNotAllowedException(EMPTY_PIT_MESSAGE);
		case INVALID_PIT :
			throw new OperationNotAllowedException(INVALID_PIT_MESSAGE);
		}
		return status;
	}

	/**
	 * It performs the exact operation for move of marble in a anti-clockwise direction.
	 * @param kalaScoreMap
	 * @param pitid
	 * @param houseName
	 * @return Map<Integer, Integer>
	 */
	private Map<Integer, Integer> moveMarble(Map<Integer, Integer> kalaScoreMap,int pitid,KALAH_REMARK houseName){

		int marbleCount = kalaScoreMap.get(pitid) ;
		kalaScoreMap.compute(pitid,(k,v)-> 0);
		for (int i = 1 ; i <= marbleCount;i++){

			int nextPit = (pitid+i)%KALAH_PITS_CNT ;

			if (houseName == KALAH_REMARK.SOUTH_PIT
					&& nextPit == NORTH_KALAH_VAL)
			{
				marbleCount+=1 ;
				continue ;
			}
			else if (houseName == KALAH_REMARK.NORTH_PIT
					&& nextPit == SOUTH_KALAH){
				marbleCount+=1 ;
				continue ;

			}
			else if(i == marbleCount 
					&& kalaScoreMap.get(nextPit) == 0
					&& isOwnPit(houseName,nextPit)){
				int kalaid = getKalaid(houseName);
				int counterPitid = KALAH_PITS_CNT - nextPit ;
				kalaScoreMap.compute(kalaid ,(k,v)-> v + kalaScoreMap.get(counterPitid)+1);
				kalaScoreMap.compute(counterPitid,(k,v)-> 0);
			}
			else {
				kalaScoreMap.compute((nextPit == 0
						? KALAH_PITS_CNT
								: nextPit) ,(k,v)-> v+1);
			}

			if(i == marbleCount){

				int nextHouse = getNextTurnHouse(houseName,nextPit);
				kalaScoreMap.put(NEXT_TURN,nextHouse);
				boolean gameComplete = isGameComplete(kalaScoreMap,houseName);
				if(gameComplete){
					LOGGER.info("The game is complete");
					kalaScoreMap.put(GAME_STATUS, GAME_COMPLETE);
				}

			}
		}
		return kalaScoreMap ;
	}

	/**
	 * It checks whether the pit belongs to that house(SOUTH/NORTH) or other(NORTH/SOUTH) one.
	 * @param houseName
	 * @param lastPit
	 * @return boolean
	 */
	private boolean isOwnPit(KALAH_REMARK houseName,int lastPit){
		if(houseName == KALAH_REMARK.SOUTH_PIT){
			return KALAH_SOUTH_PITS.contains(lastPit);
		}
		else{
			return KALAH_NORTH_PITS.contains(lastPit);
		}
	}

	/**
	 * It defines the next house name based on the lastly dropped marble in a pit or kalah.
	 * @param houseName
	 * @param lastPit
	 * @return int
	 */
	private int getNextTurnHouse(KALAH_REMARK houseName,int lastPit){
		if (houseName == KALAH_REMARK.NORTH_PIT
				&& lastPit == NORTH_KALAH_VAL){
			return NORTH_KALAH ;
		}
		else if (houseName == KALAH_REMARK.SOUTH_PIT
				&& lastPit == SOUTH_KALAH)
		{
			return SOUTH_KALAH;
		}
		else if (houseName == KALAH_REMARK.NORTH_PIT){
			return SOUTH_KALAH ;
		}
		else {
			return NORTH_KALAH;
		}
	}

	/**
	 * It defines the next house name based on the kalah for each house.
	 * @param houseid
	 * @return String
	 */
	private String nextTurnHouse(int houseid){
		if(houseid == NORTH_KALAH){
			return NORTH_HOUSE;
		}
		else{
			return SOUTH_HOUSE;
		}
	}
	
	/**
	 * It validates the actual house name for this term along from the house request raised .
	 * @param pitid
	 * @param houseName
	 * @return boolean
	 */
	private boolean validateTurn(int pitid, String houseName){
		if ( !(KALAH_SOUTH_PITS.contains(pitid) 
				|| KALAH_NORTH_PITS.contains(pitid)))
		{
			return true;
		}
		else if(SOUTH_HOUSE.equalsIgnoreCase(houseName)){
			return (KALAH_SOUTH_PITS.contains(pitid));
		}
		else if(NORTH_HOUSE.equalsIgnoreCase(houseName)){
			return (KALAH_NORTH_PITS.contains(pitid));
		}
		else 
			return false ;
	}

	/**
	 * It validates whether the request for the game is already a completed one or can be proceed to make operation . 
	 * @param kalaScoreMap
	 * @param houseName
	 * @return boolean
	 */
	private boolean isGameComplete(Map<Integer, Integer> kalaScoreMap,KALAH_REMARK houseName){
		boolean allZeros = false ;
		switch (houseName){
		case SOUTH_PIT:
			allZeros = KALAH_NORTH_PITS.stream()
			.filter(kalaScoreMap::containsKey)
			.map(kalaScoreMap::get)
			.allMatch(e -> e == EMPTY_PITS);
			break;
		case NORTH_PIT:
			allZeros = KALAH_SOUTH_PITS.stream()
			.filter(kalaScoreMap::containsKey)
			.map(kalaScoreMap::get)
			.allMatch(e -> e == EMPTY_PITS);
			break;
		default:
			break;
		}
		return allZeros  ;
	}

	/**
	 * It provides the Kalah id based on the each side house .
	 * @param houseName
	 * @return int
	 */
	private int getKalaid(KALAH_REMARK houseName){
		return  (houseName == KALAH_REMARK.SOUTH_PIT ? SOUTH_KALAH : NORTH_KALAH );
	}
}
