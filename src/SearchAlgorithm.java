

import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

class SelectUnassignedCourses {
    
        
  public int course_id;
  public HashMap<Integer, Room> minimum_remaining_rooms;
  public int non_zero_time_slot_values;

  public SelectUnassignedCourses(int course_id, HashMap<Integer, Room> minimum_remaining_rooms, int non_zero_time_slot_values) {
    this.course_id = course_id;
    this.minimum_remaining_rooms = minimum_remaining_rooms;
    this.non_zero_time_slot_values = non_zero_time_slot_values;
  }
        
  public static Comparator<SelectUnassignedCourses> cmp1 = new Comparator<SelectUnassignedCourses>() {

  public int compare(SelectUnassignedCourses c1, SelectUnassignedCourses c2) {        
    if (c1.non_zero_time_slot_values != c2.non_zero_time_slot_values) {
      return c1.non_zero_time_slot_values - c2.non_zero_time_slot_values;
    }
    else {
      return c1.minimum_remaining_rooms.size() - c2.minimum_remaining_rooms.size();
    }
   }};
}

class OrderDomainValues {
    
        
  public int time_slot_value;
  public int index_of_time_slot_value;

  public OrderDomainValues(int time_slot_value, int index_of_time_slot_value) {
    this.time_slot_value = time_slot_value;
    this.index_of_time_slot_value = index_of_time_slot_value;
  }
        
  public static Comparator<OrderDomainValues> cmp1 = new Comparator<OrderDomainValues>() {

  public int compare(OrderDomainValues v1, OrderDomainValues v2) {        
    return v2.time_slot_value - v1.time_slot_value;
   }};
}
public class SearchAlgorithm {
    

  // Your search algorithm should return a solution in the form of a valid
  // schedule before the deadline given (deadline is given by system time in ms)
  public Schedule SolveBacktrackingSearch(SchedulingProblem problem, long deadline) {

    Schedule solution = problem.getEmptySchedule();
    
    //Heuristic for Select Unassigned Variables:Minimum Remaining Rooms,If ties then try with minimum non-zero time slots
    ArrayList<SelectUnassignedCourses> unassignedcourselist = new ArrayList<SelectUnassignedCourses>();
    
    for (int i = 0; i < problem.courses.size(); i++) {
      Course c = problem.courses.get(i);
      //int avaiable_rooms = 0;
      int non_zero_time_slot = 0;
      //ArrayList<Room> available_rooms = new ArrayList<Room>();
      HashMap<Integer, Room> available_rooms = new HashMap<Integer, Room>();
      for (int k = 0; k < problem.rooms.size(); k++) {
        Room r = problem.rooms.get(k);  
        if (c.enrolledStudents <= r.capacity) {
          //available_rooms.add(r);
          available_rooms.put(k, r);
        }
      }
      
      for (int j = 0; j < c.timeSlotValues.length; j++) {
        if (c.timeSlotValues[j] > 0) {
          non_zero_time_slot++;  
        }
      }
      
      unassignedcourselist.add(new SelectUnassignedCourses(i, available_rooms, non_zero_time_slot));
    }
    Collections.sort(unassignedcourselist, SelectUnassignedCourses.cmp1);
    
    for (int i = 0; i < unassignedcourselist.size();i++) {
      SelectUnassignedCourses c = unassignedcourselist.get(i);
      if(c.minimum_remaining_rooms.size() == 0) continue;
      
      // Order Domain Value Heuristic: Max time slot value for a course (Sort time slot value in descending order)
      Course course = problem.courses.get(c.course_id);
      ArrayList<OrderDomainValues> ordered_time_slot_values = new ArrayList<OrderDomainValues>();
      for (int j = 0; j < course.timeSlotValues.length; j++) { 
        if (course.timeSlotValues[j] != 0) ordered_time_slot_values.add(new OrderDomainValues(course.timeSlotValues[j], j));
      }
      Collections.sort(ordered_time_slot_values, OrderDomainValues.cmp1);
      
      //Inference: Try to find preffered room with maximum time slot value, If not possible then try with available one with maximum time slot value
      
      boolean flag = false; 
      //for (int j = 0; j < problem.rooms.size() && !flag; j++) { capitalCities.keySet()
      for (Integer j :  c.minimum_remaining_rooms.keySet()) {
        if (flag) break;
        //Room r = problem.rooms.get(j);
        Room r = c.minimum_remaining_rooms.get(j);
        if (r.b.equals(course.preferredLocation)) {
          for (int k = 0;  k < ordered_time_slot_values.size(); k++ ) {
            OrderDomainValues o = ordered_time_slot_values.get(k);
            //if (o.time_slot_value == 0) break;
            if (solution.schedule[j][o.index_of_time_slot_value] < 0) {
              solution.schedule[j][o.index_of_time_slot_value] = c.course_id;
              flag = true;
              break;
            }  
          }   
        }
      }
      // ....Slot then rooms, 
      if (!flag) {
        for (int j = 0; j <  ordered_time_slot_values.size() && !flag; j++) {
          OrderDomainValues o = ordered_time_slot_values.get(j); 
          //if (o.time_slot_value == 0) break;
          for (Integer k :  c.minimum_remaining_rooms.keySet()) {
          //for (int k = 0;  k < problem.rooms.size(); k++ ) {
            //Room r = problem.rooms.get(k); 
            Room r = c.minimum_remaining_rooms.get(k);
            //if (r.capacity < course.enrolledStudents) continue;
            if (solution.schedule[k][o.index_of_time_slot_value] < 0 ) {
              solution.schedule[k][o.index_of_time_slot_value] = c.course_id;
              flag = true;
              break;
            }  
          }   
        }
      }
      
      //System.out.println(c.course_id);
      //problem.PrintSchedule(solution);
      
    }
       
    return solution;
  }

