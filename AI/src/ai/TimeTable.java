package ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Collections.copy;
import java.util.List;
import java.util.Random;

public class TimeTable {

    int id;
    int timetable_fitness;

    int rows = 223;
    int cols = 3169;
    int totalRooms = 46;
    int room_capcaity = 50;
    int generalInfo[] = new int[2];

    List<Integer> roomCap = new ArrayList<Integer>();
    List<Integer> table[][] = new List[3][7]; //rooms and slots

    char Reg[][] = new char[rows][cols];
    char Rooms[] = new char[totalRooms];

    public TimeTable(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<Integer>[][] getTimeTable() {
        return table;
    }

    int fitness(TimeTable temp) throws IOException {

        timetable_fitness = 0;

        List<Integer> obj = new ArrayList<Integer>();
        feedRoomsData(obj);    //conatin room

        int[] arr = new int[223];     //check array for all courses check

        for (int i = 0; i < 223; i++) {
            arr[i] = 0;
        }

        /*------------------------------------------CASE 2(ROOM CHECK) && CASE 1(ALL COURSES EXIST)-----------------------------------------*/
        int total_capacity = 0;
        for (int i = 0; i < obj.size(); i++) {
            total_capacity += obj.get(i);      //total room count
        }

        int Clist = 0;
        int get_id = 0, total_student = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {
                Clist = temp.table[i][j].size();

                for (int k = 0; k < Clist; k++) {
                    get_id = temp.table[i][j].get(k);
                    arr[get_id] = 1;
                    total_student += getStudentscount(get_id);

                }

                if (total_student > total_capacity) {
                    timetable_fitness = timetable_fitness - 1;
                } else {
                    timetable_fitness = timetable_fitness + 20;
                }
                total_student = 0;
            }
        }

        for (int i = 0; i < 223; i++) {
            if (arr[i] == 0) {
                timetable_fitness = timetable_fitness - 1;
            } else {
                timetable_fitness = timetable_fitness + 20;
            }
        }

        /*------------------------------------------------CASE 3 && CASE 5(STUDENT HAVING TWO EXAMS/CLASH ON GIVEN SLOT)--------------------------*/
        int count_total_courses = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {

                List<Integer> st = new ArrayList<>();
                st = temp.table[i][j];       //for each List on index of timetable

                List<Integer> StudentInfo[] = new List[st.size()];          //make array equal to number of courses for students

                for (int k = 0; k < st.size(); k++) {
                    StudentInfo[k] = getStudentsList(st.get(k));
                    count_total_courses++;

                }

                timetable_fitness = timetable_fitness + get_Clashes_Fitness(StudentInfo, st.size());
            }
        }

