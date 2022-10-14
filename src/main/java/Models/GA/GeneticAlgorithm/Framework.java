package Models.GA.GeneticAlgorithm;



import Entity.Matrix;
import Models.GA.Entity.Individual;
import Models.GA.MyThread.BlockingBuffer;
import Models.GA.MyThread.GenerateThread;
import Models.GA.MyThread.InitializationThread;
import Models.GA.ObjectiveFunction.FitnessCalculator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Framework {
    private int numPopulation;
    private int loops;
    private int terminal;
    private int initializationThreadNum;
    private int elitePer;
    private double crossoverRate;
    private double mutationRate;
    private int generateThreadNum;
    private ArrayList<Individual> population;
    private FitnessCalculator fitnessCalculator;
    private double[] weights;
    private Matrix distance_matrix;
    private List<Integer> nodes;

    public Framework(String configPath, Matrix distance_matrix, List<Integer> nodes) throws IOException {

        //read Config
        readConfig(configPath);

        //read input
        this.distance_matrix = distance_matrix;
        this.nodes = nodes;


        //generate fitnessCalculator
        fitnessCalculator = new FitnessCalculator(distance_matrix, weights);

        //initialize population
        population = new ArrayList<>();

    }

    public void startAll() throws InterruptedException {
        //Initialization
        initialization();

        //Calculate fitness


        float bestFitness = 1000000;
        int currentConvect = 0;
        Individual bestSolution = null;

        BlockingBuffer newPopulation = new BlockingBuffer(numPopulation+100);
        //For each generation
        for (int i=0; i<loops;i++){
            //Selection
            int numElite = selection(newPopulation);

            int numGenerate = numPopulation - numElite;
            //Generate
            generate(newPopulation, numGenerate);

            //Calculate fitness



            //Log
            Collections.sort(population);
            if (bestFitness>population.get(0).getFitness()){
                bestFitness = population.get(0).getFitness();
                bestSolution = population.get(0);
                currentConvect = 0;
            }else{
                currentConvect++;
                if (currentConvect>terminal){
                    break;
                }
            }
            System.out.println(String.format("Generation %d best fitness: %f", i, bestFitness));
            System.out.println("Solution size " + bestSolution.solution.size());
            System.out.println("solution i " + bestSolution.toString() );
            System.out.println();



        }


    }

    public void calcuateFitness(){

//        population.forEach(i -> i.setFitness(fitnessCalculator.calculateFitess(i)));
    }

    private int selection(BlockingBuffer newPopulation) throws InterruptedException {


        int eliteNumber = numPopulation/elitePer;

        for (int i=0; i<eliteNumber; i++){
            newPopulation.push(population.get(i));
        }

        return eliteNumber;

    }


    public int initialization(){
        BlockingBuffer initializationPopulation = new BlockingBuffer(numPopulation);
        //initialize executor
        ExecutorService executorService = Executors.newFixedThreadPool(initializationThreadNum);

        //initialize population
        for (int i=0; i<numPopulation; i++){
            //generate thread
            Runnable thread = new InitializationThread(initializationPopulation, nodes, fitnessCalculator);

            executorService.execute(thread);
        }

        //shutdown thread
        executorService.shutdown();

        while (!executorService.isTerminated()){

        }

        System.out.println("Finish initiation phase");



        return initializationPopulation.drainTo(population);
    }


    public void generate(BlockingBuffer newPopulation, int numNewIndividual){
        //initialize executor
        ExecutorService executorService = Executors.newFixedThreadPool(generateThreadNum);

        //initialize population
        for (int i=0; i<numNewIndividual/2; i++){
            //generate thread
            Runnable thread = new GenerateThread(newPopulation, nodes, population, crossoverRate, mutationRate, fitnessCalculator);

            executorService.execute(thread);
        }

        //shutdown thread
        executorService.shutdown();

        while (!executorService.isTerminated()){

        }

//        for (int i=0; i<numNewIndividual/2; i++){
//            //generate thread
//            GenerateThread thread = new GenerateThread(newPopulation, nodes, population, crossoverRate, mutationRate, fitnessCalculator);
//
//            thread.test();
//        }

        System.out.println("Finish generate phase");

        population.clear();
        newPopulation.drainTo(population);
    }

    public void log(){

    }

    private void readConfig(String configPath) throws IOException {

        //load stream
        InputStream inputStream = new FileInputStream(configPath);

        //load config
        Properties prop = new Properties();

        prop.load(inputStream);

        loops = Integer.parseInt(prop.getProperty("genetic.loops"));
        numPopulation = Integer.parseInt(prop.getProperty("genetic.numPopulation"));
        terminal = Integer.parseInt(prop.getProperty("genetic.terminal"));
        elitePer = Integer.parseInt(prop.getProperty("genetic.elitePercent"));
        crossoverRate = Double.parseDouble(prop.getProperty("genetic.crossoverRate"));
        mutationRate = Double.parseDouble(prop.getProperty("genetic.mutationRate"));
        generateThreadNum = Integer.parseInt(prop.getProperty("genetic.generateThreadNum"));
        initializationThreadNum = Integer.parseInt(prop.getProperty("genetic.initializationThreadNum"));
        weights =  Arrays.stream(prop.getProperty("objectives.weights").split(",")).mapToDouble(Double::parseDouble).toArray();

    }
}
