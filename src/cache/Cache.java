package cache;
import java.util.*;
import java.lang.*;
public class Cache {
	//show all the elements of 2D array
	public static void Display(int[][] x) {
		System.out.println("Slot"+" "+"Dirty"+" "+"Valid"+" "+"Tag"+"    "+"Data");
		for(int row=0;row<x.length;row++) {
			if(x[row] != null) {
				for(int column=0;column<4;column++) {
					System.out.print("  "+String.format("%X", x[row][column])+"  ");
				}
				System.out.print("   ");
				for(int column=4;column<x[row].length;column++) {
					System.out.print(String.format("%02X", x[row][column])+" ");
				}
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		int[] Main_mem=new int[0x800];
		for(int i=0;i<0x800;i++) {
			int j=i;
			j=j&0xFF;
			Main_mem[i]=j;
		}
		int[][] Cache=new int[16][20];//default value is 0
		for(int i=0;i<16;i++) {       //initialize slot number
			Cache[i][0]=i;
		}
		while(true) {
		String input1=null;			//R,W,D
		int input2=0;               //address
		int input3=0;				//data
		Scanner scan = new Scanner(System.in);
		while (true) {
	        System.out.println("(R)ead, (W)rite,or (D)isplay Cache?");
	        input1 = scan.nextLine();
	        if(input1.equals("R")|input1.equals("W")|input1.equals("D")){
	            break;
	        }else if(input1.equals("exit")){		//function:exit
	        	System.out.println("exit...");
	        	scan.close();
	        	System.exit(0);
	        }
	    }
		if(input1.equals("R")) {
			while(true) {
				System.out.println("What address would you like to read?");
				try {
	                input2 = Integer.parseInt(scan.nextLine(),16);
	                break;
	            } catch (NumberFormatException nfe) {
	                System.out.print("Try again: ");
	            }
			}
			int bo = input2 & 0x00F;
			int block_begin= input2 & 0xFF0;
			int slot = (input2 & 0xF0)>>>4;
			int tag = (input2 & 0xF00)>>8;
			if(Cache[slot][2]==1&Cache[slot][3]==tag) {
				System.out.println("At that byte there is the value "+String.format("%02X",Cache[slot][bo+4])+" (Cache Hit)");
			}else {
				if(Cache[slot][1]==1) {              //Dirty bit
					int tag2=Cache[slot][3];
					int block_begin2=(tag2<<8)+(slot<<4);
					for(int i=0;i<16;i++) {
						Main_mem[block_begin2+i]=Cache[slot][i+4];
					}
				}
				for(int i=0;i<16;i++) {
					Cache[slot][i+4]=Main_mem[block_begin+i];
				}
				Cache[slot][1]=0;					//block brought into cache, Dirty=0
				Cache[slot][2]=1;
				Cache[slot][3]=tag;
				System.out.println("At that byte there is the value "+String.format("%02X",Cache[slot][bo+4])+" (Cache Miss)");
			}
		}
		
		else if(input1.equals("W")) {
			//get address
			while(true) {
				System.out.println("What address would you like to write to?");
				try {
	                input2 = Integer.parseInt(scan.nextLine(),16);
	                break;
	            } catch (NumberFormatException nfe) {
	                System.out.print("Try again: ");
	            }
			}
			//get data
			while(true) {
				System.out.println("What data would you like to write at that address?");
				try {
	                input3 = Integer.parseInt(scan.nextLine(),16);
	                if(input3>0xFF) {
	                	continue;
	                }else {
	                	break;}
	            } catch (NumberFormatException nfe) {
	                System.out.print("Try again: ");
	            }	
			}
			int bo = input2 & 0x00F;
			int block_begin= input2 & 0xFF0;
			int slot = (input2 & 0xF0)>>>4;
			int tag = (input2 & 0xF00)>>8;
			if(Cache[slot][2]==1&Cache[slot][3]==tag) {
				Cache[slot][bo+4]=input3;
				Cache[slot][1]=1;					//after revised, Dirty bit=1
				System.out.println("Value "+String.format("%02X",input3)+" has been written to address "+String.format("%02X",input2)+" (Cache Hit)");
			}else {
				if(Cache[slot][1]==1) {              //Dirty bit
					int tag2=Cache[slot][3];
					int block_begin2=(tag2<<8)+(slot<<4);
					for(int i=0;i<16;i++) {
						Main_mem[block_begin2+i]=Cache[slot][i+4];
					}
				}
				for(int i=0;i<16;i++) {
					Cache[slot][i+4]=Main_mem[block_begin+i];
				}
				Cache[slot][1]=0;				//block brought into cache,Dirty bit=0
				Cache[slot][2]=1;
				Cache[slot][3]=tag;
				Cache[slot][bo+4]=input3;
				Cache[slot][1]=1;				//after revised, Dirty bit=1
				System.out.println("Value "+String.format("%02X",input3)+" has been written to address "+String.format("%02X",input2)+" (Cache Miss)");
			}
		}
		
		else {
			Display(Cache);
		}
		System.out.println();
		}
	}
}
