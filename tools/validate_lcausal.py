import argparse

config_path = './output/config'
output_path = './output/proc0'   # append id.output later, id<=9

def read_config():
    f = open(config_path,"r")
    data = f.readlines()
    m = 0
    dict = {}
    count = 0
    for line in data:
        splited = line.split(" ")
        if count == 0:
            m = int(splited[0]) # how many messages each process should broadcast
        else:
	        sender = int(splited[0])
	        for i in range(len(splited)):
	            dict.setdefault(sender,[]).append(int(splited[i])) # dependency (including sender itself)
        count+=1
    return dict

# find all 'd sender seq' in output_id, where sender is in depent set
def getDependentList(id, dset):
    cur_output_path = output_path+str(id)+'.output'
    f = open(cur_output_path,"r")
    data = f.readlines()
    
    sequence = []
    for line in data:
        splited = line.split(" ")
        if splited[0] == "d": # only consider diliver
            if int(splited[1]) in dset: # only consider sender in depent set
                sequence.append(line)
    print(str(id),'#msg:',len(sequence))
    return sequence

# find all 'b seq' and relevent delivered message in output_id, where sender is in depent set
def getDependentListOfCreator(id, dset):
    cur_output_path = output_path+str(id)+'.output'
    f = open(cur_output_path,"r")
    data = f.readlines()
    
    sequence = []
    for line in data:
        splited = line.split(" ")
        if splited[0] == "b":
            sequence.append(line)
        else:
            if int(splited[1]) in dset:
                sequence.append(line)
    print(str(id),'#msg:',len(sequence))
    return sequence
    
# get vector clock associated with each message created by id from sequence
def getVectorClock(sequence, id, proc_num):
    clock = []
    msg_clock_list = []
    for i in range(proc_num+1):
        clock.append(0)
    for msg in sequence:
        splited = msg.split(" ")
        sender = int(splited[1])
        clock[sender]+=1
        if sender==id:
            msg_clock_list.append(clock.copy())
    return msg_clock_list

# get vector clock associated with each message broadcasted by id from sequence
def getVectorClockOfCreator(sequence, id, proc_num):
    clock = []
    msg_clock_list = []
    for i in range(proc_num+1):
        clock.append(0)
    for msg in sequence:
        splited = msg.split(" ")
        sender = int(splited[1])
        if splited[0]=='d':
            clock[sender]+=1
        else:
            msg_clock_list.append(clock.copy())
    return msg_clock_list
    
# check if dependency of id is satisfied in all other process
def checkProcessId(id, dset):
    print('checing process', str(id), ',depend on', dset)
    
    # sequence of currrent process
    ref_sequence = getDependentListOfCreator(id, dset)
    ref_clock = getVectorClockOfCreator(ref_sequence, id, proc_num)
    print(id,'#clock:',len(ref_clock))
    
    for i in range(proc_num):
        cur_id = i+1
        if cur_id != id:
            # sequence of other process
            sequence = getDependentList(cur_id, dset)
            # get vector clock
            clock = getVectorClock(sequence, id, proc_num)
            print(cur_id,'#clock:',len(clock))
            
            # number should be less of equal
            if len(clock)>len(ref_clock):
                print('Number exceeds!')
                return False
            # output should be the same with ref_sequence
            for i in range(len(clock)):
                for j in range(proc_num+1):
                    if ref_clock[i][j] > clock[i][j]:
                        print('Clock not match!')
                        print('ref_clock:',ref_clock[i])
                        print('clock    :',clock[i])
                        return False
    return True

# check output of all processes
def checkProcess(proc_num):
    depend = read_config()
    print('dependency:',depend,'\n')
    
    for i in range(proc_num):
        id = i+1
        if checkProcessId(id, depend[id])==False:
            return False
        print('validate process',str(id),'OK\n')
    return True

if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "--proc_num",
        required=True,
        dest="proc_num",
        help="Total number of processes",
    )

    results = parser.parse_args()
    
    proc_num = int(results.proc_num)
    if checkProcess(proc_num):
        print("Validation OK")
    else:
        print("Validation failed!")