# MagicSquare

use number n^2 numbers 1 to n^2 to fix into a n*n square to make the sum of per row ,per col, main diagonal and back diagonal equal.

背景：

据了解，4阶幻方个数的基本型就有880个，通过旋转和反射总共可有7040个不同的形式的，5阶幻方基本型有275 305 224个，6阶幻方的个数非常之多，皮恩和维茨考夫斯基利用蒙特卡洛模拟和统计学方法，也只能获得一个大概的估计数字，其数量在1.7743 x 10^19 ~ 1.7766 x 10^19之间，可见随着幻方阶数的增加其数量将会快速地增长。同时及时是6阶幻方得到的仅仅是理论值，其中的幻方未必就能利用现有的方法构造出来。


先说明几个基本的概念：

1)	不存在2阶幻方；

2)	奇数阶幻方：阶数n为奇数的幻方；

3)	双偶数阶幻方：阶数n可以被4整除的偶数阶幻方，即4K阶幻方（如n=4，8，12 ……的幻方）；

4)	单偶数阶幻方：阶数n不可以被4整除的偶数阶幻方, 即4K+2阶幻方（如n=6，10，14……的幻方）；

5)	幻和：幻方行、列 、主副对角线的和，为n*(n*n+1)/2；

6)	互补数：在n阶幻方中，和为n^2+1的两个数为互补数；

7)	同构幻方：可以通过矩阵行列交换相互得到的幻方；

8)	映射：幻方左右或上下两侧关于对称轴对称；

9)	映射行、列：关于幻方水平对称轴对称的行，关于垂直对称轴对称的的列；

10)	映射操作：对映射列或映射行操作。


基本思想：

1、奇数阶幻方：罗伯法（连续摆数法）
  
把1（或最小的数）放在第一行正中，并按以下规律排列剩下的(n×n－1)个数：

（1）每一个数放在前一个数的右上一格；

（2）如果这个数所要放的格已经超出了顶行那么就把它放在底行，仍然要放在右一列；

（3）如果这个数所要放的格已经超出了最右列，那么就把它放在最左列，仍然要放在上一行；

（4）如果这个数所要放的格已经超出了顶行且超出了最右列，那么就把它放在底行且最左列；

（5）如果这个数所要放的格已经有数填入，那么就把它放在前一个数的下一行同一列的格内。


2、自创素数阶幻方构成法

  该方法是基于罗伯法的基础上而得出的，也是连续摆数法。我发现在罗伯法的基础上，如果有第一步改为前一个数右上1~n-2行都可以成立（有通过计算机对3~5000内的所有素数进行过测试）。因此对于素数n，通过这个方法可得到n-2个幻方。

3、双偶数阶幻方:海尔法

4、单偶数阶幻方：斯特拉兹法

5、	通过矩阵行列交换的映射操作、旋转实现幻方个数的暴增

更加详细的描述之后有空,我会在我的博客中发表，有兴趣的可以关注下：http://blog.csdn.net/DoneSpeak。
