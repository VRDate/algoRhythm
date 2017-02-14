package regan_danny.javasynth;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.Circuit;
import com.jsyn.unitgen.EnvelopeDAHDSR;
import com.jsyn.unitgen.FilterLowPass;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.UnitOscillator;

public class Filter extends Circuit{
	private FilterLowPass filter, volume;
	private UnitOscillator lfo;
	private EnvelopeDAHDSR filterEnv;
	private SineOscillator envTrigger;
	
	public Filter(FilterLowPass filter, UnitOscillator lfo, EnvelopeDAHDSR filterEnv, FilterLowPass volume){
		add(this.filter = filter);
		add(this.lfo = lfo);
		add(this.filterEnv = filterEnv);
		add(envTrigger = new SineOscillator());
		add(this.volume = volume);
		this.volume.frequency.set(6000);
		this.volume.Q.set(1);
		this.lfo.amplitude.set(0.3); // set amp so filter doesn't get too loud
		this.lfo.frequency.setup(0.0,0.0,30.0);
		
		this.filterEnv.decay.setup(0.1,0.1,8.0);
        this.filterEnv.release.setup(0.1,0.1,8.0);
		// connect components
		this.filter.output.connect(this.volume.input);
	}
	
	public void startLFO(){
		lfo.output.connect(filter.amplitude);
	}
	
	public void stopLFO(){
		lfo.output.disconnect(filter.amplitude);
	}
	
	public UnitInputPort getInput(){
		return filter.input;
	}
	
	public UnitOutputPort getOutput() {
		return volume.output;
	}
	
	public void triggerFilterEnv(){
		envTrigger.amplitude.set(1);
		// connect filter envelope
        envTrigger.output.connect(filterEnv.input);
        filterEnv.output.connect(filter.amplitude);
	}
}
