import java.util.*;

public class SchedulingProblem {
    

  public static final int NUM_TIME_SLOTS = 10;
  public static final double MAX_X_COORD = 10;
  public static final double MAX_Y_COORD = 10;
  public static final double DISTANCE_PENALTY = 2.5d;

  ArrayList<Building> buildings;
  ArrayList<Room> rooms;
  ArrayList<Course> courses;

  Random random;

  SchedulingProblem(long seed) {
    if (seed > 0) {
      random = new Random(seed);
    } else {
      random = new Random();
    }

    buildings = new ArrayList<Building>();
    rooms = new ArrayList<Room>();
    courses = new ArrayList<Course>();
  }

  public void createRandomInstance(int nBuildings, int nRooms, int nCourses) {

    // create random buildings
    for (int i = 0; i < nBuildings; i++) {
      Building tmp = new Building();
      tmp.xCoord = random.nextDouble() * MAX_X_COORD;
      tmp.yCoord = random.nextDouble() * MAX_Y_COORD;
      buildings.add(tmp);
    }
    /*
    for(int i = 0; i < buildings.size(); i++) {
        Building b = buildings.get(i);
        System.out.println("Buildings "+ i + " X Y: " + b.xCoord + " " + b.yCoord);
    }
    */
    // create random rooms
    for (int i = 0; i < nRooms; i++) {
      Room tmp = new Room();
      tmp.b = buildings.get((int) (random.nextDouble() * nBuildings));
      tmp.capacity = ((int)(random.nextDouble() * 70)) + 30;
      rooms.add(tmp);
    }
    /*
    for(int i = 0; i < rooms.size(); i++) {
        Room r = rooms.get(i);
        System.out.println("Rooms "+ i + " Building Capacity : " + buildings.indexOf(r.b) + " " + r.capacity);
    }
    */

    // create random courses
    for (int i = 0; i < nCourses; i++) {
      Course tmp = new Course();
      tmp.enrolledStudents = ((int) (random.nextDouble() * 70)) + 30;
      tmp.preferredLocation = buildings.get((int) (random.nextDouble() * nBuildings));
      tmp.value = random.nextDouble() * 100;
      tmp.timeSlotValues = new int[NUM_TIME_SLOTS];
      for (int j = 0; j < NUM_TIME_SLOTS; j++) {
        if (random.nextDouble() < 0.3d) {
          tmp.timeSlotValues[j] = 0;
        } else {
          tmp.timeSlotValues[j] = (int)(random.nextDouble() * 10);
        }
      }
      courses.add(tmp);
    }
    /*
    for(int i = 0; i < courses.size(); i++) {
        Course c = courses.get(i);
        System.out.println("Course "+ i + " Std Number Value PB : " + c.enrolledStudents + " " + c.value + " " + buildings.indexOf(c.preferredLocation));
        System.out.println("Course "+ i + " TS Values: ");
        for(int j = 0; j < 10; j++) System.out.print(c.timeSlotValues[j] + " ");
        System.out.println(); 
    }
    */
    //System.out.println("Courses: " + courses);
    //
  }

  public Schedule getEmptySchedule() {
    Schedule tmp = new Schedule(rooms.size(), NUM_TIME_SLOTS);

    for (int i = 0; i < rooms.size(); i++) {
      for (int j = 0; j < NUM_TIME_SLOTS; j++) {
        tmp.schedule[i][j] = -1;
      }
    }
    return tmp;
  }
  
  // Added to Print Schedule, not in provided code
  public void PrintSchedule(Schedule solutionSchedule) {
    int[][] s = solutionSchedule.schedule;

    for (int i = 0; i < rooms.size(); i++) {
        System.out.println("Room " + i);
        for (int j = 0; j < NUM_TIME_SLOTS; j++) System.out.print(s[i][j] + " ");
        System.out.println();
    }
   
  }
  //

  public double evaluateSchedule(Schedule solutionSchedule) {
    int[][] s = solutionSchedule.schedule;

    if (s.length != rooms.size() || s[0].length != NUM_TIME_SLOTS) {
      System.out.println("ERROR: invalid schedule dimensions");
      return Double.NEGATIVE_INFINITY;
    }

    // check that all classes are assigned only once
    int[] assigned = new int[courses.size()];
    for (int i = 0; i < s.length; i++) {
      for (int j = 0; j < s[0].length; j++) {

        // indicates an unassigned time slot
        if (s[i][j] < 0 || s[i][j] > courses.size()) continue;

        // class that hase been scheduled more than once
        if (assigned[s[i][j]] > 0) {
          System.out.println("ERROR: Invalid schedule");
          return Double.NEGATIVE_INFINITY;
        }

        assigned[s[i][j]]++;
      }
    }

    double value = 0d;
    //int count =0;
    for (int i = 0; i < s.length; i++) {
      for (int j = 0; j < s[0].length; j++) {

        // indicates an unassigned time slot
        if (s[i][j] < 0 || s[i][j] > courses.size()) continue;

        Course c = courses.get(s[i][j]);
        Room r = rooms.get(i);

        // course was not assigned to a feasible time slot
        if (c.timeSlotValues[j] <= 0) {
          //System.out.println(s[i][j]+" s");  
          continue;
        }

        // course was assigned to a room that is too small
        if (c.enrolledStudents > r.capacity) {
          //System.out.println(s[i][j]+" a");  
          continue;
        }
        //count++;
        //System.out.println("count value time slot : " + count + " "+ c.value + " " + c.timeSlotValues[j]);
        // add in the value for the class
        value += c.value;
        value += c.timeSlotValues[j];

        // calculate the distance penalty
        Building b1 = r.b;
        Building b2 = c.preferredLocation;
        double xDist = (b1.xCoord - b2.xCoord) * (b1.xCoord - b2.xCoord);
        double yDist = (b1.yCoord - b2.yCoord) * (b1.yCoord - b2.yCoord);
        double dist = Math.sqrt(xDist + yDist);
        
        value -= DISTANCE_PENALTY * dist;
        
        //System.out.println(s[i][j]+" "+(c.value+c.timeSlotValues[j]-DISTANCE_PENALTY * dist));
      }
    }

    return value;
  }

}
