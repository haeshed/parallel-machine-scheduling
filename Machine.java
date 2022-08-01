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
    Priority priorityList;

    public Machine(int ID, int policy, double speed) {
        this.ID = ID;
        this.CompletionTime = 0;
        this.jobList = new List();
        this.policy = policy;
        this.speed = speed;
    }

    public Machine(int ID, int policy, double speed, int[] inputPriorityList) {
        this.ID = ID;
        this.CompletionTime = 0;
        this.jobList = new List();
        this.policy = policy;
        this.speed = speed;
        Priority priority = new Priority(inputPriorityList);
        this.priorityList = priority;
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
     * policy 4 = adds to the correct position according to this machine's priority
     * list
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
        } else if (policy == 4) {
            int index = this.priorityList.find(job.getID());
            int insertIndex = 0;
            int[] arr = priorityList.getRealPriorityList();
            for (int i = 0; i < index; i++) {
                if (jobList.indexOf(arr[i]) != -1) {
                    insertIndex++;
                }
            }
            jobList.add(insertIndex, job);
            job.setRunningMachine(this);
            setCompletionTime();
        }
    }

    public void setCompletionTime() {
        ListIterator iterator = jobList.iterator();
        while (iterator.current != null) {
            iterator.current.job.setCompletionTime();
            iterator.next();
        }
        int index = this.jobList.getSize();
        if (index > 0) {
            this.CompletionTime = this.jobList.getNode(index - 1).job.getcompletionTime();
        } else if (index == 0) {
            this.CompletionTime = 0;
        }
    }

    /*
     * removes the given job from this jobList
     */
    public void remove(Job job) {
        jobList.remove(job);
        this.setCompletionTime();
    }

    public void checkValidity() {
        if (policy < 0 || policy > 4) {
            throw new IllegalArgumentException("machine " + this.ID + " policy must be bewtween 0 - 4");
        }
        if (policy == 4) {
            int prioritySize = priorityList.getRealPriorityList().length;
            int[] valid = new int[prioritySize];
            for (int i = 0; i < prioritySize; i++) {
                int x = priorityList.getRealPriorityList()[i];
                if (x >= prioritySize || prioritySize < 0) {
                    throw new IllegalArgumentException(
                            "machine " + this.ID + " priority list values must be between 0 - (jobNum - 1)");
                }
                valid[x] = 1;
            }
            for (int j = 0; j < valid.length; j++) {
                if (valid[j] != 1)
                    throw new IllegalArgumentException(
                            "machine " + this.ID + " priority list has duplicates");
            }
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder("");
        s.append("Machine " + this.ID + ":      " + jobList.toString() + "\n");
        return s.toString();
    }

}
