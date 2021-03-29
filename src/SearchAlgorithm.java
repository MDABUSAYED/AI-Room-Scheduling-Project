
import java.util.Comparator;

class SelectUnassignedCourses {
    
        
        public int course_id;
        public int minimum_remaining_rooms;
        public int non_zero_time_slot_values;

        public SelectUnassignedCourses(int course_id, int minimum_remaining_rooms, int non_zero_time_slot_values) {
            this.course_id = course_id;
            this.minimum_remaining_rooms = minimum_remaining_rooms;
            this.non_zero_time_slot_values = non_zero_time_slot_values;
        }
        
        public static Comparator<SelectUnassignedCourses> cmp1 = new Comparator<SelectUnassignedCourses>() {

	public int compare(SelectUnassignedCourses c1, SelectUnassignedCourses c2) {

            
           if (c1.minimum_remaining_rooms != c2.minimum_remaining_rooms) {
               return c1.minimum_remaining_rooms - c2.minimum_remaining_rooms;
           }
           else {
               return c1.non_zero_time_slot_values - c2.non_zero_time_slot_values;
           }
   }};
    }
public class SearchAlgorithm {

  // Your search algorithm should return a solution in the form of a valid
  // schedule before the deadline given (deadline is given by system time in ms)
  public Schedule SolveBacktrackingSearch(SchedulingProblem problem, long deadline) {

    Schedule solution = problem.getEmptySchedule();
    
    //Heuristic for Select Unassigned Variables
    
    
    
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
