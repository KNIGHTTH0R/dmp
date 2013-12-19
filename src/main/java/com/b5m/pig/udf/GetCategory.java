package com.b5m.pig.udf;

import com.b5m.maxent.CategoryClassifier;
import com.b5m.maxent.MaxEnt;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * UDF that maps a string to a product category.
 * @author Paolo D'Apice
 */
public class GetCategory extends EvalFunc<String> {

    private final String filename;
    private final boolean isLocal;

    private CategoryClassifier classifier = null;

    /**
     * @param filename file containing the MaxEnt model
     * @param local flag indicating whether the model is on local filesystem
     */
    public GetCategory(String filename, String local) {
        this.filename = filename;
        this.isLocal = local.equals("local");
    }

    @Override
    public String exec(Tuple tuple) throws IOException {
        if (tuple == null || tuple.size() == 0) {
            return null;
        }

        String title = (String) tuple.get(0);

        if (classifier == null) init();

        return classifier.getCategory(title);
    }

    /*
     * XXX Note that cache is disabled when Pig is running in local mode.
     * https://issues.apache.org/jira/browse/PIG-1752
     */

    @Override
    public List<String> getCacheFiles() {
        String name = String.format("%s#maxent", filename);
        return Arrays.asList(name);
    }

    private void init() throws IOException {
        File file = new File(isLocal ? filename : "./maxent");
        classifier = new MaxEnt(file);
    }

}