  public Schedule SolveSimulatedAnnealing(SchedulingProblem problem, long deadline) {

    // get an empty solution to start from
    Schedule current = problem.getEmptySchedule();
    
    double alpha = 0.9;
    double T = 1.0;
    double T_min = 0.00001;
    double old_cost, best = 0.0;
    double new_cost, del_e, probability;
    //while (T > T_min) {
      //System.out.println(" T : " + T);    
      int k = 0;
      while (k <= problem.courses.size()*100) {
      //while (k <= 300) {
            
        System.out.println("Iteration : " + k);  
        // Calculate Neighbor  
        int[][] s = current.schedule;
        
        int[] assigned = new int[problem.courses.size()];
        ArrayList<Integer> position = new ArrayList<Integer>();
        int scheduled_course_number = 0;
        old_cost = problem.evaluateSchedule(current);
        //System.out.println("Old Cost :" + old_cost);
        //System.out.println("New Cost :" + new_cost);
        if (best < old_cost) best = old_cost;
        
        for (int i = 0; i < s.length; i++) {
          for (int j = 0; j < s[0].length; j++) {    
            if (s[i][j] > -1 && s[i][j] < problem.courses.size()) {
              assigned[s[i][j]]++;
              scheduled_course_number++;
            }
            if (s[i][j]== -1) {position.add(i); position.add(j); }
          }   
        }
        System.out.println("scheduled_course_number : " + scheduled_course_number);
        ArrayList<Schedule> neighbor = new ArrayList<Schedule>(); 
        //System.out.println("NS0 :" + neighbor.size());
        Random random = new Random();
        // Not Fully Assigned
        if (scheduled_course_number != problem.rooms.size() * 10 && scheduled_course_number != problem.courses.size()) {
          int j = 0;  
          //System.out.println("yyy");
          for (int i = 0; i < problem.courses.size(); i++)  {
            if (j >= position.size()) break;
            if (assigned[i] == 0) {
              //System.out.println("yya");
              int x = position.get(j);
              int y = position.get(j + 1);
              j += 2;
              Schedule sc = new Schedule(problem.rooms.size(), s[0].length);
              //sc.schedule = current.schedule.clone();
              for (int a = 0; a < s.length; a++) {
                  for (int b = 0; b < s[0].length; b++) { 
                      sc.schedule[a][b] = current.schedule[a][b];
                  }
              }
              sc.schedule[x][y] = i;
              neighbor.add(sc);
            }
          }
          //System.out.println("NS1 :" + neighbor.size());
        }
        // Fully Assigned Courses, Need to Assign new courses or Swap
        else {
          
          int i = 0;
          if (scheduled_course_number < problem.courses.size())  {
            int row1, col1;
            //System.out.println("yyb");
            row1 = (int)(random.nextDouble() * problem.rooms.size());
            col1 = (int)(random.nextDouble() * s[0].length);   
            while(assigned[i] !=0) i++;
            Schedule sc = new Schedule(problem.rooms.size(), s[0].length);
            //sc.schedule = current.schedule.clone();
            for (int a = 0; a < s.length; a++) {
              for (int b = 0; b < s[0].length; b++) { 
                sc.schedule[a][b] = current.schedule[a][b];
                }
            }
            sc.schedule[row1][col1] = i;
            neighbor.add(sc);
            
          }
          //System.out.println("NS2 :" + neighbor.size());
          i = 0;
          while (i < (int)(problem.courses.size() / 2)) {
            int row1, row2, col1, col2,tmp;
            row1 = (int)(random.nextDouble() * problem.rooms.size());
            row2 = (int)(random.nextDouble() * problem.rooms.size());
            col1 = (int)(random.nextDouble() * s[0].length);  
            col2 = (int)(random.nextDouble() * s[0].length);
            Schedule sc = new Schedule(problem.rooms.size(), s[0].length);
            //sc.schedule = current.schedule.clone();
            for (int a = 0; a < s.length; a++) {
              for (int b = 0; b < s[0].length; b++) { 
                sc.schedule[a][b] = current.schedule[a][b];
              }
            }
            tmp = sc.schedule[row1][col1];
            sc.schedule[row1][col1] = sc.schedule[row2][col2];
            sc.schedule[row2][col2] = tmp;
            neighbor.add(sc);
            i++;
          }
          //System.out.println("NS3 :" + neighbor.size());
        }
        
        int index;
        index = (int)(random.nextDouble() * neighbor.size());
        System.out.println(neighbor.size());
        Schedule next = neighbor.get(index);
        new_cost = problem.evaluateSchedule(next);
        System.out.println("Old Cost :" + old_cost);
        System.out.println("New Cost :" + new_cost);
        del_e = new_cost - old_cost;
        
        if (del_e > 0)  current = next;
        else {
          //probability = Math.exp(del_e / T);
          probability = (1/( 1 + Math.pow(Math.E,(-1*(k - problem.courses.size()*50)))));
          //System.out.println("yyya");
          if ((1 - probability) > 0.5) {
          
          //if (k < problem.courses.size()*50) {    
            current = next;
            System.out.println("yyyb");
          }
        }        
        k++;
      }
      //T = T * alpha;
    //}
    System.out.println("Best Cost :" + best);
    return current;
  }
  public Schedule SimulatedAnnealing(SchedulingProblem problem, long deadline) {

		// At first set an empty solution 
		Schedule currentSolution = problem.getEmptySchedule();

		// Track Best Solution

		// First solution is the naiveBaseline scheduling
		currentSolution = naiveBaseline(problem, deadline);
		Schedule bestSolution = currentSolution;
		double bestEnergy = 0.0;
		bestEnergy = problem.evaluateSchedule(currentSolution);
		System.out.println("Best En: "+ bestEnergy);

		// Second solution is no course are scheduled
		Schedule secondSolution = problem.getEmptySchedule();

		// Starting with a temperature where our main intention is to reduce the temperature with a coolingRate
		double temp = 100000;
		double coolingRate = 0.003;

		//The loop will continue until the temperature reduced to below 1
		while(temp > 1){

			// secondSolution is always an empty solution where no course is scheduled
			secondSolution = problem.getEmptySchedule();

			// This loop is for every course
			for(int i = 0; i < problem.courses.size() -1; i++){

				// Get the course to schedule
				Course courseI = problem.courses.get(i);
				boolean isScheduled = false;

				// Start scheduling
				isScheduled = false;

				// This loop is for every timeslots 
				for (int j = 0; j < courseI.timeSlotValues.length; j++) {
					if (isScheduled) break;
					// timeslotvalues means preferred time for a course
					if (courseI.timeSlotValues[j] > 0) {
						// looping through all the rooms
						for (int k = 0; k < problem.rooms.size(); k++) {
							// randomly pick one room
							int randRoom = (int)(Math.random()*problem.rooms.size());
							// randomly pick any timeslot
							int randSlot = (int)(Math.random()*courseI.timeSlotValues.length);
							// check if the random room on random time slot is already scheduled or not
							if (secondSolution.schedule[randRoom][randSlot] < 0) {
								secondSolution.schedule[randRoom][randSlot] = i;
								isScheduled = true;
								break;
							}//end if
						}//end for
					}//end if
				}//end for

			}//end for (going through all the courses)
			double currentEnergy = problem.evaluateSchedule(currentSolution);
			double secondEnergy = problem.evaluateSchedule(secondSolution);

			// 
			if(probabilityOfAcceptance(currentEnergy, secondEnergy, temp) >=1.0) {
				currentSolution = secondSolution;
				currentEnergy = secondEnergy;
			}
			else if(probabilityOfAcceptance(currentEnergy, secondEnergy, temp) > Math.random()) {
				currentSolution = secondSolution;
				currentEnergy = secondEnergy;
			}

			if(currentEnergy > bestEnergy) {
				bestSolution = currentSolution;
			}
			temp *= 1-coolingRate;
		}//end while

		return bestSolution;
	}//end simulatedAnnealing


	public static double probabilityOfAcceptance(double energy, double newEnergy, double temp){
		if(newEnergy > energy)
			return 1.0;
		return Math.exp((newEnergy - energy) / temp); 
	}//end acceptanceProbability
  public Schedule SolveGeneticAlgorithm(SchedulingProblem problem, long deadline) {

    // get an empty solution to start from
    Schedule solution = problem.getEmptySchedule();

    // YOUR CODE HERE

    return solution;
  }
  // This is a very naive baseline scheduling strategy
  // It should be easily beaten by any reasonable strategy
  public Schedule naiveBaseline(SchedulingProblem problem, long deadline) {

    // get an empty solution to start from
    Schedule solution = problem.getEmptySchedule();

    for (int i = 0; i < problem.courses.size(); i++) {
      Course c = problem.courses.get(i);
      boolean scheduled = false;
      for (int j = 0; j < c.timeSlotValues.length; j++) {
        if (scheduled) break;
        if (c.timeSlotValues[j] > 0) {
          for (int k = 0; k < problem.rooms.size(); k++) {
            if (solution.schedule[k][j] < 0) {
              solution.schedule[k][j] = i;
              scheduled = true;
              break;
            }
          }
        }
      }
    }

    return solution;
  }
}
