public class Simulator {
    /**
     * Represents a simulator.
     * A job has an array of machines, a chronoList (which is a list keeping all the
     * machines in an ascending oreder of their IDs) and a round (which is the
     * order of iteraing threw the jobs and asking them if hey want to change
     * machines).
     */
    Machine[] machines;
    List chronoList;
    int round;

    public Simulator(int machineNum, int round) {
        this.machines = new Machine[machineNum];
        for (int i = 0; i < machines.length; i++) {
            machines[i] = new Machine(i, 0);
        }
        this.round = round;
        this.chronoList = new List();
    }

    public static void main(String[] args) {
        // Building a simulator
        int machineNum = Integer.parseInt(args[0]);
        int jobNum = Integer.parseInt(args[1]);
        Simulator sim1 = new Simulator(machineNum, 0);
        sim1.runSimulator(machineNum, jobNum);
        System.out.println("there");
        System.out.println(sim1.toString());
        sim1.test1(sim1.machines[0].getJob(0), sim1.machines[1]);
        System.out.println(sim1.toString());
    }

    public void test1(Job job, Machine destMachineID) {
        try {
            this.move(job, destMachineID);
        } catch (Exception e) {
            System.out.println("Exception: No such job/machine exists.\n");
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
        System.out.println("moved job: " + job.getID() + " from machine: " + currMachineID + " to machine: " + job.runningMachine.ID + "\n");
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < machines.length; i++) {
            s.append(machines[i].toString());
        }
        return s.toString();
    }

    public void runSimulator(int numMachines, int numJobs) {
        for (int i = 0; i < numJobs; i++) {
            int processingTime = (int) (Math.random() * 10 + 1);
            int MachineID = (int) (Math.random() * numMachines);
            Job newJob = new Job(i, processingTime, this.machines[MachineID]);
            this.chronoList.addLast(newJob);
        }
    }
}
