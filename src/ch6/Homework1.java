package ch6;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class Homework1 extends RecursiveTask<Double>{   
    private int start;
    private int end;
  
    
    public Homework1(int start,int end){
        this.start=start;
        this.end=end;
       
       
    }
    
    public Double compute(){
        double sum=0;
        boolean canCompute = (end-start)<1000;
        if(canCompute){
            for(double i=(double)start;i<=(double) end;i+=0.01){
                sum +=(0.01*(1/i));
            }
        }else{
            //分成100个小任务
            int step=2;
            ArrayList<Homework1> subTasks=new ArrayList<Homework1>();
            int pos=start;
            for(int i=0;i<50;i++){
                int lastOne=pos+step;
                if(lastOne>end) lastOne=end;
                Homework1 subTask=new Homework1(pos,lastOne);
                pos+=step;
                subTasks.add(subTask);
                subTask.fork();
            }
            for(Homework1  t:subTasks){
                sum+=t.join();
            }
        }
        return sum;
    }
    
    public static void main(String[]args){
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Homework1 task = new Homework1(1,100);
        ForkJoinTask<Double> result = forkJoinPool.submit(task);
        try{
            double res = result.get();
            System.out.println("sum="+res);
        }catch(InterruptedException e){
            e.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }
        System.out.println("one thread:"+onethread());
    }
    static double onethread() {
    	double sum = 0;
    	for(double i=1;i<=100;i+=0.01) {    		
    		sum+=(0.01*(1/i));
    	}
    	return sum;
    }
}