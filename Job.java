public class Job {
    /**
     * Represents a job.
     * A job has an ID, peocessing time and a completionTime (which is the
     * completion time as suppose to the absoloute time).
     */

    int ID;
    double processingTime;
    double completionTime;
    Machine runningMachine;

    public Job(int ID, double processingTime, Machine runningMachine) {
        this.ID = ID;
        this.processingTime = processingTime;
        this.runningMachine = runningMachine;
        runningMachine.insert(this);
        this.setCompletionTime();
    }

    public double getProcessingTime() {
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
            double accCompletionTime = 0;
            double runningMachineSpeed = this.runningMachine.getSpeed();
            while (iterator.current != null) {
                if (iterator.current.job == this)
                    break;
                accCompletionTime += (iterator.current.job.getProcessingTime() / runningMachineSpeed);
                iterator.next();
            }
            this.completionTime = accCompletionTime + (this.processingTime / runningMachineSpeed);
        }
    }

    public void setMachine(Machine newMachine) {
        this.runningMachine = newMachine;
    }

    public String toString() {
        return String.format("%1$47s", "{ Job " + this.ID + " Processing Time: " + processingTime + " completionTime "
                + completionTime + " }");
    }

    public String toString2() {
        return String.format("%1$22s", "{ Job " + this.ID + " PT: " + String.format("%.2f", processingTime) + " CT "
                + String.format("%.2f", completionTime) + " }");
    }

    public String toString3() {
        return String.format("%1$" + 10 + processingTime / 2 + "s",
                "{ Job " + this.ID + " PT: " + processingTime + " CT " + completionTime + " }");
    }

    public String toString4() {
        String s = " {";
        for (int i = 0; i < processingTime; i++) {
            s += (" " + this.ID);
        }
        // s += " (" + processingTime + ", " + completionTime + ") }";
        return s + " }";
    }
}
