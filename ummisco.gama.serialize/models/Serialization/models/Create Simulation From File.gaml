/***
* Name: CreateSimuGraph2
* Author: Benoit Gaudou
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model CreateSimuGraph2

import "Memorize Experiment.gaml"

experiment saveSimu type: gui {
	
	init {
		write "Run the simulation until cycle 5, when it will be saved in a file.";
	}
	
	reflex store when: cycle = 5 {		
		write "================ START SAVE + self " + " - " + cycle ;		
		save saved_simulation_file('saveSimu.gsim', [simulation]);
		write "================ END SAVE + self " + " - " + cycle ;			
	}	
	
	output {
		display main_display {
			species road aspect: geom;
			species people aspect: base;						
		}
	}	
}

experiment reloadSimu type: gui {
	
	init {
		create simulation from: saved_simulation_file("saveSimu.gsim");	
		write "init simulation at step " + simulation.cycle;
	}
	
	output {
		display main_display {
			species road aspect: geom;
			species people aspect: base;						
		}
	}	
}

experiment reloadSavedSimuOnly type: gui {
	
	action _init_ {
		create simulation from: saved_simulation_file("saveSimu.gsim");	
	}

	output {
		display main_display {
			species road aspect: geom;
			species people aspect: base;						
		}
	}	
}