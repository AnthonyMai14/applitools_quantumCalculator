package src;

class ApplitoolsQuantumCalculator {
    
    protected static final int SIZE = 20;
    protected static final int NUM_OF_CALCULATORS = 1;


    static class QuantumCalculator implements Runnable {
        Thread calculate;
        public Calculator calculatorObj;
        public Double successRate;
        protected Double[][] randNum;
        protected Double[] actualSolution, errorSolution;
        protected Boolean[] comparison;

        QuantumCalculator(Calculator obj) {
            this.calculatorObj = obj;
            //where the 20 random numbers will be stored
            this.randNum  = new Double[SIZE][SIZE];
            //where the actual solution will be stored
            this.actualSolution = new Double[SIZE];
            //where the soltuion with the quantum error will be stored
            this.errorSolution = new Double[SIZE];
            //where keep track the number of success/failures
            this.comparison = new Boolean[SIZE];
            //set default success rate to zero
            successRate = 0.0;
        }

        protected void generateRand() {
            for (int i = 0; i < SIZE; ++i) {
                this.randNum[i][0] = Math.random();
                this.randNum[i][1] = Math.random();
            }
        }

        //calculation the addition of the the SIZE pairs both traditionally and via the quantum calculator
        protected void calculate() {
            for (int i = 0; i < SIZE; ++i) {
                //set temporary variable so that don't have to keep accessing via array and decrease error
                Double firstVal = this.randNum[i][0];
                Double secondVal = this.randNum[i][1];
                //calculate for actually solution
                this.actualSolution[i] =  firstVal + secondVal;
                //calculate using quantum calculator
                this.errorSolution[i] = this.calculatorObj.add(firstVal, secondVal);
            }
        }

        //see if the quantum calculator had an error or not and record it into comparison[]
        protected void calculateSuccess() {
            for (int i = 0; i < SIZE; ++i) {
                this.comparison[i] = (this.actualSolution[i] == this.errorSolution[i]) ? true : false;
            }
        }

        protected String interpretQuantumSuccess(int i) {
            if (comparison[i] == true) {
                return "(correct)";
            }
            else {
                return "(error)";
            }
        }
    
        public void printSolutions() {
            System.out.println("Calculator " + this.calculatorObj.getName() + ":");
            for (int i = 0; i < SIZE; ++i) {
                System.out.print(this.randNum[i][0] + " + " + this.randNum[i][1] + " = " + errorSolution[i] + " ");
                System.out.println(interpretQuantumSuccess(i)); 
            }
        }

        public Double calculateSuccessRate() {
            Double rate = 0.0;
            for (int i = 0; i < SIZE; ++i) {
                if (this.comparison[i] == true) {
                    rate += 1;
                }
            }
            return successRate =  rate / (double) SIZE;
        }
        
        public void printSuccessRate() {
            System.out.print(this.calculatorObj.getName() + " Success rate: ");
            calculateSuccessRate();
            System.out.println(this.successRate);
        }
        
        @Override
        public void run() {
            //generate random number of 20
            this.generateRand();
            try {
                this.calculate();
                this.calculateSuccess();
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.out.println("Thread has been interrupted");
            }
        }
        public void start() {
            if (calculate == null) {
             calculate = new Thread(this, calculatorObj.getName());
             calculate.start();
            }
        }

    }
    
    static class FindBestCalculator {
        public QuantumCalculator[] bestCalculator;
        public QuantumCalculator[] listOfCalculators;
        private int numOfBestCalculator = 0;

        FindBestCalculator() {
            this.bestCalculator = new QuantumCalculator[NUM_OF_CALCULATORS];
            this.listOfCalculators = new QuantumCalculator[NUM_OF_CALCULATORS];
        }

        public void calculateBestCalculator() { 
            //begin search
            for (int i = 0; i < NUM_OF_CALCULATORS; ++i)  {
                if (i == 0) { 
                    bestCalculator[i] = listOfCalculators[i]; 
                    ++numOfBestCalculator;
                }
                else if (bestCalculator[numOfBestCalculator - 1].successRate == listOfCalculators[i].calculateSuccessRate()) {
                    bestCalculator[numOfBestCalculator] = listOfCalculators[i];
                    ++numOfBestCalculator;
                }
                else if (bestCalculator[numOfBestCalculator - 1].successRate < listOfCalculators[i].calculateSuccessRate()) {
                    numOfBestCalculator = 1;
                    bestCalculator[0] = listOfCalculators[i];
                }
            }
        }
    
        public void printBestCalculator() {
            for (int i = 1; i <= numOfBestCalculator; ++i) {
                if((numOfBestCalculator > 1) && (i != 1)) {
                    System.out.print(", ");
                }
                System.out.print(bestCalculator[i-1].calculatorObj.getName());
            }
            if (numOfBestCalculator == 1) {
                System.out.println(" is better.");
            }
            else {
                System.out.println(" are better.");
            }
        }
    }
    public static void main(String[] args) {
        
        //create calculator, wrapped in a QuantumnCalculator class and start()
        Calculator crystal1 = new Calculator("Crystal 1");
        QuantumCalculator thread1 = new QuantumCalculator(crystal1);
        thread1.start();
        Calculator crystal2 = new Calculator("Crystal 2");
        QuantumCalculator thread2 = new QuantumCalculator(crystal2);
        thread2.start();

        //print solutions
        thread1.printSolutions();
        thread2.printSolutions();
        System.out.println();
        //print success rate
        thread1.printSuccessRate();
        thread2.printSuccessRate();

        FindBestCalculator findBest = new FindBestCalculator();
        //determine which is a success rate
        findBest.listOfCalculators[0] = thread1;
        findBest.listOfCalculators[1] = thread2;
        findBest.calculateBestCalculator();
        findBest.printBestCalculator();

    }
}