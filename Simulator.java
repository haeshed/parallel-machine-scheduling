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
    int policies;
//    int round;

    public Simulator(int machineNum, int policies) {
        this.machines = new Machine[machineNum];
        for (int i = 0; i < machines.length; i++) {
            machines[i] = new Machine(i, policies);
        }
//        this.round = round;
        this.allJobs = new List();
    }

    public Simulator() {
        this.machines = new Machine[5];
        for (int i = 0; i < 5; i++) {
            machines[i] = new Machine(i, 0);
        }
//        this.round = 10;
        this.allJobs = new List();
    }

    public static void main(String[] args) {
        // Building a simulator
        int machineNum = Integer.parseInt(args[0]);
        int jobNum = Integer.parseInt(args[1]);
        Simulator sim1 = new Simulator(3, 0);
//        Simulator sim1 = new Simulator(machineNum, Integer.parseInt(args[2]));
//        sim1.addJobs2Sim(jobNum);
        sim1.addJobs2Sim();
        System.out.println(sim1.toString());
//        sim1.runRound();
        sim1.runSimulator();
    }

    public void addJobs2Sim(int numJobs) {
        for (int i = 0; i < numJobs; i++) {
            int processingTime = (int) (Math.random() * 10 + 1);
            int MachineID = (int) (Math.random() * this.machines.length);
            Job newJob = new Job(i, processingTime, this.machines[MachineID]);
            this.allJobs.addLast(newJob);
        }
    }

    public void addJobs2Sim() {
        for (int i = 0; i < 20; i++) {
            int processingTime = 20 - i;
            int MachineID = i / 7;
            Job newJob = new Job(i, processingTime, this.machines[MachineID]);
            this.allJobs.addLast(newJob);
        }
    }

    public void runSimulator(int times) {
        int rounds = 0;
        for (int i = 0; i < times; i++) {
            runRound();
            System.out.println("End of round " + i+"\n" + this.toString());
            rounds++;
        }
        System.out.println("Sim finished given " + rounds + " rounds.");
    }

    public void runSimulator() {
        boolean same = false;
        int rounds = 0;
        while (!same) {
            same = runRound();
            System.out.println(this.toString());
            rounds++;
        }
        System.out.println("Sim ended after " + rounds + " rounds.");
    }

    private boolean runRound() {
        ListIterator iterator = this.allJobs.iterator();
        boolean same = false;
        while (iterator.current != null) {
            Machine curMachine = iterator.current.job.runningMachine;
            Machine bestMachine = this.bestResponseJob(iterator.current.job);
            if (curMachine != bestMachine) {
                this.move(iterator.current.job, bestMachine);
                System.out.println("finished BRJ, moved to: " + iterator.current.job.runningMachine.ID + " <mach | time> " + iterator.current.job.completionTime);
                same = true;
            } else
                System.out.println("finished BRJ, stayed at: " + iterator.current.job.runningMachine.ID + " <mach | time> " + iterator.current.job.completionTime);
            System.out.println();
            System.out.println(this.toString());
            iterator.next();
        }
        return same;
    }

    public Machine bestResponseJob(Job job) {
        System.out.println("Started BRJ for job: " + job.getID());
        Machine fromMachine = job.runningMachine;
        int startIndex = fromMachine.jobList.indexOf(job);
        Machine toMachine = job.runningMachine;
        int bestCompTime = job.completionTime;
        for (Machine machine : this.machines) {
            this.move(job, machine);
//            System.out.println(bestCompTime + " <best | cur> " + job.completionTime);
//            System.out.println(toMachine.ID + " <bestM | curM> " + job.runningMachine.ID);
            if (job.completionTime < bestCompTime) {
                System.out.println("-----switched best to mach " + job.runningMachine.ID + " time " + job.completionTime);
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
//        System.out.println("moved job id: " + job.getID() + "   machines: " + currMachineID + " ---> " + job.runningMachine.ID);
    }

    public void setCompletionTime() {
        for (Machine machine : machines) {
            machine.setCompletionTime();
        }
    }

    public int checkFirstNull() {
        for (int i = 0; i < machines.length; i++) {
            if (machines[i].jobList.getFirst() == null) return i;
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
