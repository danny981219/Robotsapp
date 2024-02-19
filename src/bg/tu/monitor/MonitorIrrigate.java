package bg.tu.monitor;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.*;

import static bg.tu.common.Constants.*;

public class MonitorIrrigate extends JFrame implements WindowListener {

	private static final Color GREEN_COLOR = Color.getHSBColor(0.4f, 1.0f, 0.6f);
	private static final Color RED_COLOR = Color.getHSBColor(1.0f, 0.8f, 0.7f);
	private static final Color PEACH_COLOR = Color.getHSBColor(0.0f, 0.0f, 0.9f);
	private static final int maxWidth = 1240;
	private static final int maxHeignt = 700;

	Date systemDate = new Date();
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(systemDate);
		cal.add(Calendar.MONTH, 1);
		systemDate = cal.getTime();
	}

	JButton btnMonitoring;
	JLabel historyLabel;

	JDatePickerImpl historyDatePicker;

	JButton historyButton;

	JButton historyMinusButton;

	JButton historyPlusButton;

	PanelMain dpnl = new PanelMain();
	PanelHeader dpnlHeader = new PanelHeader();

	PanelRobot dpRobot;

	List<JComponent> components = new ArrayList<>();

	Map<Integer, JButton> robotButtons = new HashMap<>();

   	boolean monitoringRunning = false;

	Font font = new Font("Arial", Font.PLAIN, 12);

	int maxRobots = 4;

    public MonitorIrrigate(Map<Integer, String> config) {
		//float[] c = Color.RGBtoHSB(0xe6, 0xe6, 0xe6, null);
		this.maxRobots = config.size();
		dpRobot = new PanelRobot(systemDate, maxRobots, config);
        initUI();
		timer.start();
    }
        
    public final void initUI() {
        
    	dpnl.setLayout(new BoxLayout(dpnl, BoxLayout.Y_AXIS));
        add(dpnl);

		dpnlHeader.setMaximumSize(new Dimension(maxWidth, 40));
		dpnlHeader.setLayout(null);
		dpnlHeader.setBorder(BorderFactory.createLineBorder(Color.white, 2));
		Font font = new Font("Arial", Font.PLAIN, 12);
		dpnlHeader.setFont(font);
		initButtons();

		dpRobot.setMaximumSize(new Dimension(maxWidth, maxHeignt - 40));
		dpRobot.setBorder(BorderFactory.createLineBorder(Color.white, 2));

		dpnl.add(dpnlHeader);
		dpnl.add(dpRobot);

    	setSize(maxWidth, maxHeignt);
        setTitle("1D Filter");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.addWindowListener(new WindowListener() {
            
        	
            public void windowClosing(WindowEvent e) {

				stopMonitoring();

            	dpRobot.saveData();

				dpRobot.saveConfiguration();

				System.exit(-1);
            }

            @Override 
            public void windowOpened(WindowEvent e) {}

            @Override 
            public void windowClosed(WindowEvent e) {}

            @Override 
            public void windowIconified(WindowEvent e) {}

            @Override 
            public void windowDeiconified(WindowEvent e) {}

            @Override 
            public void windowActivated(WindowEvent e) {}

            @Override 
            public void windowDeactivated(WindowEvent e) {}

        });
    }

	private final void initButtons() {
		btnMonitoring = new JButton("Connect");
		btnMonitoring.setBounds(5,5,120,20);
		btnMonitoring.setSize(120,20);
		btnMonitoring.setForeground(Color.black);
		btnMonitoring.setBackground(RED_COLOR);
		btnMonitoring.setFont(font);
		btnMonitoring.setVisible(true);
		dpnlHeader.add(btnMonitoring);

		// ------------------------------------------------------------------------

		JLabel systemDateLabel = new JLabel("Current");
		systemDateLabel.setBounds(420,5,100,20);
		systemDateLabel.setForeground(Color.black);
		dpnlHeader.add(systemDateLabel);

		Properties cp = new Properties();
		cp.put("text.today", "Today");
		cp.put("text.month", "Month");
		cp.put("text.year", "Year");
		JDatePanelImpl systemDatePanel = new JDatePanelImpl(new UtilDateModel(systemDate), cp);
		JDatePickerImpl systemDatePicker = new JDatePickerImpl(systemDatePanel, new DateLabelFormatter());
		systemDatePicker.setBounds(470, 5, 120, 20);
		systemDatePicker.setForeground(Color.black);
		dpnlHeader.add(systemDatePicker);

		JButton systemDateButton = new JButton("Show");
		systemDateButton.setBounds(600,5,80,20);
		systemDateButton.setMaximumSize(new Dimension(80, 10));
		systemDateButton.setForeground(Color.black);
		systemDateButton.setBackground(GREEN_COLOR);
		systemDateButton.setFont(font);
		dpnlHeader.add(systemDateButton);

		JButton systemDateMinusButton = new JButton("-");
		systemDateMinusButton.setBounds(680,5,50,20);
		systemDateMinusButton.setMaximumSize(new Dimension(50, 10));
		systemDateMinusButton.setForeground(Color.black);
		systemDateMinusButton.setBackground(GREEN_COLOR);
		systemDateMinusButton.setFont(font);
		dpnlHeader.add(systemDateMinusButton);

		JButton systemDatePlusButton = new JButton("+");
		systemDatePlusButton.setBounds(730,5,50,20);
		systemDatePlusButton.setMaximumSize(new Dimension(50, 10));
		systemDatePlusButton.setForeground(Color.black);
		systemDatePlusButton.setBackground(GREEN_COLOR);
		systemDatePlusButton.setFont(font);
		dpnlHeader.add(systemDatePlusButton);

		// ------------------------------------------------------------------------

		historyLabel = new JLabel("History");
		historyLabel.setBounds(850,5,100,20);
		historyLabel.setForeground(Color.black);
		dpnlHeader.add(historyLabel);

		Properties hp = new Properties();
		hp.put("text.today", "Today");
		hp.put("text.month", "Month");
		hp.put("text.year", "Year");
		JDatePanelImpl historyDatePanel = new JDatePanelImpl(new UtilDateModel(), hp);
		historyDatePicker = new JDatePickerImpl(historyDatePanel, new DateLabelFormatter());
		historyDatePicker.setBounds(900, 5, 120, 20);
		historyDatePicker.setForeground(Color.black);
		dpnlHeader.add(historyDatePicker);

		historyButton = new JButton("Show");
		historyButton.setBounds(1030,5,80,20);
		historyButton.setMaximumSize(new Dimension(80, 10));
		historyButton.setForeground(Color.black);
		historyButton.setBackground(RED_COLOR);
		historyButton.setFont(font);
		dpnlHeader.add(historyButton);

		historyMinusButton = new JButton("-");
		historyMinusButton.setBounds(1110,5,50,20);
		historyMinusButton.setMaximumSize(new Dimension(50, 10));
		historyMinusButton.setForeground(Color.black);
		historyMinusButton.setBackground(RED_COLOR);
		historyMinusButton.setFont(font);
		dpnlHeader.add(historyMinusButton);

		historyPlusButton = new JButton("+");
		historyPlusButton.setBounds(1160,5,50,20);
		historyPlusButton.setMaximumSize(new Dimension(50, 10));
		historyPlusButton.setForeground(Color.black);
		historyPlusButton.setBackground(RED_COLOR);
		historyPlusButton.setFont(font);
		dpnlHeader.add(historyPlusButton);

		// ------------------------------------------------------------------------

		btnMonitoring.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (! monitoringRunning) {
					dpRobot.initData(systemDate);
					startMonitoring();
				} else {
					stopMonitoring();
					dpRobot.saveData();
				}
			}
		});

		systemDateButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				btnMonitoring.setVisible(true);
				systemDateButton.setBackground(GREEN_COLOR);
				systemDateMinusButton.setBackground(GREEN_COLOR);
				systemDatePlusButton.setBackground(GREEN_COLOR);
				historyButton.setBackground(RED_COLOR);
				historyMinusButton.setBackground(RED_COLOR);
				historyPlusButton.setBackground(RED_COLOR);
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, systemDatePicker.getModel().getYear());
				cal.set(Calendar.MONTH, systemDatePicker.getModel().getMonth());
				cal.set(Calendar.DATE, systemDatePicker.getModel().getDay());
				systemDate = cal.getTime();

				refreshData();
				dpRobot.repaint();
			}
		});

		historyButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				btnMonitoring.setVisible(false);
				systemDateButton.setBackground(RED_COLOR);
				systemDateMinusButton.setBackground(RED_COLOR);
				systemDatePlusButton.setBackground(RED_COLOR);
				historyButton.setBackground(GREEN_COLOR);
				historyMinusButton.setBackground(GREEN_COLOR);
				historyPlusButton.setBackground(GREEN_COLOR);
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, historyDatePicker.getModel().getYear());
				cal.set(Calendar.MONTH, historyDatePicker.getModel().getMonth());
				cal.set(Calendar.DATE, historyDatePicker.getModel().getDay());
				systemDate = cal.getTime();
				dpRobot.readData(systemDate);
				dpRobot.repaint();
			}
		});

		systemDateMinusButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, systemDatePicker.getModel().getYear());
				cal.set(Calendar.MONTH, systemDatePicker.getModel().getMonth());
				cal.set(Calendar.DATE, systemDatePicker.getModel().getDay());
				cal.add(Calendar.DATE, -1);
				systemDate = cal.getTime();
				systemDatePicker.getModel().
						setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

				refreshData();
				dpRobot.repaint();
			}
		});

		systemDatePlusButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, systemDatePicker.getModel().getYear());
				cal.set(Calendar.MONTH, systemDatePicker.getModel().getMonth());
				cal.set(Calendar.DATE, systemDatePicker.getModel().getDay());
				cal.add(Calendar.DATE, 1);
				systemDate = cal.getTime();
				systemDatePicker.getModel().
						setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

				refreshData();
				dpRobot.repaint();
			}
		});

		historyMinusButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, historyDatePicker.getModel().getYear());
				cal.set(Calendar.MONTH, historyDatePicker.getModel().getMonth());
				cal.set(Calendar.DATE, historyDatePicker.getModel().getDay());
				cal.add(Calendar.DATE, -1);
				historyDatePicker.getModel().
						setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
				systemDate = cal.getTime();
				dpRobot.readData(systemDate);
				dpRobot.repaint();
			}
		});

		historyPlusButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, historyDatePicker.getModel().getYear());
				cal.set(Calendar.MONTH, historyDatePicker.getModel().getMonth());
				cal.set(Calendar.DATE, historyDatePicker.getModel().getDay());
				cal.add(Calendar.DATE, 1);
				historyDatePicker.getModel().
						setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
				systemDate = cal.getTime();
				dpRobot.readData(systemDate);
				dpRobot.repaint();
			}
		});

		initToggleRobotButton(0, 130);
		initToggleRobotButton(1, 180);
		initToggleRobotButton(2, 230);
		initToggleRobotButton(3, 280);
		initToggleRobotButton(-1, 330);
	}

	private void initToggleRobotButton(int robot, int x0) {
		JButton btnRobot = new JButton("" + (robot == -1 ? "All" : (robot + 1)));
		btnRobot.setBounds(x0,5,50,20);
		btnRobot.setSize(50,20);
		btnRobot.setForeground(Color.black);
		btnRobot.setBackground(robot < maxRobots || robot == -1 ? GREEN_COLOR : Color.DARK_GRAY);
		btnRobot.setFont(font);
		btnRobot.setVisible(true);
		btnRobot.setEnabled(robot < maxRobots || robot == -1);
		dpnlHeader.add(btnRobot);

		robotButtons.put(robot, btnRobot);

		btnRobot.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				dpRobot.setRobotVisible(robot);

				for (Map.Entry<Integer, JButton> btn : robotButtons.entrySet()) {
					if (dpRobot.isRobotVisible(btn.getKey())) {
						btn.getValue().setBackground(GREEN_COLOR);
					} else {
						btn.getValue().setBackground(RED_COLOR);
					}
				}

				for (JComponent bth : components) {
					dpRobot.remove(bth);
				}
				components.clear();
				if (dpRobot.visibleRobotsCount() == 1) {
					initGraphButtons(dpRobot.getFirstVisibleRobot());
				}
				dpRobot.repaint();
			}
		});
	}

	private final void initGraphButtons(int robot) {
		GroupLayout layoutRobot = new GroupLayout(dpRobot);
		dpRobot.setLayout(layoutRobot);
		layoutRobot.setAutoCreateGaps(true);
		layoutRobot.setAutoCreateContainerGaps(true);

		JLabel robotLabel = new JLabel(dpRobot.getRoobotName(robot));
		robotLabel.setForeground(Color.cyan);
		components.add(robotLabel);

		JTextField tMaxThresholdInput = new JTextField(dpRobot.getTemperatureThreshold("Max", robot).toString());
		tMaxThresholdInput.setMaximumSize(new Dimension(100, 10));
		tMaxThresholdInput.setForeground(Color.red);
		tMaxThresholdInput.setBackground(Color.black);
		tMaxThresholdInput.setEnabled(monitoringRunning);
		JButton tMaxThresholdButton = new JButton("Max Threshold");
		tMaxThresholdButton.setMaximumSize(new Dimension(100, 10));
		tMaxThresholdButton.setForeground(Color.black);
		tMaxThresholdButton.setBackground(RED_COLOR);
		tMaxThresholdButton.setFont(font);
		tMaxThresholdButton.setEnabled(monitoringRunning);
		components.add(tMaxThresholdInput);
		components.add(tMaxThresholdButton);

		tMaxThresholdButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				String threshold = tMaxThresholdInput.getText();
				dpRobot.setTemperatureThreshold("Max", Integer.parseInt(threshold), robot);
				dpRobot.repaint();
				dpRobot.sendConfiguration(robot);
			}
		});

		JTextField tMinThresholdInput = new JTextField(dpRobot.getTemperatureThreshold("Min", robot).toString());
		tMinThresholdInput.setMaximumSize(new Dimension(100, 10));
		tMinThresholdInput.setForeground(Color.green);
		tMinThresholdInput.setBackground(Color.black);
		tMinThresholdInput.setEnabled(monitoringRunning);
		JButton tMinThresholdButton = new JButton("Min Threshold");
		tMinThresholdButton.setMaximumSize(new Dimension(100, 10));
		tMinThresholdButton.setForeground(Color.black);
		tMinThresholdButton.setBackground(GREEN_COLOR);
		tMinThresholdButton.setFont(font);
		tMinThresholdButton.setEnabled(monitoringRunning);
		components.add(tMinThresholdInput);
		components.add(tMinThresholdButton);

		tMinThresholdButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				String threshold = tMinThresholdInput.getText();
				dpRobot.setTemperatureThreshold("Min", Integer.parseInt(threshold), robot);
				dpRobot.repaint();
				dpRobot.sendConfiguration(robot);
			}
		});

		JTextField hMaxThresholdInput = new JTextField(dpRobot.getHumidityThreshold("Max", robot).toString());
		hMaxThresholdInput.setMaximumSize(new Dimension(100, 10));
		hMaxThresholdInput.setForeground(GREEN_COLOR);
		hMaxThresholdInput.setBackground(Color.black);
		hMaxThresholdInput.setEnabled(monitoringRunning);
		JButton hMaxThresholdButton = new JButton("Max Threshold");
		hMaxThresholdButton.setMaximumSize(new Dimension(100, 10));
		hMaxThresholdButton.setForeground(Color.black);
		hMaxThresholdButton.setBackground(GREEN_COLOR);
		hMaxThresholdButton.setFont(font);
		hMaxThresholdButton.setEnabled(monitoringRunning);
		components.add(hMaxThresholdInput);
		components.add(hMaxThresholdButton);

		hMaxThresholdButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				String threshold = hMaxThresholdInput.getText();
				dpRobot.setHumidityThreshold("Max", Integer.parseInt(threshold), robot);
				dpRobot.repaint();
				dpRobot.sendConfiguration(robot);
			}
		});

		JTextField hMinThresholdInput = new JTextField(dpRobot.getHumidityThreshold("Min", robot).toString());
		hMinThresholdInput.setMaximumSize(new Dimension(100, 10));
		hMinThresholdInput.setForeground(RED_COLOR);
		hMinThresholdInput.setBackground(Color.black);
		hMinThresholdInput.setEnabled(monitoringRunning);
		JButton hMinThresholdButton = new JButton("Min Threshold");
		hMinThresholdButton.setMaximumSize(new Dimension(100, 10));
		hMinThresholdButton.setForeground(Color.black);
		hMinThresholdButton.setBackground(RED_COLOR);
		hMinThresholdButton.setFont(font);
		hMinThresholdButton.setEnabled(monitoringRunning);
		components.add(hMinThresholdInput);
		components.add(hMinThresholdButton);

		hMinThresholdButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				String threshold = hMinThresholdInput.getText();
				dpRobot.setHumidityThreshold("Min", Integer.parseInt(threshold), robot);
				dpRobot.repaint();
				dpRobot.sendConfiguration(robot);
			}
		});

		JButton togglePumpButton = new JButton("Toggle pump");
		togglePumpButton.setMaximumSize(new Dimension(100, 10));
		togglePumpButton.setForeground(Color.black);
		togglePumpButton.setBackground(dpRobot.isPumpOn(robot) ? GREEN_COLOR : RED_COLOR);
		togglePumpButton.setFont(font);
		togglePumpButton.setEnabled(monitoringRunning);
		components.add(togglePumpButton);

		togglePumpButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(systemDate);
				if (dpRobot.isPumpOn(robot)) {
					dpRobot.pumpOff(robot);
				} else {
					dpRobot.pumpOn(robot);
				}
				togglePumpButton.setBackground(dpRobot.isPumpOn(robot) ? GREEN_COLOR : RED_COLOR);
				dpRobot.repaint();
			}
		});

		layoutRobot.setHorizontalGroup(
				layoutRobot.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(robotLabel)
						.addComponent(tMaxThresholdButton).addComponent(tMaxThresholdInput)
						.addComponent(tMinThresholdButton).addComponent(tMinThresholdInput)
						.addComponent(hMaxThresholdButton).addComponent(hMaxThresholdInput)
						.addComponent(hMinThresholdButton).addComponent(hMinThresholdInput)
						.addComponent(togglePumpButton));
		layoutRobot.setVerticalGroup(
				layoutRobot.createSequentialGroup()
						.addComponent(robotLabel).addGap(10)
						.addComponent(tMaxThresholdButton).addGap(10)
						.addComponent(tMaxThresholdInput).addGap(20)
						.addComponent(tMinThresholdButton).addGap(10)
						.addComponent(tMinThresholdInput).addGap(120)
						.addComponent(hMaxThresholdButton).addGap(10)
						.addComponent(hMaxThresholdInput).addGap(20)
						.addComponent(hMinThresholdButton).addGap(10)
						.addComponent(hMinThresholdInput).addGap(80)
						.addComponent(togglePumpButton));
	}

	private int getRandomNumber(float min, float max) {
		return (int) ((Math.random() * (max - min + 1)) + min);
	}

	private Timer timer = new Timer(50, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			//timerEvent();
			if (comunicationIsRunning() && ! monitoringRunning) {
				btnMonitoring.setLabel("Disconnect");
				btnMonitoring.setBackground(GREEN_COLOR);
				monitoringRunning = true;
			} else if (! comunicationIsRunning() && monitoringRunning){
				btnMonitoring.setLabel("Connect");
				btnMonitoring.setBackground(RED_COLOR);
				monitoringRunning = false;
			} else {
				dpRobot.repaint();
				return;
			}
			for (Component component : components) {
				if (component instanceof JButton || component instanceof JTextField) {
					component.setEnabled(monitoringRunning);
				}
			}
			historyLabel.setVisible(!monitoringRunning);
			historyDatePicker.setVisible(!monitoringRunning);
			historyButton.setVisible(!monitoringRunning);
			historyMinusButton.setVisible(!monitoringRunning);
			historyPlusButton.setVisible(!monitoringRunning);
			dpRobot.repaint();
		}
	});

	private void startMonitoring() {
		//timer.start();
		for (int i = 0; i < maxRobots; i++) {
			dpRobot.connect(i);
		}
	}

	private void stopMonitoring() {
		// timer.stop();
		for (int i = 0; i < maxRobots; i++) {
			dpRobot.disconnect(i);
		}
	}

	private boolean comunicationIsRunning() {
		for (int i = 0; i < maxRobots; i++) {
			if (! dpRobot.comunicationIsRunning(i)) {
				return  false;
			}
		}
		return true;
	}

	private void refreshData() {
		if (monitoringRunning) {
			dpRobot.saveData();
			dpRobot.initData(systemDate);
			dpRobot.sendDate();
		} else {
			dpRobot.readData(systemDate);
		}
	}

   /*public void timerEvent() {
		for (int i = 0; i < maxRobots; i++) {
			int count = dpRobot.getTemperaturesCount(i);
			if (count >= 240) {
				continue;
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(systemDate);

			float tempMin = temperatureMin[cal.get(Calendar.MONTH)][count / 10];
			float tempMax = temperatureMax[cal.get(Calendar.MONTH)][count / 10];
			float humMin = humidityMin[cal.get(Calendar.MONTH)][count / 10];
			float humMax = humidityMax[cal.get(Calendar.MONTH)][count / 10];

			if (count == 0) {
				dpRobot.setTemperature(getRandomNumber(tempMin, tempMax), i);
				dpRobot.setHumidity(getRandomNumber(humMin, humMax), i);
			} else {
				float tempMinPrev = temperatureMin[cal.get(Calendar.MONTH)][(count - 1) / 10];
				float tempMaxPrev = temperatureMax[cal.get(Calendar.MONTH)][(count - 1) / 10];
				float tempCoeff = (tempMax - tempMin) / (tempMaxPrev - tempMinPrev);
				int prevTempValue = dpRobot.getTemperature(count - 1, i);
				int newTempValue = (int) tempMin + (int) (0.5 + tempCoeff * (prevTempValue - tempMinPrev));
				newTempValue = Math.max(Math.min(getRandomNumber(newTempValue - 1.5f, newTempValue + 1.5f),
						(int) tempMax), (int) tempMin);
				dpRobot.setTemperature(newTempValue, i);

				//if (i == 0) System.out.println("tempMin=" + tempMin + "; tempMax=" + tempMax +
				//		"; tempCoeff=" + tempCoeff + "; prevTempValue=" + prevTempValue +
				//		"; newTempValue=" + newTempValue);

				float humMinPrev = humidityMin[cal.get(Calendar.MONTH)][(count - 1) / 10];
				float humMaxPrev = humidityMax[cal.get(Calendar.MONTH)][(count - 1) / 10];
				float humCoeff = (humMax - humMin) / (humMaxPrev - humMinPrev);
				int prevHumValue = dpRobot.getHumidity(count - 1, i);
				int newHumValue = (int) humMin + (int) (humCoeff * (prevHumValue - humMinPrev));
				if (dpRobot.isPumpOn(i)) {
					newHumValue = Math.max(Math.min(getRandomNumber(newHumValue, newHumValue + 5.0f),
							100), (int) humMin);
				} else {
					newHumValue = Math.max(Math.min(getRandomNumber(newHumValue - 1.5f, newHumValue + 1.5f),
							(int) humMax), (int) humMin);
				}

				if (i == 0) System.out.println("humMin=" + humMin + "; humMax=" + humMax +
						"; humCoeff=" + humCoeff + "; prevHumValue=" + prevHumValue +
						"; newHumValue=" + newHumValue + " >> " + getRandomNumber(humMin, humMax));

				dpRobot.setHumidity(newHumValue, i);
			}

			float averageTemperature = dpRobot.getAverageTemperature(i);
			float averageHumidity = dpRobot.getAverageHumidity(i);
			if ((count == 60 || count == 200) && ! dpRobot.isPumpOn(i) &&
				averageTemperature > dpRobot.getTemperatureThreshold("Min", i) &&
				averageHumidity < dpRobot.getHumidityThreshold("Max", i)) {
				int timeOn = pumpMaxTimeAuto[cal.get(Calendar.MONTH)];
				timeOn *= ((averageTemperature - dpRobot.getTemperatureThreshold("Min", i)) /
							(dpRobot.getTemperatureThreshold("Max", i) - dpRobot.getTemperatureThreshold("Min", i))) *
						  ((dpRobot.getHumidityThreshold("Max", i) - averageHumidity) /
								  dpRobot.getHumidityThreshold("Max", i));
				dpRobot.togglePump(i, timeOn);
				//System.out.println("Robot " + i + " : start pump for " + timeOn + " steps");
			}

			dpRobot.setPump(getRandomNumber(9, 10), i);
		}
		dpRobot.repaint();
	}*/

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
}