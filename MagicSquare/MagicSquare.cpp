// MagicSquare.cpp : 定义控制台应用程序的入口点。
//编译器：VS 2015编译执行

#include "stdafx.h"

#include<iostream>
#include<cstring>
#include<string>
#include<cstdio>
#include<cmath>
#include<cstdlib>
#include<fstream>
using namespace std;

const string homePageString ="\
==========================================================================\n\
||  |  @  | @@@ |  @  |   -幻方---                                      ||\n\
||  | @ @ | @@@ |     |                                                 ||\n\
||  |  @  | @@@ |  @  |      将1~N^2的连续整数填入到一个NxN的网格中，   ||\n\
||  |-----------------|  使得每一行、每一列以及主副对角线的和都相等。   ||\n\
||  |   @ | @ @ | @ @ |                                                 ||\n\
||  |  @  |  @  | @@@ |                                                 ||\n\
||  | @   | @ @ | @ @ |                                                 ||\n\
||  |-----------------|                                                 ||\n\
||  | @@@ |     | @@  |                                                 ||\n\
||  | @ @ |  @  | @ @ |                                                 ||\n\
||  | @@@ |     |  @@ |                                                 ||\n\
==========================================================================\n\
|                1 开始            2 设置            3 退出              |\n\
==========================================================================";

const string endString = "\
==========================================================================\n\
|            1 再输入一个           2 回到首页         3 退出            |\n\
==========================================================================";

const string settingString = "\
==========================================================================\n\
|         1 最多输出幻方数           2 输出同构           3 说明         |\n\
==========================================================================";

const string directionString = "\
********************************************************\n\
*                                                      *\n\
*  各项设置说明：                                      *\n\
*  1 最多输出幻方数：                                  *\n\
*      由于每一个n阶的幻方的个数都会有很多，所以用户   *\n\
*  可以根据自己的需求设置自己想要的幻方个数上限。      *\n\
*                                                      *\n\
*  2 输出同构：                                        *\n\
*      能通过行列的交换相互得到的幻方为同构幻方，设置  *\n\
*  为false，可以使得输出的幻方更加特别。               *\n\
*                                                      *\n\
********************************************************";

#define MaxSizeOfPrimeNum 10000
#define Odd 1
#define SingleDouble 2
#define DDouble 4
typedef long long LL;


//variatesList 变量列表
int jieNum;
int primeNums[MaxSizeOfPrimeNum+10];
LL sum;
bool endOrNot;
int **MSForSDMS; //单偶幻方的右上角幻方
int MSType;
int SingleDMS_Complete;
ofstream fout; 

//默认设置
_int64 maxShowNum = 100;
bool showSameStruct = true;

//函数声明
void showCurSetting();
bool judgeJie(double& jie);
bool judgeYN(string YOrN);
void swap(int *a, int *b);
bool isMagicSquare(int **MS,_int64 jieN);
void destroyMS(int **MS,int jieN);
int show(int **MS,int jieN);
void createPriemNum();
void increaseTypes(int **MSquare,int jieN);
void permForRows(int **MS,int jieN,int *list, int k, int m);
void SwapListRows(int **MS,int jieN,int *comb,int combLen);
void combineForRows(int **MS,int jieN, int *list, int listLen, int combLen);
void permForCols(int **MS,int jieN,int *list, int start, int endI);
void SwapListCols(int **MS,int jieN,int *comb,int combLen);
void combineForCols(int **MS,int jieN, int *list, int listLen, int combLen);
void createOddMagicSquare(int **MS, int jieN,int steps);
void createOddAddPrimeMagicSquare(int jieN);
void createDoubleEvenMagicSquare(int jieN);
void createSingleEvenMagicSqure(int jieN);

//输出当前设置信息
void showCurSetting()
{
	cout<<"=========================================================================="<<endl;
	cout<<"| 当前设置:"<<endl;
	cout<<"| 幻方个数: ";
	printf("%ld",maxShowNum);
	cout<<"     输出同构: ";
	if(showSameStruct)
		cout<<"true";
	else
		cout<<"false";
	cout<<endl;
}

