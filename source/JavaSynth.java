package regan_danny.javasynth;

import javax.swing.JPanel;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

import java.awt.Color;

import javax.swing.JSlider;
import javax.swing.border.LineBorder;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.swing.ExponentialRangeModel;
import com.jsyn.swing.PortModelFactory;
import com.jsyn.unitgen.EnvelopeDAHDSR;
import com.jsyn.unitgen.FilterLowPass;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.PulseOscillator;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.Font;

import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;

public class JavaSynth extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ButtonGroup oscSelection = new ButtonGroup();
	private Synthesizer synth;
	private PulseOscillator pulse;
	private SawtoothOscillator saw;
	private SineOscillator lfo;
	private FilterLowPass filter;
	private static FilterLowPass volume;
	private EnvelopeDAHDSR filterEnv, ampEnv;
	private static Oscillator oscCircuit;
	private static Filter filterCircuit;
	private static LineOut lineOut;
	private JSlider pwSlider;
	private JSlider filterAttack, filterDecay, filterSustain, 
	filterRelease, ampAttack, ampDecay, ampSustain, ampRelease,
	cutoffFrqSlider, resonanceSlider, lfoRateSlider, volumeSlider;

	/**
	 * Create the panel.
	 */
	public JavaSynth() {
		UIManager.put("TitledBorder.border", new LineBorder(Color.DARK_GRAY, 1, true));
		UIManager.put("TitledBorder.font", new Font("Miriam Fixed", Font.BOLD, 12));
		
		synth = JSyn.createSynthesizer();
		//synth.setRealTime(false);
		synth.start();
		ampEnv = new EnvelopeDAHDSR();
		synth.add(oscCircuit = new Oscillator(ampEnv));//lfo = new SineOscillator()));
		pulse = new PulseOscillator();
		//pulse.frequency.set(100);
		saw = new SawtoothOscillator();
		//saw.frequency.set(100);
		synth.add(pulse);
		synth.add(saw);
		// initial set oscillator to pulse wave
		oscCircuit.selectOscillator(pulse);
		pulse.amplitude.set(0);
		saw.amplitude.set(0);
		filter = new FilterLowPass();
		lfo = new SineOscillator();
		filterEnv = new EnvelopeDAHDSR();
		
		volume = new FilterLowPass();
		synth.add(filterCircuit = new Filter(filter,lfo,filterEnv,volume));
		lineOut = new LineOut();
		synth.add(lineOut);
		lineOut.start();
		
		// connect circuits
		oscCircuit.getOutput().connect(filterCircuit.getInput());
		filterCircuit.getOutput().connect(0,lineOut.input,0);
		filterCircuit.getOutput().connect(0,lineOut.input,1);
		
		setBackground(new Color(169, 169, 169));
		setLayout(null);
		
		JPanel oscPanel = new JPanel();
		oscPanel.setBackground(new Color(169, 169, 169));
		oscPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Oscillator", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		oscPanel.setBounds(110, 11, 200, 260);
		add(oscPanel);
		oscPanel.setLayout(null);
		
		JPanel oscImages = new JPanel();
		oscImages.setBackground(new Color(169, 169, 169));
		oscImages.setBounds(60, 79, 40, 90);
		oscPanel.add(oscImages);
		oscImages.setLayout(new GridLayout(2, 1, 0, 0));
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBackground(new Color(169, 169, 169));
		lblNewLabel.setIcon(new ImageIcon(JavaSynth.class.getResource("/regan_danny/javasynth/images/pulse.png")));
		oscImages.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBackground(new Color(169, 169, 169));
		lblNewLabel_1.setIcon(new ImageIcon(JavaSynth.class.getResource("/regan_danny/javasynth/images/saw.png")));
		oscImages.add(lblNewLabel_1);
		
		// pulsewidth slider
		ExponentialRangeModel pwModel = PortModelFactory.createExponentialModel(pulse.width);
		pwSlider = new JSlider(pwModel);
		pwSlider.setMinorTickSpacing(5);
		pwSlider.setPaintLabels(true);
		pwSlider.setMajorTickSpacing(10);
		pwSlider.setMinimum(50);
		pwSlider.setMaximum(100);
		pwSlider.setPaintTicks(true);
		pwSlider.setBackground(new Color(169, 169, 169));
		pwSlider.setForeground(Color.DARK_GRAY);
		pwSlider.setToolTipText("");
		pwSlider.setBounds(110, 45, 80, 150);
		pwSlider.setUI(new CustomSliderUI(pwSlider));
		
		oscPanel.add(pwSlider);
		pwSlider.setOrientation(SwingConstants.VERTICAL);
		
		JLabel lblNewLabel_2 = new JLabel("Pulse Width");
		lblNewLabel_2.setFont(new Font("Miriam Fixed", Font.BOLD, 10));
		lblNewLabel_2.setBackground(new Color(169, 169, 169));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(110, 206, 80, 14);
		oscPanel.add(lblNewLabel_2);
		
		JPanel oscButtons = new JPanel();
		oscButtons.setBorder(new BevelBorder(BevelBorder.RAISED, new Color(128, 128, 128), null, new Color(128, 128, 128), new Color(0, 0, 0)));
		oscButtons.setBackground(new Color(169, 169, 169));
		oscButtons.setBounds(10, 79, 40, 90);
		oscPanel.add(oscButtons);
		oscButtons.setLayout(new GridLayout(2, 1, 0, 0));
		
		JToggleButton btnPulse = new JToggleButton("");
		btnPulse.setSelectedIcon(new ImageIcon(JavaSynth.class.getResource("/regan_danny/javasynth/images/toggleDown.png")));
		btnPulse.setIcon(new ImageIcon(JavaSynth.class.getResource("/regan_danny/javasynth/images/toggleUp.png")));
		btnPulse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(filterCircuit.getInput().isConnected()){
					filterCircuit.getInput().disconnectAll(0);
				}
				oscCircuit.selectOscillator(pulse);
				oscCircuit.getOutput().connect(filterCircuit.getInput());
				oscCircuit.amplitude.set(0);
			}
		});
		btnPulse.setSelected(true);
		oscSelection.add(btnPulse);
		oscButtons.add(btnPulse);
		
		JToggleButton btnSaw = new JToggleButton("");
		btnSaw.setSelectedIcon(new ImageIcon(JavaSynth.class.getResource("/regan_danny/javasynth/images/toggleDown.png")));
		btnSaw.setIcon(new ImageIcon(JavaSynth.class.getResource("/regan_danny/javasynth/images/toggleUp.png")));
		btnSaw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(filterCircuit.getInput().isConnected()){
					filterCircuit.getInput().disconnectAll(0);
				}
				oscCircuit.selectOscillator(saw);
				oscCircuit.getOutput().connect(filterCircuit.getInput());
				oscCircuit.amplitude.set(0);
			}
		});
		oscSelection.add(btnSaw);
		oscButtons.add(btnSaw);
		
		JPanel envPanel = new JPanel();
		envPanel.setBackground(new Color(169, 169, 169));
		envPanel.setBorder(new TitledBorder(null, "Envelope Generators", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		envPanel.setBounds(320, 11, 400, 260);
		add(envPanel);
		envPanel.setLayout(null);
		
		JPanel filterEnvPanel = new JPanel();
		filterEnvPanel.setBackground(new Color(169, 169, 169));
		filterEnvPanel.setBorder(new TitledBorder(null, "Filter", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		filterEnvPanel.setBounds(10, 25, 180, 170);
		envPanel.add(filterEnvPanel);
		filterEnvPanel.setLayout(new GridLayout(0, 4, 0, 0));
		
		// adjust filter envelope...
		// attack
		ExponentialRangeModel faModel = PortModelFactory.createExponentialModel(filterEnv.attack);
		filterAttack = new JSlider(faModel);
		filterAttack.setMinorTickSpacing(10);
		filterAttack.setMajorTickSpacing(20);
		filterAttack.setMaximum(100);
		filterAttack.setBackground(new Color(169, 169, 169));
		filterAttack.setValue(30);
		filterAttack.setToolTipText("");
		filterAttack.setPaintTicks(true);
		filterAttack.setOrientation(SwingConstants.VERTICAL);
		filterAttack.setForeground(Color.DARK_GRAY);
		filterAttack.setUI(new CustomSliderUI(filterAttack));
		filterEnvPanel.add(filterAttack);
		
		// decay
		ExponentialRangeModel fdModel = PortModelFactory.createExponentialModel(filterEnv.decay);
		filterDecay = new JSlider(fdModel);
		filterDecay.setMinorTickSpacing(10);
		filterDecay.setMajorTickSpacing(20);
		filterDecay.setMaximum(100);
		filterDecay.setBackground(new Color(169, 169, 169));
		filterDecay.setValue(10);
		filterDecay.setToolTipText("");
		filterDecay.setPaintTicks(true);
		filterDecay.setOrientation(SwingConstants.VERTICAL);
		filterDecay.setForeground(Color.DARK_GRAY);
		filterDecay.setUI(new CustomSliderUI(filterDecay));
		filterEnvPanel.add(filterDecay);
		
		// sustain
		ExponentialRangeModel fsModel = PortModelFactory.createExponentialModel(filterEnv.sustain);
		filterSustain = new JSlider(fsModel);
		filterSustain.setMinorTickSpacing(10);
		filterSustain.setMajorTickSpacing(20);
		filterSustain.setMaximum(100);
		filterSustain.setBackground(new Color(169, 169, 169));
		filterSustain.setValue(25);
		filterSustain.setToolTipText("");
		filterSustain.setPaintTicks(true);
		filterSustain.setOrientation(SwingConstants.VERTICAL);
		filterSustain.setForeground(Color.DARK_GRAY);
		filterSustain.setUI(new CustomSliderUI(filterSustain));
		filterEnvPanel.add(filterSustain);
		
		// release
		ExponentialRangeModel frModel = PortModelFactory.createExponentialModel(filterEnv.release);
		filterRelease = new JSlider(frModel);
		filterRelease.setMinorTickSpacing(10);
		filterRelease.setMajorTickSpacing(20);
		filterRelease.setMaximum(100);
		filterRelease.setBackground(new Color(169, 169, 169));
		filterRelease.setValue(20);
		filterRelease.setToolTipText("");
		filterRelease.setPaintTicks(true);
		filterRelease.setOrientation(SwingConstants.VERTICAL);
		filterRelease.setForeground(Color.DARK_GRAY);
		filterRelease.setUI(new CustomSliderUI(filterRelease));
		filterEnvPanel.add(filterRelease);
		
		JPanel ampEnvPanel = new JPanel();
		ampEnvPanel.setBackground(new Color(169, 169, 169));
		ampEnvPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Amplifier", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		ampEnvPanel.setBounds(210, 25, 180, 170);
		envPanel.add(ampEnvPanel);
		ampEnvPanel.setLayout(new GridLayout(0, 4, 0, 0));
		
		// adjust amplifier envelope...
		// attack
		ExponentialRangeModel aaModel = PortModelFactory.createExponentialModel(ampEnv.attack);
		ampAttack = new JSlider(aaModel);
		ampAttack.setMaximum(100);
		ampAttack.setMinorTickSpacing(10);
		ampAttack.setMajorTickSpacing(20);
		ampAttack.setBackground(new Color(169, 169, 169));
		ampAttack.setValue(0);
		ampAttack.setToolTipText("");
		ampAttack.setPaintTicks(true);
		ampAttack.setOrientation(SwingConstants.VERTICAL);
		ampAttack.setForeground(Color.DARK_GRAY);
		ampAttack.setUI(new CustomSliderUI(ampAttack));
		ampEnvPanel.add(ampAttack);
		
		// decay
		ExponentialRangeModel adModel = PortModelFactory.createExponentialModel(ampEnv.decay);
		ampDecay = new JSlider(adModel);
		ampDecay.setMaximum(100);
		ampDecay.setMinorTickSpacing(10);
		ampDecay.setMajorTickSpacing(20);
		ampDecay.setBackground(new Color(169, 169, 169));
		ampDecay.setValue(25);
		ampDecay.setToolTipText("");
		ampDecay.setPaintTicks(true);
		ampDecay.setOrientation(SwingConstants.VERTICAL);
		ampDecay.setForeground(Color.DARK_GRAY);
		ampDecay.setUI(new CustomSliderUI(ampDecay));
		ampEnvPanel.add(ampDecay);
		
		// sustain
		ExponentialRangeModel asModel = PortModelFactory.createExponentialModel(ampEnv.sustain);
		ampSustain = new JSlider(asModel);
		ampSustain.setMaximum(100);
		ampSustain.setMinorTickSpacing(10);
		ampSustain.setMajorTickSpacing(20);
		ampSustain.setBackground(new Color(169, 169, 169));
		ampSustain.setValue(30);
		ampSustain.setToolTipText("");
		ampSustain.setPaintTicks(true);
		ampSustain.setOrientation(SwingConstants.VERTICAL);
		ampSustain.setForeground(Color.DARK_GRAY);
		ampSustain.setUI(new CustomSliderUI(ampSustain));
		ampEnvPanel.add(ampSustain);
		
		// release
		ExponentialRangeModel arModel = PortModelFactory.createExponentialModel(ampEnv.release);
		ampRelease = new JSlider(arModel);
		ampRelease.setMaximum(100);
		ampRelease.setMinorTickSpacing(10);
		ampRelease.setMajorTickSpacing(20);
		ampRelease.setBackground(new Color(169, 169, 169));
		ampRelease.setValue(10);
		ampRelease.setToolTipText("");
		ampRelease.setPaintTicks(true);
		ampRelease.setOrientation(SwingConstants.VERTICAL);
		ampRelease.setForeground(Color.DARK_GRAY);
		ampRelease.setUI(new CustomSliderUI(ampRelease));
		ampEnvPanel.add(ampRelease);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(169, 169, 169));
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 0));
		panel.setBounds(10, 206, 180, 33);
		envPanel.add(panel);
		panel.setLayout(new GridLayout(0, 4, 0, 0));
		
		JLabel lblNewLabel_3 = new JLabel("A");
		lblNewLabel_3.setFont(new Font("Miriam Fixed", Font.BOLD, 14));
		lblNewLabel_3.setBackground(new Color(128, 128, 128));
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("D");
		lblNewLabel_4.setFont(new Font("Miriam Fixed", Font.BOLD, 14));
		lblNewLabel_4.setBackground(new Color(128, 128, 128));
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel_4);
		
		JLabel lblS = new JLabel("S");
		lblS.setFont(new Font("Miriam Fixed", Font.BOLD, 14));
		lblS.setBackground(new Color(128, 128, 128));
		lblS.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblS);
		
		JLabel lblR = new JLabel("R");
		lblR.setFont(new Font("Miriam Fixed", Font.BOLD, 14));
		lblR.setBackground(new Color(128, 128, 128));
		lblR.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblR);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(169, 169, 169));
		panel_1.setBorder(new LineBorder(Color.BLACK, 0));
		panel_1.setBounds(210, 206, 180, 33);
		envPanel.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 4, 0, 0));
		
		JLabel label = new JLabel("A");
		label.setFont(new Font("Miriam Fixed", Font.BOLD, 14));
		label.setBackground(new Color(128, 128, 128));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(label);
		
		JLabel label_1 = new JLabel("D");
		label_1.setFont(new Font("Miriam Fixed", Font.BOLD, 14));
		label_1.setBackground(new Color(128, 128, 128));
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(label_1);
		
		JLabel label_2 = new JLabel("S");
		label_2.setFont(new Font("Miriam Fixed", Font.BOLD, 14));
		label_2.setBackground(new Color(128, 128, 128));
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(label_2);
		
		JLabel label_3 = new JLabel("R");
		label_3.setFont(new Font("Miriam Fixed", Font.BOLD, 14));
		label_3.setBackground(new Color(128, 128, 128));
		label_3.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(label_3);
		
		JPanel filterPanel = new JPanel();
		filterPanel.setBackground(new Color(169, 169, 169));
		filterPanel.setLayout(null);
		filterPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Filter", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		filterPanel.setBounds(729, 11, 200, 260);
		add(filterPanel);
		
		// adjust cutoff frequency of filter
		ExponentialRangeModel cutoffModel = PortModelFactory.createExponentialModel(filter.frequency);
		cutoffFrqSlider = new JSlider(cutoffModel);
		cutoffFrqSlider.setMinorTickSpacing(10);
		cutoffFrqSlider.setMajorTickSpacing(20);
		cutoffFrqSlider.setMaximum(100);
		cutoffFrqSlider.setBackground(new Color(169, 169, 169));
		cutoffFrqSlider.setValue(100);
		cutoffFrqSlider.setToolTipText("");
		cutoffFrqSlider.setPaintTicks(true);
		cutoffFrqSlider.setOrientation(SwingConstants.VERTICAL);
		cutoffFrqSlider.setForeground(Color.DARK_GRAY);
		cutoffFrqSlider.setBounds(10, 48, 90, 80);
		cutoffFrqSlider.setUI(new CustomSliderUI(cutoffFrqSlider));
		filterPanel.add(cutoffFrqSlider);
		
		// adjust resonance of filter
		ExponentialRangeModel resModel = PortModelFactory.createExponentialModel(filter.Q);
		resonanceSlider = new JSlider(resModel);
		resonanceSlider.setMinorTickSpacing(10);
		resonanceSlider.setMajorTickSpacing(20);
		resonanceSlider.setMaximum(100);
		resonanceSlider.setBackground(new Color(169, 169, 169));
		resonanceSlider.setValue(0);
		resonanceSlider.setToolTipText("");
		resonanceSlider.setPaintTicks(true);
		resonanceSlider.setOrientation(SwingConstants.VERTICAL);
		resonanceSlider.setForeground(Color.DARK_GRAY);
		resonanceSlider.setBounds(100, 48, 90, 80);
		resonanceSlider.setUI(new CustomSliderUI(resonanceSlider));
		filterPanel.add(resonanceSlider);
		
		// adjust lfo rate
		ExponentialRangeModel lfoRateModel = PortModelFactory.createExponentialModel(lfo.frequency);
		lfoRateSlider = new JSlider(lfoRateModel);
		lfoRateSlider.setMinorTickSpacing(10);
		lfoRateSlider.setMajorTickSpacing(20);
		lfoRateSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				// stop lfo from modulating filter if slider is at 0
				if((lfoRateSlider.getValue())>0){
					filterCircuit.startLFO();
				}
				else{
					filterCircuit.stopLFO();
				}
			}
		});
		lfoRateSlider.setMaximum(100);
		lfoRateSlider.setBackground(new Color(169, 169, 169));
		lfoRateSlider.setValue(0);
		lfoRateSlider.setToolTipText("");
		lfoRateSlider.setPaintTicks(true);
		lfoRateSlider.setOrientation(SwingConstants.VERTICAL);
		lfoRateSlider.setForeground(Color.DARK_GRAY);
		lfoRateSlider.setBounds(10, 139, 180, 80);
		lfoRateSlider.setUI(new CustomSliderUI(lfoRateSlider));
		filterPanel.add(lfoRateSlider);
		
		JLabel lblNewLabel_5 = new JLabel("Cut-off Frq.");
		lblNewLabel_5.setFont(new Font("Miriam Fixed", Font.BOLD, 10));
		lblNewLabel_5.setBackground(new Color(169, 169, 169));
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_5.setBounds(10, 23, 90, 14);
		filterPanel.add(lblNewLabel_5);
		
		JLabel lblResonance = new JLabel("Resonance");
		lblResonance.setFont(new Font("Miriam Fixed", Font.BOLD, 10));
		lblResonance.setBackground(new Color(169, 169, 169));
		lblResonance.setHorizontalAlignment(SwingConstants.CENTER);
		lblResonance.setBounds(100, 23, 90, 14);
		filterPanel.add(lblResonance);
		
		JLabel lblLfo = new JLabel("LFO");
		lblLfo.setFont(new Font("Miriam Fixed", Font.BOLD, 10));
		lblLfo.setBackground(new Color(169, 169, 169));
		lblLfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLfo.setBounds(10, 230, 180, 14);
		filterPanel.add(lblLfo);
		
		JPanel amplifierPanel = new JPanel();
		amplifierPanel.setBackground(new Color(169, 169, 169));
		amplifierPanel.setLayout(null);
		amplifierPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Amplifier", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		amplifierPanel.setBounds(939, 11, 100, 260);
		add(amplifierPanel);
		
		// control volume with filter's amplitude
		ExponentialRangeModel volModel = PortModelFactory.createExponentialModel(volume.amplitude);
		volumeSlider = new JSlider(volModel);
		volumeSlider.setMinorTickSpacing(10);
		volumeSlider.setMajorTickSpacing(20);
		volumeSlider.setMaximum(100);
		volumeSlider.setValue(75);
		volumeSlider.setBackground(new Color(169, 169, 169));
		volumeSlider.setToolTipText("");
		volumeSlider.setPaintTicks(true);
		volumeSlider.setOrientation(SwingConstants.VERTICAL);
		volumeSlider.setForeground(Color.DARK_GRAY);
		volumeSlider.setBounds(10, 45, 80, 150);
		volumeSlider.setUI(new CustomSliderUI(volumeSlider));
		amplifierPanel.add(volumeSlider);
		
		JLabel lblNewLabel_7 = new JLabel("Volume");
		lblNewLabel_7.setFont(new Font("Miriam Fixed", Font.BOLD, 10));
		lblNewLabel_7.setBackground(new Color(169, 169, 169));
		lblNewLabel_7.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_7.setBounds(10, 206, 80, 14);
		amplifierPanel.add(lblNewLabel_7);
		
		JLabel lblJavasynth = new JLabel("algoRhythm");
		lblJavasynth.setBackground(new Color(169, 169, 169));
		lblJavasynth.setHorizontalAlignment(SwingConstants.RIGHT);
		lblJavasynth.setFont(new Font("Miriam Fixed", Font.BOLD, 19));
		lblJavasynth.setForeground(Color.DARK_GRAY);
		lblJavasynth.setBounds(729, 274, 308, 31);
		add(lblJavasynth);
		
		Border border = BorderFactory.createLineBorder(Color.DARK_GRAY,5);
		JLabel label_4 = new JLabel("");
		label_4.setBackground(new Color(169, 169, 169));
		label_4.setIcon(new ImageIcon(JavaSynth.class.getResource("/regan_danny/javasynth/images/panel1.png")));
		label_4.setBounds(1050, -12, 120, 352);
		label_4.setBorder(border);
		add(label_4);
		
		JLabel label_5 = new JLabel("");
		label_5.setBackground(new Color(169, 169, 169));
		label_5.setIcon(new ImageIcon(JavaSynth.class.getResource("/regan_danny/javasynth/images/panel2.png")));
		label_5.setBounds(-27, -12, 127, 352);
		label_5.setBorder(border);
		add(label_5);
	}
	
	public static Oscillator getOscCircuit() {
		return oscCircuit;
	}
	
	public static Filter getFilterCircuit() {
		return filterCircuit;
	}
	
	// trying to get audio output
	public static float getOutput(){
		if(volume.getOutput().getValue()!=0){
			return (float)volume.getOutput().getValue();
		}
		return 0;
	}
	
	public static LineOut getLineOut() {
		return lineOut;
	}
}
