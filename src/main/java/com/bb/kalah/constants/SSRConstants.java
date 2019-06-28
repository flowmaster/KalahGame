/**
 * This class contains all the constants respective to the project.
 * There toggle switch property although i added to here but mainly those are intended for database driven.
 * @author Sambed
 * @date 23/05/2019
 * @date last update - 
 * @change by -  
 */

package com.bb.kalah.constants;

import java.util.ArrayList;
import java.util.Arrays;

public interface SSRConstants {
	public final static String SYSTEM_IP_VALIDATION = "N";
	public final static String TRUE = "Y";
	final Integer[] primeStatus = {6,6,6,6,6,6,0,6,6,6,6,6,6,0};
	public final static ArrayList<Integer> KALA_INIT_SCORE = new ArrayList<Integer>(Arrays.asList(primeStatus)) ;
	final Integer[] KALAH_SOUTH_PITS_VAL = {1,2,3,4,5,6};
	public final static ArrayList<Integer> KALAH_SOUTH_PITS = new ArrayList<Integer>(Arrays.asList(KALAH_SOUTH_PITS_VAL)) ;
	Integer[] KALAH_NORTH_PITS_VAL = {8,9,10,11,12,13};
	public final static ArrayList<Integer> KALAH_NORTH_PITS = new ArrayList<Integer>(Arrays.asList(KALAH_NORTH_PITS_VAL)) ;
	public final static int NORTH_KALAH = 14;
	public final static int SOUTH_KALAH = 7;
	public final static int NORTH_KALAH_VAL = 0;
	public final static int EMPTY_PITS = 0;
	public final static int KALAH_PITS_CNT = 14;
	public final static String SOUTH_HOUSE = "SOUTH_HOUSE";
	public final static String NORTH_HOUSE = "NORTH_HOUSE";
	public enum KALAH_REMARK {SOUTH_KALAH,NORTH_KALAH,EMPTY_PIT,SOUTH_PIT,NORTH_PIT,INVALID_PIT} ; 
	public final static String NORTH_KALAH_MESSAGE = "This is north kalah and operation not allowed";
	public final static String SOUTH_KALAH_MESSAGE = "This is south kalah and operation not allowed";
	public final static String EMPTY_PIT_MESSAGE = "This is an empty pit and operation not allowed";
	public final static String INVALID_PIT_MESSAGE = "No such pit exists";
	public final static String NOT_FOUND = "This game id %s is not present" ;
	public final static String GAME_COMPLETED_MESSAGE = "The game is completed for id %s";
	public final static String NEXT_TURN_MESSAGE = "Next turn should from %s ";
	public final static int GAME_STATUS = 0;
	public final static int GAME_COMPLETE = 2;
	public final static int INITIAL_STATUS = 1;
	public final static String NEXT_TURN_VALIDATION = "Y";
	public final static int NEXT_TURN = 15;

}
