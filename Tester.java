public class Tester {

    public static void main(String args[]) {
        int machineNum = Integer.parseInt(args[0]);
        int jobNum = Integer.parseInt(args[1]);
        int roundType = Integer.parseInt(args[2]);
        // testSort(machineNum, jobNum);
        test1(machineNum, jobNum, roundType);

    }

    public static void testSort(int machineNum, int jobNum) {
        Simulator sim1 = new Simulator(machineNum);
        sim1.addJobs2Sim(jobNum);
        System.out.println(sim1.toString());
        // sim1.allJobs.sortReverse();
        // sim1.allJobs.sort();
        System.out.println(sim1.allJobs);
    }

    public static void test1(int machineNum, int jobNum, int roundType) {
        Simulator sim1 = new Simulator(machineNum);
        sim1.addJobs2SimRand(jobNum);
        System.out.println(sim1.toString());
        // ListIterator itr = sim1.allJobs.iterator();
        // while (itr.current != null) {
        // System.out.println(itr.current.job.runningMachine.ID);
        // itr.current = itr.current.next;
        // }
        sim1.runSimulator(1);
    }

}
