package sample;


import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData;
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask;

/**
 * Created by aqram on 10/17/14.
 * 与えられた配列をソートして返すタスク
 * ヒープソートを使ってます
 */
public class SampleTaskImpl implements OffloadableTask {

    private static final String TASK_NAME = "SAMPLE";

    private double[] heap;
    private int num;


    @Override
    public OffloadableData run(OffloadableData data) {

        double[] vals = (double[])data.getData(SampleTaskKeys.DATA);

        if(vals == null){
            System.out.println("vals is null");
        }
        heap = new double[vals.length];
        num = 0;

        for(int i=0; i<vals.length; i++){
            insert(vals[i]);
        }

        for(int i = 0; num>0; i++){
            vals[i] = deletemin();
        }

        OffloadableData result = new OffloadableData(data.NAME_SPACE);
        result.putData(SampleTaskKeys.DATA,vals);

        return result;
    }

    private void insert(double d){

        heap[num++]=d;
        int i=num,j=i/2;
        while(i>1 && heap[i-1]<heap[j-1]){
            double t=heap[i-1];
            heap[i-1]=heap[j-1];
            heap[j-1]=t;
            i=j;
            j=i/2;
        }

    }

    private double deletemin(){

        double r=heap[0];
        heap[0]=heap[--num];
        int i=1,j=i*2;
        while(j<=num){
            if(j+1<=num && heap[j-1]>heap[j]) j++;
            if(heap[i-1]>heap[j-1]){
                double t=heap[i-1];
                heap[i-1]=heap[j-1];
                heap[j-1]=t;
            }
            i=j;
            j=i*2;
        }
        return r;

    }

    @Override
    public String getName() {
        return TASK_NAME;
    }
}
