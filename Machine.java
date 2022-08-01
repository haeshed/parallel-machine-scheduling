public class Machine {
    /**
     * Represents a machine.
     * A job has an ID, policy and a JobList (which is a list of the jobs currently
     * running on the machine).
     */

    int ID;
    double CompletionTime;
    List jobList;
    int policy;
    double speed;

    public Machine(int ID, int policy, double speed) {
        this.ID = ID;
        this.CompletionTime = 0;
        this.jobList = new List();
        this.policy = policy;
        this.speed = speed;
    }

    public double getCompletionTime() {
        return this.CompletionTime;
    }

    public int getPolicy() {
        return this.policy;
    }

    public double getSpeed() {
        return this.speed;
    }

    public Job getJob(int index) {
        return this.jobList.getJob(index);
    }

    public int getID() {
        return this.ID;
    }

    /*
     * inserts a new job to this machine taking into account this machine's
     * policy:
     * policy 0 = adds to the end of the line
     * policy 1 = add to the beggining of the line
     * policy 2 = adds to the correct position keeping the jobs on the machine
     * from small to large
     * policy 3 = adds to the correct position keeping the jobs on the machine
     * from large to small
     */
    public void insert(Job job) {
        if (policy == 0) {
            jobList.addLast(job);
            job.setRunningMachine(this);
            job.setCompletionTime();
        } else if (policy == 1) {
            jobList.addFirst(job);
            job.setRunningMachine(this);
            setCompletionTime();
        } else if (policy == 2) {
            jobList.addLast(job);
            job.setRunningMachine(this);
            jobList.sort();
            setCompletionTime();
        } else if (policy == 3) {
            jobList.addLast(job);
            job.setRunningMachine(this);
            jobList.sortReverse();
            setCompletionTime();
        }
    }

    public void setCompletionTime() {
        ListIterator iterator = jobList.iterator();
        while (iterator.current != null) {
            iterator.current.job.setCompletionTime();
            iterator.next();
        }
    }

    /*
     * removes the given job from this jobList
     */
    public void remove(Job job) {
        jobList.remove(job);
    }

    public String toString() {
        StringBuilder s = new StringBuilder("");
        s.append("Machine " + this.ID + ":      " + jobList.toString() + "\n");
        return s.toString();
    }

}
