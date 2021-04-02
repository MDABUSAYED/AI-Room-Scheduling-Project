
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

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
    Schedule solution = problem.getEmptySchedule();

    // YOUR CODE HERE

    return solution;
  }
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
