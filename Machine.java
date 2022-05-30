public class Machine {
    /**
     * Represents a machine.
     * A job has an ID, policy and a JobList (which is a list of the jobs currently
     * running on the machine).
     */

    int ID;
    int CompletionTime;
    List jobList;
    int policy;

    public Machine(int ID, int policy) {
        this.ID = ID;
        this.CompletionTime = 0;
        this.jobList = new List();
        this.policy = policy;
    }

    public int getCompletionTime() {
        return this.CompletionTime;
    }

    public int getPolicy() {
        return this.policy;
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
     * policy 0 - adds to the end of the line
     * policy 1 = add to the beggining of the line
     * policy 2 = adds ti the correct position keeping the jobs on the machine
     * from small to large
     * policy 3 = adds ti the correct position keeping the jobs on the machine
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
            job.setCompletionTime();
        } else if (policy == 2) {
            ListIterator iterator = jobList.iterator();
            int index = 0;
            while (iterator.current != null && iterator.current.job.getProcessingTime() <= job.getProcessingTime()) {
                iterator.next();
                index++;
            }
            if (iterator.current != null) {
                jobList.addLast(job);
            } else {
                jobList.add(index - 1, job);
            }
            job.setRunningMachine(this);
            job.setCompletionTime();
        } else if (policy == 3) {
            ListIterator iterator = jobList.iterator();
            int index = 0;
            while (iterator.current != null && iterator.current.job.getProcessingTime() >= job.getProcessingTime()) {
                iterator.next();
                index++;
            }
            if (iterator.current != null) {
                jobList.addLast(job);
            } else {
                jobList.add(index - 1, job);
            }
            job.setRunningMachine(this);
            job.setCompletionTime();
        }
    }

    public void setCompletionTime() {
        ListIterator iterator = jobList.iterator();
        int accCompletionTime = 0;
        while (iterator.hasNext()) {
            iterator.current.job.completionTime = accCompletionTime + iterator.current.job.getProcessingTime();
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
