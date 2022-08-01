public class Simulator {
    /**
     * Represents a simulator.
     * A Simulator has an array of machines, a chronoList (which is a list keeping
     * all the machines in ascending order of their IDs)
     */
    Machine[] machines;
    List allJobs;

    public Simulator(int machineNum) {
        this.machines = new Machine[machineNum];
        this.allJobs = new List();
    }

    /*
     * The main function runs the simulator with different input types. input can be
     * command line arguments (number of machines, policy (same one for all
     * machines, number of jobs, round type) or a file in the format attached with
     * this source code. the file details the same parameters as in the command line
     * arguments. in addition, the file contains of initial scheduling of the jobs
     * onto the machines, and it is possible to dictate a different policy for each
     * machine.
     * When invoked the simulator will run until reaching stable state
     * (Nash Equilibrium) or until reaching a predefined amount of rounds (currently
     * set to maximum 100 rounds)
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("missing arguments");
        } else if (args.length == 1) {
            String fileName = args[0];
            readFromFile(fileName);
        } else if (args.length == 5) {
            int machineNum = Integer.parseInt(args[0]); // an int value between 1 - n
            int policy = Integer.parseInt(args[1]); // an int value between 0 - 3 (detailed policies' behavior are in
                                                    // class Machine under insert function)
            double speed = Double.parseDouble(args[2]); // a double value larger than 0
            int jobNum = Integer.parseInt(args[3]); // an int value between 1 - n
            int roundType = Integer.parseInt(args[4]); // an int value 1 (for SPT) or 2 (LPT)
            readFromCommandLine(machineNum, policy, speed, jobNum, roundType);
        } else {
            throw new IllegalArgumentException("too many arguments");
        }
    }

    public static void readFromFile(String fileName) {
        StdIn.setInput(fileName);
        int roundType = StdIn.readInt();
        int machineNum = StdIn.readInt();
        int jobNum = StdIn.readInt();
        Simulator sim = new Simulator(machineNum);
        for (int i = 0; i < machineNum; i++) {
            double speed = StdIn.readDouble();
            int policy = StdIn.readInt();
            if (policy == 4) {
                int[] priorityList = new int[jobNum];
                for (int j = 0; j < jobNum; j++) {
                    priorityList[j] = StdIn.readInt();
                }
                sim.machines[i] = new Machine(i, policy, speed, priorityList);
            } else {
                sim.machines[i] = new Machine(i, policy, speed);
            }
        }
        sim.checkValidity(jobNum);
        sim.addJobs2Sim(jobNum, roundType);
        System.out.println(sim.toString());
        sim.runSimulator(roundType, fileName);
    }

    public void checkValidity(int jobNum) {
        for (Machine machine : this.machines) {
            machine.checkValidity();
        }
    }

    public void addJobs2Sim(int numJobs, int roundType) {
        Machine pseudoMachine = new Machine(-1, 0, 1);
        double inf = Double.POSITIVE_INFINITY;
        Job pseudoJob = new Job(-1, inf, pseudoMachine);
        for (int i = 0; i < numJobs; i++) {
            double processingTime = StdIn.readDouble();
            Job newJob = new Job(i, processingTime, pseudoMachine);
            this.allJobs.addLast(newJob);
        }
        initialSchedule(pseudoMachine, roundType);
    }

    public void initialSchedule(Machine pseudoMachine, int roundType) {
        sortAllJobs(roundType);
        ListIterator iterator = this.allJobs.iterator();
        while (iterator.current != null) {
            Machine curMachine = iterator.current.job.runningMachine;
            Machine bestMachine = this.bestResponseJobInitial(iterator.current.job, pseudoMachine);
            if (curMachine != bestMachine) {
                this.moveInitial(iterator.current.job, pseudoMachine, bestMachine);
            }
            iterator.next();
        }
        System.out.println();
    }

    public Machine bestResponseJobInitial(Job job, Machine pseudoMachine) {
        int startIndex = pseudoMachine.jobList.indexOf(job);
        Machine toMachine = pseudoMachine;
        double bestCompTime = job.completionTime;
        for (Machine machine : this.machines) {
            this.moveInitial(job, pseudoMachine, machine);
            if (job.completionTime < bestCompTime) {
                bestCompTime = job.completionTime;
                toMachine = job.runningMachine;
            }
            job.runningMachine.remove(job);
        }
        pseudoMachine.jobList.add(startIndex, job);
        job.setRunningMachine(pseudoMachine);
        this.setCompletionTime();
        return toMachine;
    }

    public void moveInitial(Job job, Machine currentMachine, Machine destMachine) {
        currentMachine.remove(job);
        ListIterator iterator = currentMachine.jobList.iterator();
        while (iterator.current != null) {
            iterator.current.job.setCompletionTime();
            iterator.next();
        }
        destMachine.insert(job);
    }

    public static void readFromCommandLine(int machineNum, int policy, double speed, int jobNum, int roundType) {
        Simulator sim = new Simulator(machineNum);
        for (int i = 0; i < sim.machines.length; i++) {
            sim.machines[i] = new Machine(i, policy, speed);
        }
        sim.addJobs2SimRand(jobNum);
        System.out.println(sim.toString());
        sim.runSimulator(roundType);
    }

    public void addJobs2SimRand(int numJobs) {
        for (int i = 0; i < numJobs; i++) {
            int processingTime = (int) (Math.random() * 10 + 1);
            int MachineID = (int) (Math.random() * this.machines.length);
            Job newJob = new Job(i, processingTime, this.machines[MachineID]);
            this.allJobs.addLast(newJob);
        }
    }

    /*
     * runs the Simulator FROM FILE with the required round type (LPT/SPT), where
     * SPT=1 and LPT=2
     */
    public void runSimulator(int lptOrSpt, String fileName) {
        sortAllJobs(lptOrSpt);
        boolean same = false;
        int rounds = 0;
        while (!same && rounds < 100) {
            same = runRound();
            rounds++;
        }
        if (rounds == 100 && !same) {
            System.out.println("Sim ended after reaching stopping condition " + rounds + " rounds.");
        } else {
            System.out.println("Sim reached stable state after " + rounds + " rounds.");
            double optimum = optimum1();
            double makeSpan = makeSpan();
            double quality = makeSpan / optimum;
            System.out.println("A lower bound for the minimal makespan is " + String.format("%.2f", optimum) + ".");
            System.out.println("The current makespan is " + String.format("%.2f", makeSpan) + ".");
            System.out.println("Scheduling quality is " + String.format("%.2f", quality) + ".");
            // double optimum = optimum2(fileName);
            // double sum = compTimeSum();
            // double quality = sum / optimum;
            // System.out.println(
            // "Optimum sum of completion time is " + String.format("%.2f", optimum) + ".");
            // System.out.println(
            // "The current sum of completion time is " + String.format("%.2f", sum) + ".");
            System.out.println();
        }
    }

    /*
     * runs the Simulator FROM COMMAND LINE with the required round type (LPT/SPT),
     * where SPT = 1 and LPT = 2
     */
    public void runSimulator(int lptOrSpt) {
        sortAllJobs(lptOrSpt);
        boolean same = false;
        int rounds = 0;
        while (!same && rounds < 100) {
            same = runRound();
            rounds++;
        }
        if (rounds == 100 && !same) {
            System.out.println("Sim ended after reaching stopping condition " + rounds +
                    " rounds.");
        } else {
            System.out.println("Sim reached stable state after " + rounds + " rounds.");
            System.out.println();
        }
    }

    private void sortAllJobs(int lptOrSpt) {
        if (lptOrSpt == 1) {
            allJobs.sort();
        } else if (lptOrSpt == 2) {
            allJobs.sortReverse();
        } else {
            throw new IllegalArgumentException("round type must be between 1 (for SPT) or 2 (for LPT)");
        }
    }

    private boolean runRound() {
        ListIterator iterator = this.allJobs.iterator();
        int[] same = new int[this.allJobs.getSize()];
        while (iterator.current != null) {
            System.out.println("Started BRJ for job: " + iterator.current.job.getID());
            Machine curMachine = iterator.current.job.runningMachine;
            Machine bestMachine = this.bestResponseJob(iterator.current.job);
            if (curMachine != bestMachine) {
                this.move(iterator.current.job, bestMachine);
                System.out.println("finished BRJ, moved to: " + iterator.current.job.runningMachine.ID
                        + " <mach | time> " + String.format("%.2f", iterator.current.job.completionTime));
                same[iterator.current.job.getID()] = 0;
            } else {
                System.out.println("finished BRJ, stayed at: " + iterator.current.job.runningMachine.ID
                        + " <mach | time> " + String.format("%.2f", iterator.current.job.completionTime));
                same[iterator.current.job.getID()] = 1;
            }
            System.out.println();
            System.out.println(this.toString());
            iterator.next();
        }
        return checkStable(same);
    }

    private boolean checkStable(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 1)
                return false;
        }
        return true;
    }

    public Machine bestResponseJob(Job job) {
        Machine fromMachine = job.runningMachine;
        int startIndex = fromMachine.jobList.indexOf(job);
        Machine toMachine = job.runningMachine;
        double bestCompTime = job.completionTime;
        for (Machine machine : this.machines) {
            this.move(job, machine);
            if (job.completionTime < bestCompTime) {
                bestCompTime = job.completionTime;
                toMachine = job.runningMachine;
            }
        }
        job.runningMachine.remove(job);
        fromMachine.jobList.add(startIndex, job);
        job.setRunningMachine(fromMachine);
        this.setCompletionTime();
        return toMachine;
    }

    public void move(Job job, Machine destMachineID) {
        int currMachineID = job.runningMachine.getID();
        machines[currMachineID].remove(job);
        ListIterator iterator = machines[currMachineID].jobList.iterator();
        while (iterator.current != null) {
            iterator.current.job.setCompletionTime();
            iterator.next();
        }
        destMachineID.insert(job);
    }

    public void setCompletionTime() {
        for (Machine machine : machines) {
            machine.setCompletionTime();
        }
    }

    /*
     * computes a lower bound for the makespan. the makespan is the latest
     * completion time of a job as suppose to all the jobs on all the machines
     */
    private double optimum1() {
        double speedSum = speedSum();
        double ProcessingTimeSum = ProcessingTimeSum();
        return ProcessingTimeSum / speedSum;
    }

    private double speedSum() {
        double sum = 0;
        for (Machine machine : this.machines) {
            double curSpeed = machine.getSpeed();
            sum += curSpeed;
        }
        return sum;
    }

    private double ProcessingTimeSum() {
        double sum = 0;
        ListIterator iterator = this.allJobs.iterator();
        while (iterator.current != null) {
            sum += iterator.current.job.getProcessingTime();
            iterator.current = iterator.current.next;
        }
        return sum;
    }

    /*
     * computes the makespan after reaching stable state (NE)
     */
    public double makeSpan() {
        double max = 0;
        for (Machine machine : this.machines) {
            double curMaxCompTime = machine.jobList.getLast().job.getcompletionTime();
            if (curMaxCompTime > max) {
                max = curMaxCompTime;
            }
        }
        return max;
    }

    /*
     * computes the sum of all completion times after initial scheduling of jobs
     * onto the machines
     */
    public static double optimum2(String fileName) {
        StdIn.setInput(fileName);
        int roundType = StdIn.readInt();
        int machineNum = StdIn.readInt();
        int jobNum = StdIn.readInt();
        Simulator optimalSim = new Simulator(machineNum);
        for (int i = 0; i < machineNum; i++) {
            double speed = StdIn.readDouble();
            int policy = StdIn.readInt();
            if (policy == 4) {
                int[] priorityList = new int[jobNum];
                for (int j = 0; j < jobNum; j++) {
                    priorityList[j] = StdIn.readInt();
                }
                optimalSim.machines[i] = new Machine(i, policy, speed, priorityList);
            } else {
                optimalSim.machines[i] = new Machine(i, policy, speed);
            }
        }
        optimalSim.addJobs2Sim(jobNum, 1);
        double sum = 0;
        for (Machine machine : optimalSim.machines) {
            ListIterator itr = machine.jobList.iterator();
            while (itr.current != null) {
                sum += itr.current.job.getcompletionTime();
                itr.current = itr.current.next;
            }
        }
        return sum;
    }

    /*
     * computes the sum of all completion times after reaching stable state (NE) of
     * jobs onto the machines
     */
    private double compTimeSum() {
        double sum = 0;
        for (Machine machine : this.machines) {
            ListIterator itr = machine.jobList.iterator();
            while (itr.current != null) {
                sum += itr.current.job.getcompletionTime();
                itr.current = itr.current.next;
            }
        }
        return sum;
    }

    public int checkFirstNull() {
        for (int i = 0; i < machines.length; i++) {
            if (machines[i].jobList.getFirst() == null)
                return i;
        }
        return -1;
    }

    public void testMove(Job job, Machine destMachineID) {
        try {
            this.move(job, destMachineID);
        } catch (Exception e) {
            System.out.println("Exception: No such job/machine exists.\n");
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Machine machine : machines) {
            s.append(machine.toString());
        }
        return s.toString();
    }
}