bool judgeJie(double& jie)
{
	while(jie < 3 || ceil(jie)!=floor(jie) )
	{
		cout<<"输入有误，请输入大于或等于3的正整数"<<endl;
		cin>>jie;
	}
	return true;
}

bool judgeYN(string YOrN)
{
	while(YOrN.length() > 1 )
	{
		cout<<"输入有误，请输入单个字符"<<endl;
		cin>>YOrN;
	}
	while(YOrN !="Y" && YOrN != "y" && YOrN != "N" && YOrN != "n")
	{
		cout<<"输入有误，请输入Y或者N"<<endl;
		cin>>YOrN;
	}
	return (YOrN == "Y" || YOrN == "y");
}

//[end]前端

//[start]后端


void swap(int *a, int *b)
{
    int m;
    m = *a;
    *a = *b;
    *b = m;
}

//检查幻方是否正确
bool isMagicSquare(int **MS,_int64 jieN)  
{  
//	cout<<n<<endl;
	int i,j;
    _int64 sum = (jieN*(jieN*jieN+1))/2;  
    _int64 SumA=0,SumB=0;  
	
    for( i=0;i<jieN;i++) //行的和 
    {  
        for(int j=0;j<jieN;j++)  
            SumA += MS[i][j]; 
        if(SumA != sum){ 
			cout<<"幻和："<<sum<<"  "<<"所得行 "<<i<<" 和："<<SumA<<endl;
			cout<<"行不对"<<endl;
            return false;  
		}
        SumA = 0;   
    }  

    for( i=0;i<jieN;i++)  //列的和
    {  
        for( j=0;j<jieN;j++)  
            SumA += MS[j][i];  
        if(SumA != sum)
		{
			cout<<"幻和："<<sum<<"  "<<"所得列 "<<i<<" 和："<<SumA<<endl;
			cout<<"列不对"<<endl;
            return false; 
		}
        SumA = 0;   
    }  
	
    for( i=0;i<jieN;i++)  
    {  
        SumA+=MS[i][i];  //主对角线 
        SumB+=MS[i][jieN-i-1];   //副对角线
    }  
    if(SumA!=sum||SumB!=sum) { 
		cout<<"幻和为"<<sum<<" SumA="<<SumA<<" "<<"SumB="<<SumB<<endl;
		cout<<"对角线不对"<<endl;
        return false;  
	}

	return true;  
} 


//释放幻方
void destroyMS(int **MS,int jieN)
{
	for(int i = 0;i<jieN;i++)
		delete [] MS[i];
	delete [] MS;
}


//输出幻方同时释放幻方内存
int show(int **MS,int jieN)
{
	if(endOrNot)
		return -1;
	int i,j;

	cout<<"*********第"<<++sum<<"个*********"<<endl;
	fout<<"*********第"<<sum<<"个*********"<<endl;
	for(i=0;i<jieN;i++)
	{
		for(j=0;j<jieN-1;j++)
		{
			cout<<MS[i][j]<<"\t";
			fout<<MS[i][j]<<"\t";
		}
		cout<<MS[i][j]<<endl;
		fout<<MS[i][j]<<endl;
	}
	cout<<endl;
	//检测是否是幻方
	if(!isMagicSquare(MS,jieN))
	{
		cout<<"不是幻方"<<endl;
		system("pause");
	}
	if(sum >= maxShowNum)
	{
		endOrNot = true;
		destroyMS(MS,jieN);
		return -1;
	}
	destroyMS(MS,jieN);
	return 1;
}

//创建10000以内的素数表
void createPriemNum()
{
	int n = MaxSizeOfPrimeNum+10;
	int i,j;
	for(i = 1;i<=n;i++)
		primeNums[i] = i;

	primeNums[0] = 0;
	primeNums[1] = 0;
	for(i = 2;i<=n;i++)
	{
		if(primeNums[i] != 0)
		{
			for(j = 2;j*i<=n;j++)
			{
				primeNums[i*j] = 0;
			}
		}
	}
}


/********************对列进行操作******************/

