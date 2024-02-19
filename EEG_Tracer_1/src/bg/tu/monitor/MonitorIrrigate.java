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

public class MonitorIrrigate extends JFrame implements WindowListener {

	public enum ComunicationStatus { CONNECTED, DISCONNECTED, WARNING }

	private static final Color GREEN_COLOR = Color.getHSBColor(0.4f, 1.0f, 0.6f);
	private static final Color RED_COLOR = Color.getHSBColor(1.0f, 0.8f, 0.7f);
	private static final Color YELLOW_COLOR = Color.getHSBColor(0.12f, 0.9f, 0.9f);
	private static final Color PEACH_COLOR = Color.getHSBColor(0.7f, 0.5f, 0.8f);
	private static final int maxWidth = 1240;
	private static final int maxHeignt = 700;

	Date systemDate = new Date();
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(systemDate);
		cal.add(Calendar.MONTH, 1);
		systemDate = cal.getTime();
	}

	JButton btnConnect, btnReconnect;
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

	ComunicationStatus comunicationStatus = ComunicationStatus.DISCONNECTED;

	Font font = new Font("Arial", Font.PLAIN, 12);

	int maxRobots = 4;

	Map<Integer, RobotConfiguration> robotConfiguration;

    public MonitorIrrigate(Map<Integer, RobotConfiguration> robotConfiguration) {
		//float[] c = Color.RGBtoHSB(0xe6, 0xe6, 0xe6, null);
		this.maxRobots = robotConfiguration.size();
		this.robotConfiguration = robotConfiguration;
		dpRobot = new PanelRobot(systemDate, maxRobots, robotConfiguration);
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
		btnConnect = new JButton("Connect");
		btnConnect.setBounds(5,5,100,20);
		btnConnect.setSize(100,20);
		btnConnect.setForeground(Color.BLACK);
		btnConnect.setBackground(RED_COLOR);
		btnConnect.setFont(font);
		btnConnect.setVisible(true);
		dpnlHeader.add(btnConnect);

		btnReconnect = new JButton("Reconnect");
		btnReconnect.setBounds(110,5,100,20);
		btnReconnect.setSize(100,20);
		btnReconnect.setForeground(Color.BLACK);
		btnReconnect.setBackground(YELLOW_COLOR);
		btnReconnect.setFont(font);
		btnReconnect.setVisible(false);
		dpnlHeader.add(btnReconnect);

		// ------------------------------------------------------------------------

		JLabel systemDateLabel = new JLabel("Current");
		systemDateLabel.setBounds(535,5,100,20);
		systemDateLabel.setForeground(Color.black);
		dpnlHeader.add(systemDateLabel);

		Properties cp = new Properties();
		cp.put("text.today", "Today");
		cp.put("text.month", "Month");
		cp.put("text.year", "Year");
		JDatePanelImpl systemDatePanel = new JDatePanelImpl(new UtilDateModel(systemDate), cp);
		JDatePickerImpl systemDatePicker = new JDatePickerImpl(systemDatePanel, new DateLabelFormatter());
		systemDatePicker.setBounds(585, 5, 110, 20);
		systemDatePicker.setForeground(Color.black);
		dpnlHeader.add(systemDatePicker);

		JButton systemDateButton = new JButton("Show");
		systemDateButton.setBounds(700,5,65,20);
		systemDateButton.setMaximumSize(new Dimension(65, 10));
		systemDateButton.setForeground(Color.black);
		systemDateButton.setBackground(GREEN_COLOR);
		systemDateButton.setFont(font);
		dpnlHeader.add(systemDateButton);

		JButton systemDateMinusButton = new JButton("-");
		systemDateMinusButton.setBounds(765,5,45,20);
		systemDateMinusButton.setMaximumSize(new Dimension(50, 10));
		systemDateMinusButton.setForeground(Color.black);
		systemDateMinusButton.setBackground(GREEN_COLOR);
		systemDateMinusButton.setFont(font);
		dpnlHeader.add(systemDateMinusButton);

		JButton systemDatePlusButton = new JButton("+");
		systemDatePlusButton.setBounds(810,5,45,20);
		systemDatePlusButton.setMaximumSize(new Dimension(50, 10));
		systemDatePlusButton.setForeground(Color.black);
		systemDatePlusButton.setBackground(GREEN_COLOR);
		systemDatePlusButton.setFont(font);
		dpnlHeader.add(systemDatePlusButton);

		// ------------------------------------------------------------------------

		historyLabel = new JLabel("History");
		historyLabel.setBounds(895,5,100,20);
		historyLabel.setForeground(Color.black);
		dpnlHeader.add(historyLabel);

		Properties hp = new Properties();
		hp.put("text.today", "Today");
		hp.put("text.month", "Month");
		hp.put("text.year", "Year");
		JDatePanelImpl historyDatePanel = new JDatePanelImpl(new UtilDateModel(), hp);
		historyDatePicker = new JDatePickerImpl(historyDatePanel, new DateLabelFormatter());
		historyDatePicker.setBounds(945, 5, 110, 20);
		historyDatePicker.setForeground(Color.black);
		dpnlHeader.add(historyDatePicker);

		historyButton = new JButton("Show");
		historyButton.setBounds(1060,5,65,20);
		historyButton.setMaximumSize(new Dimension(65, 10));
		historyButton.setForeground(Color.black);
		historyButton.setBackground(RED_COLOR);
		historyButton.setFont(font);
		dpnlHeader.add(historyButton);

		historyMinusButton = new JButton("-");
		historyMinusButton.setBounds(1125,5,45,20);
		historyMinusButton.setMaximumSize(new Dimension(50, 10));
		historyMinusButton.setForeground(Color.black);
		historyMinusButton.setBackground(RED_COLOR);
		historyMinusButton.setFont(font);
		dpnlHeader.add(historyMinusButton);

		historyPlusButton = new JButton("+");
		historyPlusButton.setBounds(1170,5,45,20);
		historyPlusButton.setMaximumSize(new Dimension(50, 10));
		historyPlusButton.setForeground(Color.black);
		historyPlusButton.setBackground(RED_COLOR);
		historyPlusButton.setFont(font);
		dpnlHeader.add(historyPlusButton);

		// ------------------------------------------------------------------------

		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (comunicationStatus == ComunicationStatus.DISCONNECTED) {
					dpRobot.initData(systemDate);
					startMonitoring();
				} else {
					stopMonitoring();
					dpRobot.saveData();
				}
			}
		});

		btnReconnect.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (comunicationStatus != ComunicationStatus.DISCONNECTED) {
					stopMonitoring();
					dpRobot.saveData();
				}
				dpRobot.initData(systemDate);
				startMonitoring();
			}
		});

		systemDateButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				btnConnect.setVisible(true);
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
				btnConnect.setVisible(false);
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

		initToggleRobotButton(0, 240);
		initToggleRobotButton(1, 290);
		initToggleRobotButton(2, 340);
		initToggleRobotButton(3, 390);
		initToggleRobotButton(-1, 440);
	}

	private void initToggleRobotButton(int robot, int x0) {
		JButton btnRobot = new JButton("" + (robot == -1 ? "All" : (robot + 1)));
		btnRobot.setBounds(x0,5,50,20);
		btnRobot.setSize(50,20);
		btnRobot.setForeground(Color.black);
		if (robot < maxRobots || robot == -1) {
			btnRobot.setBackground(RED_COLOR);
			btnRobot.setBorder(BorderFactory.createLineBorder(PEACH_COLOR, 2));
		} else {
			btnRobot.setBackground(Color.DARK_GRAY);
		}
		btnRobot.setFont(font);
		btnRobot.setVisible(true);
		btnRobot.setEnabled(robot < maxRobots || robot == -1);
		dpnlHeader.add(btnRobot);

		robotButtons.put(robot, btnRobot);

		btnRobot.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				dpRobot.setRobotVisible(robot);

				for (Map.Entry<Integer, JButton> btn : robotButtons.entrySet()) {
					if (btn.getValue().isEnabled()) {
						if (dpRobot.isRobotVisible(btn.getKey())) {
							btn.getValue().setBorder(BorderFactory.createLineBorder(PEACH_COLOR, 2));
						} else {
							btn.getValue().setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
						}
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
		tMaxThresholdInput.setForeground(Color.RED);
		tMaxThresholdInput.setBackground(Color.BLACK);
		tMaxThresholdInput.setEnabled(false);
		JButton tMaxThresholdButton = new JButton("Max Threshold");
		tMaxThresholdButton.setMaximumSize(new Dimension(100, 10));
		tMaxThresholdButton.setForeground(Color.BLACK);
		tMaxThresholdButton.setBackground(RED_COLOR);
		tMaxThresholdButton.setFont(font);
		tMaxThresholdButton.setEnabled(false);
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
		tMinThresholdInput.setForeground(GREEN_COLOR);
		tMinThresholdInput.setBackground(Color.BLACK);
		tMinThresholdInput.setEnabled(false);
		JButton tMinThresholdButton = new JButton("Min Threshold");
		tMinThresholdButton.setMaximumSize(new Dimension(100, 10));
		tMinThresholdButton.setForeground(Color.BLACK);
		tMinThresholdButton.setBackground(GREEN_COLOR);
		tMinThresholdButton.setFont(font);
		tMinThresholdButton.setEnabled(false);
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
		hMaxThresholdInput.setBackground(Color.BLACK);
		hMaxThresholdInput.setEnabled(false);
		JButton hMaxThresholdButton = new JButton("Max Threshold");
		hMaxThresholdButton.setMaximumSize(new Dimension(100, 10));
		hMaxThresholdButton.setForeground(Color.BLACK);
		hMaxThresholdButton.setBackground(GREEN_COLOR);
		hMaxThresholdButton.setFont(font);
		hMaxThresholdButton.setEnabled(false);
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
		hMinThresholdInput.setBackground(Color.BLACK);
		hMinThresholdInput.setEnabled(false);
		JButton hMinThresholdButton = new JButton("Min Threshold");
		hMinThresholdButton.setMaximumSize(new Dimension(100, 10));
		hMinThresholdButton.setForeground(Color.BLACK);
		hMinThresholdButton.setBackground(RED_COLOR);
		hMinThresholdButton.setFont(font);
		hMinThresholdButton.setEnabled(false);
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
		togglePumpButton.setEnabled(false);
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
			comunicationStatus = comunicationIsRunning();
			if (comunicationStatus == ComunicationStatus.CONNECTED) {
				btnConnect.setLabel("Disconnect");
				btnConnect.setBackground(GREEN_COLOR);
				btnReconnect.setVisible(false);
			} else if (comunicationStatus == ComunicationStatus.DISCONNECTED){
				btnConnect.setLabel("Connect");
				btnConnect.setBackground(RED_COLOR);
				btnReconnect.setVisible(false);
			} else if (comunicationStatus == ComunicationStatus.WARNING){
				btnConnect.setLabel("Disconnect");
				btnConnect.setBackground(GREEN_COLOR);
				btnReconnect.setVisible(true);
			} else {
				dpRobot.repaint();
				return;
			}
			if (dpRobot.visibleRobotsCount() == 1) {
				for (Component component : components) {
					if (component instanceof JButton || component instanceof JTextField) {
						component.setEnabled(dpRobot.comunicationIsRunning(dpRobot.getFirstVisibleRobot()));
					}
				}
			}
			historyLabel.setVisible(comunicationStatus == ComunicationStatus.DISCONNECTED);
			historyDatePicker.setVisible(comunicationStatus == ComunicationStatus.DISCONNECTED);
			historyButton.setVisible(comunicationStatus == ComunicationStatus.DISCONNECTED);
			historyMinusButton.setVisible(comunicationStatus == ComunicationStatus.DISCONNECTED);
			historyPlusButton.setVisible(comunicationStatus == ComunicationStatus.DISCONNECTED);
			for (Map.Entry<Integer, JButton> robotBtn : robotButtons.entrySet()) {
				if (robotBtn.getValue().isEnabled()) {
					if (robotBtn.getKey() == -1) {
						switch (comunicationStatus) {
							case CONNECTED:
								robotBtn.getValue().setBackground(GREEN_COLOR);
								break;
							case DISCONNECTED:
								robotBtn.getValue().setBackground(RED_COLOR);
								break;
							case WARNING:
								robotBtn.getValue().setBackground(YELLOW_COLOR);
								break;
						}
					} else {
						robotBtn.getValue().setBackground(
								dpRobot.comunicationIsRunning(robotBtn.getKey()) ? GREEN_COLOR : RED_COLOR);
					}
				}
			}

			dpRobot.repaint();
		}
	});

	private void startMonitoring() {
		//timer.start();
		for (int i = 0; i < maxRobots; i++) {
			if (robotConfiguration.get(i + 1).isCanConnect()) {
				dpRobot.connect(i);
			}
		}
	}

	private void stopMonitoring() {
		// timer.stop();
		for (int i = 0; i < maxRobots; i++) {
			dpRobot.disconnect(i);
		}
	}

	private ComunicationStatus comunicationIsRunning() {
		int countConnected = 0;
		for (int i = 0; i < maxRobots; i++) {
			if (dpRobot.comunicationIsRunning(i)) {
				countConnected++;
			}
		}
		if (countConnected == 0) {
			return ComunicationStatus.DISCONNECTED;
		} else if (countConnected == maxRobots) {
			return ComunicationStatus.CONNECTED;
		} else {
			return ComunicationStatus.WARNING;
		}
	}

	private void refreshData() {
		if (comunicationStatus != ComunicationStatus.DISCONNECTED) {
			dpRobot.saveData();
			dpRobot.initData(systemDate);
			dpRobot.sendDate();
		} else {
			dpRobot.readData(systemDate);
		}
	}

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

	public static class RobotConfiguration {
		private String name;
		private boolean canConnect;

		public RobotConfiguration() {
		}

		public RobotConfiguration(String name, boolean canConnect) {
			this.name = name;
			this.canConnect = canConnect;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isCanConnect() {
			return canConnect;
		}

		public void setCanConnect(boolean canConnect) {
			this.canConnect = canConnect;
		}
	}
}