/*
 * Copyright (c) 2025 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoverageMeasurement {

    private static Lock lock = new ReentrantLock();
    private static final int testId;
    private static Map<String, MethodCoverage> coverageData = new HashMap<String, MethodCoverage>();
    private static Map<String, Lock> locks = new HashMap<>();

    static {
        Random rand = new Random();
        testId = rand.nextInt(Integer.MAX_VALUE);
    }

    private final String methodName;

    public CoverageMeasurement(String methodName, int nbBranches) {
        this.methodName = methodName;

        lock.lock();
        locks.putIfAbsent(methodName, new ReentrantLock());
        lock.unlock();

        locks.get(methodName).lock();
        coverageData.putIfAbsent(methodName, new MethodCoverage(nbBranches));
        if (coverageData.get(methodName).getNbBranches() != nbBranches) {
            locks.get(methodName).unlock();
            throw new RuntimeException(
                    "Method "
                            + methodName
                            + " already registered with a different number of branches");
        }
        saveMethodCoverageState();
        locks.get(methodName).unlock();
    }

    public void branch(int branchId) {
        locks.get(methodName).lock();
        MethodCoverage coverage = coverageData.get(methodName);
        if (!coverage.isCovered(branchId)) {
            // If coverage has changed, save it to a file
            coverage.setCovered(branchId);
            saveMethodCoverageState();
        }
        locks.get(methodName).unlock();
    }

    private void saveMethodCoverageState() {
        MethodCoverage coverage = coverageData.get(methodName);
        StringBuilder sb = new StringBuilder();
        sb.append(methodName).append("\n");
        sb.append(coverage.getNbBranches()).append("\n");
        for (int covered : coverage.getCoveredBranches()) {
            sb.append(covered).append(" ");
        }
        try {
            Files.createDirectories(Path.of("coverage"));
            Files.writeString(Path.of("coverage/" + testId + methodName + ".cov"), sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class MethodCoverage {
    private boolean[] covered;

    public MethodCoverage(int branches) {
        covered = new boolean[branches];
    }

    public void setCovered(int branchId) {
        this.covered[branchId] = true;
    }

    public int getNbBranches() {
        return covered.length;
    }

    public boolean isCovered(int branchId) {
        return covered[branchId];
    }

    public List<Integer> getCoveredBranches() {
        List<Integer> branches = new ArrayList<Integer>();
        for (int i = 0; i < getNbBranches(); i++) {
            if (covered[i]) {
                branches.add(i);
            }
        }
        return branches;
    }
}
