package bookstack.Tools;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Statistics
{
    public Statistics() {}

    public static double getMean(List<Integer> data)
    {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/data.size();
    }

    public static double getVariance(List<Integer> data)
    {
        double mean = getMean(data);
        double temp = 0;
        for(double a :data)
            temp += (mean-a)*(mean-a);
        return temp/data.size();
    }

    public static double getStdDev(List<Integer> data)
    {
        return Math.sqrt(getVariance(data));
    }

    public static double median(List<Integer> data)
    {
       LinkedList<Integer> b = new LinkedList<>();
       b.addAll(data);
       Collections.sort(b);

       if (data.size() % 2 == 0)
       {
          return (b.get((b.size() / 2) - 1) + b.get(b.size() / 2)) / 2.0;
       } 
       else 
       {
          return b.get(b.size() / 2);
       }
    }
}