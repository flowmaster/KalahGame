/**
 * This is the Kalah game cotroller which handles all the api request and perform requisite action to dispatch the result.
 * @author Sambed
 * @date 23/05/2019
 * @date last update - 
 * @change by - 
 */

package com.bb.kalah.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bb.kalah.constants.SSRConstants;
import com.bb.kalah.persistence.KalahDdetailsRepository;
import com.bb.kalah.persistence.model.KalahDetails;
import com.bb.kalah.persistence.model.view.View;
import com.bb.kalah.service.KalahService;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiOperation;

@RestController
public class KalahController implements SSRConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(KalahController.class);
	
	@Autowired
	private KalahService kalahService;

	@Autowired
	private KalahDdetailsRepository kalahRepo ;


	/**
	 * This initiates the game with an unique identifier and returns the concern detail to the client with url and game id.
	 * It perform operation with POST request and return with status code 201 while successfully created.
	 * @return ResponseEntity<Object>
	 */
	@ApiOperation(value = "Start Game")
	@PostMapping("/games")
	@ResponseStatus()
	@JsonView(View.GameCreationView.class)
	public ResponseEntity<Object> startGame(){
		KalahDetails savedKalahGame = kalahService.startGame();
		try{
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
					.buildAndExpand(savedKalahGame.getId()).toUri();
			return ResponseEntity.created(location).body(savedKalahGame);
		}catch (Exception ex){
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * This is an interface for to play the game by moving marbles from one pit to subsequent pits in associate with 
	 * Kalah rules.It return the status of each pit along with inline with all the required detail after an iteration 
	 * with status code as 200.  
	 * @param id
	 * @param pitid
	 * @return ResponseEntity<Object>
	 */
	@ApiOperation(value = "Make a move")
	@PutMapping("/games/{id}/pits/{pitid}")
	@JsonView(View.StatusView.class)
	public ResponseEntity<Object> makeMove(@PathVariable long id,@PathVariable int pitid){
		
		KalahDetails kalahDet = kalahService.makeMove(id,pitid);
		try{
			return ResponseEntity.ok().body(kalahDet);
		}catch (Exception ex){
			LOGGER.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
	}
	
}
