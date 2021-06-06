package ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.*;

class Sortbyfitness implements Comparator<TimeTable> {

    //sort in descending order
    public int compare(TimeTable a, TimeTable b) {
        return b.timetable_fitness - a.timetable_fitness;
    }
}

public class AI {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        int population = 40;
        List<TimeTable> timetables = new ArrayList();

        for (int i = 0; i < population; i++) {

            TimeTable obj = new TimeTable(i + 1); //i+1 = assigning id
            obj.populate();
            timetables.add(obj);

        }

        List<TimeTable> generation = new ArrayList();

        //picking the top 10% fittest chromosomes
        System.out.println("Initial Population:\n");
        for (int i = 0; i < population; i++) {
            System.out.println(timetables.get(i).fitness(timetables.get(i)));
        }
         
        Boolean found=false;
        int loop=1;
        while(found==false){
        //for (int loop = 0; loop < 10; loop++) {

            Collections.sort(timetables, new Sortbyfitness());

            int _population = (50 * population) / 100;
            for (int i = 0; i < _population; i++) {
                generation.add(timetables.get(i));

            }
            //selecting best two, and making them mate
            for (int i = 0; i < _population - 1; i = i + 2) {
                CrossOver(generation.get(i), generation.get(i + 1));
            }

            Random rand = new Random();
            if (rand.nextInt() % 3 == 0) {
                mutation(generation.get(0));
            }

            for (int i = 0; i < _population - 1; i++) {
                // System.out.println("Generation " + (loop+2) + " Fitness Value " + (i + 1) 
                //       + " = " + generation.get(i).fitness(timetables.get(i)));
                generation.get(i).fitness(generation.get(i));

            }
            Collections.sort(generation, new Sortbyfitness());

            System.out.println("Best Fitness value of Generation " + (loop+1)+" = "+generation.get(0).timetable_fitness);
            loop++;
            if(generation.get(0).timetable_fitness>9000){
                found=true;
                System.out.println("\n\nBest Chromosome id= "+generation.get(0).id);
                
            }
        }
    }

    static int[] getBestChromosomes(List<TimeTable> timetables, int[] best) {

        if (timetables.get(0).timetable_fitness > timetables.get(1).timetable_fitness) {
            System.err.println(timetables.get(0).timetable_fitness + " " + timetables.get(1).timetable_fitness);
            best[0] = timetables.get(0).id;
            best[1] = timetables.get(1).id;
        } else {
            System.err.println(timetables.get(0).timetable_fitness + " " + timetables.get(1).timetable_fitness);

            best[0] = timetables.get(1).id;
            best[1] = timetables.get(0).id;
        }
        for (int i = 2; i < timetables.size(); i++) {
            if (timetables.get(i).timetable_fitness > best[0]) {
                System.err.println(timetables.get(i).timetable_fitness);

                best[1] = best[0];
                best[0] = timetables.get(i).id;

            }
        }
        // System.err.println(best);
        return best;

    }

    static void CrossOver(TimeTable a, TimeTable b) {

        Random rand = new Random();
        int crossover_point = rand.nextInt(3); //random row index
        int crossover_point2 = rand.nextInt(7); //random coll index

        ArrayList<Integer> temp1 = new ArrayList<Integer>();   //temp1 arrayList
        ArrayList<Integer> temp2 = new ArrayList<Integer>();   //temp2 arrayList

        for (int j = crossover_point; j < 3; j++) {  //for row

            for (int l = crossover_point2; l < 7; l++) {     //for column

                int store1 = a.table[j][l].size();

                for (int k = 0; k < store1; k++) {
                    temp1.add(a.table[j][l].get(k));       //copy from chromosome 'a' to temp 1
                }

                a.table[j][l].removeAll(a.table[j][l]);

                int store2 = b.table[j][l].size();

                for (int k = 0; k < store2; k++) {
                    temp2.add(b.table[j][l].get(k));      //copy from chromosome 'b' to temp 2
                }

                b.table[j][l].removeAll(b.table[j][l]);

                for (int i = 0; i < store2; i++) //copy chrosome 'b' to 'a'
                {
                    a.table[j][l].add(temp2.get(i));
                }

                for (int i = 0; i < store1; i++) //copy chrosome 'a' to 'b'
                {
                    b.table[j][l].add(temp1.get(i));
                }

            }

        }

    }

    static void mutation(TimeTable a) {

        Random rand = new Random();

        //to be swapped
        int row = rand.nextInt(3);
        int col = rand.nextInt(7);

        //to be swapped with
        int row2 = rand.nextInt(3);
        int col2 = rand.nextInt(7);

        ArrayList<Integer> temp1 = new ArrayList<Integer>();   //temp1 arrayList

        for (int i = 0; i < a.table[row][col].size(); i++) {
            temp1.add(a.table[row][col].get(i));
        }
        a.table[row][col].removeAll(a.table[row][col]);

        for (int i = 0; i < a.table[row2][col2].size(); i++) {
            a.table[row][col].add(a.table[row2][col2].get(i));
        }

        a.table[row2][col2].removeAll(a.table[row2][col2]);

        for (int i = 0; i < temp1.size(); i++) {
            a.table[row2][col2].add(temp1.get(i));
        }

        temp1.removeAll(temp1);

    }

}
