public class Simulator {
    /**
     * Represents a simulator.
     * A Simulator has an array of machines, a chronoList (which is a list keeping
     * all the
     * machines in ascending order of their IDs) and a round (which is the
     * order of iterating threw the jobs and asking them if hey want to change
     * machines).
     */
    Machine[] machines;
    List allJobs;

    public Simulator(int machineNum) {
        this.machines = new Machine[machineNum];
        this.allJobs = new List();
    }

    /*
     * The main function runs the simulator with different input types. input can be
     * command line arguments number of machines, policy (same one for all
     * machines, number of jobs, round type) or a file in the format attached with
     * this source code. the file details the same parameters as in the command line
     * arguments. in addition, the file contains of initial scheduling of the jobs
     * onto the machines, and it is possible to dictate a different policy for each
     * machine.
     * When invoked the simulator will run until reaching stable state
     * (Nash Equilibrium) or until reaching a predefined amount of rounds (currently
     * set to maximum 10 rounds)
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("missing arguments");
        } else if (args.length == 1) {
            String fileName = args[0];
            readFromFile(fileName);
        } else if (args.length == 4) {
            int machineNum = Integer.parseInt(args[0]); // an int value between 1 - n
            int policy = Integer.parseInt(args[1]); // an int value between 0 - 3 (detailed policies' behavior are in
                                                    // class Machine under insert function
            int jobNum = Integer.parseInt(args[2]); // an int value between 1 - n
            int roundType = Integer.parseInt(args[3]); // an int value 1 (for SPT) or 2 (LPT)
            readFromCommandLine(machineNum, policy, jobNum, roundType);
        } else {
            throw new IllegalArgumentException("too many arguments");
        }
    }

    public static void readFromFile(String fileName) {
        StdIn.setInput(fileName);
        int machineNum = StdIn.readInt();
        Simulator sim1 = new Simulator(machineNum);
        for (int i = 0; i < machineNum; i++) {
            int policy = StdIn.readInt();
            sim1.machines[i] = new Machine(i, policy);
        }
        int jobNum = StdIn.readInt();
        sim1.addJobs2Sim(jobNum);
        System.out.println(sim1.toString());
        int roundType = StdIn.readInt();
        sim1.runSimulator(roundType);
    }

    public void addJobs2Sim(int numJobs) {
        for (int i = 0; i < numJobs; i++) {
            int processingTime = StdIn.readInt();
            int MachineID = StdIn.readInt();
            Job newJob = new Job(i, processingTime, this.machines[MachineID]);
            this.allJobs.addLast(newJob);
        }
    }

    public static void readFromCommandLine(int machineNum, int policy, int jobNum, int roundType) {
        Simulator sim1 = new Simulator(machineNum);
        for (int i = 0; i < sim1.machines.length; i++) {
            sim1.machines[i] = new Machine(i, policy);
        }
        sim1.addJobs2SimRand(jobNum);
        System.out.println(sim1.toString());
        sim1.runSimulator(roundType);
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
     * runs the Simulator with the required round type (LPT/SPT), where SPT = 1 and
     * LPT = 2
     */
    public void runSimulator(int lptOrSpt) {
        sortAllJobs(lptOrSpt);
        boolean same = false;
        int rounds = 0;
        while (!same && rounds < 10) {
            same = runRound();
            // System.out.println(this.toString());
            rounds++;
        }
        if (rounds == 10 && !same) {
            System.out.println("Sim ended after reaching stopping condition " + rounds + " rounds.");
        } else
            System.out.println("Sim reached stable state after " + rounds + " rounds.");
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
        for (int i = 0; i < same.length; i++) {
            same[i] = 0;
        }
        while (iterator.current != null) {
            System.out.println("Started BRJ for job: " + iterator.current.job.getID());
            Machine curMachine = iterator.current.job.runningMachine;
            Machine bestMachine = this.bestResponseJob(iterator.current.job);
            if (curMachine != bestMachine) {
                this.move(iterator.current.job, bestMachine);
                System.out.println("finished BRJ, moved to: " + iterator.current.job.runningMachine.ID
                        + " <mach | time> " + iterator.current.job.completionTime);
                same[iterator.current.job.getID()] = 0;
            } else {
                System.out.println("finished BRJ, stayed at: " + iterator.current.job.runningMachine.ID
                        + " <mach | time> " + iterator.current.job.completionTime);
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
        // System.out.println("Started BRJ for job: " + job.getID());
        Machine fromMachine = job.runningMachine;
        int startIndex = fromMachine.jobList.indexOf(job);
        Machine toMachine = job.runningMachine;
        int bestCompTime = job.completionTime;
        for (Machine machine : this.machines) {
            this.move(job, machine);
            if (job.completionTime < bestCompTime) {
                // System.out
                // .println("-----switched best to mach " + job.runningMachine.ID + " time " +
                // job.completionTime);
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