//通过行列交换实现幻方个数剧增
void increaseTypes(int **MSquare,int jieN)
{
	int i;
	int *comb = new int[jieN]; //需要在后面用到
	for(i=0;i<jieN/2;i++) //前半段
	{
		comb[i] = i;
	}
	for(i=0;i<=jieN/2;i++)
	{
		//组合
		combineForCols(MSquare,jieN,comb,jieN/2,i); //对list上的值进行组合，再传到link中
		if(endOrNot)
			break;
	}
	delete [] comb;
}


//行——排列，行排列交换之后就可以输出了,其中里面包含了对偶数阶的操作方法
void permForRows(int **MS,int jieN,int *list, int k, int m) //k为开始的下标0，m为list的最后一个下标len-1,jieN/2
 {
	if(endOrNot)
		return;

	 int i;
	
    if(k > m) {  //找到一个排列
		int** newMS = new int*[jieN];
		for(i = 0;i<jieN;i++)
			newMS[i] = new int[jieN];
		int j;
		for(i = 0;i<m+1;i++)
		{
			for(j = 0;j<jieN;j++)
			{
				newMS[i][j] = MS[list[i]][j];
				newMS[jieN-i-1][j] = MS[jieN -list[i]-1][j];
			}
		}
		if(jieN % 2 != 0) //中间列复制
		{
			i = m+1; 
			for(j = 0;j<jieN;j++)
				newMS[i][j] = MS[i][j];
		}

		//show内会delete掉newMS
		if(MSType != SingleDouble)
		{
			show(newMS,jieN);
		}
		else
		{
			if(SingleDMS_Complete == 1) //上面两个幻方都完成
			{
				//此时生成的newMS是右上角的幻方B
				int i,col,row;
				int singleJieN;
				singleJieN = jieN;
				//创建一个全新的单偶幻方，利用MSForSDMS和newMS来生成幻方
				 int **singleDMS  = new int*[jieNum];
				 for(i = 0;i<jieNum;i++)
					singleDMS[i] = new int[jieNum];
				//利用生成的MSForSDMS(左上角A)生成左下角区域C
				for(row = 0; row < singleJieN; row++)
				{
					for(col = 0; col < singleJieN; col++)
					{
						singleDMS[row][col] = MSForSDMS[row][col]; //第二象限A
						singleDMS[row+singleJieN][col] = MSForSDMS[row][col] + 3*singleJieN*singleJieN;  //第三象限C
					}
				}
				//利用生成的（newMS）生成第右上角区域B生成右下角区域D
				for(row = 0; row < singleJieN; row++)
				{
					for(col = 0; col < singleJieN; col++)
					{
						singleDMS[row][col+singleJieN] = newMS[row][col] + 2*singleJieN*singleJieN;  //第一象限B
						singleDMS[row+singleJieN][col+singleJieN] = newMS[row][col] + singleJieN*singleJieN;  //第四象限D
					}
				}
				destroyMS(newMS,singleJieN);
				//交换AC相应元素
				for(row = 0; row < singleJieN; row++)
				{
					if(row == singleJieN/2) //交换x方向正中行的从左至右m-1个，N = 4*m + 2;
					{
						for(col = singleJieN / 2; col < singleJieN - 1; col++)
						{
							swap(singleDMS[row + singleJieN][col],singleDMS[row][col]);
						}
					}
					else //左边其他行，交换从左向右m列,N = 4*m + 2
					{
						for(col = 0;col < singleJieN / 2;col++)
						{
							swap(singleDMS[row][col],singleDMS[row + singleJieN][col]);
						}
					}
				}
				//交换BD相应元素
				//交换最右边m-1个数字,m = singleJieN/2
				int m = singleJieN/2;
				for(row = 0; row < singleJieN; row++)
				{
					for(col = jieNum-1;col > jieNum - 1 - (m - 1);col--)
					{
						swap(singleDMS[row][col],singleDMS[row + singleJieN][col]);
					}
				}
				//输出
				show(singleDMS,jieNum);
				if(endOrNot)
					return;
			}
			else
			{
				//未完成表示需要生成右上角区域的幻方，创建一个全新的即可
				//此时生成的幻方是左上角的幻方
				MSForSDMS = newMS;
				int steps;
				int **MSquare = new int*[jieN];
				for(int i = 0;i<jieN;i++)
					MSquare[i] = new int[jieN];
				//继续组合排序
				for(steps = 1; steps<=jieN - 2; steps++)
				{
					SingleDMS_Complete = 1;
					createOddMagicSquare(MSquare,jieN,steps);
					//交换行列使其个数增长
					
					increaseTypes(MSquare,jieN);
					if(endOrNot)
						break;
					SingleDMS_Complete = 0;
					if(primeNums[jieN] == 0)
						break;
				}
				SingleDMS_Complete = 0;
				//释放矩阵
				destroyMS(MSquare,jieN);
				destroyMS(MSForSDMS,jieN);
				if(endOrNot)
					return;
			}
			
		}
			
    }
	else { 
        for(i = k; i <= m; i++) { 
            swap(&list[k], &list[i]);
			permForRows(MS,jieN,list,k+1, m);
            swap(&list[k], &list[i]);
        }
    }
}



