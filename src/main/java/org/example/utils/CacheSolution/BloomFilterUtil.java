package org.example.utils.CacheSolution;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import kotlin.jvm.internal.PackageReference;

public class BloomFilterUtil {

    private static  final int EXPECTED_INSERTIONS = 1000;
    private static  final double FPP = 0.01;

    private static  final BloomFilter<Long> bloomfilter = BloomFilter.create(
            Funnels.longFunnel(), EXPECTED_INSERTIONS, FPP);


    public static void add(Long id){
       bloomfilter.put(id);

    }


    public static boolean mightContain(Long id) {
        return bloomfilter.mightContain(id);
    }


}
