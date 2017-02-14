package regan_danny.javasynth;

import com.jsyn.midi.MidiConstants;

import jvst.wrapper.*;
import jvst.wrapper.valueobjects.VSTEvent;
import jvst.wrapper.valueobjects.VSTEvents;
import jvst.wrapper.valueobjects.VSTMidiEvent;

public class JavaSynthVST extends VSTPluginAdapter {
	JavaSynthVSTGUI gui = null; //reference to gui
	private boolean IsNoteOn = false;
	private double currentNote;
	private float out;
	
	public JavaSynthVST(long Wrapper) {
		super(Wrapper);
		//VSTPluginAdapter._initPlugFromNative("C:/Users/Danny/Music/vst/JavaSynth", false);
		//log("Construktor jVSTxSynth() START!");

	    this.setNumInputs(0);
	    this.setNumOutputs(1);
	    //this.hasVu(false); //deprecated as of vst2.4
	    //this.hasClip(false); //deprecated as of vst2.4
	    this.canProcessReplacing(true);

	    this.isSynth(true);
	    this.out = 0;
	    //this.setUniqueID('j'<<24 | 'X'<<16 | 's'<<8 | 'y');
	    log("Constructor JavaSynthVST() INVOKED!");
	}

	@Override
	public int canDo(String feature) {
		if( feature.equals( CANDO_PLUG_RECEIVE_VST_EVENTS ) )
			return CANDO_YES;
		if( feature.equals( CANDO_PLUG_RECEIVE_VST_MIDI_EVENT ) )
			return CANDO_YES;
		return CANDO_NO;
	}

	@Override
	public int getPlugCategory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getProductString() {
		// TODO Auto-generated method stub
		return "algoRhythm";
	}

	@Override
	public String getProgramNameIndexed(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVendorString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setBypass(boolean arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean string2Parameter(int arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNumParams() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumPrograms() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getParameter(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getParameterDisplay(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameterLabel(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameterName(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getProgram() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getProgramName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processReplacing(float[][] inputs, float[][] outputs, int samples) {
		//this.out = JavaSynth.getOutput(); // makes clicking noise
		float[] output = outputs[0];
		
		if(this.IsNoteOn){
			for (int j = 0; j < samples; j++) {
				output[j] = out;
			}
		}
	}

	@Override
	public void setParameter(int arg0, float arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProgram(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProgramName(String arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public int processEvents (VSTEvents ev) {
		for (int i = 0; i < ev.getNumEvents(); i++) {
			if (ev.getEvents()[i].getType() != VSTEvent.VST_EVENT_MIDI_TYPE) continue;
			
			VSTMidiEvent event = (VSTMidiEvent)ev.getEvents()[i];
			byte[] midiData = event.getData();
			int status = midiData[0] & 0xf0;// ignoring channel
			
			if (status == 0x90 || status == 0x80) {
				double note = midiData[1] & 0x7f;
				double velocity = midiData[2] & 0x7f;
				if (status == 0x80) velocity = 0;	// note off with velocity 0
			
				if (velocity==0 && (note == currentNote)) this.noteOff();
				else this.noteOn(note, 1);
			}
			else if (status == 0xb0) {
				// all notes off
				if (midiData[1] == 0x7e || midiData[1] == 0x7b)	this.noteOff();
			}
		}

	    return 1;
	}
	
	private void noteOn(double note, double velocity) {
	  	currentNote = note;
	  	JavaSynth.getOscCircuit().noteOn(MidiConstants.convertPitchToFrequency(note),velocity);
	  	// trigger filter's envelope when note is played
	  	JavaSynth.getFilterCircuit().triggerFilterEnv();
	    this.IsNoteOn = true;
	}

	private void noteOff() {
		JavaSynth.getOscCircuit().noteOff(); 
	    this.IsNoteOn = false;
	}
}