//根据生成的组合，对行进行交换
void SwapListRows(int **MS,int jieN,int *comb,int combLen)
{
	int i,j;
	int** newMS = new int*[jieN];
	for(i = 0;i<jieN;i++)
		newMS[i] = new int[jieN];

	for(i=0;i<jieN;i++)
		for(j=0;j<jieN;j++)
			newMS[i][j] = MS[i][j];

	int otherI;
	for(i = 0;i<combLen;i++)
	{
		int j;
		for(j = 0;j<jieN;j++)
		{
			otherI = jieN - 1 - comb[i]; //映射列
			swap(newMS[comb[i]][j],newMS[otherI][j]);
		}
	}
	
	int *list = new int[jieN];
	int n = jieN/2;
	for(i = 0;i<n;i++)
	{
		list[i] = i;
	}
	//将交换得到的幻方进行行的排序交换操作
	permForRows(newMS,jieN,list, 0, n-1);
	//使用过后释放创建数组
	destroyMS(newMS,jieN);
	delete [] list;

}

//回溯法求排列组合，对幻方进行行变换
void combineForRows(int **MS,int jieN, int *list, int listLen, int combLen)
{
	int i,j;
	int** newMS = new int*[jieN];
	for(i = 0;i<jieN;i++)
		newMS[i] = new int[jieN];

	for(i=0;i<jieN;i++)
		for(j=0;j<jieN;j++)
			newMS[i][j] = MS[i][j];
	
	combLen = combLen > listLen ? listLen : combLen;
	int* order = new int[combLen+1]; //order 是位置
	for(i=0; i<=combLen; i++)
		order[i] = i-1;            // 注意这里order[0]=-1用来作为循环判断标识，回溯到原点时结束
	
	int k = combLen;
	bool flag = true;           // 标志找到一个有效组合
	while(order[0] == -1)
	{
		if(flag)                   // 输出符合要求的组合
		{
			//	cout<<"找到一组组合："<<endl;
			int *comb = new int[combLen];
			for(i=1; i<=combLen; i++){ //list
				//cout << list[order[i]] << " ";
				comb[i-1] = list[order[i]];
			}
			
			SwapListRows(newMS,jieN,comb,combLen);//利用生成的组合进行行变换，生成的组合是需要交换的行序号的组合;
			
			flag = false;
			delete [] comb;
			if(endOrNot)
			{
				delete[] order;
				destroyMS(newMS,jieN);
				return;
			}
		}
		
		order[k]++;                // 在当前位置选择新的数字
		if(order[k] == listLen)          // 当前位置已无数字可选，回溯，
		{
			order[k--] = 0; 
			continue;
		}
		
		if(k < combLen)                  // 更新当前位置的下一位置的数字        
		{
			order[++k] = order[k-1];
			continue;
		}
		
		if(k == combLen)
			flag = true;
	}
	delete[] order;
	destroyMS(newMS,jieN);
}

/*********************对行进行操作*************************/

