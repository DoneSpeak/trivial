# java_simpleChat
这是一个用java写的简单聊天工具，使用的是UDP协议。
开始聊天的需要输入自己的选择的端口号MyPort，同时输入对方的ip和端口号ToPort。
功能：
1、确定了聊天对象后除非断开，否则不允许修改自己的端口号和对方的ip地址和端口号。
2、可以清楚当前聊天记录
3、可以查看历史记录，聊天历史记录窗口仿照了qq的打开窗口，为聊天端口的侧面打开。
4、清楚历史记录暂时还有bug。

仅可以一对一的聊天，如有有第三者请求聊天，会弹出提示窗口，并写入聊天记录（这是一个bug，需要修改）。