        /*--------------------------------------------CASE 4 && CASE 6(CONSECUTIVE PAPER IN TWO SLOTS)--------------------------------------------------------------------*/
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {
                List<Integer> st = new ArrayList<>();
                st = temp.table[i][j];       //for each List on index of timetable

                List<Integer> StudentInfo[] = new List[st.size()];          //make array equal to number of courses for students

                for (int k = 0; k < st.size(); k++) {
                    StudentInfo[k] = getStudentsList(st.get(k));

                }

                List<Integer> st1 = new ArrayList<>();

                if (j < 6) {
                    st1 = temp.table[i][j + 1];       //for each List on index of timetable

                    List<Integer> StudentInfo1[] = new List[st1.size()];          //make array equal to number of courses for students

                    for (int k = 0; k < st1.size(); k++) {
                        StudentInfo1[k] = getStudentsList(st1.get(k));
                    }

                    timetable_fitness = timetable_fitness + get_consecutive_slot_fitness(StudentInfo, st.size(), StudentInfo1, st1.size());
                }

            }
        }

        /*--------------------------------------------CASE 7(THREE EXAMS IN ONE DAY)---------------------------------------------------------------------------*/
        List<Integer> student = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {

                List<Integer> st = new ArrayList<>();
                st = temp.table[i][j];       //for each List on index of timetable

                List<Integer> StudentInfo[] = new List[st.size()];          //make array equal to number of courses for students

                for (int k = 0; k < st.size(); k++) {
                    StudentInfo[k] = getStudentsList(st.get(k));

                    for (int l = 0; l < StudentInfo[k].size(); l++) {

                        student.add(StudentInfo[k].get(l));
                    }
                }

                if (i == 6) {
                    timetable_fitness = timetable_fitness + get_three_exams_Fitness(student);
                }

            }
        }

        return timetable_fitness;
    }

    int get_consecutive_slot_fitness(List<Integer>[] arr1, int size1, List<Integer>[] arr2, int size2) {
        int score = 0;

        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < arr1[i].size(); j++) {
                score = score + get_fitness_score(arr2, size2, arr1[i].get(j));
            }

        }
        return score;
    }

    int get_fitness_score(List<Integer>[] arr, int size, int student_id) {
        int score = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < arr[i].size(); j++) {
                if (student_id == arr[i].get(j)) {
                    score = score - 1;
                } else {
                    score = score + 2;
                }

            }
        }
        return score / 2000;
    }

    int get_three_exams_Fitness(List<Integer> arr) {
        int score = 0;
        int fitness = 0;

        for (int i = 0; i < arr.size(); i++) {

            score = get_count_three_exam(arr, arr.get(i));
            if (score > 3) {
                fitness = fitness - 1;
            } else {
                fitness = fitness + 20;
            }

        }

        return fitness;
    }

    int get_count_three_exam(List<Integer> arr, int s_id) {
        int count = 0;
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) == s_id) {

                count++;
            }
        }
        return count;
    }

    //case 3 function
    int get_Clashes_Fitness(List<Integer>[] arr, int size) {
        int s = 0;

        for (int i = 0; i < size; i++) {    //pick up first list of student
            for (int j = 0; j < arr[i].size(); j++) {         //size of list of student
                s = s + get_clash_info(arr, arr[i].get(j), i, size);
            }
        }

        return s;
    }

    //case 3 function
    int get_clash_info(List<Integer>[] arr, int st_id, int List_student_index, int size) {
        int fitness = 0;

        for (int i = 0; i < size && i != List_student_index; i++) {

            for (int j = 0; j < arr[i].size(); j++) {
                if (arr[i].get(j) == st_id) {
                    fitness = fitness - 1;
                }

            }

        }

        return fitness;
    }

    public List getStudentsList(int id) {
        List temp = new ArrayList();

        int r = id;

        for (int j = 0; j < cols; j++) {
            if (this.Reg[r][j] == '1') {
                temp.add(j);
            }
        }

        return temp;
    }

    public int getStudentscount(int id) {

        int r = id;
        int c1 = 0;

        for (int j = 0; j < cols; j++) {
            if (this.Reg[r][j] == '1') {

                c1++;
            }
        }

        return c1;
    }

    void feedRoomsData(List<Integer> roomCap) throws FileNotFoundException, IOException {

        File file = new File("capacity.txt");    //creates a new file instance  
        FileReader fr = new FileReader(file);   //reads the file  
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
        StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
        String line;
        int temp = 0;
        while ((line = br.readLine()) != null) {
            temp = Integer.parseInt(line);
            this.roomCap.add(temp);
        }
        fr.close();
    }

    void feedinfo() throws FileNotFoundException, IOException {

        File f = new File("general.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        int c, i = 0;

        while ((c = fr.read()) != -1) {
            if ((char) c > '0' && (char) c < '8') {
                if (i < 2) {
                    generalInfo[i++] = (char) c;
                }
            }
        }
    }

    void populate() throws FileNotFoundException, IOException {

        File f = new File("registration.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        int c, k = 0, j = 0;

        while ((c = fr.read()) != -1) {
            if ((char) c == '0' || (char) c == '1') {
                Reg[k][j] = (char) c;
                j++;
                if (j >= 3169) {
                    j = 0;
                    k++;
                }
            }
        }

        int[] Div = getSubjects();

        Div[1] = Div[0] + Div[1];
        Div[2] = Div[1] + Div[2];

        int day = 0;
        int i = 0;

        int rowT = 0;
        int colT = 0;

        for (i = 0; i < 3; i++) {
            for (j = 0; j < 7; j++) {
                table[i][j] = new ArrayList<Integer>();
            }
        }

        for (i = 0; i < 223; i++) {
            Random rand = new Random();
            colT = rand.nextInt(7);

            table[rowT][colT].add(i);

            if (i == Div[day]) {
                day++;
                rowT++;
            }
        }

//        int index = 0;
//        for (i = 0; i < 3; i++) {
//            for (j = 0; j < 7; j++) {
//                int s = table[i][j].size();
//
//                System.out.println("\nList No " + index);
//
//                for (k = 0; k < s; k++) {
//
//                    System.out.print(table[i][j].get(k) + " ");
//
//                }
//                System.out.println(" ");
//                index++;
//
//            }
//        }
    }

    int[] getSubjects() {
        int count = rows / 3;
        if (rows % 3 == 0) {
            int[] arr = {count, count, count};
            return arr;
        } else {
            int[] arr = {count, count, count + 1};
            return arr;
        }

    }

    void printTimeTable() {
        for (int i = 0; i < 223; i++) { //Reg.length
            for (int m = 0; m < 3169; m++) { //Reg[i].length
                System.out.print(Reg[i][m] + " ");
            }
            System.out.print("\n");
        }
    }

}
