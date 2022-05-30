public class Job {
    /**
     * Represents a job.
     * A job has an ID, peocessing time and a completionTime (which is the
     * completion time as suppose to the absoloute time).
     */

    int ID;
    int processingTime;
    int completionTime;
    Machine runningMachine;

    // public Job(int ID, int processingTime, int completionTime) {
    // this.ID = ID;
    // this.processingTime = processingTime;
    // this.completionTime = completionTime;
    // }

    public Job(int ID, int processingTime, Machine runningMachine) {
        this.ID = ID;
        this.processingTime = processingTime;
        this.runningMachine = runningMachine;
        runningMachine.insert(this);
        this.setCompletionTime();
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    public int getID() {
        return this.ID;
    }

    public void setRunningMachine(Machine m) {
        this.runningMachine = m;
    }

    public void setCompletionTime() {
        if (runningMachine.jobList.getSize() == 1) {
            this.completionTime = processingTime;
        } else {
            ListIterator iterator = runningMachine.jobList.iterator();
//            System.out.println(iterator.toString());
            int accCompletionTime = 0;
            while (iterator.current != null) {
                if (iterator.current.job == this) break;
                accCompletionTime += iterator.current.job.getProcessingTime();
                iterator.next();
            }
            this.completionTime = accCompletionTime + this.processingTime;
        }
    }

    public void setMachine(Machine newMachine) {
        this.runningMachine = newMachine;
    }

    public String toString() {
        return String.format("%1$47s", "{ Job " + this.ID + " Processing Time: " + processingTime + " completionTime " + completionTime + " }");
    }

    public String toString2() {
        return String.format("%1$22s", "{ Job " + this.ID + " PT: " + processingTime + " CT " + completionTime + " }");
    }
}
