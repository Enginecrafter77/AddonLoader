package lejos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import lejos.hardware.Battery;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.MindsensorsGlideWheelMRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.I2CPort;
import lejos.hardware.port.IOPort;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.PortException;
import lejos.hardware.port.SensorPort;
import lejos.hardware.port.TachoMotorPort;
import lejos.hardware.port.UARTPort;
import lejos.hardware.sensor.BaseSensor;
import lejos.remote.ev3.EV3Reply;
import lejos.remote.ev3.EV3Request;
import lejos.remote.ev3.MenuReply;
import lejos.remote.ev3.MenuRequest;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.PublishFilter;

public class RemoteMenuThread extends Thread {
	
	@Override
	public void run() {
		
		ServerSocket ss;
		Socket conn = null;
		
		try
		{
			ss = new ServerSocket(Reference.REMOTE_MENU_PORT);
			System.out.println("Remote menu server socket created");
		}
		catch(IOException e)
		{
			System.err.println("Error creating server socket: " + e);
			return;
		}
		
		Port[] ports = new Port[] {SensorPort.S1, SensorPort.S2, SensorPort.S3, SensorPort.S4, MotorPort.A, MotorPort.B, MotorPort.C, MotorPort.D};
		IOPort[] ioPorts = new IOPort[8];
		GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
		SampleProvider[] providers = new SampleProvider[4];
		BaseSensor[] sensors = new BaseSensor[4];
		RegulatedMotor[] motors = new RegulatedMotor[4];
		
		while(true)
		{
			try
			{
				System.out.println("Waiting for a remote menu connection");
				conn = ss.accept();
				conn.setSoTimeout(2000);
				conn.setTcpNoDelay(true);
				ObjectOutputStream os = new ObjectOutputStream(conn.getOutputStream());
				ObjectInputStream is = new ObjectInputStream(conn.getInputStream());
				
				try
				{
					while(true)
					{ 
						os.reset();
						Object obj = is.readObject();
						
						if(obj instanceof MenuRequest)
						{
							MenuRequest request = (MenuRequest) obj;
							MenuReply reply = new MenuReply();
							
							switch (request.request)
							{
							case RUN_PROGRAM:
								MainMenu.self.runProgram(request.name);
								break;
							case DEBUG_PROGRAM:
								MainMenu.self.debugProgram(request.name);
								break;
							case DELETE_ALL_PROGRAMS:
								MainMenu.self.deleteAllPrograms();
								break;
							case DELETE_FILE:
								reply.result = MainMenu.self.deleteFile(request.name);
								os.writeObject(reply);
								break;
							case FETCH_FILE:
								reply.contents = MainMenu.self.fetchFile(request.name);
								os.writeObject(reply);
								break;
							case GET_FILE_SIZE:
								reply.reply = (int) MainMenu.self.getFileSize(request.name);
								os.writeObject(reply);
								break;
							case GET_MENU_VERSION:
								reply.value = MainMenu.self.getMenuVersion();
								os.writeObject(reply);
								break;
							case GET_NAME:
								reply.value = MainMenu.self.getName();
								os.writeObject(reply);
								break;
							case GET_PROGRAM_NAMES:
								reply.names = MainMenu.self.getProgramNames();
								os.writeObject(reply);
								break;
							case GET_SAMPLE_NAMES:
								reply.names = MainMenu.self.getSampleNames();
								os.writeObject(reply);
								break;
							case GET_SETTING:
								reply.value = MainMenu.self.getSetting(request.name);
								os.writeObject(reply);
								break;
							case GET_VERSION:
								reply.value = MainMenu.self.getVersion();
								os.writeObject(reply);
								break;
							case RUN_SAMPLE:
								MainMenu.self.runSample(request.name);
								break;
							case SET_NAME:
								setName(request.name);
								break;
							case SET_SETTING:
								MainMenu.self.setSetting(request.name, request.value);
								break;
							case UPLOAD_FILE:
								reply.result = MainMenu.self.uploadFile(request.name, request.contents);
								os.writeObject(reply);
								break;  
							case STOP_PROGRAM:
								MainMenu.self.stopProgram();
								break;
							case SHUT_DOWN:
								MainMenu.self.shutdown();
								break;
							case GET_EXECUTING_PROGRAM_NAME:
								reply.value = MainMenu.programName;
								os.writeObject(reply);
								break;
							case SUSPEND:
								MainMenu.self.suspend();
								System.out.println("Menu suspended");
								break;
							case RESUME:
								MainMenu.self.resume();
								System.out.println("Menu resumed");
							}
						}
						else if(obj instanceof EV3Request)
						{
							EV3Request request = (EV3Request) obj;
							EV3Reply reply = new EV3Reply();
							try
							{			 		
								switch(request.request)
								{
								case GET_VOLTAGE_MILLIVOLTS:
									reply.reply = Battery.getVoltageMilliVolt();
									os.writeObject(reply);
									break;
								case GET_VOLTAGE:
									reply.floatReply = Battery.getVoltage();
									os.writeObject(reply);
									break;
								case GET_BATTERY_CURRENT:
									reply.floatReply = Battery.getBatteryCurrent();
									os.writeObject(reply);
									break;
								case GET_MOTOR_CURRENT:
									reply.floatReply = Battery.getMotorCurrent();
									os.writeObject(reply);
									break;
								case SYSTEM_SOUND:
									Sound.systemSound(false, request.intValue);
									break;
								case PLAY_SAMPLE:
									reply.reply = Sound.playSample(request.file);
									os.writeObject(reply);
									break;
								case GET_NAME:
									reply.value = MainMenu.self.getName();
									os.writeObject(reply);
									break;
								case LED_PATTERN:
									LocalEV3.get().getLED().setPattern(request.intValue);
									break;
								case WAIT_FOR_ANY_EVENT:
									reply.reply = Button.waitForAnyEvent(request.intValue);
									os.writeObject(reply);
									break;
								case WAIT_FOR_ANY_PRESS:
									reply.reply = Button.waitForAnyPress(request.intValue);
									os.writeObject(reply);
									break;
								case GET_BUTTONS:
									reply.reply = Button.getButtons();
									os.writeObject(reply);
									break;
								case READ_BUTTONS:
									reply.reply = Button.readButtons();
									os.writeObject(reply);
									break;
								case LCD_REFRESH:
									LCD.refresh();
									break;
								case LCD_CLEAR:
									LCD.clear();
									break;
								case LCD_GET_WIDTH:
									reply.reply = LCD.SCREEN_WIDTH;
									os.writeObject(reply);
									break;
								case LCD_GET_HEIGHT:
									reply.reply = LCD.SCREEN_HEIGHT;
									os.writeObject(reply);
									break;
								case LCD_GET_HW_DISPLAY:
									break;
								case LCD_BITBLT_1:
									break;
								case LCD_BITBLT_2:
									break;
								case LCD_SET_AUTO_REFRESH:
									LCD.setAutoRefresh(request.flag);
									break;
								case LCD_SET_AUTO_REFRESH_PERIOD:
									LCD.setAutoRefreshPeriod(request.intValue);
									break;
								case LCD_DRAW_CHAR:
									LCD.drawChar(request.ch, request.intValue, request.intValue2);
									break;
								case LCD_DRAW_STRING_INVERTED:
									LCD.drawString(request.str, request.intValue, request.intValue2, request.flag);
									break;
								case LCD_DRAW_STRING:
									LCD.drawString(request.str, request.intValue, request.intValue2);
									break;
								case LCD_DRAW_INT:
									LCD.drawInt(request.intValue, request.intValue2, request.intValue3);
									break;
								case LCD_DRAW_INT_PLACES:
									LCD.drawInt(request.intValue, request.intValue2, request.intValue3, request.intValue4);
									break;
								case LCD_CLEAR_LINES:
									LCD.clear(request.intValue, request.intValue2, request.intValue3);
									break;
								case LCD_CLEAR_LINE:
									LCD.clear(request.intValue);
									break;
								case LCD_SCROLL:
									LCD.scroll();
									break;
								case LCD_GET_FONT:
									break;
								case LCD_GET_TEXT_WIDTH:
									reply.reply = LCD.DISPLAY_CHAR_WIDTH;
									os.writeObject(reply);
									break;
								case LCD_GET_TEXT_HEIGHT:
									reply.reply = LCD.DISPLAY_CHAR_DEPTH;
									os.writeObject(reply);
									break;
								case OPEN_MOTOR_PORT:
									ioPorts[4+request.intValue] = ports[4+request.intValue].open(TachoMotorPort.class);
									break;
								case CLOSE_MOTOR_PORT:
									ioPorts[4+request.intValue].close();
									break;
								case CONTROL_MOTOR:
									((TachoMotorPort) ioPorts[4+request.intValue]).controlMotor(request.intValue2, request.intValue3);
									break;
								case GET_TACHO_COUNT:
									reply.reply = ((TachoMotorPort) ioPorts[4+request.intValue]).getTachoCount();
									os.writeObject(reply);
									break;
								case RESET_TACHO_COUNT:
									((TachoMotorPort) ioPorts[4+request.intValue]).resetTachoCount();
									break;
								case KEY_IS_DOWN:
									reply.result = LocalEV3.get().getKey(request.str).isDown();
									os.writeObject(reply);
									break;
								case KEY_WAIT_FOR_PRESS:
									LocalEV3.get().getKey(request.str).waitForPress();
									os.writeObject(reply);
									break;
								case KEY_WAIT_FOR_PRESS_AND_RELEASE:
									LocalEV3.get().getKey(request.str).waitForPress();
									os.writeObject(reply);
									break;
								case KEY_SIMULATE_EVENT:
									LocalEV3.get().getKey(request.str).simulateEvent(request.intValue);
									break;
								case OPEN_ANALOG_PORT:
									ioPorts[request.intValue] = ports[request.intValue].open(AnalogPort.class);
									os.writeObject(reply);
									break;
								case OPEN_I2C_PORT:
									ioPorts[request.intValue] = ports[request.intValue].open(I2CPort.class);
									os.writeObject(reply);
									break;
								case OPEN_UART_PORT:
									ioPorts[request.intValue] = ports[request.intValue].open(UARTPort.class);
									os.writeObject(reply);
									break;
								case CLOSE_SENSOR_PORT:
									ioPorts[request.intValue].close();
									break;
								case GET_PIN_6:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.floatReply= ((AnalogPort) ioPorts[request.intValue]).getPin6();
									os.writeObject(reply);
									break;
								case GET_PIN_1:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.floatReply= ((AnalogPort) ioPorts[request.intValue]).getPin1();
									os.writeObject(reply);
									break;
								case SET_PIN_MODE:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									((AnalogPort) ioPorts[request.intValue]).setMode(request.intValue);
									break;
								case GET_FLOATS:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.floats = new float[request.intValue2];
									((AnalogPort) ioPorts[request.intValue]).getFloats(reply.floats, 0, request.intValue2);
									os.writeObject(reply);
									break;
								case LCD_G_SET_PIXEL:
									g.setPixel(request.intValue, request.intValue2, request.intValue3);
									break;
								case LCD_G_GET_PIXEL:
									break;
								case LCD_G_DRAW_STRING:
									g.drawString(request.str, request.intValue, request.intValue2, request.intValue3);
									break;
								case LCD_G_DRAW_STRING_INVERTED:
									g.drawString(request.str, request.intValue, request.intValue2, request.intValue3, request.flag);
									break;
								case LCD_G_DRAW_CHAR:
									g.drawChar(request.ch, request.intValue, request.intValue2, request.intValue3);
									break;
								case LCD_G_DRAW_SUBSTRING:
									g.drawSubstring(request.str, request.intValue, request.intValue2, request.intValue3, request.intValue4, request.intValue5);
									break;
								case LCD_G_DRAW_CHARS:
									g.drawChars(request.chars, request.intValue, request.intValue2, request.intValue3, request.intValue4, request.intValue5);
									break;
								case LCD_G_GET_STROKE_STYLE:
									break;
								case LCD_G_SET_STROKE_STYLE:
									g.setStrokeStyle(request.intValue);
									break;
								case LCD_G_DRAW_REGION_ROP:
									g.drawRegionRop(request.image, request.intValue, request.intValue2, request.intValue3, request.intValue4, request.intValue5, request.intValue6, request.intValue7, request.intValue8);
									break;
								case LCD_G_DRAW_REGION_ROP_TRANSFORM:
									g.drawRegionRop(request.image, request.intValue, request.intValue2, request.intValue3, request.intValue4, request.intValue5, request.intValue6, request.intValue7, request.intValue8, request.intValue9);
									break;
								case LCD_G_DRAW_REGION:
									g.drawRegion(request.image, request.intValue, request.intValue2, request.intValue3, request.intValue4, request.intValue5, request.intValue6, request.intValue7, request.intValue8);
									break;
								case LCD_G_DRAW_IMAGE:
									g.drawImage(request.image, request.intValue, request.intValue2, request.intValue3);
									break;
								case LCD_G_DRAW_LINE:
									g.drawLine(request.intValue, request.intValue2, request.intValue3, request.intValue4);
									break;
								case LCD_G_DRAW_ARC:
									g.drawArc(request.intValue, request.intValue2, request.intValue3, request.intValue4, request.intValue5, request.intValue6);
									break;
								case LCD_G_FILL_ARC:
									g.fillArc(request.intValue, request.intValue2, request.intValue3, request.intValue4, request.intValue5, request.intValue6);
									break;
								case LCD_G_DRAW_ROUND_RECT:
									g.drawRoundRect(request.intValue, request.intValue2, request.intValue3, request.intValue4, request.intValue5, request.intValue6);
									break;
								case LCD_G_DRAW_RECT:
									g.drawRect(request.intValue, request.intValue2, request.intValue3, request.intValue4);
									break;
								case LCD_G_FILL_RECT:
									g.fillRect(request.intValue, request.intValue2, request.intValue3, request.intValue4);
									break;
								case LCD_G_TRANSLATE:
									g.translate(request.intValue, request.intValue2);
									break;
								case LCD_G_GET_TRANSLATE_X:
									break;
								case LCD_G_GET_TRANSLATE_Y:
									break;
								case I2C_TRANSACTION:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.contents = new byte[request.intValue6];
									((I2CPort) ioPorts[request.intValue]).i2cTransaction(request.intValue2, request.byteData, 
											request.intValue3, request.intValue5, reply.contents, 0, request.intValue7);
									os.writeObject(reply);
									break;
								case UART_GET_BYTE:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.reply = ((UARTPort) ioPorts[request.intValue]).getByte();
									os.writeObject(reply);
									break;
								case UART_GET_BYTES:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.contents = new byte[request.intValue2];
									((UARTPort) ioPorts[request.intValue]).getBytes(reply.contents, 0, request.intValue2);
									os.writeObject(reply);
									break;
								case UART_GET_SHORT:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.reply = ((UARTPort) ioPorts[request.intValue]).getShort();
									os.writeObject(reply);
									break;
							   case UART_GET_SHORTS:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.shorts = new short[request.intValue2];
									((UARTPort) ioPorts[request.intValue]).getShorts(reply.shorts, 0, request.intValue2);
									os.writeObject(reply);
									break;
								case UART_INITIALISE_SENSOR:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.result = ((UARTPort) ioPorts[request.intValue]).initialiseSensor(request.intValue2);
									os.writeObject(reply);
									break;
								case UART_RESET_SENSOR:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									((UARTPort) ioPorts[request.intValue]).resetSensor();
									break;
								case UART_SET_MODE:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.result = ((UARTPort) ioPorts[request.intValue]).setMode(request.intValue2);
									os.writeObject(reply);
									break;
								case UART_WRITE:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.reply = ((UARTPort) ioPorts[request.intValue]).write(request.byteData, request.intValue2, request.intValue3);
									os.writeObject(reply);
									break;
								case UART_RAW_WRITE:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.reply = ((UARTPort) ioPorts[request.intValue]).rawWrite(request.byteData, request.intValue2, request.intValue3);
									os.writeObject(reply);
									break;
								case UART_RAW_READ:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									reply.reply = ((UARTPort) ioPorts[request.intValue]).rawRead(request.byteData, request.intValue2, request.intValue3);
									os.writeObject(reply);
									break;
								case UART_SET_BIT_RATE:
									if (ioPorts[request.intValue] == null) throw new PortException("Port not open");
									((UARTPort) ioPorts[request.intValue]).setBitRate(request.intValue2);
									break;
									case CREATE_REGULATED_MOTOR:
									System.out.println("Creating motor on port " + request.str);
									Port p = LocalEV3.get().getPort(request.str); // port name
									RegulatedMotor motor = null;
									switch(request.ch)
									{
									case 'N':
										motor = new NXTRegulatedMotor(p);
										break;
									case 'L':
										motor = new EV3LargeRegulatedMotor(p);
										break;
									case 'M':
										motor = new EV3MediumRegulatedMotor(p);
										break;
									case 'G':
										motor = new MindsensorsGlideWheelMRegulatedMotor(p);
									}
									motors[request.str.charAt(0) - 'A'] = motor;
									break;
								case MOTOR_FORWARD:
									motors[request.intValue].forward();
									break;
								case MOTOR_BACKWARD:
									motors[request.intValue].backward();
									break;
								case MOTOR_STOP:
									motors[request.intValue].stop();
									os.writeObject(reply);
									break;
								case MOTOR_FLT:
									motors[request.intValue].flt();
									os.writeObject(reply);
									break;
								case MOTOR_IS_MOVING:
									reply.result = motors[request.intValue].isMoving();
									os.writeObject(reply);
									break;
								case MOTOR_GET_ROTATION_SPEED:
									reply.reply = motors[request.intValue].getRotationSpeed();
									os.writeObject(reply);
									break;
								case MOTOR_GET_TACHO_COUNT:
									reply.reply = motors[request.intValue].getTachoCount();
									os.writeObject(reply);
									break;
								case MOTOR_RESET_TACHO_COUNT:
									motors[request.intValue].resetTachoCount();
									break;
								case MOTOR_STOP_IMMEDIATE:
									motors[request.intValue].stop(request.flag);
									os.writeObject(reply);
									break;
								case MOTOR_FLT_IMMEDIATE:
									motors[request.intValue].flt(request.flag);
									os.writeObject(reply);
									break;
								case MOTOR_WAIT_COMPLETE:
									motors[request.intValue].waitComplete();
									os.writeObject(reply);
									break;
								case MOTOR_ROTATE:
									System.out.println("Rotating port " + request.intValue + " by " + request.intValue2);
									motors[request.intValue].rotate(request.intValue2);
									os.writeObject(reply);
									break;
								case MOTOR_ROTATE_IMMEDIATE:
									motors[request.intValue].rotate(request.intValue2, request.flag);
									if (!request.flag) os.writeObject(reply);
									break;
								case MOTOR_ROTATE_TO:
									motors[request.intValue].rotateTo(request.intValue2);
									os.writeObject(reply);
									break;
								case MOTOR_ROTATE_TO_IMMEDIATE:
									motors[request.intValue].rotateTo(request.intValue2, request.flag);
									if (!request.flag) os.writeObject(reply);
									break;
								case MOTOR_GET_LIMIT_ANGLE:
									reply.reply = motors[request.intValue].getLimitAngle();
									os.writeObject(reply);
									break;
								case MOTOR_GET_SPEED:
									reply.reply = motors[request.intValue].getSpeed();
									os.writeObject(reply);
									break;
								case MOTOR_SET_SPEED:
									motors[request.intValue].setSpeed(request.intValue2);
									break;
								case MOTOR_GET_MAX_SPEED:
									reply.floatReply = motors[request.intValue].getMaxSpeed();
									os.writeObject(reply);
									break;
								case MOTOR_IS_STALLED:
									reply.result = motors[request.intValue].isStalled();
									os.writeObject(reply);
									break;
								case MOTOR_SET_STALL_THRESHOLD:
									motors[request.intValue].setStallThreshold(request.intValue, request.intValue2);
									break;
								case MOTOR_SET_ACCELERATION:
									motors[request.intValue].setAcceleration(request.intValue2);
									break;
								case MOTOR_CLOSE:
									motors[request.intValue].close();
									os.writeObject(reply);
									break;
								case CREATE_SAMPLE_PROVIDER_PUBLISH:
								case CREATE_SAMPLE_PROVIDER:
									float frequency = (request.request == EV3Request.Request.CREATE_SAMPLE_PROVIDER_PUBLISH ? request.floatValue : 0f);
									System.out.println("Creating " + request.str + " on " + request.str2 + " with mode " + request.str3);
									Class<?> c = Class.forName(request.str); // sensor class
									Class<?>[] params = new Class<?>[1];
									params[0] = Port.class;
									Constructor<?> con = c.getConstructor(params);
									Object[] args = new Object[1];
									args[0] = LocalEV3.get().getPort(request.str2); // port name
									BaseSensor sensor = (BaseSensor) con.newInstance(args);
									SampleProvider provider;
									if (request.str3 == null) provider = (SampleProvider) sensor;
									else provider = sensor.getMode(request.str3);
									int pn = request.str2.charAt(1) - '1';
									if (frequency > 0) providers[pn] = new PublishFilter(provider,request.str4,frequency);
									else providers[pn] = provider;
									sensors[pn] = sensor;
									os.writeObject(reply);
									break;
								case SAMPLE_SIZE:
									if (providers[request.intValue] == null) throw new PortException("Port not open");
									reply.reply = providers[request.intValue].sampleSize();
									os.writeObject(reply);
									break;
								case FETCH_SAMPLE:
									if (providers[request.intValue] == null) throw new PortException("Port not open");
									reply.floats = new float[providers[request.intValue].sampleSize()];
									providers[request.intValue].fetchSample(reply.floats, 0);
									os.writeObject(reply);
									break;
								case CLOSE_SENSOR:
									if (sensors[request.intValue] == null) throw new PortException("Port not open");
									sensors[request.intValue].close();
									break;
								default:
									break;
								}
							}
							catch (Exception e)
							{
								e.printStackTrace();
								if(request.replyRequired)
								{
									reply.e = e;
									os.writeObject(reply);
								}
							}
						}
					}
				
				}
				catch(SocketException e)
				{
					System.out.println("Error reading from remote request socket: " + e);
					try
					{
						conn.close();
					}
					catch (IOException e1)
					{
						System.err.println("Error closing connection: " + e);
					}
				}
				
			}
			catch(Exception e)
			{
				System.err.println("Error accepting connection " + e);
				break;
			}		
		}
		try
		{
			ss.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}