//列——排列
void permForCols(int **MS,int jieN,int *list, int start, int endI) //k为开始的下标0，m为list的最后一个下标len-1,jieN/2
{ 
	if(endOrNot)
		return;

    int i;
	
    if(start > endI) {  //每次递归调用k都会加1，当递归完全部就表示找到一个排列
		//为了不改变之前的数组，这里要创建一个新的数组存放之前的值
		int** newMS = new int*[jieN];
		for(i = 0;i<jieN;i++)
			newMS[i] = new int[jieN];

		//利用生成的排列对幻方进行列交换，从而生成另一个幻方
		//映射列赋值
		int j;
		for(i = 0;i<endI+1;i++)
		{
			for(j = 0;j<jieN;j++)
			{
				newMS[j][i] = MS[j][list[i]];
				newMS[j][jieN-i-1] = MS[j][jieN -list[i]-1];
			}
		}
		//如果是奇数阶就中间列复制
		if(jieN % 2 != 0)
		{
			i = endI+1;
			for(j = 0;j<jieN;j++)
				newMS[j][i] = MS[j][i];
		}

		//这里开始对行进行组合排序
		int *comb = new int[jieN];
		for(i=0;i<jieN/2;i++) //利用前半段的序号进行组合，后半段到时映射操作即可
		{
			comb[i] = i;
		}

		for(i=0;i<=jieN/2;i++)
		{
			//对0~jieN/2个数进行组合
			combineForRows(newMS,jieN,comb,jieN/2,i); //对list上的值进行组合，再传到link中
			if(endOrNot)
			{
				destroyMS(newMS,jieN);
				delete [] comb;
				return;
			}
		}

		//利用这个矩阵生成其他转换行的矩阵后要释放掉
		destroyMS(newMS,jieN);
		delete [] comb;
			
    }
	else { 
        for(i = start; i <= endI; i++) 
		{ 
            swap(&list[start], &list[i]);
			permForCols(MS,jieN,list,start+1, endI);
            swap(&list[start], &list[i]);
        }
    }
}

//根据生成的组合进行列交换
void SwapListCols(int **MS,int jieN,int *comb,int combLen)
{
	if(endOrNot)
		return;
	int i,j;
	int** newMS = new int*[jieN];
	for(i = 0;i<jieN;i++)
		newMS[i] = new int[jieN];

	for(i=0;i<jieN;i++)
		for(j=0;j<jieN;j++)
			newMS[i][j] = MS[i][j];

	int otherI;
	for(i = 0;i<combLen;i++)
	{
		int j;
		for(j = 0;j<jieN;j++)
		{
			otherI = (jieN - 1) - comb[i]; //映射列
			swap(newMS[j][comb[i]],newMS[j][otherI]);
		}
	}
	
	int* sortList = new int[jieN];
	for(i = 0;i<jieN/2;i++)
	{
		sortList[i] = i;
	}
	
	permForCols(newMS,jieN,sortList, 0, jieN/2-1);
	
	destroyMS(newMS,jieN);
	delete [] sortList;
}


//回溯法求组合，组合选出的下标作为交换的列的下标
void combineForCols(int **MS,int jieN, int *list, int listLen, int combLen)
{
	int i,j;
	int** newMS = new int*[jieN];
	for(i = 0;i<jieN;i++)
		newMS[i] = new int[jieN];

	for(i=0;i<jieN;i++)
		for(j=0;j<jieN;j++)
			newMS[i][j] = MS[i][j];
	
	combLen = combLen > listLen ? listLen : combLen; //防止需要组合长度大于原来数组的长度
	int* order = new int[combLen+1]; //order 是位置
	for(i=0; i<=combLen; i++)
		order[i] = i-1;            // 注意这里order[0]=-1用来作为循环判断标识，回溯到原点时结束
	
	
	int k = combLen;
	bool flag = true;           
	while(order[0] == -1)   // 标志找到一个有效组合
	{
		if(flag)                   // 输出符合要求的组合
		{
			int *comb = new int[combLen];  //存放组合结果
			
			//利用生成的组合进行行变换，生成的组合是需要交换的行序号的组合
			for(i=1; i<=combLen; i++){ 
				comb[i-1] = list[order[i]];
				
			}
			SwapListCols(newMS,jieN,comb,combLen);
			delete [] comb;
			flag = false;
			if(endOrNot)
				return;
		}
		
		order[k]++;                // 在当前位置选择新的数字
		if(order[k] == listLen)          // 当前位置已无数字可选，回溯，
		{
			order[k--] = 0; 
			continue;
		}
		
		if(k < combLen)                  // 更新当前位置的下一位置的数字        
		{
			order[++k] = order[k-1];
			continue;
		}
		
		if(k == combLen)
			flag = true;
	}
	delete[] order;
	
	destroyMS(newMS,jieN);
}


