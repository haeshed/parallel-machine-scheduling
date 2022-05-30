public class Simulator {
    /**
     * Represents a simulator.
     * A job has an array of machines, a chronoList (which is a list keeping all the
     * machines in an ascending oreder of their IDs) and a round (which is the
     * order of iteraing threw the jobs and asking them if hey want to change
     * machines).
     */
    Machine[] machines;
    List allJobs;
    int round;

    public Simulator(int machineNum, int round) {
        this.machines = new Machine[machineNum];
        for (int i = 0; i < machines.length; i++) {
            machines[i] = new Machine(i, 0);
        }
        this.round = round;
        this.allJobs = new List();
    }

    public Simulator() {
        this.machines = new Machine[5];
        for (int i = 0; i < 5; i++) {
            machines[i] = new Machine(i, 0);
        }
        this.round = 10;
        this.allJobs = new List();
    }

    public static void main(String[] args) {
        // Building a simulator
        int machineNum = Integer.parseInt(args[0]);
        int jobNum = Integer.parseInt(args[1]);
        Simulator sim1 = new Simulator(machineNum, 0);
//        sim1.buildSimulator(machineNum, jobNum);
        sim1.buildSimulator();
//        System.out.println(sim1.toString());
//        sim1.testMove(sim1.machines[0].getJob(0), sim1.machines[1]);
        System.out.println(sim1.toString());
//        System.out.println("decided: " + sim1.bestResponseJob(sim1.machines[0].getJob(1)));
        sim1.runRound();
        System.out.println(sim1.toString());
    }

    public void testMove(Job job, Machine destMachineID) {
        try {
            this.move(job, destMachineID);
        } catch (Exception e) {
            System.out.println("Exception: No such job/machine exists.\n");
        }
    }

    public Machine bestResponseJob(Job job) {
        System.out.println("Started BRJ");
        Machine fromMachine = job.runningMachine;
        int startIndex = fromMachine.jobList.indexOf(job);
        Machine toMachine = job.runningMachine;
        int bestCompTime = job.completionTime;
        for (Machine machine : this.machines) {
            this.move(job, machine);
            System.out.println(bestCompTime + " <best | cur> " + job.completionTime);
            System.out.println(toMachine.ID + " <bestM | curM> " + job.runningMachine.ID);
            if (job.completionTime < bestCompTime) {
                System.out.println("entered");
                System.out.println(job.completionTime + " <time | mach> " + job.runningMachine.ID);
                bestCompTime = job.completionTime;
                toMachine = job.runningMachine;
            }
        }
        if (toMachine == fromMachine) {
            fromMachine.jobList.add(startIndex, job);
        }
        return toMachine;
    }

    private void runRound() {
        ListIterator iterator = this.allJobs.iterator();
        while (iterator.current != null) {
            Machine bestMachine = this.bestResponseJob(iterator.current.job);
            if (iterator.current.job.runningMachine != bestMachine) {
                this.move(iterator.current.job, bestMachine);
            }
            this.toString();
            iterator.next();
        }
    }

    public void runSimulator(int times) {
        for (int i = 0; i < times; i++) {
            System.out.println();
        }
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
        System.out.println("moved job: " + job.getID() + " from machine: " + currMachineID + " to machine: " + job.runningMachine.ID);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Machine machine : machines) {
            s.append(machine.toString());
        }
        return s.toString();
    }

    public void buildSimulator(int numMachines, int numJobs) {
        for (int i = 0; i < numJobs; i++) {
            int processingTime = (int) (Math.random() * 10 + 1);
            int MachineID = (int) (Math.random() * numMachines);
            Job newJob = new Job(i, processingTime, this.machines[MachineID]);
            this.allJobs.addLast(newJob);
        }
    }

    public void buildSimulator() {
        for (int i = 0; i < 10; i++) {
            int processingTime = 10 - i;
            int MachineID = i/2;
            Job newJob = new Job(i, processingTime, this.machines[MachineID]);
            this.allJobs.addLast(newJob);
        }
    }
}
