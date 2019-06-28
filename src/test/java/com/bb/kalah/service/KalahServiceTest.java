/**
 * This is a JUnit test case class which is only meant to validate the service methods which being work on  the 
 * controller dispatch request.
 * @author Sambed
 * @date 23/05/2019
 * @date last update - 
 * @change by -
 */

package com.bb.kalah.service;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.junit.Before;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.bb.kalah.constants.SSRConstants;
import com.bb.kalah.controller.KalahController;
import com.bb.kalah.exception.KalahException;
import com.bb.kalah.exception.OperationNotAllowedException;
import com.bb.kalah.persistence.KalahDdetailsRepository;
import com.bb.kalah.persistence.model.KalahDetails;
import com.bb.kalah.service.KalahService;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class KalahServiceTest {

	/**
	 * This stipulate to have a exception assertion rule .
	 */
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * The service that we want to test that getting inject.
	 */
	@InjectMocks
	private KalahService kalahService;

	/**
	 * A mock version of KalahRepository for use in our tests.
	 */
	@Mock
	private KalahDdetailsRepository kalahRepo ;

	/**
	 * The controller that we want to test that getting inject.
	 */
	@InjectMocks 
	public KalahController controller;

	/**
	 * A mock version of http request for use in our tests , as getting the request host name.
	 */
	@Mock
	HttpServletRequest request;

	/**
	 * Initializing event of mockito 
	 */
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

	}


	/**
	 * This is the method to test start game call of service class that pertain to controller invocation. 
	 * @param
	 * @return
	 */
	@Test
	public void testStartGame(){
		KalahDetails mockKalaDet = new KalahDetails(1L,"localhost:8080", "localhost:8080","localhost:8080",1);
		Mockito.doReturn(mockKalaDet).when(kalahRepo).save(Mockito.any());
		KalahDetails returnKalahDet = kalahService.startGame(); 
		assertNotNull("The object you enter return null",returnKalahDet);
		assertEquals("1", returnKalahDet.getId().toString());
		assertEquals(1, returnKalahDet.getStatusId());
	}


	/**
	 * This is the method to test move based on the game id and pit id call of service class that pertain to controller invocation.
	 * We validate here once all the marbles from a south house pit moved to following pits in anti-clockwise respective to 
	 *  kalah ruls , the current count on that pit should be zero.
	 * @param
	 * @return
	 */	
	@Test
	public void testMakeMove(){
		Map<Integer, Integer> statusMap = IntStream.range(0, SSRConstants.KALA_INIT_SCORE.size())
				.boxed()
				.collect(Collectors.toMap(i -> i+1, SSRConstants.KALA_INIT_SCORE::get));

		Long id = 1L;
		KalahDetails mockKalaDet = new KalahDetails(1L,"localhost:8080", "localhost:8080","localhost:8080",1);
		mockKalaDet.setStatus(statusMap);
		Mockito.when(kalahRepo.findById(id)).thenReturn(Optional.of(mockKalaDet));
		Mockito.doReturn(mockKalaDet).when(kalahRepo).save(Mockito.any());
		KalahDetails returnKalaDet = kalahService.makeMove(1, 1);
		assertEquals("This is kalah gonna 0 ",0, returnKalaDet.getStatus().get(1).intValue());

	}

	/**
	 * This is the method to test move based on the game id and pit id call of service class that pertain to controller invocation.
	 * We validate here once all the marbles from a north house pit moved to following pits in anti-clockwise respective to 
	 *  kalah rules , the current count on that pit should be zero.
	 * @param
	 * @return
	 */	
	@Test
	public void testMakeMoveStatus2(){
		Map<Integer, Integer> statusMap = IntStream.range(0, SSRConstants.KALA_INIT_SCORE.size())
				.boxed()
				.collect(Collectors.toMap(i -> i+1, SSRConstants.KALA_INIT_SCORE::get));

		Long id = 1L;
		KalahDetails mockKalaDet = new KalahDetails(1L,"localhost", "localhost","localhost:8080",1);
		mockKalaDet.setStatus(statusMap);
		Mockito.when(kalahRepo.findById(id)).thenReturn(Optional.of(mockKalaDet));
		Mockito.doReturn(mockKalaDet).when(kalahRepo).save(Mockito.any());
		KalahDetails returnKalaDet = kalahService.makeMove(1, 13);
		assertEquals("This is kalah gonna 0 ",0, returnKalaDet.getStatus().get(13).intValue());

	}

	/**
	 * This is the method to test move based on a game id which does not exists.
	 * @param
	 * @return
	 */
	@Test
	public void shouldThrowExceptionWhenMovingGameIdZero(){
		expectedException.expect(KalahException.class);
		kalahService.makeMove(0, 1);
	}


	/**
	 * This is the method to test move based on the game id and pit id call of service class that pertain to controller 
	 * invocation. We validate here to make an operation from south Kalah which is not allowed and should throw a custom
	 * exception as OperationNotAllowedException .
	 * @param
	 * @return
	 */
	@Test
	public void testMakeMoveStatusSouthKalah(){
		Map<Integer, Integer> statusMap = IntStream.range(0, SSRConstants.KALA_INIT_SCORE.size())
				.boxed()
				.collect(Collectors.toMap(i -> i+1, SSRConstants.KALA_INIT_SCORE::get));

		Long id = 1L;
		KalahDetails mockKalaDet = new KalahDetails(id,"localhost", "localhost","localhost:8080",1);
		mockKalaDet.setStatus(statusMap);
		Mockito.when(kalahRepo.findById(id)).thenReturn(Optional.of(mockKalaDet));
		try{
			kalahService.makeMove(1, 7);
		}catch(OperationNotAllowedException ox){
			assertTrue(ox.getClass().equals(OperationNotAllowedException.class));
		}
	}
}
