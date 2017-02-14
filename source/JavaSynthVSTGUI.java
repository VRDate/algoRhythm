package regan_danny.javasynth;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jvst.wrapper.VSTPluginAdapter;
import jvst.wrapper.VSTPluginGUIAdapter;
import jvst.wrapper.gui.VSTPluginGUIRunner;

public class JavaSynthVSTGUI extends VSTPluginGUIAdapter implements ChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3435167965087673525L;
	private VSTPluginAdapter pPlugin;
	protected static boolean DEBUG = false;
	private JavaSynth panel;
	
	public JavaSynthVSTGUI(VSTPluginGUIRunner r, VSTPluginAdapter plugin) {
        super(r, plugin);
        pPlugin = plugin;
        
        // new thread to prevent host from becoming unresponsive while plugin loads
        EventQueue.invokeLater(new Runnable() {
			public void run() {
				init();
				JavaSynth.getOutput();
			}
        });
		
		if( RUNNING_MAC_X ) setVisible( true );
	}
	
	public void init(){
		if (!DEBUG) {
	    	((JavaSynthVST)plugin).gui=this; //tell the plugin that it has a gui
		}
		setTitle("algoRhythm");
		setSize(1155,340);
		setResizable(false);
		setContentPane(panel = new JavaSynth());
	}
	
	// for testing purposes
	public static void main(String[] args) throws Exception {
		DEBUG=true;
			  
		JavaSynthVSTGUI gui = new JavaSynthVSTGUI(null,null);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
		
		//MidiHandler midi = new MidiHandler();
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
