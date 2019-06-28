/**
 * This is a JUnit test case class which is only meant to validate the controller methods which handles the request.
 * @author Sambed
 * @date 23/05/2019
 * @date last update - 
 * @change by -
 */

package com.bb.kalah.controller;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import com.bb.kalah.constants.SSRConstants;
import com.bb.kalah.controller.KalahController;
import com.bb.kalah.exception.OperationNotAllowedException;
import com.bb.kalah.persistence.model.KalahDetails;
import com.bb.kalah.service.KalahService;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration
public class KalahControllerTest {

	/**
	 * This stipulate to have a exception assertion rule .
	 */
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * The service that we want to test that getting inject.
	 */
	@Mock
	private KalahService kalahService;


	/**
	 * The controller that we want to test that getting inject.
	 */
	@InjectMocks 
	public KalahController controller;

	/**
	 * Initializing event of mockito 
	 */
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

	}
	
	/**
	 * This unit test case method check the game creation url for which it thrown an exception as the location object not available.
	 * @return
	 * @param
	 */
	@Test
	public void returnResponseForCreate(){
		KalahDetails mockKalaDet = new KalahDetails(1L,"localhost", "localhost","http://localhost:8080/games",1);
		Mockito.when(kalahService.startGame()).thenReturn(mockKalaDet);
		ResponseEntity<Object> response = controller.startGame();
		assertEquals("HttpStatus should be 500", 500, response.getStatusCodeValue());
	}

	/**
	 * This unit test case method check the movement of a pit from south house and respective value.
	 * @return
	 * @param
	 */
	@Test
	public void shouldRetornResponseForMakeMoveSouthHouse(){
		Map<Integer, Integer> statusMap = IntStream.range(0, SSRConstants.KALA_INIT_SCORE.size())
				.boxed()
				.collect(Collectors.toMap(i -> i+1, SSRConstants.KALA_INIT_SCORE::get));
		KalahDetails mockKalaDet = new KalahDetails(1L,"localhost", "localhost","http://localhost:8080/games",1);
		mockKalaDet.setStatus(statusMap);
		ResponseEntity<Object> response = controller.makeMove(1,1);
		assertEquals("HttpStatus should be 200", 200, response.getStatusCodeValue());
	}
	
	
	/**
	 * This unit test case method check the movement of a pit from north house and respective value.
	 * @return
	 * @param
	 */
	@Test
	public void shouldRetornResponseForMakeMoveNorthHouse(){
		Map<Integer, Integer> statusMap = IntStream.range(0, SSRConstants.KALA_INIT_SCORE.size())
				.boxed()
				.collect(Collectors.toMap(i -> i+1, SSRConstants.KALA_INIT_SCORE::get));
		KalahDetails mockKalaDet = new KalahDetails(1L,"localhost", "localhost","http://localhost:8080/games/1",1);
		mockKalaDet.setStatus(statusMap);
		ResponseEntity<Object> response = controller.makeMove(1,12);
		assertEquals("HttpStatus should be 200", 200, response.getStatusCodeValue());
	}

	
	/**
	 * In this unit test case trying to move the marbles from the south kalah , which is not allowed and throws an exception
	 * @return
	 * @param
	 */
	@Test
	public void testMakeMoveStatusSouthKalah(){
		try{
			controller.makeMove(1,7);
		}catch(OperationNotAllowedException ox){
			assertTrue(ox.getClass().equals(OperationNotAllowedException.class));
		}
	}

	/**
	 * In this unit test case trying to move the marbles from the north kalah , which is not allowed and throws an exception
	 * @return
	 * @param
	 */
	@Test
	public void testMakeMoveStatusNorthKalah(){
		try{
			controller.makeMove(1,14);
		}catch(OperationNotAllowedException ox){
			assertTrue(ox.getClass().equals(OperationNotAllowedException.class));
		}
	}
}
