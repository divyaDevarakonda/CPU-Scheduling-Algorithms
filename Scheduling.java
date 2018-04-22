import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Scheduling {
	public static final int cols=4;
	public static int time_quantum=0,numOfProcesses=0;
	public static void main(String[] args) {

		
		try {
			int flag=0;
			String line;
			int k=0,counter=0;
			String scheduling = null;
			int processInfo[][] = null;
			BufferedWriter bw=null;
			FileWriter fw=null;
			BufferedReader reader=new BufferedReader(new FileReader("/Users/divyadevarakonda/Documents/139/test_cases/input16.txt"));
			File outFile = new File("/Users/divyadevarakonda/Documents/139/output.txt");
			
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			fw = new FileWriter(outFile); 
			bw = new BufferedWriter(fw);
			while((line=reader.readLine())!=null)
			{
				if(line.length() >0){
				String[] tempStr=line.split("\\s");
				
					if(counter==0)
					{
						scheduling=tempStr[0];
						if(tempStr.length==2)
						{
						 time_quantum=Integer.parseInt(tempStr[1]);
						
						}
						counter++;
						
					}
					else if (counter==1)
					{
						numOfProcesses=Integer.parseInt(tempStr[0]);
						counter++;
					}
					else
					{
						if(flag==0){
							processInfo=new int[numOfProcesses][cols];
							flag=1;
						}
						for(int j=0;j<cols;j++)
						{
							processInfo[k][j]=Integer.parseInt(tempStr[j]);
						}
						k++;
						
					}
					
			}
				}
			
			alorithmSelection(scheduling,processInfo,numOfProcesses,bw);
			reader.close();
		} catch (FileNotFoundException e) {
			e.getMessage();
		} catch (IOException e) {
			e.getMessage();
		}
		
	
	}

	private static void alorithmSelection(String scheduling, int[][] processTable, int num_processes2, BufferedWriter bw) {
		if(scheduling.equalsIgnoreCase("RR"))
		{
			roundRobin(processTable,numOfProcesses,bw);
		}
		else if(scheduling.equalsIgnoreCase("SJF"))
		{
			shortestJobFirst(processTable,numOfProcesses,bw);
		}
		else if(scheduling.equalsIgnoreCase("PR_noPREMP"))
		{
			prioritySchedulingwithoutPreemption(processTable, numOfProcesses, bw);
		}
		else if(scheduling.equalsIgnoreCase("PR_withPREMP"))
		{
			prioritySchedulingwithPreemption(processTable, numOfProcesses, bw);
		}
	}

	public static void roundRobin(int schldProcesses[][],int processCount,BufferedWriter bw)
	{	 
		try{ 
			bw.write("RR");

			System.out.println("Round Robin:");
			int count=0,timeQuamtum,tempTime=0;
			int checkProCount=1,sum=0,flag=1;
			timeQuamtum=time_quantum;
			float waitingTime=0,avg=0;
			int i=0;
			int availableProcess = 0;
			int remainingTime[]=new int[schldProcesses.length];
			for(int j=0;j<schldProcesses.length;j++){
				remainingTime[j]=schldProcesses[j][2];
				sum=sum+remainingTime[j];
			}
			while(count!=sum){
				if(tempTime==timeQuamtum || remainingTime[i]==0){
					availableProcess=0;
					for(int j=0;j<schldProcesses.length;j++)
					{
						if(schldProcesses[j][1]<=count )
						{
							availableProcess++;
						}
					}
					if(i==availableProcess)
					{
						i=0;
					}
					
					i=(i+1)%availableProcess;
					flag=1;
					tempTime=0;
				}
				if(remainingTime[i]>0){
					if(flag==1){
						
						System.out.println(count+" "+ schldProcesses[i][0]);
						bw.newLine();
						bw.write(count+" "+ schldProcesses[i][0]);
						flag=0;
					}
					if(remainingTime[i]==1){
						if(checkProCount<processCount){
							waitingTime=waitingTime+((count+1)-schldProcesses[i][2]);
						}
						if(checkProCount==processCount){
							waitingTime=waitingTime+((count+1)-schldProcesses[i][2]);
							break;
						}
						checkProCount++;
					}
					remainingTime[i]=remainingTime[i]-1;
					tempTime++;
					count++;
					
				}
			}
			avg=waitingTime/processCount;
			System.out.println("Average Waiting Time for Round Robin:"+avg);
			bw.newLine();
			bw.write("Average Waiting Time:"+avg);
			bw.newLine();
			bw.close();
		}catch(IOException exe){
			System.out.println(exe +" occure in RR");
		}
	}

	
	public static void shortestJobFirst(int schldProcesses[][],int processCount,BufferedWriter bw){
		try{
			
			bw.write("SJF");
			int count=0,sum=0,time=0,selectedProcess=0,flag=0,n=processCount; 
			float waitingtime=0;
			int temp[]=new int[cols];
			int rt[]=new int[schldProcesses.length];
			int holdQueueProcess[]=new int[schldProcesses.length];
			HashMap hash=new HashMap();
			System.out.println("SJF");
			for(int i=0;i<schldProcesses.length;i++){
				for(int jq=0;jq<schldProcesses.length;jq++){
					if(schldProcesses[i][1]==schldProcesses[jq][1]){
						if(schldProcesses[i][2]<schldProcesses[jq][2]){
							temp=schldProcesses[i];
							schldProcesses[i]=schldProcesses[jq];
							schldProcesses[jq]=temp;
						}

					}
				}
			}
			for (int i=0;i<schldProcesses.length;i++){	
				holdQueueProcess[i]=-99;
				hash.put(new Integer(schldProcesses[i][0]), new Integer(i));
				rt[i]=schldProcesses[i][2];
				sum=sum+rt[i];
			} 	
			int i=0;
			int k=0;
			int checkProcessCount=0;
			while(count!=sum){
				if(count==0){
					flag=1;
				}
				if(rt[i]==0){
					checkProcessCount++;
					if(holdQueueProcess.length!=0){
						int min=Integer.MAX_VALUE;
						int processSelIndexInQueue=0;
						for(int j=0;j<holdQueueProcess.length;j++){
							if(holdQueueProcess[j]!=-99){
								int processId=holdQueueProcess[j];
								Integer index=(Integer)hash.get(new Integer(processId));

								int burstTime=schldProcesses[index.intValue()][2];
								if(min>burstTime){
									min=burstTime;
									selectedProcess=index.intValue();
									processSelIndexInQueue=j;
								}
							}
						}
						i=selectedProcess;
						flag=1;
						holdQueueProcess[processSelIndexInQueue]=-99;
					}else{
						i++;
					}
					if(checkProcessCount==processCount){
						break;
					}
				}
				while((k+1)< schldProcesses.length && schldProcesses[k+1][1]==count ){
					holdQueueProcess[k]=schldProcesses[k+1][0];
					k++;
				}
				if(flag==1){
					System.out.println(time  +" "+schldProcesses[i][0]);// + " Remaining Time  :"+rt[i]);
					bw.newLine();
					bw.write(time  +" "+schldProcesses[i][0]);

					flag=0;
					waitingtime=waitingtime+(time-schldProcesses[i][1]);
				}

				rt[i]=rt[i]-1;
				time++;
				count++;
			}

			waitingtime=waitingtime/n;
			System.out.println("Average Wait Time for SJF::"+waitingtime);
			bw.newLine();
			bw.write("Average Waiting Time :"+waitingtime);
			bw.newLine();
			bw.close();
		}catch(Exception e){
			e.getMessage();
		}
	}

	public static void prioritySchedulingwithoutPreemption(int schldProcesses[][],int processCount,BufferedWriter bw){
		try{ 
			bw.write("PR_noPREMP");
			HashMap hash=new HashMap();
			int count=0,sum=0,time=0,selectedProcess=0,flag=0,n=processCount; 
			float waitingtime=0;
			int temp[]=new int[cols];
			int rt[]=new int[schldProcesses.length];
			int holdQueueProcess[]=new int[schldProcesses.length];
			System.out.println("Output for Priority Scheduling Without Preemption:");
			for(int i=0;i<schldProcesses.length;i++){
				for(int jq=0;jq<schldProcesses.length;jq++){
					if(schldProcesses[i][1]==schldProcesses[jq][1]){
						if(schldProcesses[i][3]<schldProcesses[jq][3]){
							temp=schldProcesses[i];
							schldProcesses[i]=schldProcesses[jq];
							schldProcesses[jq]=temp;
						}

					}
				}
			}
			for (int i=0;i<schldProcesses.length;i++){	
				holdQueueProcess[i]=-99;
				hash.put(new Integer(schldProcesses[i][0]), new Integer(i));
				rt[i]=schldProcesses[i][2];
				sum=sum+rt[i];
			} 	
			int i=0;
			int k=0;
			int checkProcessCount=0;
			while(count!=sum){
				if(count==0){
					flag=1;
				}
				if(rt[i]==0){
					checkProcessCount++;
					if(holdQueueProcess.length!=0){
						int min=Integer.MAX_VALUE;
						int processSelIndexInQueue=0;
						for(int j=0;j<holdQueueProcess.length;j++){
							if(holdQueueProcess[j]!=-99){
								int processId=holdQueueProcess[j];
								Integer index=(Integer)hash.get(new Integer(processId));

								int priority=schldProcesses[index.intValue()][3];
								if(min>priority){
									min=priority;
									selectedProcess=index.intValue();
									processSelIndexInQueue=j;
								}
							}
						}
						i=selectedProcess;
						flag=1;
						holdQueueProcess[processSelIndexInQueue]=-99;
					}else{
						i++;
					}
					if(checkProcessCount==processCount){
						//System.out.println("I am breaking the loop");
						break;
					}
				}
				while((k+1)< schldProcesses.length && schldProcesses[k+1][1]==count ){
					//System.out.println("I am puting waiting process in queue:"+finalTa[k+1][0]);
					holdQueueProcess[k]=schldProcesses[k+1][0];
					k++;
				}
				if(flag==1){
					bw.newLine();
					bw.write(time  +" "+schldProcesses[i][0]);
					System.out.println(time  +" "+schldProcesses[i][0]);
					flag=0;
					waitingtime=waitingtime+(time-schldProcesses[i][1]);
				}

				rt[i]=rt[i]-1;
				time++;
				count++;
			}

			waitingtime=waitingtime/n;
			System.out.println("Average Wait Time for PR_withOutPreempt::"+waitingtime);
			bw.newLine();
			bw.write("Average Waiting Time :"+waitingtime);
			bw.newLine();
			bw.close();
		}catch(Exception e){
			e.getMessage();
		}
	}


	public static void  prioritySchedulingwithPreemption(int schldProcesses[][],int processCount,BufferedWriter bw){
		try{ 
			bw.write("PR_withPREMP");
			int count=0,i,n; 
			int temp[]=new int[cols];
			int rt[]=new int[schldProcesses.length]; 
			n=processCount; 
			System.out.println("priority scheduling with preemption");
			for(i=0;i<schldProcesses.length;i++){
				for(int jq=0;jq<schldProcesses.length;jq++){
					if(schldProcesses[i][1]<schldProcesses[jq][1]){ 
						temp=schldProcesses[i];
						schldProcesses[i]=schldProcesses[jq];
						schldProcesses[jq]=temp;
					}
					else if(schldProcesses[i][1]==schldProcesses[jq][1]){
						if(schldProcesses[i][3]<schldProcesses[jq][3]){
							temp=schldProcesses[i];
							schldProcesses[i]=schldProcesses[jq];
							schldProcesses[jq]=temp;
						}
					}
				}
			}
			for(i=0;i<schldProcesses.length;i++){
				rt[i]=schldProcesses[i][2];
			}
			boolean hasFinished=false;
			int currentPriority = schldProcesses[0][3];
			float longTime=0;
			int oldKey=-1;
			float avgWaitingTime=0;
			while(!hasFinished){
				int candidateProcessIndex  = -1;
				int priority = currentPriority;
				//flag=1;
				for(i=0;i < schldProcesses.length ;i++){

					if (rt[i] == 0){	
						hasFinished = true;
						continue;
					}
					if(schldProcesses[i][3] <= priority && schldProcesses[i][1] <= count){
						candidateProcessIndex = i;
						priority = schldProcesses[i][3];
					}
				}

				if (candidateProcessIndex > -1)
				{	

					if(oldKey!=candidateProcessIndex){
						System.out.println(" "+count+" "+schldProcesses[candidateProcessIndex][0]);
						bw.newLine();
						bw.write(" "+count+" "+schldProcesses[candidateProcessIndex][0]);;
					}
					rt[candidateProcessIndex] =  rt[candidateProcessIndex] - 1;
					if (rt[candidateProcessIndex] == 0 )
					{
						longTime=longTime+((count+1)-schldProcesses[candidateProcessIndex][2]-schldProcesses[candidateProcessIndex][1]);
						oldKey=candidateProcessIndex;
						currentPriority = Integer.MAX_VALUE;

					}
					else
					{
						oldKey=candidateProcessIndex;
						//System.out.println("Setting updated Priority");
						currentPriority = schldProcesses[candidateProcessIndex][3];
					}
					hasFinished = false;
				}

				count++;
			}

			avgWaitingTime=(longTime/n);
			System.out.println("Average Waiting Time  for Priority Scheduling With Preemption:"+avgWaitingTime);
			bw.newLine();
			bw.write("AVG Waiting Time: "+avgWaitingTime);
			bw.newLine();
			bw.close();
		}catch(IOException e){
			e.getMessage();
		}

	}
}
