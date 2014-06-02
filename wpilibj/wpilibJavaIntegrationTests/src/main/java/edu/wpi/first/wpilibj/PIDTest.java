/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2014. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wpi.first.wpilibj.PIDSource.PIDSourceParameter;
import edu.wpi.first.wpilibj.fixtures.MotorEncoderFixture;
import edu.wpi.first.wpilibj.test.AbstractComsSetup;
import edu.wpi.first.wpilibj.test.TestBench;


/**
 * @author Kacper Puczydlowski
 * @author Jonathan Leitschuh
 *
 */

@RunWith(Parameterized.class)
public class PIDTest extends AbstractComsSetup {
	private static final Logger logger = Logger.getLogger(PIDTest.class.getName());
	
	private PIDController controller = null;
	private static MotorEncoderFixture me = null;
	
	public PIDTest(MotorEncoderFixture mef){
		logger.fine("Constructor with: " + mef.getType());
		if(PIDTest.me != null && !PIDTest.me.equals(mef)) PIDTest.me.teardown();
		PIDTest.me = mef;
	}
	
	@Parameters
	public static Collection<MotorEncoderFixture[]> generateData(){
		//logger.fine("Loading the MotorList");
		return Arrays.asList(new MotorEncoderFixture[][]{
				 {TestBench.getInstance().getTalonPair()},
				 {TestBench.getInstance().getVictorPair()},
				 {TestBench.getInstance().getJaguarPair()}
			//	 TestBench.getInstance().getCanJaguarPair()
		});
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logger.fine("TearDownAfterClass: " + me.getType());
		me.teardown();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		logger.fine("Setup: " + me.getType());
		me.setup();
		controller = new PIDController(0.003, .001, 0, me.getEncoder(), me.getMotor());
		controller.setAbsoluteTolerance(15);
		controller.setOutputRange(-0.2, 0.2);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		logger.fine("Teardown: " + me.getType());
		controller.disable();
		controller.free();
		controller = null;
		me.reset();
	}
	
	@Test
	public void testInitialSettings(){
		controller.disable();
		assertTrue("PID did not start at 0", controller.getError() == 0);
	}
	
	@Test
	public void testSetSetpoint(){
		Double setpoint = 2500.0;
		controller.disable();
		controller.setSetpoint(setpoint);
		controller.enable();
		assertEquals(setpoint, new Double(controller.getSetpoint()));
	}

	@Test (timeout = 6000)
	public void testRotateToTarget() {
		double setpoint = 2500;
		System.out.println("Entering testRotateToTarget");
		assertEquals("PID did not start at 0", 0, controller.get(), 0);
		controller.setSetpoint(setpoint);
		assertEquals("PID did not have an error of " + setpoint, setpoint, controller.getError(), 0);
		controller.enable();
		Timer.delay(5);
		controller.disable();
		assertTrue(controller.onTarget());
	}
	
}
