# java_NetSourcesCatcher

1.	使用Socket编程  
2.	获取指定网页的html文件，并保存到本地，仅支持http协议。但尚未解决部分网页重定向问题，及部分网页禁止访问问题。  
3.	获取网页中\<img \>\<script\>\<link\>标签中图片文件，js脚本文件，css样式文件，
保存到本地（部分图片链接不是放在\<img\>标签中的，如果网页的框架是用table搭建出来的，图片可能就会被用来作为background展示，当然其他的可以作为background。有些图片文件，js文件，css文件是通过https协议才能访问到的，而我这个只限于http协议，所以无法完成）。  
4.	替换获得的html文件中的<img ><script><link>指定的资源链接为本地路径，使得在断网的情况下通过浏览器打开，绝大部分图片正常显示，渲染和原网页差别不大。由于上面的原因，所以不能保证和原网页一模一样的展示。  
