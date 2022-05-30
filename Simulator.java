public class Simulator {
    /**
     * Represents a simulator.
     * A job has an array of machines, a chronoList (which is a list keeping all the
     * machines in an ascending oreder of their IDs) and a round (which is the
     * order of iteraing threw the jobs and asking them if hey want to change
     * machines).
     */
    Machine[] sim;
    List chronoList;
    int round;

    public Simulator(int machineNum, int round) {
        this.sim = new Machine[machineNum];
        for (int i = 0; i < sim.length; i++) {
            sim[i] = new Machine(i, 0);
        }
        this.round = round;
        this.chronoList = new List();
    }

    public void move(Job job, Machine destMachineID) {
        int currMachineID = job.runningMachine.getID();
        sim[currMachineID].remove(job);
        ListIterator iterator = sim[currMachineID].jobList.iterator();
        while (iterator.current != null) {
            iterator.current.job.setCompletionTime();
            iterator.next();
        }
        destMachineID.insert(job);
    }

    public String toString() {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < sim.length; i++) {
            s.append(sim[i].toString() + "\n");
        }
        return s.toString();
    }

    public static void main(String[] args) {
        // Building a simulator
        int machineNum = Integer.parseInt(args[0]);
        int jobNum = Integer.parseInt(args[1]);
        Simulator sim1 = new Simulator(machineNum, 0);
        for (int i = 0; i < jobNum; i++) {
            int processingTime = (int) (Math.random() * 10 + 1);
            int MachineID = (int) (Math.random() * machineNum);
            Job newJob = new Job(i, processingTime, sim1.sim[MachineID]);
            sim1.chronoList.addLast(newJob);
        }
        System.out.println(sim1.toString());
        // test1(sim1, sim1.sim[0].getJob(0), sim1.sim[1]);
    }

    public static void test1(Simulator sim, Job job, Machine destMachineID) {
        sim.move(job, destMachineID);
        System.out.println(sim.toString());
    }
}