//普通奇数阶
void createOddMagicSquare(int **MS, int jieN,int steps)  
{  
	int i,col,row;

	col = jieN/2;
	row = 0;
	MS[row][col] = 1; //填写1，到第一行的中间位置

	for(i = 2;i <= jieN*jieN;i++) //每组有jieNum个，所以jieNum的倍数是每一组的最后一个数字，之后就会往下掉
	{
		if((i-1)%jieN == 0)  //每取到jieN个数后的下一个数就会有出现往下掉一格
		{
			row++; //往下掉一行就等于行加1
		}
		else		//继续往右上移steps步，row -= steps,col++
		{
			row -= steps;
			row = (row+jieN)%jieN; //取余是为了当row小于0时可以回绕

			col ++;
			col %= jieN;
		}
		MS[row][col] = i;
	}
}



//奇数、素数阶
void createOddAddPrimeMagicSquare(int jieN)
{
	int steps;
	int **MSquare;
	sum = 0;
	for(steps = 1; steps<=jieN - 2; steps++)
	{
		MSquare = new int*[jieN];
		for(int i = 0;i<jieN;i++)
			MSquare[i] = new int[jieN];

		createOddMagicSquare(MSquare,jieN,steps);
		
		//交换行列使其个数增长
		if(showSameStruct)
		{
			increaseTypes(MSquare,jieN);
			//释放矩阵
			destroyMS(MSquare,jieN);
		}
		else 
		{
			show(MSquare,jieN);
		}
		
		if(primeNums[jieN] == 0)
			break;
	}
	cout<<"共有："<<sum<<"个"<<endl;
	fout<<"共有："<<sum<<"个"<<endl;
}

//双偶阶 jieNum = 4*m (m = 1,2,3,...)
void createDoubleEvenMagicSquare(int jieN)
{
	int i,j,k;
	sum = 0;
	int **MSquare = new int*[jieN];
	for(i = 0; i < jieN; i++)
		MSquare[i] = new int[jieN];
    int num = 1;  
    //从1到jieN的平方依次赋值   
    for( i = 0; i < jieN; i ++)  
        for( j = 0; j < jieN; j ++)  
            MSquare[i][j] = num++ ;  
              
    //各个4x4小正方形的对角线上的数字取其补数   	
    for( i = 0; i < jieN; i ++)  
	{
        for( j = 0; j < jieN; j ++)  
        {  
            if( i % 4 == 0 && abs(i-j) % 4 == 0)  //副对角线方向\的最左上端点
                for( k = 0; k < 4; k ++)  
                    MSquare[i+k][j+k] = abs( jieN*jieN +1 - MSquare[i+k][j+k] );
				
            else if ( i % 4 == 3 && (i+j) % 4 == 3) //主对角线方向/的最右上端点 
                for( k = 0; k < 4; k ++)  
                    MSquare[i-k][j+k] = abs( jieN*jieN +1 - MSquare[i-k][j+k] );
        }
	}
	//生成一个幻方,交换行列使其个数增长
	if(showSameStruct)
	{
		increaseTypes(MSquare,jieN);
		//释放矩阵
		destroyMS(MSquare,jieN);
	}
	else
		show(MSquare,jieN);
	
	cout<<"共有："<<sum<<"个"<<endl;
	fout<<"共有："<<sum<<"个"<<endl;
}

