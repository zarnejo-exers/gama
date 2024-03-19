/**
* Name: segregationAgents
* Author: 
* Description: A model showing the segregation of the people just by putting a similarity wanted parameter using agents 
* 	to represent the individuals
* Tags: grid
*/

model segregation

//import the Common Schelling Segregation model
import "../includes/Common Schelling Segregation.gaml"

global {
	//List of all the free places
	list<space> free_places ;
	//List of all the places
	list<space> all_places ;
	//Shape of the world
	geometry shape <- square(dimensions);
	
	//Action to initialize the people agents
	action initialize_people { 
		create people number: number_of_people; 
		all_people <- people as list ;  
	} 
	//Action to initialize the places
	action initialize_places { 
		all_places <- shuffle (space);
		free_places <- all_places;  
	} 
	
	reflex observe_emergence{
		int temp_count <- 0;
		int largest <- 0;
		int smallest <- 99;
		
		loop i from: 0 to: number_of_groups{
			list<list<people>> people_groups <- ((people where (each.color = colors[i])) simple_clustering_by_distance 1) where ((length(each)) > 5);
			write "Color: "+colors[i];
			loop j over: people_groups{
				write "Group size: "+length(j);
				if(length(j) > largest){
					largest <- length(j);
				}
				if(length(j) < smallest){
					smallest <- length(j);
				}
				rgb var0 <- rnd_color(255);
				write "Temp_color: "+var0;
				loop x over: j{
					x.temp_color <- var0;
				}
			}
			temp_count <- temp_count + length(people_groups);
		}
		cluster_count <- temp_count;
		largest_cluster_size <- largest;
		smallest_cluster_size <- smallest;
	}
}
//Grid to discretize space, each cell representing a free space for the people agents
grid space width: dimensions height: dimensions neighbors: 8 use_regular_agents: false frequency: 0{
	rgb color  <- #black;
}

//Species representing the people agents
species people parent: base  {
	//Color of the people agent
	rgb color <- colors at (rnd (number_of_groups - 1));
	rgb temp_color <- #black;
	//List of all the neighbours of the agent
	list<people> my_neighbours -> people at_distance neighbours_distance ;
	//Cell representing the place of the agent
	space my_place;
	init {
		//The agent will be located on one of the free places
		my_place <- one_of(free_places);
		location <- my_place.location; 
		//As one agent is in the place, the place is removed from the free places
		free_places >> my_place;
	} 
	//Reflex to migrate the people agent when it is not happy 
	reflex migrate when: !is_happy {
		//Add the place to the free places as it will move to another place
		free_places << my_place;
		//Change the place of the agent
		my_place <- one_of(free_places);
		location <- my_place.location; 
		//Remove the new place from the free places
		free_places >> my_place;
	}
	
	aspect default{ 
		draw circle (0.5) color: color; 
	}
	
	aspect default2{
		draw circle (0.5) color: temp_color; 
		temp_color<- #black;
	}
}



experiment schelling type: gui {	
	output {
		monitor "# of clusters" value: cluster_count refresh: every(1#cycle);
		monitor "largest cluster size" value: largest_cluster_size refresh: every(1#cycle);
		monitor "smallest cluster size" value: smallest_cluster_size refresh: every(1#cycle);
		
		display Segregation {
			species people;
		}
		
		display Segregation2{
			species people aspect: default2;
		}
			
		display Charts  type: 2d {
			chart "Proportion of happiness" type: pie background: #gray style: exploded position: {0,0} size: {1.0,0.5}{
				data "Unhappy" value: number_of_people - sum_happy_people color: #green;
				data "Happy" value: sum_happy_people color: #yellow;
			}
			chart "Global happiness and similarity" type: series background: #gray axes: #white position: {0,0.5} size: {1.0,0.5} {
				data "happy" color: #blue value:  (sum_happy_people / number_of_people) * 100 style: spline ;
				data "similarity" color: #red value:  (sum_similar_neighbours / sum_total_neighbours) * 100 style: step ;
			}
		}
	}
}

//	//Number of groups
//	int number_of_groups <- 2 max: 8 parameter: "Number of groups:" category: "Population";
//	//Density of the people
//	float density_of_people <- 0.7 parameter: "Density of people:" category: "Population" min: 0.01 max: 0.99;
//	//Percentage of similar wanted for segregation
//	float percent_similar_wanted <- 0.5 min: float (0) max: float (1) parameter: "Desired percentage of similarity:" category: "Population";
//  Neighbours distance for the perception of the agents
//  int neighbours_distance <- 2 max: 10 min: 1 parameter: "Distance of perception:" category: "Population";

experiment Sobol type: batch keep_seed:true until:( cycle > 20) {
    parameter "Density of people:" var: density_of_people min: 0.01 max: 0.99; 
    parameter "Desired percentage of similarity:" var: percent_similar_wanted min: float (0) max: float (1);
    parameter "Distance of perception::" var: neighbours_distance max: 10 min: 1;
     
    method sobol outputs:["cluster_count", "largest_cluster_size", "smallest_cluster_size"] sample:5 report:"sobol_20_5.txt" results:"sobol_raw_20_5.csv";
}