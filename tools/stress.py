#!/usr/bin/env python3

import argparse
import os, atexit
import textwrap
import time
import threading, subprocess


import signal
import random
import time
from enum import Enum

from collections import defaultdict, OrderedDict

PROCESSES_BASE_IP = 11000

class ProcessState(Enum):
    RUNNING = 1
    STOPPED = 2
    TERMINATED = 3

class ProcessInfo:
    def __init__(self, handle):
        self.lock = threading.Lock()
        self.handle = handle
        self.state = ProcessState.RUNNING

    @staticmethod
    def stateToSignal(state):
        if state == ProcessState.RUNNING:
            return signal.SIGCONT

        if state == ProcessState.STOPPED:
            return signal.SIGSTOP

        if state == ProcessState.TERMINATED:
            return signal.SIGTERM

    @staticmethod
    def stateToSignalStr(state):
        if state == ProcessState.RUNNING:
            return "SIGCONT"

        if state == ProcessState.STOPPED:
            return "SIGSTOP"

        if state == ProcessState.TERMINATED:
            return "SIGTERM"

    @staticmethod
    def validStateTransition(current, desired):
        if current == ProcessState.TERMINATED:
            return False

        if current == ProcessState.RUNNING:
            return desired == ProcessState.STOPPED or desired == ProcessState.TERMINATED

        if current == ProcessState.STOPPED:
            return desired == ProcessState.RUNNING

        return False

class AtomicSaturatedCounter:
    def __init__(self, saturation, initial=0):
        self._saturation = saturation
        self._value = initial
        self._lock = threading.Lock()

    def reserve(self):
        with self._lock:
            if self._value < self._saturation:
                self._value += 1
                return True
            else:
                return False