//单偶阶 jieNum = 4*m + 2 (m = 1,2,2,...)
void createSingleEvenMagicSqure(int jieN)
{
	int i,steps;
	int singleJieN = jieN/2;
	sum = 0;
	int **MSquare = new int*[singleJieN];
	for(i = 0;i<singleJieN;i++)
		MSquare[i] = new int[singleJieN];
	for(steps = 1; steps<=singleJieN - 2; steps++)
	{
		SingleDMS_Complete = 0; //表示还没有生成幻方
		createOddMagicSquare(MSquare,singleJieN,steps); //对左上角区域创建奇数阶幻方
		increaseTypes(MSquare,singleJieN);
		if(primeNums[singleJieN]==0)
			return;
	}
	destroyMS(MSquare,singleJieN);
	cout<<"共有："<<sum<<"个"<<endl;
	fout<<"共有："<<sum<<"个"<<endl;
	//释放矩阵
}

int main()
{
	double option;
	createPriemNum();
	fout.open("MagicSquare.txt");
	while(true)
	{
		sum = 0;
		endOrNot = false;
		cout<<homePageString<<endl;
		cin>>option;
		while(option != 1 && option !=2 && option != 3)
		{
			cout<<"输入有误，请输入选项编号"<<endl;
			cin>>option;
		}
		
		if(option ==1)
		{
			while(true)
				{
				double jie;
				cout<<"请输入幻方阶数"<<endl;
				cin>>jie;
				judgeJie(jie); //判断合法性
				jieNum = jie;
				if(jieNum%4 == 0 )
				{
					//双偶
					MSType = DDouble;
					createDoubleEvenMagicSquare(jieNum);
				}
				else if(jieNum%2 == 0)
				{
					//单偶阶
					MSType = SingleDouble;
					createSingleEvenMagicSqure(jieNum);
				}
				else
				{
					//奇数阶（含素数）
					MSType = Odd;
					createOddAddPrimeMagicSquare(jieNum);
				}
				cout<<endl;
				cout<<"输出完成，输入1继续输入，输入2返回主界面"<<endl;
				double choose;
				cin>>choose;
				while(choose != 1 && choose !=2)
				{
					cout<<"输入有误，请输入选项编号"<<endl;
					cin>>choose;
				}
				if(choose == 1)
				{
					endOrNot = false;
				}
				else if(choose == 2)
				{
					system("cls");
					break;
				}
				system("cls");
			}

		}
		else if(option == 2)
		{
			system("cls");
			string setting;
			long long showNum = maxShowNum;
			bool showSame = showSameStruct;
			showCurSetting();
			cout<<settingString<<endl;
			cout<<endl;
			
			cout<<"请输入设置编号，输入\"S\"保存放回首页,\"R\"放弃设置"<<endl;
		
			while(true)
			{
				cin>>setting;
				while(setting.length() > 1)
				{
					cout<<"输入有误，请输入单个字符"<<endl;
					cin>>setting;
				}
				//处理选项
				if(setting == "1") //设置输出幻方个数
				{
					cout<<"请输入最多幻方个数"<<endl;
					cin>>showNum;
					while(showNum < -1 || showNum>9223372036854775807 || ceil(showNum) != floor(showNum))
					{
						cout<<"输入有误，请输入 1 ~ 922,3372,0368,5477,5807 内的正整数"<<endl;
						cin>>showNum;
					}
				}
				else if(setting == "2") //输出同构
				{
					cout<<"是否输出同构幻方(Y/N)"<<endl;
					string YOrN;
					cin>>YOrN;
					showSame = judgeYN(YOrN);
					
				}
				else if(setting == "3") //说明
				{
					cout<<directionString<<endl;
				}
				else if(setting == "s" || setting == "S")
				{
					showSameStruct = showSame;
					maxShowNum = showNum;
					system("cls");
					break;
				}
				else if(setting == "R" || setting == "r")
				{
					system("cls");
					break;
				}
				else
				{
					cout<<"请输入选项编号（如需保存退出请输入\"S\",放弃设置输入\"R\"）"<<endl;
				}
			}
			continue;
		}
		else if(option == 3)
		{
			cout<<"谢谢您的使用"<<endl;
			return 0;
		}
		
	}
	return 0;
}
