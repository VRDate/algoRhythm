package regan_danny.javasynth;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.Circuit;
import com.jsyn.unitgen.EnvelopeDAHDSR;
import com.jsyn.unitgen.PulseOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.UnitOscillator;
import com.jsyn.unitgen.UnitVoice;
import com.softsynth.shared.time.TimeStamp;

public class Oscillator extends Circuit implements UnitVoice{
	private UnitOscillator oscillator;
	private EnvelopeDAHDSR ampEnv;
	private SineOscillator envTrigger;
	public UnitInputPort amplitude;
    public UnitInputPort frequency;

	public Oscillator(EnvelopeDAHDSR ampEnv){
		add(oscillator = new PulseOscillator());
		add(this.ampEnv = ampEnv);
		add(envTrigger = new SineOscillator());
        addPort(amplitude = oscillator.amplitude);
        addPort(frequency = oscillator.frequency);
        amplitude.setup(0.0, 0.0, 1.0);
        
        this.ampEnv.decay.setup(0.1,0.1,8.0);
        this.ampEnv.release.setup(0.1,0.1,8.0);
	}
	
	public void selectOscillator(final UnitOscillator osc){
		oscillator = osc;
	}
	
	@Override
	public UnitOutputPort getOutput() {
		return oscillator.output;
	}

	public void noteOff() {
		envTrigger.amplitude.set(0);
		oscillator.amplitude.set(0);
	}

	public void noteOn(double frequency, double amplitude) {
		envTrigger.amplitude.set(amplitude);
		// connect amplifier envelope
        envTrigger.output.connect(ampEnv.input);
        ampEnv.output.connect(oscillator.amplitude);
        oscillator.frequency.set(frequency);
	}

	@Override
	public void noteOff(TimeStamp arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noteOn(double arg0, double arg1, TimeStamp arg2) {
		// TODO Auto-generated method stub
		
	}

}