class Validation:
    def __init__(self, procs, msgs):
        self.processes = procs
        self.messages = msgs

    def generatePerfectLinksConfig(self, directory):
        hostsfile = os.path.join(directory, 'hosts')
        configfile = os.path.join(directory, 'config')

        with open(hostsfile, 'w') as hosts:
            for i in range(1, self.processes + 1):
                hosts.write("{} localhost {}\n".format(i, PROCESSES_BASE_IP+i))

        with open(configfile, 'w') as config:
            config.write("{} 1\n".format(self.messages))

        return (hostsfile, configfile)

    def generateFifoConfig(self, directory):
        hostsfile = os.path.join(directory, 'hosts')
        configfile = os.path.join(directory, 'config')

        with open(hostsfile, 'w') as hosts:
            for i in range(1, self.processes + 1):
                hosts.write("{} localhost {}\n".format(i, PROCESSES_BASE_IP+i))

        with open(configfile, 'w') as config:
            config.write("{}\n".format(self.messages))

        return (hostsfile, configfile)

    def generateLcausalConfig(self, directory):
        hostsfile = os.path.join(directory, 'hosts')
        configfile = os.path.join(directory, 'config')

        with open(hostsfile, 'w') as hosts:
            for i in range(1, self.processes + 1):
                hosts.write("{} localhost {}\n".format(i, PROCESSES_BASE_IP+i))

        with open(configfile, 'w') as config:
            config.write("{}\n".format(self.messages))
            for i in range(1, self.processes + 1):
                others = list(range(1, self.processes + 1))
                others.remove(i)
                random.shuffle(others)
                if len(others) // 2 > 0:
                    others = others[0: len(others) // 2]

                config.write("{} {}\n".format(i, ' '.join(map(str, others))))


        return (hostsfile, configfile)

class StressTest:
    def __init__(self, procs, concurrency, attempts, attemptsRatio):
        self.processes = len(procs)
        self.processesInfo = dict()
        for (logicalPID, handle) in procs:
            self.processesInfo[logicalPID] = ProcessInfo(handle)
        self.concurrency = concurrency
        self.attempts = attempts
        self.attemptsRatio = attemptsRatio

        maxTerminatedProcesses = self.processes // 2 if self.processes % 2 == 1 else (self.processes - 1) // 2
        self.terminatedProcs = AtomicSaturatedCounter(maxTerminatedProcesses)

    def stress(self):
        selectProc = list(range(1, self.processes+1))
        random.shuffle(selectProc)

        selectOp = [ProcessState.STOPPED] * int(1000 * self.attemptsRatio['STOP']) + \
                    [ProcessState.RUNNING] * int(1000 * self.attemptsRatio['CONT']) + \
                    [ProcessState.TERMINATED] * int(1000 * self.attemptsRatio['TERM'])
        random.shuffle(selectOp)

        successfulAttempts = 0
        while successfulAttempts < self.attempts:
            proc = random.choice(selectProc)
            op = random.choice(selectOp)
            info = self.processesInfo[proc]

            with info.lock:
                if ProcessInfo.validStateTransition(info.state, op):

                    if op == ProcessState.TERMINATED:
                        reserved = self.terminatedProcs.reserve()
                        if reserved:
                            selectProc.remove(proc)
                        else:
                            continue

                    time.sleep(float(random.randint(50, 500)) / 1000.0)
                    info.handle.send_signal(ProcessInfo.stateToSignal(op))
                    info.state = op
                    successfulAttempts += 1
                    print("Sending {} to process {}".format(ProcessInfo.stateToSignalStr(op), proc))

                    # if op == ProcessState.TERMINATED and proc not in terminatedProcs:
                    #     if len(terminatedProcs) < maxTerminatedProcesses:

                    #         terminatedProcs.add(proc)

                    # if len(terminatedProcs) == maxTerminatedProcesses:
                    #     break

    def remainingUnterminatedProcesses(self):
        remaining = []
        for pid, info in self.processesInfo.items():
            with info.lock:
                if info.state != ProcessState.TERMINATED:
                    remaining.append(pid)

        return None if len(remaining) == 0 else remaining

    def terminateAllProcesses(self):
        for _, info in self.processesInfo.items():
            with info.lock:
                if info.state != ProcessState.TERMINATED:
                    if info.state == ProcessState.STOPPED:
                        info.handle.send_signal(ProcessInfo.stateToSignal(ProcessState.RUNNING))

                    info.handle.send_signal(ProcessInfo.stateToSignal(ProcessState.TERMINATED))

        return False

    def continueStoppedProcesses(self):
        for _, info in self.processesInfo.items():
            with info.lock:
                if info.state != ProcessState.TERMINATED:
                    if info.state == ProcessState.STOPPED:
                        info.handle.send_signal(ProcessInfo.stateToSignal(ProcessState.RUNNING))

    def run(self):
        if self.concurrency > 1:
            threads = [threading.Thread(target=self.stress) for _ in range(self.concurrency)]
            [p.start() for p in threads]
            [p.join() for p in threads]
        else:
            self.stress()

def startProcesses(processes, runscript, hostsFilePath, configFilePath, outputDir):
    runscriptPath = os.path.abspath(runscript)
    if not os.path.isfile(runscriptPath):
        raise Exception("`{}` is not a file".format(runscriptPath))

    if os.path.basename(runscriptPath) != 'run.sh':
        raise Exception("`{}` is not a runscript".format(runscriptPath))

    outputDirPath = os.path.abspath(outputDir)
    if not os.path.isdir(outputDirPath):
        raise Exception("`{}` is not a directory".format(outputDirPath))

    baseDir, _ = os.path.split(runscriptPath)
    bin_cpp = os.path.join(baseDir, "bin", "da_proc")
    bin_java = os.path.join(baseDir, "bin", "da_proc.jar")

    if os.path.exists(bin_cpp):
        cmd = [bin_cpp]
    elif os.path.exists(bin_java):
        cmd = ['java', '-jar', bin_java]
    else:
        raise Exception("`{}` could not find a binary to execute. Make sure you build before validating".format(runscriptPath))

    procs = []
    for pid in range(1, processes+1):
        cmd_ext = ['--id', str(pid),
                   '--hosts', hostsFilePath,
                   '--output', os.path.join(outputDirPath, 'proc{:02d}.output'.format(pid)),
                   configFilePath]

        stdoutFd = open(os.path.join(outputDirPath, 'proc{:02d}.stdout'.format(pid)), "w")
        stderrFd = open(os.path.join(outputDirPath, 'proc{:02d}.stderr'.format(pid)), "w")


        procs.append((pid, subprocess.Popen(cmd + cmd_ext, stdout=stdoutFd, stderr=stderrFd)))

    return procs

def main(processes, messages, runscript, testType, logsDir, testConfig):
    if not os.path.isdir(logsDir):
        raise ValueError('Directory `{}` does not exist'.format(logsDir))


    validation = Validation(processes, messages)
    if testType == "perfect":
        hostsFile, configFile = validation.generatePerfectLinksConfig(logsDir)
    elif testType == "fifo":
        hostsFile, configFile = validation.generateFifoConfig(logsDir)
    elif testType == "lcausal":
        hostsFile, configFile = validation.generateLcausalConfig(logsDir)
    else:
        raise ValueError('Unrecognised test type')

    try:
        # Start the processes and get their PIDs
        procs = startProcesses(processes, runscript, hostsFile, configFile, logsDir)

        # Create the stress test
        st = StressTest(procs,
                        testConfig['concurrency'],
                        testConfig['attempts'],
                        testConfig['attemptsDistribution'])

        for (logicalPID, procHandle) in procs:
            print("Process with logicalPID {} has PID {}".format(logicalPID, procHandle.pid))

        st.run()
        print("StressTest is complete.")

        print("Resuming stopped processes.")
        st.continueStoppedProcesses()

        input("Press `Enter` when all processes have finished processing messages.")

        unterminated = st.remainingUnterminatedProcesses()
        if unterminated is not None:
            st.terminateAllProcesses()

        mutex = threading.Lock()

        def waitForProcess(logicalPID, procHandle, mutex):
            procHandle.wait()

            with mutex:
                print("Process {} exited with {}".format(logicalPID, procHandle.returncode))

        # Monitor which processes have exited
        monitors = [threading.Thread(target=waitForProcess, args=(logicalPID, procHandle, mutex)) for (logicalPID, procHandle) in procs]
        [p.start() for p in monitors]
        [p.join() for p in monitors]

    finally:
        if procs is not None:
            for _, p in procs:
                p.kill()

if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "-r",
        "--runscript",
        required=True,
        dest="runscript",
        help="Path to run.sh",
    )

    parser.add_argument(
        "-t",
        "--test",
        choices=["perfect", "fifo", "lcausal"],
        required=True,
        dest="testType",
        help="Which test to run",
    )

    parser.add_argument(
        "-l",
        "--logs",
        required=True,
        dest="logsDir",
        help="Directory to store stdout, stderr and outputs generated by the processes",
    )

    parser.add_argument(
        "-p",
        "--processes",
        required=True,
        type=int,
        dest="processes",
        help="Number of processes that broadcast",
    )

    parser.add_argument(
        "-m",
        "--messages",
        required=True,
        type=int,
        dest="messages",
        help="Maximum number (because it can crash) of messages that each process can broadcast",
    )

    results = parser.parse_args()

    testConfig = {
        'concurrency' : 8, # How many threads are interferring with the running processes
        'attempts' : 8, # How many interferring attempts each threads does
        'attemptsDistribution' : { # Probability with which an interferring thread will
            'STOP': 0.48,          # select an interferring action (make sure they add up to 1)
            'CONT': 0.48,
            'TERM':0.04
        }
    }

    main(results.processes, results.messages, results.runscript, results.testType, results.logsDir, testConfig)
